package de.applejuicenet.client.gui;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.upload.UploadDataTableModel;
import de.applejuicenet.client.gui.tables.upload.UploadTableCellRenderer;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/UploadPanel.java,v 1.12 2003/08/09 10:57:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UploadPanel.java,v $
 * Revision 1.12  2003/08/09 10:57:29  maj0r
 * UploadTabelle weitergeführt.
 *
 * Revision 1.11  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.10  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class UploadPanel
        extends JPanel
        implements LanguageListener, RegisterI, DataUpdateListener {
    private JTable uploadDataTable;
    private int anzahlClients = 0;
    private JLabel label1 = new JLabel("0 Clients in Deiner Uploadliste");

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
        uploadDataTable = new JTable();
        uploadDataTable.setModel(new UploadDataTableModel());
        UploadTableCellRenderer renderer = new UploadTableCellRenderer();
        for (int i = 1; i < uploadDataTable.getColumnModel().getColumnCount(); i++) {
            uploadDataTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        JScrollPane aScrollPane = new JScrollPane();
        aScrollPane.getViewport().add(uploadDataTable);
        add(aScrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(label1);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.add(panel, BorderLayout.WEST);
        add(panel2, BorderLayout.SOUTH);
        DataManager.getInstance().addDataUpdateListener(this,
                DataUpdateListener.UPLOAD_CHANGED);
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"mainform", "uplcounttext"}));
        label1.setText(temp.replaceAll("%d", Integer.toString(anzahlClients)));
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
    }

    public void fireContentChanged(int type, Object content) {
        if (type != DataUpdateListener.UPLOAD_CHANGED ||
                !(content instanceof HashMap)) {
            return;
        }
        ((UploadDataTableModel) uploadDataTable.getModel()).setTable((HashMap)
                content);
    }

    public void registerSelected() {
//    DataManager.getInstance().updateModifiedXML();
    }
}