package de.applejuicenet.client.shared;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class SoundPlayerTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Path base;
    private final AtomicBoolean enabled = new AtomicBoolean(true);
    private final AtomicReference<String> pack = new AtomicReference<>("classic");
    private final List<FakePlayback> opened = new CopyOnWriteArrayList<>();
    private final List<String> events = new CopyOnWriteArrayList<>();
    private final AtomicReference<Thread> openThread = new AtomicReference<>();

    @Before
    public void createSounds() throws IOException {
        base = folder.getRoot().toPath();
        for (String name : new String[] {"classic", "warp"}) {
            Files.createDirectories(base.resolve(name));
            for (String file : new String[] {"laden", "suchen", "komplett", "gespeichert"}) {
                Files.write(base.resolve(name).resolve(file + ".wav"), new byte[] {1});
            }
        }
    }

    private class FakePlayback implements SoundPlayer.Playback {
        final Path file;
        final AtomicBoolean closed = new AtomicBoolean();
        final CountDownLatch started = new CountDownLatch(1);
        volatile Runnable onFinished;
        volatile boolean listenerBeforeStart;

        FakePlayback(Path file) {
            this.file = file;
        }

        public long durationMillis() {
            return 100;
        }

        public void start(Runnable onFinished) {
            this.onFinished = onFinished;
            listenerBeforeStart = true;
            events.add("start " + file.getParent().getFileName() + "/" + file.getFileName());
            started.countDown();
        }

        public void close() {
            closed.set(true);
        }

        void finish() {
            onFinished.run();
        }
    }

    private SoundPlayer player(SoundPlayer.Opener opener) {
        return new SoundPlayer(opener, enabled::get, pack::get, () -> base);
    }

    private SoundPlayer.Opener fakeOpener() {
        return file -> {
            openThread.set(Thread.currentThread());
            FakePlayback playback = new FakePlayback(file);
            opened.add(playback);
            return playback;
        };
    }

    private static void await(java.util.function.BooleanSupplier condition, String message) throws Exception {
        long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (System.nanoTime() < end) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(10);
        }
        fail(message);
    }

    @Test(timeout = 20000)
    public void callerNeverOpensOrPlaysTheFile() throws Exception {
        SoundPlayer player = player(fakeOpener());
        player.playSound(SoundPlayer.LADEN);
        await(() -> !opened.isEmpty(), "Ton wurde nicht geoeffnet");
        assertNotSame(Thread.currentThread(), openThread.get());
        assertEquals("SoundPlayer", openThread.get().getName());
        assertTrue(openThread.get().isDaemon());
        opened.get(0).finish();
    }

    @Test(timeout = 20000)
    public void clipIsClosedAtEndAndSlotIsFreed() throws Exception {
        SoundPlayer player = player(fakeOpener());
        player.playSound(SoundPlayer.LADEN);
        await(() -> !opened.isEmpty() && opened.get(0).started.getCount() == 0, "nicht gestartet");
        assertEquals(1, player.activeSounds());
        assertFalse(opened.get(0).closed.get());
        opened.get(0).finish();
        assertTrue(opened.get(0).closed.get());
        assertEquals(0, player.activeSounds());
        opened.get(0).finish();
        assertEquals(0, player.activeSounds());
    }

    @Test(timeout = 20000)
    public void failedOpenFreesTheSlot() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        SoundPlayer player = player(file -> {
            calls.incrementAndGet();
            throw new LineUnavailableException("belegt");
        });
        for (int i = 0; i < 20; i++) {
            player.playSound(SoundPlayer.SUCHEN);
        }
        await(() -> player.activeSounds() == 0, "Slots nach Fehlern nicht frei: " + player.activeSounds());
        assertTrue(calls.get() > 0);
    }

    @Test(timeout = 20000)
    public void invalidFormatAndMissingFileFreeTheSlot() throws Exception {
        SoundPlayer player = player(file -> {
            throw new UnsupportedAudioFileException("kaputt");
        });
        player.playSound(SoundPlayer.LADEN);
        await(() -> player.activeSounds() == 0, "Slot nach ungueltigem Format nicht frei");

        pack.set("gibtsnicht");
        AtomicInteger calls = new AtomicInteger();
        SoundPlayer missing = player(file -> {
            calls.incrementAndGet();
            return new FakePlayback(file);
        });
        missing.playSound(SoundPlayer.LADEN);
        await(() -> missing.activeSounds() == 0, "Slot nach fehlender Datei nicht frei");
        assertEquals(0, calls.get());
    }

    @Test(timeout = 20000)
    public void limitsConcurrentSoundsAndRecoversWhenTheyEnd() throws Exception {
        SoundPlayer player = player(fakeOpener());
        for (int i = 0; i < 12; i++) {
            player.playSound(SoundPlayer.LADEN);
        }
        await(() -> opened.size() == SoundPlayer.MAX_ACTIVE_SOUNDS, "erwartet " + SoundPlayer.MAX_ACTIVE_SOUNDS);
        Thread.sleep(200);
        assertEquals(SoundPlayer.MAX_ACTIVE_SOUNDS, opened.size());
        assertEquals(SoundPlayer.MAX_ACTIVE_SOUNDS, player.activeSounds());

        for (FakePlayback playback : opened) {
            playback.finish();
        }
        assertEquals(0, player.activeSounds());
        player.playSound(SoundPlayer.LADEN);
        await(() -> opened.size() == SoundPlayer.MAX_ACTIVE_SOUNDS + 1, "kein neuer Ton nach Freigabe");
        opened.get(opened.size() - 1).finish();
    }

    @Test(timeout = 20000)
    public void lostEndEventIsCaughtByWatchdog() throws Exception {
        SoundPlayer player = player(fakeOpener());
        player.playSound(SoundPlayer.LADEN);
        await(() -> !opened.isEmpty(), "nicht geoeffnet");
        await(() -> opened.get(0).closed.get(), "Watchdog hat den Clip nicht geschlossen");
        assertEquals(0, player.activeSounds());
    }

    @Test(timeout = 20000)
    public void disabledSoundsStaySilentButPreviewPlays() throws Exception {
        enabled.set(false);
        SoundPlayer player = player(fakeOpener());
        player.playSound(SoundPlayer.LADEN);
        Thread.sleep(200);
        assertTrue(opened.isEmpty());
        player.playPreview();
        await(() -> !opened.isEmpty(), "Hoerprobe wurde nicht gespielt");
        assertEquals("gespeichert.wav", opened.get(0).file.getFileName().toString());
        opened.get(0).finish();
    }

    @Test(timeout = 20000)
    public void soundPackChangeTakesEffectWithoutRestart() throws Exception {
        SoundPlayer player = player(fakeOpener());
        player.playSound(SoundPlayer.KOMPLETT);
        await(() -> opened.size() == 1, "erster Ton");
        opened.get(0).finish();
        pack.set("warp");
        player.playSound(SoundPlayer.KOMPLETT);
        await(() -> opened.size() == 2, "zweiter Ton");
        assertEquals("classic", opened.get(0).file.getParent().getFileName().toString());
        assertEquals("warp", opened.get(1).file.getParent().getFileName().toString());
        opened.get(1).finish();
    }

    @Test(timeout = 20000)
    public void unknownSoundIdsAreIgnored() throws Exception {
        SoundPlayer player = player(fakeOpener());
        player.playSound(-1);
        player.playSound(99);
        Thread.sleep(200);
        assertTrue(opened.isEmpty());
        assertEquals(0, player.activeSounds());
    }
}
