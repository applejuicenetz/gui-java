package de.applejuicenet.client.gui;

import java.util.ArrayList;
import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
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
import de.applejuicenet.client.gui.tables.NormalHeaderRenderer;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.upload.UploadDataTableModel;
import de.applejuicenet.client.gui.tables.upload.UploadTablePercentCellRenderer;
import de.applejuicenet.client.gui.tables.upload.UploadTableVersionCellRenderer;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/UploadPanel.java,v 1.41 2004/03/01 15:10:09 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: UploadPanel.java,v $
 * Revision 1.41  2004/03/01 15:10:09  maj0r
 * TableHeader werden in allen Tabellen gleich dargestellt.
 *
 * Revision 1.40  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.39  2004/02/09 14:21:32  maj0r
 * Icons für Upload-DirectStates eingebaut.
 *
 * Revision 1.38  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.37  2004/02/04 14:26:05  maj0r
 * Bug #185 gefixt (Danke an muhviestarr)
 * Einstellungen des GUIs werden beim Schliessen des Core gesichert.
 *
 * Revision 1.36  2004/01/20 14:18:29  maj0r
 * Spaltenindizes werden jetzt gespeichert.
 *
 * Revision 1.35  2004/01/12 07:26:10  maj0r
 * Tabellenspalte nun ueber Headerkontextmenue ein/ausblendbar.
 *
 * Revision 1.34  2004/01/09 19:21:17  maj0r
 * Kleine Korrekturen.
 *
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

    private static UploadPanel instance;

    private JTreeTable uploadDataTable;
    private int anzahlClients = 0;
    private JLabel label1 = new JLabel("0 Clients in Deiner Uploadliste");
    private String clientText;
    private UploadDataTableModel uploadDataTableModel;
    private boolean initizialiced = false;
    private Logger logger;
    private boolean panelSelected = false;

    private JPopupMenu columnPopup = new JPopupMenu();
    private TableColumn[] columns = new TableColumn[7];
    private JCheckBoxMenuItem[] columnPopupItems = new JCheckBoxMenuItem[
        columns.length];

    public static synchronized UploadPanel getInstance() {
        if (instance == null) {
            instance = new UploadPanel();
        }
        return instance;
    }

    private UploadPanel() {
        logger = Logger.getLogger(getClass());
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
                if (uploadDataTable.columnAtPoint(p) != 0) {
                    int selectedRow = uploadDataTable.rowAtPoint(p);
                    if (e.getClickCount() == 2) {
                        ( (TreeTableModelAdapter) uploadDataTable.getModel()).
                            expandOrCollapseRow(selectedRow);
                    }
                }
            }
        });
        uploadDataTable.getColumnModel().getColumn(4).setCellRenderer(new
            UploadTablePercentCellRenderer());
        uploadDataTable.getColumnModel().getColumn(6).setCellRenderer(new
            UploadTableVersionCellRenderer());

        TableColumnModel model = uploadDataTable.getColumnModel();
        for (int i = 0; i < columns.length; i++) {
            columns[i] = model.getColumn(i);
            columnPopupItems[i] = new JCheckBoxMenuItem( (String) columns[i].
                getHeaderValue());
            final int x = i;
            columnPopupItems[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    if (columnPopupItems[x].isSelected()) {
                        uploadDataTable.getColumnModel().addColumn(columns[x]);
                        PropertiesManager.getPositionManager().
                            setUploadColumnVisible(x, true);
                        PropertiesManager.getPositionManager().
                            setUploadColumnIndex(
                            x,
                            uploadDataTable.getColumnModel().getColumnIndex(columns[
                            x].getIdentifier()));
                    }
                    else {
                        uploadDataTable.getColumnModel().removeColumn(columns[x]);
                        PropertiesManager.getPositionManager().
                            setUploadColumnVisible(x, false);
                        for (int i = 0; i < columns.length; i++) {
                            try {
                                PropertiesManager.getPositionManager().
                                    setUploadColumnIndex(
                                    i,
                                    uploadDataTable.getColumnModel().
                                    getColumnIndex(columns[i].getIdentifier()));
                            }
                            catch (IllegalArgumentException niaE) {
                                //nix zu tun
                            }
                        }
                    }
                }
            });
            columnPopup.add(columnPopupItems[i]);
        }
        columnPopupItems[0].setEnabled(false);

        JTableHeader header = uploadDataTable.getTableHeader();
        header.addMouseListener(new HeaderPopupListener());
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                PositionManager pm = PropertiesManager.getPositionManager();
                TableColumnModel columnModel = uploadDataTable.getColumnModel();
                for (int i = 0; i < columns.length; i++) {
                    try {
                        pm.setUploadColumnIndex(i,
                                                columnModel.getColumnIndex(columns[
                            i].getIdentifier()));
                    }
                    catch (IllegalArgumentException niaE) {
                        //nix zu tun
                    }
                }
            }
        });
        JScrollPane aScrollPane = new JScrollPane(uploadDataTable);
        aScrollPane.setBackground(uploadDataTable.getBackground());
//        uploadDataTable.getTableHeader().setBackground(uploadDataTable.getBackground());
        aScrollPane.getViewport().setOpaque(false);
        add(aScrollPane, BorderLayout.CENTER);

        int n = model.getColumnCount();
        TableCellRenderer renderer = new NormalHeaderRenderer();
        for (int i = 0; i < n; i++) {
            model.getColumn(i).setHeaderRenderer(renderer);
        }

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
            getFirstAttrbuteByTagName(".root.mainform.uplcounttext"));
        label1.setText(clientText.replaceAll("%d", Integer.toString(anzahlClients)));
        String[] columnsText = new String[7];
        columnsText[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col0caption"));
        columnsText[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col3caption"));
        columnsText[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col1caption"));
        columnsText[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col2caption"));
        columnsText[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.col6caption"));
        columnsText[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col4caption"));
        columnsText[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col5caption"));
        for (int i = 0; i < columns.length; i++) {
            columns[i].setHeaderValue(columnsText[i]);
        }
        columns[0].setPreferredWidth(100);
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
        int[] widths = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            widths[i] = columns[i].getWidth();
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
                    boolean[] visibilies = pm.getUploadColumnVisibilities();
                    int[] indizes = pm.getUploadColumnIndizes();
                    ArrayList visibleColumns = new ArrayList();
                    for (int i = 0; i < columns.length; i++) {
                        columns[i].setPreferredWidth(widths[i]);
                        uploadDataTable.removeColumn(columns[i]);
                        if (visibilies[i]) {
                            visibleColumns.add(columns[i]);
                        }
                    }
                    int pos = -1;
                    for (int i = 0; i < visibleColumns.size(); i++) {
                        for (int x = 0; x < columns.length; x++) {
                            if (visibleColumns.contains(columns[x])
                                && indizes[x] == pos + 1) {
                                uploadDataTable.addColumn(columns[x]);
                                pos++;
                                break;
                            }
                        }
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

    class HeaderPopupListener
        extends MouseAdapter {
        private TableColumnModel model;

        public HeaderPopupListener() {
            model = uploadDataTable.getColumnModel();
            columnPopupItems[0].setSelected(true);
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
                for (int i = 1; i < columns.length; i++) {
                    try {
                        model.getColumnIndex(columns[i].getIdentifier());
                        columnPopupItems[i].setSelected(true);
                    }
                    catch (IllegalArgumentException niaE) {
                        columnPopupItems[i].setSelected(false);
                    }
                }
                columnPopup.show(uploadDataTable.getTableHeader(), e.getX(),
                                 e.getY());
            }
        }
    }
}
