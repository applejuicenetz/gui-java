package de.applejuicenet.client.gui;

import java.util.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.upload.UploadDataTableModel;
import de.applejuicenet.client.gui.tables.upload.UploadTableCellRenderer;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/UploadPanel.java,v 1.21 2003/09/02 16:08:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UploadPanel.java,v $
 * Revision 1.21  2003/09/02 16:08:14  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.20  2003/08/31 11:06:44  maj0r
 * Groesse der ersten Upload-Spalte geaendert.
 *
 * Revision 1.19  2003/08/30 19:45:20  maj0r
 * Auf JTreeTable umgebaut.
 *
 * Revision 1.18  2003/08/22 14:16:00  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.17  2003/08/22 13:52:25  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.16  2003/08/18 17:10:22  maj0r
 * Debugausgabe entfernt.
 *
 * Revision 1.15  2003/08/18 14:51:04  maj0r
 * Anzeige korrigiert.
 *
 * Revision 1.14  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.13  2003/08/10 21:08:18  maj0r
 * Diverse �nderungen.
 *
 * Revision 1.12  2003/08/09 10:57:29  maj0r
 * UploadTabelle weitergef�hrt.
 *
 * Revision 1.11  2003/07/01 18:41:39  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.10  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class UploadPanel
        extends JPanel
        implements LanguageListener, RegisterI, DataUpdateListener {
    private JTreeTable uploadDataTable;
    private int anzahlClients = 0;
    private JLabel label1 = new JLabel("0 Clients in Deiner Uploadliste");
    private String clientText;
    private UploadDataTableModel uploadDataTableModel;

    public UploadPanel() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        setLayout(new BorderLayout());
        LanguageSelector.getInstance().addLanguageListener(this);
        uploadDataTableModel = new UploadDataTableModel();
        uploadDataTable = new JTreeTable(uploadDataTableModel);
        uploadDataTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point p = e.getPoint();
                int selectedRow = uploadDataTable.rowAtPoint(p);
                if (e.getClickCount() == 2) {
                    ((TreeTableModelAdapter) uploadDataTable.getModel()).expandOrCollapseRow(selectedRow);
                }
            }
        });
//        uploadDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UploadTableCellRenderer renderer = new UploadTableCellRenderer();
        for (int i = 0; i < uploadDataTable.getColumnModel().getColumnCount(); i++) {
            uploadDataTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        JScrollPane aScrollPane = new JScrollPane(uploadDataTable);
        add(aScrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(label1);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.add(panel, BorderLayout.WEST);
        add(panel2, BorderLayout.SOUTH);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
                DataUpdateListener.UPLOAD_CHANGED);
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        clientText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uplcounttext"}));
        label1.setText(clientText.replaceAll("%d", Integer.toString(anzahlClients)));
        String[] columns = new String[7];
        columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uploads",
                                                       "col0caption"}));
        columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uploads",
                                                       "col3caption"}));
        columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uploads",
                                                       "col1caption"}));
        columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uploads",
                                                       "col2caption"}));
        columns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                      "col6caption"}));
        columns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uploads",
                                                       "col4caption"}));
        columns[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uploads",
                                                       "col5caption"}));
        TableColumnModel tcm = uploadDataTable.getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            tcm.getColumn(i).setHeaderValue(columns[i]);
        }
        tcm.getColumn(0).setPreferredWidth(100);
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.UPLOAD_CHANGED ||
                !(content instanceof HashMap)) {
            uploadDataTableModel.setTable((HashMap)content);
            uploadDataTable.updateUI();
            anzahlClients = uploadDataTableModel.getRowCount();
            label1.setText(clientText.replaceAll("%d", Integer.toString(anzahlClients)));
        }
    }

    public void registerSelected() {
//    ApplejuiceFassade.getInstance().updateModifiedXML();
    }
}