package de.applejuicenet.client.shared;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.PropertiesManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/SoundPlayer.java,v 1.7 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SoundPlayer.java,v $
 * Revision 1.7  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.6  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.5  2003/12/18 12:50:53  maj0r
 * Bug bei der Wiedergabe von Sounds korrigiert (Danke an mrbond).
 * Sounddevice wird nun nach Ausgabe eines Sounds wieder freigegeben.
 *
 * Revision 1.4  2003/11/05 11:01:35  maj0r
 * Fehler bei der Soundausgabe bei fehlerhaften Sounddateien (z.B. falsches Format) oder fehlendem Sounddevice behoben.
 *
 * Revision 1.3  2003/10/31 19:04:58  maj0r
 * Sounds eingebaut.
 *
 * Revision 1.2  2003/10/31 16:24:58  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt.
 *
 * Revision 1.1  2003/10/31 11:31:45  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt. Kommen noch mehr.
 *
 *
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
    private String soundPath;

    private SoundPlayer() {
        soundPath = System.getProperty("user.dir") + File.separator +
            "sounds" + File.separator;
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
            if (!PropertiesManager.getOptionsManager().isSoundEnabled()) {
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
                default: {
                    logger.error(
                        "SoundPlayer::playSound() ungueltiger Parameter: " +
                        sound);
                }
            }
            final Clip soundToPlay = loadSound(soundFile);
            new Thread() {
                public void run() {
                    if (soundToPlay != null) {
                        soundToPlay.start();
                        while (soundToPlay.isRunning()) {
                            try {
                                sleep(50);
                            }
                            catch (InterruptedException ex) {
                                soundToPlay.stop();
                                interrupt();
                            }
                        }
                        soundToPlay.stop();
                    }
                }
            }

            .start();
        }
        catch (Exception e) {
            logger.error(e);
        }
    }

    private Clip loadSound(File file) {
        Clip clip = null;
        try {
            AudioInputStream sound = null;
            sound = AudioSystem.getAudioInputStream(file);
            AudioFormat format = sound.getFormat();

            if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
                AudioFormat tmp = new AudioFormat(AudioFormat.Encoding.
                                                  PCM_SIGNED,
                                                  format.getSampleRate(),
                                                  format.getSampleSizeInBits() *
                                                  2,
                                                  format.getChannels(),
                                                  format.getFrameSize() * 2,
                                                  format.getFrameRate(), true);
                sound = AudioSystem.getAudioInputStream(tmp, sound);
                format = tmp;
            }
            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat(),
                ( (int) sound.getFrameLength() * format.getFrameSize()));
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(sound);
        }
        catch (LineUnavailableException luE) {
            logger.info(
                "Kein Audioausgabegeraet gefunden. Bitte Soundausgabe deaktivieren.",
                luE);
        }
        catch (IOException ioE) {
            logger.error("Die Datei " + file.getAbsolutePath() +
                         " konnte nicht gefunden werden.");
        }
        catch (UnsupportedAudioFileException uafE) {
            logger.error("Die Datei " + file.getAbsolutePath() +
                " hat ein ungueltiges Format und kann nicht ausgegeben werden.");
        }
        catch (Exception e) {
            logger.error(e);
        }
        return clip;
    }
}
