package de.applejuicenet.client.gui;

import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.gui.trees.chooser.DirectoryChooserTreeModel;
import de.applejuicenet.client.gui.trees.chooser.DirectoryChooserTreeCellRenderer;
import de.applejuicenet.client.gui.trees.chooser.DirectoryChooserNode;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.shared.ZeichenErsetzer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODDirectoryChooser.java,v 1.1 2003/08/24 19:36:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODDirectoryChooser.java,v $
 * Revision 1.1  2003/08/24 19:36:23  maj0r
 * no message
 *
 *
 */

public class ODDirectoryChooser extends JDialog{
    private JTree folderTree = new JTree();
    private JButton uebernehmen = new JButton();
    private JButton abbrechen = new JButton();

    private boolean change = false;
    private String path;

    public ODDirectoryChooser(JDialog parent, String title){
        super(parent, true);
        setTitle(title);
        init();
    }

    private void init(){
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(folderTree), BorderLayout.CENTER);
        folderTree.setModel(new DefaultTreeModel(new WaitNode()));
        folderTree.setCellRenderer(new DirectoryChooserTreeCellRenderer());
        uebernehmen.setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                DirectoryChooserTreeModel treeModel = new DirectoryChooserTreeModel();
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
            getFirstAttrbuteByTagName(new String[] {"einstform", "Button1",
                                      "caption"})));
        uebernehmen.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uebernehmen();
          }
        });
        abbrechen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"einstform", "Button2",
                                      "caption"})));
        abbrechen.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        });
        getContentPane().add(aPanel, BorderLayout.SOUTH);
        pack();
        setSize(getWidth()*2, getHeight());
    }

    public boolean isNewPathSelected(){
        return change;
    }

    public String getSelectedPath(){
        return path;
    }

    private void uebernehmen(){
        if (folderTree.getSelectionCount()!=0){
            change = true;
            DirectoryChooserNode node = (DirectoryChooserNode)
                           folderTree.getLastSelectedPathComponent();
            path = node.getDO().getPath();
        }
        dispose();
    }
}
