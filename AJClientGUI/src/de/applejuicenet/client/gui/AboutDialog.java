package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.CropImageFilter;
import java.awt.image.ImageFilter;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/AboutDialog.java,v 1.6 2003/09/06 14:47:40 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AboutDialog.java,v $
 * Revision 1.6  2003/09/06 14:47:40  maj0r
 * Credits angepasst.
 *
 * Revision 1.5  2003/09/05 11:13:27  maj0r
 * Credits geaendert.
 *
 * Revision 1.4  2003/09/05 08:24:40  maj0r
 * Threadverwendung verbessert.
 *
 * Revision 1.3  2003/09/04 22:12:45  maj0r
 * Logger verfeinert.
 * Threadbeendigung korrigiert.
 *
 * Revision 1.2  2003/09/04 13:57:04  maj0r
 * Credits eingebaut.
 *
 * Revision 1.1  2003/08/29 14:24:15  maj0r
 * About-Dialog mit entsprechendem Menuepunkt eingefuehrt.
 *
 *
 */

public class AboutDialog extends JDialog {
    private Logger logger;
    private Thread worker;

    public AboutDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        try{
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    if (worker!=null){
                        worker.interrupt();
                    }
                }
            });
            init();
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() {
        setResizable(false);
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"mainform", "aboutbtn", "caption"})));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new BackPanel(), BorderLayout.CENTER);
        pack();
    }


    class BackPanel extends JPanel {
        private Image backgroundImage;
        private Image flagge;
        private JLabel version = new JLabel();
        private ArrayList credits = new ArrayList();
        private Logger logger;

        public BackPanel() {
            super();
            logger = Logger.getLogger(getClass());
            try{
                init();
            }
            catch (Exception e){
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error("Unbehandelte Exception", e);
            }
            worker = new Thread() {
                public void run() {
                    if (logger.isEnabledFor(Level.DEBUG))
                        logger.debug("About-Workerthread gestartet. " + worker);
                    Image new_img, toDraw;
                    ImageFilter filter = new ImageFilter();
                    int creditsHoehe = 60;
                    int creditsBreite = 135;
                    int imageX = backgroundImage.getWidth(BackPanel.this) / 2 + 20;
                    int imageY = backgroundImage.getHeight(BackPanel.this) / 2 - 15;
                    filter = new CropImageFilter(imageX, imageY, creditsBreite, creditsHoehe);
                    new_img = createImage(new FilteredImageSource(backgroundImage.getSource(), filter));
                    filter = new CropImageFilter(0, 0, creditsBreite, creditsHoehe);
                    int y = creditsHoehe;
                    try
                    {
                        sleep(1000);
                        Graphics g = BackPanel.this.getGraphics();
                        g.setColor(Color.BLACK);
                        CreditsEntry entry;
                        Graphics toDrawGraphics;
                        FontMetrics fm;
                        int strWidth;
                        while (!isInterrupted())
                        {
                            try{
                                sleep(100);
                            }
                            catch (InterruptedException iE){
                                interrupt();
                            }
                            toDraw = createImage(creditsBreite, creditsHoehe);
                            toDrawGraphics = toDraw.getGraphics();
                            toDrawGraphics.drawImage(new_img, 0, 0, BackPanel.this);
                            y--;
                            int abstand = -15;
                            for (int i=0; i<credits.size(); i++){
                                entry = (CreditsEntry)credits.get(i);
                                if (entry.isUeberschrift()){
                                    abstand += 20;
                                    toDrawGraphics.setFont(new Font("Arial", Font.BOLD, 12));
                                    toDrawGraphics.setColor(Color.BLUE);
                                }
                                else{
                                    abstand += 15;
                                    toDrawGraphics.setFont(new Font("Arial", Font.PLAIN, 12));
                                    toDrawGraphics.setColor(Color.BLACK);
                                }
                                fm = toDrawGraphics.getFontMetrics();
                                strWidth = fm.stringWidth ( entry.getAusgabetext() );
                                toDrawGraphics.drawString(entry.getAusgabetext(), (creditsBreite - strWidth) / 2, y + abstand);
                            }
                            g.drawImage(toDraw, imageX, imageY, BackPanel.this);
                            if (y==-5 - credits.size() * 15){
                                y = creditsHoehe;
                            }
                        }
                    }
                    catch (Exception e){
                        if (logger.isEnabledFor(Level.ERROR))
                            logger.error("Unbehandelte Exception", e);
                    }
                    if (logger.isEnabledFor(Level.DEBUG))
                        logger.debug("About-Workerthread beendet. " + worker);
                }
            };
            worker.start();
        }

        private void init() {
            credits.add(new CreditsEntry(true, "Programmierung"));
            credits.add(new CreditsEntry(false, "Maj0r"));
            credits.add(new CreditsEntry(false, "(aj@tkl-soft.de)"));
            credits.add(new CreditsEntry(true, "Besonderen Dank an"));
            credits.add(new CreditsEntry(false, "muhviehstarr"));
            credits.add(new CreditsEntry(true, "Banner & Bilder"));
            credits.add(new CreditsEntry(false, "saschxd"));
            credits.add(new CreditsEntry(true, "Kontakt"));
            credits.add(new CreditsEntry(false, "irc.bongster.de"));
            credits.add(new CreditsEntry(false, "#applejuice"));
            credits.add(new CreditsEntry(false, "www.applejuicenet.org"));

            backgroundImage = IconManager.getInstance().getIcon("applejuiceinfobanner").getImage();
            flagge = IconManager.getInstance().getIcon("deutsch").getImage();
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(backgroundImage, 0);
            try
            {
                mt.waitForAll();
            }
            catch (InterruptedException x)
            {
                //kein Bild da, dann kack drauf ;-)
            }
            version.setText("Version " + ApplejuiceFassade.GUI_VERSION);
            Font font = version.getFont();
            font = new Font(font.getName(), Font.PLAIN, font.getSize());
            version.setFont(font);
            setLayout(new BorderLayout());
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panel1.add(version);
            panel1.setOpaque(false);
            add(panel1, BorderLayout.SOUTH);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Color saved = g.getColor();
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(saved);

            if (backgroundImage != null)
            {
                int imageX = (getWidth() - backgroundImage.getWidth(this)) / 2;
                int imageY = (getHeight() - backgroundImage.getHeight(this)) / 2;
                g.drawImage(backgroundImage, imageX, imageY, this);
                if (flagge!=null){
                    g.drawImage(flagge, backgroundImage.getWidth(this) - flagge.getWidth(this), 0, this);
                }
            }
        }

        public Dimension getPreferredSize() {
            Dimension oldSize = super.getPreferredSize();
            Dimension newSize = new Dimension();
            Dimension returnSize = new Dimension();

            if (backgroundImage != null)
            {
                newSize.width = backgroundImage.getWidth(this) + 1;
                newSize.height = backgroundImage.getHeight(this) + 1;
            }
            if (oldSize.height > newSize.height)
            {
                returnSize.height = oldSize.height;
            }
            else
            {
                returnSize.height = newSize.height;
            }
            if (oldSize.width > newSize.width)
            {
                returnSize.width = oldSize.width;
            }
            else
            {
                returnSize.width = newSize.width;
            }
            return (returnSize);
        }
    }

    class CreditsEntry{
        private boolean ueberschrift;
        private String ausgabetext;

        public CreditsEntry(boolean ueberschrift, String ausgabetext) {
            this.ueberschrift = ueberschrift;
            this.ausgabetext = ausgabetext;
        }

        public boolean isUeberschrift() {
            return ueberschrift;
        }

        public void setUeberschrift(boolean ueberschrift) {
            this.ueberschrift = ueberschrift;
        }

        public String getAusgabetext() {
            return ausgabetext;
        }

        public void setAusgabetext(String ausgabetext) {
            this.ausgabetext = ausgabetext;
        }
    }
}
