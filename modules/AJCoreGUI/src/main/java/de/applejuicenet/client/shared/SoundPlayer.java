/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
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
    private static SoundPlayer instance = null;
    private static Logger logger;
    private final String soundPath;

    private SoundPlayer() {
        String soundPack = OptionsManagerImpl.getInstance().getSoundSetName();
        soundPath = System.getProperty("user.dir") + File.separator + "sounds" + File.separator + soundPack + File.separator;
    }

    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
            logger = Logger.getLogger(instance.getClass());
        }

        return instance;
    }

    public void playSound(int sound) {
        try {
            if (!OptionsManagerImpl.getInstance().isSoundEnabled()) {
                return;
            }

            File soundFile = null;

            switch (sound) {

                case ABGEBROCHEN: {
                    soundFile = new File(soundPath + "abgebrochen.wav");
                    break;
                }

                case SUCHEN: {
                    soundFile = new File(soundPath + "suchen.wav");
                    break;
                }

                case VERBINDEN: {
                    soundFile = new File(soundPath + "verbinden.wav");
                    break;
                }

                case GESPEICHERT: {
                    soundFile = new File(soundPath + "gespeichert.wav");
                    break;
                }

                case KOMPLETT: {
                    soundFile = new File(soundPath + "komplett.wav");
                    break;
                }

                case LADEN: {
                    soundFile = new File(soundPath + "laden.wav");
                    break;
                }

                case POWER: {
                    soundFile = new File(soundPath + "pwdl.wav");
                    break;
                }

                case VERWEIGERT: {
                    soundFile = new File(soundPath + "verweigert.wav");
                    break;
                }

                case KONKRETISIEREN: {
                    soundFile = new File(soundPath + "konkretisieren.wav");
                    break;
                }

                case ZUGANG_GEWAEHRT: {
                    soundFile = new File(soundPath + "zuganggestattet.wav");
                    break;
                }

                case GESTARTET: {
                    soundFile = new File(soundPath + "gestartet.wav");
                    break;
                }

                default:
                    logger.error("SoundPlayer::playSound() ungueltiger Parameter: " + sound);
            }

            final Clip soundToPlay = loadSound(soundFile);

            if (null != soundToPlay) {
                new Thread("SoundPlayer") {
                    public void run() {
                        soundToPlay.start();
                        soundToPlay.addLineListener(e -> {
                            if (e.getType() == LineEvent.Type.STOP) {
                                interrupt();
                            }
                        });
                    }
                }.start();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private Clip loadSound(File file) {
        Clip clip = null;

        try {
            AudioInputStream sound;

            sound = AudioSystem.getAudioInputStream(file);
            AudioFormat format = sound.getFormat();

            if ((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
                AudioFormat tmp = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(),
                        format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2,
                        format.getFrameRate(), true);

                sound = AudioSystem.getAudioInputStream(tmp, sound);
                format = tmp;
            }

            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat(),
                    ((int) sound.getFrameLength() * format.getFrameSize()));

            clip = (Clip) AudioSystem.getLine(info);
            clip.open(sound);
        } catch (LineUnavailableException luE) {
            logger.info("Kein Audioausgabegeraet gefunden. Bitte Soundausgabe deaktivieren.", luE);
        } catch (IOException ioE) {
            logger.error("Die Datei " + file.getAbsolutePath() + " konnte nicht gefunden werden.");
        } catch (UnsupportedAudioFileException uafE) {
            logger.error("Die Datei " + file.getAbsolutePath() + " hat ein ungueltiges Format und kann nicht ausgegeben werden.");
        } catch (Exception e) {
            logger.error(e);
        }

        return clip;
    }
}
