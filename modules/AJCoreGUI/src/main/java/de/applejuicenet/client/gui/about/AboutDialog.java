/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.about;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: General Public License
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
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        setResizable(false);
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        setTitle(languageSelector.getFirstAttributeByTagName("mainform.aboutbtn.caption"));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(backPanel, BorderLayout.CENTER);
        pack();
    }

    class BackPanel extends JPanel {
        private Image backgroundImage;
        private Image flag;
        private JLabel version = new JLabel();
        private List<CreditsEntry> credits = new ArrayList<CreditsEntry>();
        private Logger logger;
        private JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        public BackPanel() {
            super();
            logger = LoggerFactory.getLogger(getClass());
            try {
                init();
            } catch (Exception e) {
                logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            }

        }

        private void init() {
            credits.add(new CreditsEntry(true, GuiText.text("javagui.about.programming")));
            credits.add(new CreditsEntry(false, "Maj0r"));
            credits.add(new CreditsEntry(false, "loevenwong"));
            credits.add(new CreditsEntry(false, "red171"));
            credits.add(new CreditsEntry(true, GuiText.text("javagui.about.thanks")));
            credits.add(new CreditsEntry(false, "muhviehstarr"));

            backgroundImage = IconManager.getInstance().getIcon("applejuiceinfobanner").getImage();
            flag = IconManager.getInstance().getIcon("deutsch").getImage();
            MediaTracker mt = new MediaTracker(this);

            mt.addImage(backgroundImage, 0);
            try {
                mt.waitForAll();
            } catch (InterruptedException x) {

                //no image available, so skip it ;-)
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
                if (flag != null) {
                    g.drawImage(flag, backgroundImage.getWidth(this) - flag.getWidth(this), 0, this);
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
            int top = (flag == null ? 0 : flag.getHeight(this)) + 8;
            int right = imageX + imageWidth - 8;
            int bottom = getHeight() - footer.getPreferredSize().height - 8;
            return new Rectangle(left, top, Math.max(0, right - left), Math.max(0, bottom - top));
        }

        private int getCreditsHeight() {
            int height = 0;
            for (CreditsEntry entry : credits) {
                height += entry.isHeading() ? 20 : 15;
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
                    drawing.setFont(entry.isHeading() ? fontBold : fontPlain);
                    drawing.setColor(entry.isHeading() ? Color.BLUE : Color.BLACK);
                    int width = drawing.getFontMetrics().stringWidth(entry.getOutputText());
                    drawing.drawString(entry.getOutputText(), (bounds.width - width) / 2, textY);
                    textY += entry.isHeading() ? 20 : 15;
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
        private boolean heading;
        private String outputText;

        public CreditsEntry(boolean heading, String outputText) {
            this.heading = heading;
            this.outputText = outputText;
        }

        public boolean isHeading() {
            return heading;
        }

        public void setHeading(boolean heading) {
            this.heading = heading;
        }

        public String getOutputText() {
            return outputText;
        }

        public void setOutputText(String outputText) {
            this.outputText = outputText;
        }
    }


}
