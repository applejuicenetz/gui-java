package de.applejuicenet.client.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SearchResultTabbedPane.java,v 1.5 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class SearchResultTabbedPane extends JTabbedPane
    implements MouseListener
{
    private static final long serialVersionUID = 8221325852686435683L;
	private static Icon icon = IconManager.getInstance().getIcon("abbrechen");

    public SearchResultTabbedPane() {
        addMouseListener(this);
    }

    public void addTab(String title, Component component) {
        super.addTab(title, component);
    }

    public void enableIconAt(int index){
        super.setIconAt(index, new CloseIcon());
    }

    public void mouseClicked(MouseEvent e) {
        int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
        if (tabNumber < 0){
            return;
        }
        CloseIcon icon = ( (CloseIcon) getIconAt(tabNumber));
        if (icon != null){
            Rectangle rect = icon.getBounds();
            if (rect.contains(e.getX(), e.getY())){
                removeTabAt(tabNumber);
            }
        }
    }

    public void mouseEntered(MouseEvent mouseevent) {
    }

    public void mouseExited(MouseEvent mouseevent) {
    }

    public void mousePressed(MouseEvent mouseevent) {
    }

    public void mouseReleased(MouseEvent mouseevent) {
    }

    private class CloseIcon
        implements Icon {

        private int x_pos;
        private int y_pos;
        private int width;
        private int height;

        public CloseIcon() {
            width = icon.getIconWidth();
            height = icon.getIconHeight();
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            x_pos = x;
            y_pos = y;
            if (icon != null){
                icon.paintIcon(c, g, x_pos, y_pos);
            }
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }

        public Rectangle getBounds() {
            return new Rectangle(x_pos, y_pos, width, height);
        }
    }
}
