package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Splash.java,v 1.1 2003/08/24 19:27:57 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: Splash.java,v $
 * Revision 1.1  2003/08/24 19:27:57  maj0r
 * Splashscreen eingefuegt.
 *
 *
 */

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;

public class Splash extends Window {
    private Image back;
    private Image image;

    public Splash(Image image) {
        super(new Frame());
        this.image = image;
    }

    public void paint(Graphics g) {
        if (back != null) {
            g.drawImage(back, 0, 0, this);
        }
        g.drawImage(image, 0, 0, this);
    }

    public void show() {
        int w = image.getWidth(this);
        int h = image.getHeight(this);
        if (w != -1 && h != -1) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds((d.width - w) / 2, (d.height - h) / 3, w, h);
            try {
                back = new Robot().createScreenCapture(getBounds());
            }
            catch (AWTException e) {
            }
            super.show();
        }
    }

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        if ((infoflags & WIDTH + HEIGHT) != 0) {
            show();
        }
        return super.imageUpdate(img, infoflags, x, y, w, h);
    }

    public void dispose() {
        super.dispose();
        back = null;
        image = null;
    }
}