package de.applejuicenet.client.gui.options;

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

import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.options.directorytree.DirectoryChooserNode;
import de.applejuicenet.client.gui.options.directorytree.DirectoryChooserTreeCellRenderer;
import de.applejuicenet.client.gui.options.directorytree.DirectoryChooserTreeModel;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODDirectoryChooser.java,v 1.2 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ODDirectoryChooser
    extends JDialog {
	
    private static final long serialVersionUID = 6193113845377232917L;
    
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }
}
