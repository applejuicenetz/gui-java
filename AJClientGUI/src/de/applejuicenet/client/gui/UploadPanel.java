package de.applejuicenet.client.gui;

import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.upload.UploadDataTableModel;
import de.applejuicenet.client.gui.tables.upload.UploadTablePercentCellRenderer;
import de.applejuicenet.client.gui.tables.upload.UploadTableTreeCellRenderer;
import de.applejuicenet.client.gui.tables.upload.UploadTableVersionCellRenderer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseMotionAdapter;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/UploadPanel.java,v 1.33 2004/01/09 15:08:44 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: UploadPanel.java,v $
 * Revision 1.33  2004/01/09 15:08:44  maj0r
 * Erste Spalte kann nun nicht mehr verschoben werden.
 *
 * Revision 1.32  2004/01/09 14:35:15  maj0r
 * Spalten der Uploadtabelle koennen nun ordentlich verschoben werden.
 *
 * Revision 1.31  2004/01/08 07:48:22  maj0r
 * Wenn das Panel nicht selektiert ist, wird die Tabelle nun nicht mehr aktualisiert.
 *
 * Revision 1.30  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.29  2003/12/19 09:54:14  maj0r
 * Bug der Tableheader der Share- und der Uploadtabelle behoben (Danke an muhviestarr).
 *
 * Revision 1.28  2003/12/17 11:06:30  maj0r
 * RegisterI erweitert, um auf Verlassen eines Tabs reagieren zu koennen.
 *
 * Revision 1.27  2003/12/05 11:18:02  maj0r
 * Workaround fürs Setzen der Hintergrundfarben der Scrollbereiche ausgebaut.
 *
 * Revision 1.26  2003/11/30 17:01:33  maj0r
 * Hintergrundfarbe aller Scrollbereiche an ihre Tabellen angepasst.
 *
 * Revision 1.25  2003/10/14 15:40:25  maj0r
 * Logger eingebaut.
 *
 * Revision 1.24  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.23  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.22  2003/09/07 09:29:55  maj0r
     * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
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
 * Diverse Änderungen.
 *
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

    public static UploadPanel _this;

    private JTreeTable uploadDataTable;
    private int anzahlClients = 0;
    private JLabel label1 = new JLabel("0 Clients in Deiner Uploadliste");
    private String clientText;
    private UploadDataTableModel uploadDataTableModel;
    private boolean initizialiced = false;
    private Logger logger;
    private boolean panelSelected = false;

    public UploadPanel() {
        logger = Logger.getLogger(getClass());
        _this = this;
        try {
            init();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    private void init() throws Exception {
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
                    ( (TreeTableModelAdapter) uploadDataTable.getModel()).
                        expandOrCollapseRow(selectedRow);
                }
            }
        });
        uploadDataTable.getColumnModel().getColumn(0).setCellRenderer(new
            UploadTableTreeCellRenderer());
        uploadDataTable.getColumnModel().getColumn(4).setCellRenderer(new
            UploadTablePercentCellRenderer());
        uploadDataTable.getColumnModel().getColumn(6).setCellRenderer(new
            UploadTableVersionCellRenderer());
        JTableHeader header = uploadDataTable.getTableHeader();
        header.addMouseMotionListener(new HeaderDragListener(header));

        JScrollPane aScrollPane = new JScrollPane(uploadDataTable);
        aScrollPane.setBackground(uploadDataTable.getBackground());
        aScrollPane.getViewport().setOpaque(false);
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
            getFirstAttrbuteByTagName(new String[] {"mainform", "uplcounttext"}));
        label1.setText(clientText.replaceAll("%d",
                                             Integer.toString(anzahlClients)));
        String[] columns = new String[7];
        columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "uploads",
                                      "col0caption"}));
        columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "uploads",
                                      "col3caption"}));
        columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "uploads",
                                      "col1caption"}));
        columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "uploads",
                                      "col2caption"}));
        columns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                      "col6caption"}));
        columns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "uploads",
                                      "col4caption"}));
        columns[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "uploads",
                                      "col5caption"}));
        TableColumnModel tcm = uploadDataTable.getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            tcm.getColumn(i).setHeaderValue(columns[i]);
        }
        tcm.getColumn(0).setPreferredWidth(100);
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.UPLOAD_CHANGED) {
                uploadDataTableModel.setTable( (HashMap) content);
                if (panelSelected) {
                    uploadDataTable.updateUI();
                }
                anzahlClients = uploadDataTableModel.getRowCount();
                label1.setText(clientText.replaceAll("%d",
                    Integer.toString(anzahlClients)));
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public int[] getColumnWidths() {
        TableColumnModel tcm = uploadDataTable.getColumnModel();
        int[] widths = new int[tcm.getColumnCount()];
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            widths[i] = tcm.getColumn(i).getWidth();
        }
        return widths;
    }

    public void registerSelected() {
        try {
            panelSelected = true;
            if (!initizialiced) {
                initizialiced = true;
                TableColumnModel headerModel = uploadDataTable.getTableHeader().
                    getColumnModel();
                int columnCount = headerModel.getColumnCount();
                PositionManager pm = PropertiesManager.getPositionManager();
                if (pm.isLegal()) {
                    int[] widths = pm.getUploadWidths();
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(widths[i]);
                    }
                }
                else {
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(
                            uploadDataTable.getWidth() / columnCount);
                    }
                }
                uploadDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
            uploadDataTable.updateUI();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public void lostSelection() {
        panelSelected = false;
    }

    class HeaderDragListener
         extends MouseMotionAdapter {
         private JTableHeader header;

         public HeaderDragListener(JTableHeader header) {
             this.header = header;
         }

         public void mouseDragged(MouseEvent e) {
             int col = header.columnAtPoint(e.getPoint());
             if (col == 0) {
                 header.setDraggedColumn(null);
                 header.setDraggedDistance(0);
             }
             else{
                 super.mouseDragged(e);
             }
         }

     }
}