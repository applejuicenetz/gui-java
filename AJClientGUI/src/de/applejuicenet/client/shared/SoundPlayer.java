package de.applejuicenet.client.shared;

import org.apache.log4j.Logger;

import java.io.File;

import de.applejuicenet.client.gui.controller.PropertiesManager;

import javax.sound.sampled.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/SoundPlayer.java,v 1.2 2003/10/31 16:24:58 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SoundPlayer.java,v $
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

    private static SoundPlayer instance = null;
    private static Logger logger;
    private String soundPath;

    private SoundPlayer() {
        soundPath = System.getProperty("user.dir") + File.separator +
                    "sounds" + File.separator;
    }

    public static SoundPlayer getInstance() {
        if (instance == null)
        {
            instance = new SoundPlayer();
            logger = Logger.getLogger(instance.getClass());
        }
        return instance;
    }

    public void playSound(int sound){
        if (!PropertiesManager.getOptionsManager().isSoundEnabled()){
            return;
        }
        File soundFile = null;
        switch (sound){
            case ABGEBROCHEN:{
                soundFile = new File(soundPath + "abgebrochen.wav");
                break;
            }
            case SUCHEN:{
                soundFile = new File(soundPath + "suchen.wav");
                break;
            }
            case VERBINDEN:{
                soundFile = new File(soundPath + "verbinden.wav");
                break;
            }
            case GESPEICHERT:{
                soundFile = new File(soundPath + "gespeichert.wav");
                break;
            }
            case KOMPLETT:{
                soundFile = new File(soundPath + "komplett.wav");
                break;
            }
            case LADEN:{
                soundFile = new File(soundPath + "laden.wav");
                break;
            }
            case POWER:{
                soundFile = new File(soundPath + "pwdl.wav");
                break;
            }
            default:
            {
                logger.error("SoundPlayer::playSound() ungueltiger Parameter: " + sound);
            }
        }
        Clip soundToPlay = loadSound(soundFile);
        if (soundToPlay!=null){
            soundToPlay.start();
        }
    }

    private Clip loadSound(File file){
        Clip clip = null;
        try
        {
            AudioInputStream sound = null;
            sound = AudioSystem.getAudioInputStream(file);
            AudioFormat format = sound.getFormat();

            if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                (format.getEncoding() == AudioFormat.Encoding.ALAW))
            {
                AudioFormat tmp = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                  format.getSampleRate(),format.getSampleSizeInBits() * 2,
                                                  format.getChannels(),format.getFrameSize() * 2,
                                                  format.getFrameRate(),true);
                sound = AudioSystem.getAudioInputStream(tmp, sound);
                format = tmp;
            }
            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat(), ((int) sound.getFrameLength()*format.getFrameSize()));
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(sound);
        }
        catch(Exception e)
        {
            logger.error(e);
        }
        return clip;
    }
}
