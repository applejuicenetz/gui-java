package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.gui.tables.download.DownloadTableCellRenderer;
import de.applejuicenet.client.gui.tables.download.DownloadNode;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.JTreeTable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPanel.java,v 1.37 2003/08/22 12:39:46 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadPanel.java,v $
 * Revision 1.37  2003/08/22 12:39:46  maj0r
 * Bug ID 798
 *
 * Revision 1.36  2003/08/22 10:03:11  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.35  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.34  2003/08/11 14:10:27  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 * Revision 1.33  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.32  2003/08/09 10:56:25  maj0r
 * DownloadTabelle weitergeführt.
 *
 * Revision 1.31  2003/08/05 20:47:06  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.30  2003/08/05 05:11:59  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.29  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.28  2003/07/04 15:25:38  maj0r
 * Version erhöht.
 * DownloadModel erweitert.
 *
 * Revision 1.27  2003/07/04 06:43:51  maj0r
 * Diverse Änderungen am DownloadTableModel.
 *
 * Revision 1.26  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.25  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.24  2003/07/01 18:49:03  maj0r
 * Struktur verändert.
 *
 * Revision 1.23  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.22  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.21  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadPanel
        extends JPanel
        implements LanguageListener, RegisterI, DataUpdateListener {
    private DownloadDOOverviewPanel downloadDOOverviewPanel = new DownloadDOOverviewPanel();
    private JTextField downloadLink = new JTextField();
    private JButton btnStartDownload = new JButton("Download");
    private JPanel overviewPanel = new JPanel(new BorderLayout());
    private PowerDownloadPanel powerDownloadPanel;
    private JTreeTable downloadTable;
    private JLabel linkLabel = new JLabel("ajfsp-Link hinzufügen");
    private DownloadModel downloadModel;
    private JPopupMenu popup = new JPopupMenu();
    private boolean initizialiced = false;
    private JScrollPane aScrollPane;
    JMenuItem item1;
    JMenuItem item2;
    JMenuItem item3;
    JMenuItem item4;
    JMenuItem item5;
    JMenuItem item6;

    public DownloadPanel() {
        powerDownloadPanel = new PowerDownloadPanel(this);
        try {
            jbInit();
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        item1 = new JMenuItem("Abbrechen");
        item2 = new JMenuItem("Pause/Fortsetzen");
        item4 = new JMenuItem("Umbenennen");
        item5 = new JMenuItem("Zielordner ändern");
        item6 = new JMenuItem("Fertige Übertragungen entfernen");
        //todo
        item4.setEnabled(false);
        item5.setEnabled(false);
        //
        popup.add(item1);
        popup.add(item2);
        popup.add(item4);
        popup.add(item5);
        popup.add(item6);

        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems != null && selectedItems.length != 0) {
                    int result = JOptionPane.showConfirmDialog(null, "Wollen Sie wirklich diese Downloads abbrechen", "Bestätigung",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (((DownloadNode) selectedItems[i]).getNodeType() == DownloadNode.DOWNLOAD_NODE) {
                                DownloadDO downloadDO = ((DownloadNode) selectedItems[i]).getDownloadDO();
                                ApplejuiceFassade.getInstance().cancelDownload(downloadDO.getId());
                            }
                        }
                    }
                }
            }
        });

        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems != null && selectedItems.length != 0) {
                    for (int i = 0; i < selectedItems.length; i++) {
                        if (((DownloadNode) selectedItems[i]).getNodeType() == DownloadNode.DOWNLOAD_NODE) {
                            DownloadDO downloadDO = ((DownloadNode) selectedItems[i]).getDownloadDO();
                            if (downloadDO.getStatus() == DownloadDO.PAUSIERT) {
                                ApplejuiceFassade.getInstance().resumeDownload(downloadDO.getId());
                            }
                            else {
                                ApplejuiceFassade.getInstance().pauseDownload(downloadDO.getId());
                            }
                        }
                    }
                }
            }
        });

        item6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ApplejuiceFassade.getInstance().cleanDownloadList();
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BorderLayout());
        tempPanel.add(linkLabel, BorderLayout.WEST);
        tempPanel.add(downloadLink, BorderLayout.CENTER);
        tempPanel.add(btnStartDownload, BorderLayout.EAST);
        topPanel.add(tempPanel, constraints);
        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.weightx = 1;

        downloadModel = new DownloadModel();
        downloadTable = new JTreeTable(downloadModel);
        downloadTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DownloadTableCellRenderer renderer = new DownloadTableCellRenderer();
        for (int i = 1; i < downloadTable.getColumnModel().getColumnCount(); i++) {
            downloadTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        btnStartDownload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String link = downloadLink.getText();
                if (link.length() != 0) {
                    ApplejuiceFassade.getInstance().processLink(link);
                    downloadLink.setText("");
                }
            }
        });
        downloadTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point p = e.getPoint();
                int selectedRow = downloadTable.rowAtPoint(p);
                DownloadNode node = (DownloadNode) ((TreeTableModelAdapter) downloadTable.getModel()).nodeForRow(selectedRow);
                if (downloadTable.columnAtPoint(p) != 0) {
                    if (e.getClickCount() == 2) {
                        ((TreeTableModelAdapter) downloadTable.getModel()).expandOrCollapseRow(selectedRow);
                    }
                }
                if (node.getNodeType() == DownloadNode.DOWNLOAD_NODE) {
                    powerDownloadPanel.btnPdl.setEnabled(true);
                    downloadDOOverviewPanel.setDownloadDO(node.getDownloadDO());
                }
                else {
                    powerDownloadPanel.btnPdl.setEnabled(false);
                }
            }

            public void mousePressed(MouseEvent me) {
                super.mousePressed(me);
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(downloadTable, e.getX(), e.getY());
                }
            }
        });

        aScrollPane = new JScrollPane();
        aScrollPane.getViewport().add(downloadTable);
        topPanel.add(aScrollPane, constraints);

        bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);

        bottomPanel.add(downloadDOOverviewPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this, DataUpdateListener.DOWNLOAD_CHANGED);
    }

    public Object[] getSelectedDownloadItems() {
        int count = downloadTable.getSelectedRowCount();
        Object[] result = null;
        if (count == 1) {
            result = new Object[count];
            result[0] = ((TreeTableModelAdapter) downloadTable.getModel()).nodeForRow(downloadTable.getSelectedRow());
        }
        else if (count > 1) {
            result = new Object[count];
            int[] indizes = downloadTable.getSelectedRows();
            for (int i = 0; i < indizes.length; i++) {
                result[i] = ((TreeTableModelAdapter) downloadTable.getModel()).nodeForRow(i);
            }
        }
        return result;
    }

    public void registerSelected() {
        if (!initizialiced){
            initizialiced = true;
            int width = aScrollPane.getWidth() - 18;
            TableColumnModel headerModel = downloadTable.getTableHeader().getColumnModel();
            int columnCount = headerModel.getColumnCount();
            for (int i=0; i<columnCount; i++){
                headerModel.getColumn(i).setPreferredWidth(width/columnCount);
            }

        }
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String text = languageSelector.getFirstAttrbuteByTagName(new String[]{
            "mainform", "Label14", "caption"});
        linkLabel.setText(ZeichenErsetzer.korrigiereUmlaute(text));
        btnStartDownload.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "downlajfsp",
                                                       "caption"})));
        btnStartDownload.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform",
                                                                        "downlajfsp", "hint"})));
        String[] tableColumns = new String[10];
        tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col0caption"}));
        tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col1caption"}));
        tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col2caption"}));
        tableColumns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col3caption"}));
        tableColumns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col4caption"}));
        tableColumns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col5caption"}));
        tableColumns[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col6caption"}));
        tableColumns[7] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col7caption"}));
        tableColumns[8] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col8caption"}));
        tableColumns[9] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "queue",
                                                       "col9caption"}));

        TableColumnModel tcm = downloadTable.getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            tcm.getColumn(i).setHeaderValue(tableColumns[i]);
        }

        item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "canceldown",
                                                       "caption"})));
        String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "pausedown",
                                                       "caption"}));
        temp += "/" +
                ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{
                    "mainform", "resumedown", "caption"}));
        item2.setText(temp);
        item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "renamefile",
                                                       "caption"})));
        item5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "changetarget",
                                                       "caption"})));
        item6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform",
                                                       "Clearfinishedentries1", "caption"})));
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
            HashMap downloads = (HashMap) content;
            if (downloads != null && downloads.size() != 0) {
                Iterator it = downloads.values().iterator();
                DownloadDO downloadDO = null;
                DownloadNode node = null;
                while (it.hasNext()) {
                    downloadDO = (DownloadDO) it.next();
                    node = new DownloadNode(downloadDO);

                }
                DownloadNode.clearOldNodes();
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        downloadTable.updateUI();
                    }
                });
            }
        }
    }
}