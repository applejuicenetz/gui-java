/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.about;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import de.tklsoft.gui.controls.TKLLabel;
import de.tklsoft.gui.controls.TKLPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/about/AboutDialog.java,v 1.14 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class AboutDialog extends JDialog {
    private Logger logger;
    private BackPanel backPanel = new BackPanel();

    public AboutDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        setResizable(false);
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        setTitle(languageSelector.getFirstAttrbuteByTagName("mainform.aboutbtn.caption"));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(backPanel, BorderLayout.CENTER);
        pack();
    }

    class BackPanel extends TKLPanel {
        private Image backgroundImage;
        private Image flagge;
        private TKLLabel version = new TKLLabel();
        private List<CreditsEntry> credits = new ArrayList<CreditsEntry>();
        private Logger logger;
        private TKLPanel footer = new TKLPanel(new FlowLayout(FlowLayout.RIGHT));

        public BackPanel() {
            super();
            logger = LoggerFactory.getLogger(getClass());
            try {
                init();
            } catch (Exception e) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }

        }

        private void init() {
            credits.add(new CreditsEntry(true, "Programmierung"));
            credits.add(new CreditsEntry(false, "Maj0r"));
            credits.add(new CreditsEntry(false, "loevenwong"));
            credits.add(new CreditsEntry(false, "red171"));
            credits.add(new CreditsEntry(true, "Besonderen Dank an"));
            credits.add(new CreditsEntry(false, "muhviehstarr"));

            backgroundImage = IconManager.getInstance().getIcon("applejuiceinfobanner").getImage();
            flagge = IconManager.getInstance().getIcon("deutsch").getImage();
            MediaTracker mt = new MediaTracker(this);

            mt.addImage(backgroundImage, 0);
            try {
                mt.waitForAll();
            } catch (InterruptedException x) {

                //kein Bild da, dann kack drauf ;-)
            }

            version.setText("Version " + AppleJuiceDialog.getVersion());
            Font font = version.getFont();

            font = new Font(font.getName(), Font.PLAIN, font.getSize());
            version.setFont(font);
            setLayout(new BorderLayout());
            footer.add(version);
            footer.setOpaque(false);
            add(footer, BorderLayout.SOUTH);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Color saved = g.getColor();

            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(saved);

            if (backgroundImage != null) {
                int imageX = (getWidth() - backgroundImage.getWidth(this)) / 2;
                int imageY = (getHeight() - backgroundImage.getHeight(this)) / 2;

                g.drawImage(backgroundImage, imageX, imageY, this);
                if (flagge != null) {
                    g.drawImage(flagge, backgroundImage.getWidth(this) - flagge.getWidth(this), 0, this);
                }
                paintCredits(g);
            }
        }

        Rectangle getCreditsBounds() {
            if (backgroundImage == null) {
                return new Rectangle();
            }
            int imageWidth = backgroundImage.getWidth(this);
            int imageX = (getWidth() - imageWidth) / 2;
            int left = imageX + imageWidth / 2 + 20;
            int top = (flagge == null ? 0 : flagge.getHeight(this)) + 8;
            int right = imageX + imageWidth - 8;
            int bottom = getHeight() - footer.getPreferredSize().height - 8;
            return new Rectangle(left, top, Math.max(0, right - left), Math.max(0, bottom - top));
        }

        private int getCreditsHeight() {
            int height = 0;
            for (CreditsEntry entry : credits) {
                height += entry.isUeberschrift() ? 20 : 15;
            }
            return height;
        }

        private void paintCredits(Graphics graphics) {
            Rectangle bounds = getCreditsBounds();
            Graphics drawing = graphics.create(bounds.x, bounds.y, bounds.width, bounds.height);
            try {
                Font fontBold = new Font("Arial", Font.BOLD, 12);
                Font fontPlain = new Font("Arial", Font.PLAIN, 12);
                int textY = Math.max(fontPlain.getSize(), (bounds.height - getCreditsHeight()) / 2
                        + drawing.getFontMetrics(fontBold).getAscent());
                for (CreditsEntry entry : credits) {
                    drawing.setFont(entry.isUeberschrift() ? fontBold : fontPlain);
                    drawing.setColor(entry.isUeberschrift() ? Color.BLUE : Color.BLACK);
                    int width = drawing.getFontMetrics().stringWidth(entry.getAusgabetext());
                    drawing.drawString(entry.getAusgabetext(), (bounds.width - width) / 2, textY);
                    textY += entry.isUeberschrift() ? 20 : 15;
                }
            } finally {
                drawing.dispose();
            }
        }

        public Dimension getPreferredSize() {
            if (backgroundImage != null) {
                int width = backgroundImage.getWidth(this) + 3;
                int height = backgroundImage.getHeight(this) + 3;

                return new Dimension(width, height);
            } else {
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
