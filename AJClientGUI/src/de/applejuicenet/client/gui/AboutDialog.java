package de.applejuicenet.client.gui;

import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/AboutDialog.java,v 1.14 2004/02/05 23:11:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AboutDialog.java,v $
 * Revision 1.14  2004/02/05 23:11:26  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.13  2004/01/18 12:21:44  maj0r
 * Credits geaendert.
 *
 * Revision 1.12  2004/01/05 13:55:23  maj0r
 * Credits geaendert.
 *
 * Revision 1.11  2004/01/04 16:36:40  maj0r
 * Credits geaendert.
 *
 * Revision 1.10  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.9  2003/12/18 11:27:46  maj0r
 * Creditsanzeige korrigiert.
 *
 * Revision 1.8  2003/11/16 12:34:23  maj0r
 * Themes einngebaut (Danke an LinuxDoc)
 *
 * Revision 1.7  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
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

public class AboutDialog
    extends JDialog {
    private Logger logger;
    private Thread worker;
    private BackPanel backPanel = new BackPanel();

    public AboutDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        try {
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    if (worker != null) {
                        worker.interrupt();
                    }
                }
            });
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public Dimension getPreferredSize() {
        if (backPanel != null) {
            return backPanel.getPreferredSize();
        }
        else {
            return super.getPreferredSize();
        }
    }

    private void init() {
        setResizable(false);
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "aboutbtn",
                                      "caption"})));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(backPanel, BorderLayout.CENTER);
        pack();
    }

    class BackPanel
        extends JPanel {
        private Image backgroundImage;
        private Image flagge;
        private JLabel version = new JLabel();
        private ArrayList credits = new ArrayList();
        private Logger logger;

        public BackPanel() {
            super();
            logger = Logger.getLogger(getClass());
            try {
                init();
            }
            catch (Exception e) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Unbehandelte Exception", e);
                }
            }
            worker = new Thread() {
                public void run() {
                    if (logger.isEnabledFor(Level.DEBUG)) {
                        logger.debug("About-Workerthread gestartet. " + worker);
                    }
                    Image new_img, toDraw;
                    ImageFilter filter = new ImageFilter();
                    int creditsHoehe = 60;
                    int creditsBreite = 135;
                    int imageX = backgroundImage.getWidth(BackPanel.this) / 2 +
                        20;
                    int imageY = backgroundImage.getHeight(BackPanel.this) / 2 -
                        15;
                    filter = new CropImageFilter(imageX, imageY, creditsBreite,
                                                 creditsHoehe);
                    new_img = createImage(new FilteredImageSource(
                        backgroundImage.getSource(), filter));
                    filter = new CropImageFilter(0, 0, creditsBreite,
                                                 creditsHoehe);
                    int y = creditsHoehe;
                    try {
                        sleep(1000);
                        Graphics g = BackPanel.this.getGraphics();
                        g.setColor(Color.BLACK);
                        CreditsEntry entry;
                        Graphics toDrawGraphics;
                        FontMetrics fm;
                        int strWidth;
                        while (!isInterrupted()) {
                            try {
                                sleep(100);
                            }
                            catch (InterruptedException iE) {
                                interrupt();
                            }
                            toDraw = createImage(creditsBreite, creditsHoehe);
                            toDrawGraphics = toDraw.getGraphics();
                            toDrawGraphics.drawImage(new_img, 0, 0, BackPanel.this);
                            y--;
                            int abstand = -15;
                            for (int i = 0; i < credits.size(); i++) {
                                entry = (CreditsEntry) credits.get(i);
                                if (entry.isUeberschrift()) {
                                    abstand += 20;
                                    toDrawGraphics.setFont(new Font("Arial",
                                        Font.BOLD, 12));
                                    toDrawGraphics.setColor(Color.BLUE);
                                }
                                else {
                                    abstand += 15;
                                    toDrawGraphics.setFont(new Font("Arial",
                                        Font.PLAIN, 12));
                                    toDrawGraphics.setColor(Color.BLACK);
                                }
                                fm = toDrawGraphics.getFontMetrics();
                                strWidth = fm.stringWidth(entry.getAusgabetext());
                                toDrawGraphics.drawString(entry.getAusgabetext(),
                                    (creditsBreite - strWidth) / 2, y + abstand);
                            }
                            g.drawImage(toDraw, imageX + 1, imageY - 11,
                                        BackPanel.this);
                            if (y == -5 - credits.size() * 15) {
                                y = creditsHoehe;
                            }
                        }
                    }
                    catch (Exception e) {
                        if (logger.isEnabledFor(Level.ERROR)) {
                            logger.error("Unbehandelte Exception", e);
                        }
                    }
                    if (logger.isEnabledFor(Level.DEBUG)) {
                        logger.debug("About-Workerthread beendet. " + worker);
                    }
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
            credits.add(new CreditsEntry(true, "Übersetzung"));
            credits.add(new CreditsEntry(false, "BlueTiger"));
            credits.add(new CreditsEntry(false, "nurseppel"));
            credits.add(new CreditsEntry(true, "Kontakt"));
            credits.add(new CreditsEntry(false, "irc.bongster.de"));
            credits.add(new CreditsEntry(false, "#applejuice"));
            credits.add(new CreditsEntry(false, "www.applejuicenet.de"));

            backgroundImage = IconManager.getInstance().getIcon(
                "applejuiceinfobanner").getImage();
            flagge = IconManager.getInstance().getIcon("deutsch").getImage();
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(backgroundImage, 0);
            try {
                mt.waitForAll();
            }
            catch (InterruptedException x) {
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

            if (backgroundImage != null) {
                int imageX = (getWidth() - backgroundImage.getWidth(this)) / 2;
                int imageY = (getHeight() - backgroundImage.getHeight(this)) /
                    2;
                g.drawImage(backgroundImage, imageX, imageY, this);
                if (flagge != null) {
                    g.drawImage(flagge,
                                backgroundImage.getWidth(this) - flagge.getWidth(this),
                                0, this);
                }
            }
        }

        public Dimension getPreferredSize() {
            if (backgroundImage != null) {
                int width = backgroundImage.getWidth(this) + 3;
                int height = backgroundImage.getHeight(this) + 3;
                return new Dimension(width, height);
            }
            else {
                return super.getPreferredSize();
            }
        }
    }

    class CreditsEntry {
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
