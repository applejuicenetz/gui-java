package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.gui.trees.chooser.DirectoryChooserNode;
import de.applejuicenet.client.gui.trees.chooser.
    DirectoryChooserTreeCellRenderer;
import de.applejuicenet.client.gui.trees.chooser.DirectoryChooserTreeModel;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODDirectoryChooser.java,v 1.5 2004/02/21 18:20:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODDirectoryChooser.java,v $
 * Revision 1.5  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.4  2004/02/05 23:11:26  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.1  2003/08/24 19:36:23  maj0r
 * no message
 *
 *
 */

public class ODDirectoryChooser
    extends JDialog {
    private JTree folderTree = new JTree();
    private JButton uebernehmen = new JButton();
    private JButton abbrechen = new JButton();

    private boolean change = false;
    private String path;

    private Logger logger;

    public ODDirectoryChooser(JDialog parent, String title) {
        super(parent, true);
        logger = Logger.getLogger(getClass());
        try {
            setTitle(title);
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(folderTree), BorderLayout.CENTER);
        folderTree.setModel(new DefaultTreeModel(new WaitNode()));
        folderTree.setCellRenderer(new DirectoryChooserTreeCellRenderer());
        uebernehmen.setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                DirectoryChooserTreeModel treeModel = new
                    DirectoryChooserTreeModel();
                folderTree.setModel(treeModel);
                folderTree.setRootVisible(false);
                uebernehmen.setEnabled(true);
                return null;
            }
        };
        worker.start();
        JPanel aPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aPanel.add(uebernehmen);
        aPanel.add(abbrechen);
        uebernehmen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Button1.caption")));
        uebernehmen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uebernehmen();
            }
        });
        abbrechen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Button2.caption")));
        abbrechen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getContentPane().add(aPanel, BorderLayout.SOUTH);
        pack();
        setSize(getWidth() * 2, getHeight());
    }

    public boolean isNewPathSelected() {
        return change;
    }

    public String getSelectedPath() {
        return path;
    }

    private void uebernehmen() {
        try {
            if (folderTree.getSelectionCount() != 0) {
                change = true;
                DirectoryChooserNode node = (DirectoryChooserNode)
                    folderTree.getLastSelectedPathComponent();
                path = node.getDO().getPath();
            }
            dispose();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }
}
