package com.jeans.trayicon;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class SwingTrayPopup
    extends JPopupMenu {

    // Swing popup menu for a TrayIcon
    private WindowsTrayIcon m_Icon;
    private MouseListener m_Listener;
    private Frame parent;

    public SwingTrayPopup(Frame parent) {
    	this.parent = parent;
    }

    // Attach the menu to a TrayIcon
    public void setTrayIcon(WindowsTrayIcon icon) {
        if (icon != null) {
            m_Icon = icon;
            WindowsTrayIcon.initJAWT();
            WindowsTrayIcon.initHook();
            m_Listener = new ActivateListener();
            m_Icon.addMouseListener(m_Listener);
        }
        else {
            if (m_Icon != null) {
                m_Icon.removeMouseListener(m_Listener);
                m_Icon = null;
            }
        }
    }

    // Show the popup menu (internal use only)
    public void showMenu(int xp, int yp) {
        // This should show the menu at a better location :-)
        //  * Thanks to Danny <danny@isfantastisch.nl> for the
        //    setAlwaysOnTop and updateUI() hint

        WindowsTrayIcon.setMouseClickHook(new ClickListener());
//        WindowsTrayIcon.setAlwaysOnTop(parent, true);
        Dimension d = getPreferredSize();
        show(parent, xp - d.width, yp - d.height);
        updateUI();
    }

    // Test if mouse is in menu or submenu
    private boolean componentContains(JComponent comp, int xp, int yp) {
        if (!comp.isVisible()) {
            return false;
        }
        Point pt = comp.getLocationOnScreen();
        Dimension s = comp.getSize();
        boolean contains = xp > pt.x && xp < pt.x + s.width && yp > pt.y &&
            yp < pt.y + s.height;
        if (contains) {
            return true;
        }
        for (int i = 0; i < comp.getComponentCount(); i++) {
            JComponent child = (JComponent) comp.getComponent(i);
            if (child instanceof JMenu) {
                JMenu submenu = (JMenu) child;
                if (componentContains(submenu.getPopupMenu(), xp, yp)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Test if mouse is in menu or submenu
    private boolean menuContains(int xp, int yp) {
        return componentContains(this, xp, yp);
    }

    // Callback listener handles icon events (Mouse hook)
    private class ClickListener
        extends MouseAdapter {

        public void mousePressed(MouseEvent evt) {
            if (!menuContains(evt.getX(), evt.getY())) {
                setVisible(false);
                WindowsTrayIcon.setMouseClickHook(null);
            }
        }
    }

    // Callback listener handles icon events
    private class ActivateListener
        extends MouseAdapter {

        public void mousePressed(MouseEvent evt) {
            if (evt.isPopupTrigger() &&
                (evt.getModifiers() & MouseEvent.BUTTON2_MASK) != 0 &&
                evt.getClickCount() == 1) {
                showMenu(evt.getX(), evt.getY());
            }
        }
    }
}
