package de.applejuicenet.client.gui;

import java.util.ArrayList;
import java.util.List;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/AboutDialog.java,v 1.23 2004/10/14 08:57:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class AboutDialog
    extends JDialog {
    private static final long serialVersionUID = 6074385586703205113L;
	private Logger logger;
    private WorkerThread worker = null;
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
            getFirstAttrbuteByTagName(".root.mainform.aboutbtn.caption")));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(backPanel, BorderLayout.CENTER);
        pack();
    }

    class BackPanel
        extends JPanel{
        private static final long serialVersionUID = 2117067663733359921L;
		private Image backgroundImage;
        private Image flagge;
        private JLabel version = new JLabel();
        private List credits = new ArrayList();
        private Logger logger;

        public BackPanel() {
            super();
            logger = Logger.getLogger(getClass());
            try {
                init();
            }
            catch (Exception e) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                }
            }
            worker = new WorkerThread(backgroundImage, this, credits);
            worker.start();
            addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent me){
                    if (worker!=null){
                        worker.toggleRunStatus();
                    }
                }
            });
        }

        private void init() {
            credits.add(new CreditsEntry(true, "Programmierung"));
            credits.add(new CreditsEntry(false, "Maj0r"));
            credits.add(new CreditsEntry(false, "(aj@tkl-soft.de)"));
            credits.add(new CreditsEntry(false, "loevenwong"));
            credits.add(new CreditsEntry(false, "(timo@loevenwong.de)"));
            credits.add(new CreditsEntry(true, "Besonderen Dank an"));
            credits.add(new CreditsEntry(false, "muhviehstarr"));
            credits.add(new CreditsEntry(true, "Banner & Bilder"));
            credits.add(new CreditsEntry(false, "saschxd"));
            credits.add(new CreditsEntry(true, "Übersetzung"));
            credits.add(new CreditsEntry(false, "BlueTiger"));
            credits.add(new CreditsEntry(false, "nurseppel"));
            credits.add(new CreditsEntry(true, "Kontakt"));
            credits.add(new CreditsEntry(false, "irc.p2pchat.net"));
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

    private class WorkerThread extends Thread{
        private Image backgroundImage;
        private BackPanel backPanel;
        private List credits;
        private boolean run = true;
        private Logger logger;

        public WorkerThread(Image backgroundImage, BackPanel backPanel, List credits){
            logger = Logger.getLogger(getClass());
            this.backgroundImage = backgroundImage;
            this.backPanel = backPanel;
            this.credits = credits;
        }

        public void toggleRunStatus(){
            run = !run;
        }

        public void run() {
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug("About-Workerthread gestartet. " + worker);
            }
            Image new_img, toDraw;
            ImageFilter filter = new ImageFilter();
            int creditsHoehe = 60;
            int creditsBreite = 135;
            int imageX = backgroundImage.getWidth(backPanel) / 2 +
                20;
            int imageY = backgroundImage.getHeight(backPanel) / 2 -
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
                Graphics g = backPanel.getGraphics();
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
                    if (run){
                        toDraw = createImage(creditsBreite, creditsHoehe);
                        toDrawGraphics = toDraw.getGraphics();
                        toDrawGraphics.drawImage(new_img, 0, 0, backPanel);
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
                                    backPanel);
                        if (y == -5 - credits.size() * 15) {
                            y = creditsHoehe;
                        }
                    }
                }
            }
            catch (Exception e) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                }
            }
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug("About-Workerthread beendet. " + worker);
            }
        }
    }
}
