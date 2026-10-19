/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.options.directorytree.DirectoryChooserNode;
import de.applejuicenet.client.gui.options.directorytree.DirectoryChooserTreeCellRenderer;
import de.applejuicenet.client.gui.options.directorytree.DirectoryChooserTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Copyright: General Public License
 */
public class ODDirectoryChooser extends JDialog {
    private JTree folderTree = new JTree();
    private JButton apply = new JButton();
    private JButton cancel = new JButton();
    private boolean change = false;
    private String path;
    private Logger logger;

    public ODDirectoryChooser(JDialog parent, String title) {
        super(parent, true);
        logger = LoggerFactory.getLogger(getClass());
        try {
            setTitle(title);
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(folderTree), BorderLayout.CENTER);
        folderTree.setModel(new DefaultTreeModel(new WaitNode()));
        folderTree.setCellRenderer(new DirectoryChooserTreeCellRenderer());
        folderTree.setRowHeight(0);
        apply.setEnabled(false);
        new SwingWorker<DirectoryChooserTreeModel, Void>() {
            protected DirectoryChooserTreeModel doInBackground() {
                return new DirectoryChooserTreeModel();
            }

            protected void done() {
                try {
                    folderTree.setModel(get());
                    folderTree.setRootVisible(false);
                    apply.setEnabled(true);
                } catch (Exception e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                }
            }
        }.execute();
        JPanel aPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aPanel.add(apply);
        aPanel.add(cancel);
        apply.setText(languageSelector.getFirstAttributeByTagName("einstform.Button1.caption"));
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        cancel.setText(languageSelector.getFirstAttributeByTagName("einstform.Button2.caption"));
        cancel.addActionListener(new ActionListener() {
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

    private void apply() {
        try {
            if (folderTree.getSelectionCount() != 0) {
                change = true;
                DirectoryChooserNode node = (DirectoryChooserNode) folderTree.getLastSelectedPathComponent();

                path = node.getDirectory().getPath();
            }

            dispose();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }
}
