package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/AboutDialog.java,v 1.1 2003/08/29 14:24:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AboutDialog.java,v $
 * Revision 1.1  2003/08/29 14:24:15  maj0r
 * About-Dialog mit entsprechendem Menuepunkt eingefuehrt.
 *
 *
 */

public class AboutDialog extends JDialog {
    public AboutDialog(Frame parent, boolean modal) {
        super(parent, modal);
        init();
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
        private JLabel version = new JLabel();

        public BackPanel() {
            super();
            init();
        }

        private void init(){
            backgroundImage = IconManager.getInstance().getIcon("applejuiceinfobanner").getImage();
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
}
