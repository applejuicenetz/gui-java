package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dnd.DndTargetAdapter;
import de.applejuicenet.client.gui.tables.dateiliste.DateiListeTable;
import de.applejuicenet.client.gui.tables.dateiliste.DateiListeTableModel;
import de.applejuicenet.client.gui.tables.share.ShareNode;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DateiListeDialog.java,v 1.1 2003/08/27 16:44:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DateiListeDialog.java,v $
 * Revision 1.1  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 *
 */

public class DateiListeDialog extends JDialog {
    private JLabel speicherTxt = new JLabel();
    private JLabel speicherHtml = new JLabel();
    private DateiListeTable table = new DateiListeTable();

    public DateiListeDialog(Frame parent, boolean modal) {
        super(parent, modal);
        init();
    }

    private void init() {
        getContentPane().setLayout(new GridBagLayout());
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        IconManager im = IconManager.getInstance();
        speicherTxt.setIcon(im.getIcon("speichern"));
        speicherHtml.setIcon(im.getIcon("web"));
        panel1.add(speicherTxt);
        panel1.add(speicherHtml);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        getContentPane().add(panel1, constraints);
        constraints.weightx = 0;
        constraints.gridy = 1;
        getContentPane().add(new JLabel("just drag your files in"), constraints);
        constraints.gridy = 2;
        constraints.weighty = 1;
        getContentPane().add(new JScrollPane(table), constraints);
        constraints.weighty = 0;
        pack();
    }
}
