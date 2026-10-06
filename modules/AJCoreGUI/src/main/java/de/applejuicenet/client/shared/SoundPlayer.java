/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * <p>Dateien werden in einem eigenen Thread geladen und abgespielt, nie im Aufrufer (EDT, Poller).
 * Jeder Clip wird nach dem Ende oder bei einem Fehler geschlossen. Es laufen hoechstens
 * {@value #MAX_ACTIVE_SOUNDS} Toene gleichzeitig; weitere werden verworfen, statt Audio-Lines zu belegen.</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class SoundPlayer {
    public static final int ABGEBROCHEN = 0;
    public static final int SUCHEN = 1;
    public static final int VERBINDEN = 2;
    public static final int GESPEICHERT = 3;
    public static final int KOMPLETT = 4;
    public static final int LADEN = 5;
    public static final int POWER = 6;
    public static final int VERWEIGERT = 7;
    public static final int KONKRETISIEREN = 8;
    public static final int ZUGANG_GEWAEHRT = 9;
    public static final int GESTARTET = 10;

    static final int MAX_ACTIVE_SOUNDS = 4;
    private static final int MAX_QUEUED_SOUNDS = 8;
    private static final long WATCHDOG_GRACE_MILLIS = 3000;
    private static final String[] FILE_NAMES = {
            "abgebrochen.wav", "suchen.wav", "verbinden.wav", "gespeichert.wav", "komplett.wav", "laden.wav",
            "pwdl.wav", "verweigert.wav", "konkretisieren.wav", "zuganggestattet.wav", "gestartet.wav"
    };

    /** Ein geoeffneter, abspielbarer und schliessbarer Ton. */
    interface Playback {
        long durationMillis();

        /** Startet die Wiedergabe; {@code onFinished} wird nach dem Ende aufgerufen. */
        void start(Runnable onFinished);

        void close();
    }

    @FunctionalInterface
    interface Opener {
        Playback open(Path file) throws Exception;
    }

    private static final Logger logger = LoggerFactory.getLogger(SoundPlayer.class);
    private static SoundPlayer instance = null;

    private final Opener opener;
    private final BooleanSupplier soundEnabled;
    private final Supplier<String> soundPack;
    private final Supplier<Path> soundBase;
    private final AtomicInteger active = new AtomicInteger();
    private final ThreadPoolExecutor executor;
    private final ScheduledThreadPoolExecutor watchdog;

    private SoundPlayer() {
        this(SoundPlayer::openClip,
                () -> OptionsManagerImpl.getInstance().isSoundEnabled(),
                () -> OptionsManagerImpl.getInstance().getSoundSetName(),
                () -> Path.of(System.getProperty("user.dir"), "sounds"));
    }

    SoundPlayer(Opener opener, BooleanSupplier soundEnabled, Supplier<String> soundPack, Supplier<Path> soundBase) {
        this.opener = opener;
        this.soundEnabled = soundEnabled;
        this.soundPack = soundPack;
        this.soundBase = soundBase;
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(MAX_QUEUED_SOUNDS),
                runnable -> daemon(runnable, "SoundPlayer"));
        this.watchdog = new ScheduledThreadPoolExecutor(1, runnable -> daemon(runnable, "SoundPlayerWatchdog"));
        this.watchdog.setRemoveOnCancelPolicy(true);
    }

    private static Thread daemon(Runnable runnable, String name) {
        Thread thread = new Thread(runnable, name);

        thread.setDaemon(true);
        return thread;
    }

    public static synchronized SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }

        return instance;
    }

    /** Hoerprobe: spielt auch dann, wenn die normalen Toene ausgeschaltet sind. */
    public void playPreview() {
        playSound(GESPEICHERT, true);
    }

    public void playSound(int sound) {
        playSound(sound, false);
    }

    private void playSound(int sound, boolean preview) {
        try {
            if (!preview && !soundEnabled.getAsBoolean()) {
                return;
            }

            if (sound < 0 || sound >= FILE_NAMES.length) {
                logger.error("SoundPlayer::playSound() ungueltiger Parameter: " + sound);
                return;
            }

            // Soundpack erst jetzt lesen, damit ein Packwechsel ohne Neustart wirkt
            Path file = soundBase.get().resolve(soundPack.get()).resolve(FILE_NAMES[sound]);

            if (active.incrementAndGet() > MAX_ACTIVE_SOUNDS) {
                active.decrementAndGet();
                logger.debug("Zu viele gleichzeitige Toene, " + file.getFileName() + " wird nicht gespielt");
                return;
            }

            try {
                executor.execute(() -> play(file));
            } catch (RejectedExecutionException e) {
                active.decrementAndGet();
                logger.debug("Soundwarteschlange voll, " + file.getFileName() + " wird nicht gespielt");
            }
        } catch (Exception e) {
            logger.error("Sound konnte nicht abgespielt werden", e);
        }
    }

    int activeSounds() {
        return active.get();
    }

    private void play(Path file) {
        AtomicBoolean finished = new AtomicBoolean();
        Playback[] playback = new Playback[1];
        ScheduledFuture<?>[] timeout = new ScheduledFuture<?>[1];
        Runnable release = () -> {
            if (finished.compareAndSet(false, true)) {
                ScheduledFuture<?> pending = timeout[0];

                if (pending != null) {
                    pending.cancel(false);
                }

                try {
                    if (playback[0] != null) {
                        playback[0].close();
                    }
                } catch (RuntimeException e) {
                    logger.debug("Audioressource konnte nicht geschlossen werden", e);
                } finally {
                    active.decrementAndGet();
                }
            }
        };
        boolean started = false;

        try {
            if (!Files.isRegularFile(file)) {
                logger.error("Die Datei " + file.toAbsolutePath() + " konnte nicht gefunden werden.");
                return;
            }

            playback[0] = opener.open(file);
            playback[0].start(release);
            started = true;
            // Faengt ein ausbleibendes Ende-Ereignis ab (Geraet entfernt, Sink haengt), sonst blieben Slots belegt
            timeout[0] = watchdog.schedule(release, playback[0].durationMillis() + WATCHDOG_GRACE_MILLIS, TimeUnit.MILLISECONDS);
            if (finished.get()) {
                timeout[0].cancel(false);
            }
        } catch (LineUnavailableException e) {
            logger.info("Kein Audioausgabegeraet gefunden oder es ist belegt. Bitte Soundausgabe deaktivieren.", e);
        } catch (UnsupportedAudioFileException e) {
            logger.error("Die Datei " + file.toAbsolutePath() + " hat ein ungueltiges Format und kann nicht ausgegeben werden.");
        } catch (IOException e) {
            logger.error("Die Datei " + file.toAbsolutePath() + " konnte nicht gelesen werden.", e);
        } catch (Exception | LinkageError e) {
            logger.error("Sound konnte nicht abgespielt werden", e);
        } finally {
            // Ohne gestartete Wiedergabe kommt nie ein Ende-Ereignis: sofort freigeben
            if (!started) {
                release.run();
            }
        }
    }

    private static Playback openClip(Path file) throws Exception {
        AudioInputStream sound = AudioSystem.getAudioInputStream(file.toFile());

        try {
            AudioFormat format = sound.getFormat();

            if ((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
                AudioFormat tmp = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(),
                        format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2,
                        format.getFrameRate(), true);

                sound = AudioSystem.getAudioInputStream(tmp, sound);
                format = tmp;
            }

            // Ohne feste Puffergroesse: manche Geraete lehnen die berechnete Groesse ab
            Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, format));

            try {
                clip.open(sound);
            } catch (Exception | Error e) {
                clip.close();
                throw e;
            }

            return new ClipPlayback(clip);
        } finally {
            // Der Clip hat die Daten beim Oeffnen kopiert, der Stream wird nicht mehr gebraucht
            sound.close();
        }
    }

    private static final class ClipPlayback implements Playback {
        private final Clip clip;

        ClipPlayback(Clip clip) {
            this.clip = clip;
        }

        @Override
        public long durationMillis() {
            return clip.getMicrosecondLength() / 1000;
        }

        @Override
        public void start(Runnable onFinished) {
            // Listener vor dem Start, sonst geht ein sehr kurzes STOP-Ereignis verloren
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    onFinished.run();
                }
            });
            clip.start();
        }

        @Override
        public void close() {
            clip.close();
        }
    }
}
