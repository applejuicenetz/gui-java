package de.applejuicenet.client.gui.memorymonitor;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/memorymonitor/MemoryMonitorDialog.java,v 1.1 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class MemoryMonitorDialog
    extends JDialog {
    private static final long serialVersionUID = -6067451756047283278L;
	private MemoryMonitor memoryMonitorPanel;

    public MemoryMonitorDialog(Dialog parent) {
        super(parent, false);
        init();
    }

    public MemoryMonitorDialog(Frame parent) {
        super(parent, false);
        init();
    }

    private void init() {
        setTitle("aj Memory Monitor");
        memoryMonitorPanel = new MemoryMonitor();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                memoryMonitorPanel.stopMemoryMonitor();
            }
        });
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(memoryMonitorPanel, BorderLayout.CENTER);
        pack();
        setSize(new Dimension(200, 200));
    }

    public void setVisible(boolean display) {
        super.setVisible(display);
        memoryMonitorPanel.startMemoryMonitor();
    }
}
