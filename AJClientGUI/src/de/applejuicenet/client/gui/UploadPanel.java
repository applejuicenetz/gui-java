package de.applejuicenet.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.NormalHeaderRenderer;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.upload.UploadDataTableModel;
import de.applejuicenet.client.gui.tables.upload.UploadTablePercentCellRenderer;
import de.applejuicenet.client.gui.tables.upload.UploadTableVersionCellRenderer;
import de.applejuicenet.client.gui.tables.upload.UploadTableWholeLoadedPercentCellRenderer;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.shared.dac.UploadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/UploadPanel.java,v 1.48 2004/06/23 13:31:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class UploadPanel
    extends JPanel
    implements LanguageListener, RegisterI, DataUpdateListener {

    private static UploadPanel instance;

    private JTreeTable uploadDataTable;
    private int anzahlClients = 0;
    private JLabel label1 = new JLabel("0 Clients in Deiner Uploadliste");
    private String warteschlangeVoll = "";
    private String clientText;
    private UploadDataTableModel uploadDataTableModel;
    private boolean initizialiced = false;
    private Logger logger;
    private boolean panelSelected = false;
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem itemCopyToClipboard;

    private JPopupMenu columnPopup = new JPopupMenu();
    private TableColumn[] columns = new TableColumn[8];
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
        uploadDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        uploadDataTable.getColumnModel().getColumn(4).setCellRenderer(new
            UploadTablePercentCellRenderer());
        uploadDataTable.getColumnModel().getColumn(5).setCellRenderer(new
            UploadTableWholeLoadedPercentCellRenderer());

        uploadDataTable.getColumnModel().getColumn(7).setCellRenderer(new
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
                        PositionManagerImpl.getInstance().
                            setUploadColumnVisible(x, true);
                        PositionManagerImpl.getInstance().
                            setUploadColumnIndex(
                            x,
                            uploadDataTable.getColumnModel().getColumnIndex(columns[
                            x].getIdentifier()));
                    }
                    else {
                        uploadDataTable.getColumnModel().removeColumn(columns[x]);
                        PositionManagerImpl.getInstance().
                            setUploadColumnVisible(x, false);
                        for (int y = 0; y < columns.length; y++) {
                            try {
                                PositionManagerImpl.getInstance().
                                    setUploadColumnIndex(
                                    y,
                                    uploadDataTable.getColumnModel().
                                    getColumnIndex(columns[y].getIdentifier()));
                            }
                            catch (IllegalArgumentException niaE) {
                                ;
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
                PositionManager pm = PositionManagerImpl.getInstance();
                TableColumnModel columnModel = uploadDataTable.getColumnModel();
                for (int i = 0; i < columns.length; i++) {
                    try {
                        pm.setUploadColumnIndex(i,
                                                columnModel.getColumnIndex(columns[
                            i].getIdentifier()));
                    }
                    catch (IllegalArgumentException niaE) {
                        ;
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
        itemCopyToClipboard = new JMenuItem();
        itemCopyToClipboard.setIcon(IconManager.getInstance().getIcon("clipboard"));
        itemCopyToClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	copyLinkToClipboard();
            }
        });
        popupMenu.add(itemCopyToClipboard);
        uploadDataTable.addMouseListener(new MouseAdapter() {
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
                    Point p = e.getPoint();
                    int selectedRow = uploadDataTable.rowAtPoint(p);
                    if (selectedRow != -1) {
                    	uploadDataTable.setRowSelectionInterval(selectedRow,
                                selectedRow);
                        Object selectedItem = ((TreeTableModelAdapter) uploadDataTable.getModel()).
						nodeForRow(selectedRow);
                        if (selectedItem.getClass() == UploadDO.class){
                            popupMenu.show(uploadDataTable, e.getX(), e.getY());
                        }
                    }
                }
            }
        });

        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
            DataUpdateListener.UPLOAD_CHANGED);
    }
    
    private void copyLinkToClipboard(){
        Object selectedItem = ((TreeTableModelAdapter) uploadDataTable.getModel()).
			nodeForRow(uploadDataTable.getSelectedRow());
        if (selectedItem.getClass() == UploadDO.class){
        	String shareFileId = ((UploadDO)selectedItem).getShareFileIDAsString();
            Map share = ApplejuiceFassade.getInstance().getShare(false);
            if (share.containsKey(shareFileId)){
            	ShareDO shareDO = (ShareDO) share.get(shareFileId);
            	if (shareDO != null){
                    Clipboard cb = Toolkit.getDefaultToolkit().
	                getSystemClipboard();
		            StringBuffer toCopy = new StringBuffer();
		            toCopy.append("ajfsp://file|");
	                toCopy.append(shareDO.getShortfilename() + "|" +
	                		shareDO.getCheckSum() + "|" +
	                		shareDO.getSize() + "/");
	                StringSelection contents = new StringSelection(toCopy.
	                    toString());
                    cb.setContents(contents, null);
            	}
            }
        }
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        clientText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uplcounttext"));
        label1.setText(clientText.replaceAll("%d", Integer.toString(anzahlClients)));
        String[] columnsText = new String[8];
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
            getFirstAttrbuteByTagName(".root.javagui.uploadform.columnwasserstand"));
        columnsText[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col4caption"));
        columnsText[7] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.col5caption"));
        for (int i = 0; i < columns.length; i++) {
            columns[i].setHeaderValue(columnsText[i]);
        }
        columns[0].setPreferredWidth(100);
        warteschlangeVoll = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.downloadform.warteschlangevoll"));
        itemCopyToClipboard.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.UPLOAD_CHANGED) {
                uploadDataTableModel.setTable( (HashMap) content);
                if (panelSelected) {
                    uploadDataTable.updateUI();
                }
                anzahlClients = uploadDataTableModel.getRowCount();
                String tmp = clientText.replaceAll("%d",
                    Integer.toString(anzahlClients));
                long maxUploadPos = ApplejuiceFassade.getInstance().
                    getInformation().getMaxUploadPositions();
                if (anzahlClients >= maxUploadPos){
                    tmp += " (" + warteschlangeVoll + ")";
                }
                label1.setText(tmp);
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
                PositionManager pm = PositionManagerImpl.getInstance();
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
