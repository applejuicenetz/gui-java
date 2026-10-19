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
 * Copyright: General Public License
 *
 * Files are loaded and played in their own thread, never in the caller (EDT, poller).
 * Every clip is closed after it ends or on an error. At most
 * {@value #MAX_ACTIVE_SOUNDS} sounds play at the same time; further ones are dropped instead of occupying audio lines.
 */
public class SoundPlayer {
    public static final int CANCELLED = 0;
    public static final int SEARCH = 1;
    public static final int CONNECT = 2;
    public static final int SAVED = 3;
    public static final int COMPLETE_SOUND = 4;
    public static final int LOADING = 5;
    public static final int POWER = 6;
    public static final int DENIED = 7;
    public static final int SPECIFY = 8;
    public static final int ACCESS_GRANTED = 9;
    public static final int STARTED = 10;

    static final int MAX_ACTIVE_SOUNDS = 4;
    private static final int MAX_QUEUED_SOUNDS = 8;
    private static final long WATCHDOG_GRACE_MILLIS = 3000;
    private static final String[] FILE_NAMES = {
            "abgebrochen.wav", "suchen.wav", "verbinden.wav", "gespeichert.wav", "komplett.wav", "laden.wav",
            "pwdl.wav", "verweigert.wav", "konkretisieren.wav", "zuganggestattet.wav", "gestartet.wav"
    };

    /** An opened, playable and closeable sound. */
    interface Playback {
        long durationMillis();

        /** Starts playback; {@code onFinished} is called after the end. */
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

    /** Preview: plays even when the normal sounds are switched off. */
    public void playPreview() {
        playSound(SAVED, true);
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
                logger.error("SoundPlayer::playSound() invalid parameter: " + sound);
                return;
            }

            // Read the sound pack only now so a pack change takes effect without a restart
            Path file = soundBase.get().resolve(soundPack.get()).resolve(FILE_NAMES[sound]);

            if (active.incrementAndGet() > MAX_ACTIVE_SOUNDS) {
                active.decrementAndGet();
                logger.debug("Too many simultaneous sounds, " + file.getFileName() + " is not played");
                return;
            }

            try {
                executor.execute(() -> play(file));
            } catch (RejectedExecutionException e) {
                active.decrementAndGet();
                logger.debug("Sound queue is full, " + file.getFileName() + " is not played");
            }
        } catch (Exception e) {
            logger.error("Sound could not be played", e);
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
                    logger.debug("Audio resource could not be closed", e);
                } finally {
                    active.decrementAndGet();
                }
            }
        };
        boolean started = false;

        try {
            if (!Files.isRegularFile(file)) {
                logger.error("The file " + file.toAbsolutePath() + " could not be found.");
                return;
            }

            playback[0] = opener.open(file);
            playback[0].start(release);
            started = true;
            // Catches a missing end event (device removed, sink hangs), otherwise slots would stay occupied
            timeout[0] = watchdog.schedule(release, playback[0].durationMillis() + WATCHDOG_GRACE_MILLIS, TimeUnit.MILLISECONDS);
            if (finished.get()) {
                timeout[0].cancel(false);
            }
        } catch (LineUnavailableException e) {
            logger.info("No audio output device found or it is busy. Please disable sound output.", e);
        } catch (UnsupportedAudioFileException e) {
            logger.error("The file " + file.toAbsolutePath() + " has an invalid format and cannot be played.");
        } catch (IOException e) {
            logger.error("The file " + file.toAbsolutePath() + " could not be read.", e);
        } catch (Exception | LinkageError e) {
            logger.error("Sound could not be played", e);
        } finally {
            // Without started playback no end event ever arrives: release immediately
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

            // Without a fixed buffer size: some devices reject the calculated size
            Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, format));

            try {
                clip.open(sound);
            } catch (Exception | Error e) {
                clip.close();
                throw e;
            }

            return new ClipPlayback(clip);
        } finally {
            // The clip copied the data when opening, the stream is no longer needed
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
            // Listener before the start, otherwise a very short STOP event is lost
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
