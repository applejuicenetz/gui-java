package de.applejuicenet.client.gui;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.gui.shared.SortButtonRenderer;
import de.applejuicenet.client.gui.shared.HeaderListener;
import de.applejuicenet.client.gui.tables.server.ServerTableCellRenderer;
import de.applejuicenet.client.gui.tables.server.ServerTableModel;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ServerPanel.java,v 1.32 2003/10/05 11:48:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ServerPanel.java,v $
 * Revision 1.32  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
 * Revision 1.31  2003/10/01 14:45:40  maj0r
 * Suche fortgesetzt.
 *
 * Revision 1.30  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.29  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.28  2003/09/08 06:26:31  maj0r
 * Ein Panel entfernt. War ohne Funktion.
 *
 * Revision 1.27  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.26  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.25  2003/08/26 19:46:34  maj0r
 * Sharebereich weiter vervollstaendigt.
 *
 * Revision 1.24  2003/08/20 16:18:51  maj0r
 * Server koennen nun entfernt werden.
 *
 * Revision 1.23  2003/08/19 16:02:16  maj0r
 * Optimierungen.
 *
 * Revision 1.22  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.21  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.20  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.19  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.18  2003/07/01 14:53:48  maj0r
 * Unnützes Update der Serverliste entfernt.
 *
 * Revision 1.17  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.16  2003/06/22 20:34:25  maj0r
 * Konsolenausgaben hinzugefügt.
 *
 * Revision 1.15  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ServerPanel
        extends JPanel
        implements LanguageListener, DataUpdateListener, RegisterI {

    public static ServerPanel _this;

    private JTable serverTable;
    private JLabel sucheServer = new JLabel(
            "<html><font><u>mehr Server gibt es hier</u></font></html>");
    private JPopupMenu popup = new JPopupMenu();
    private JPopupMenu popup2 = new JPopupMenu();
    private JMenuItem item1;
    private JMenuItem item2;
    private JMenuItem item3;
    private JMenuItem item4;
    private Logger logger;
    private boolean initizialiced = false;

    public ServerPanel() {
        _this = this;
        logger = Logger.getLogger(getClass());
        try
        {
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        LanguageSelector.getInstance().addLanguageListener(this);

        item1 = new JMenuItem("Verbinden");
        item2 = new JMenuItem("Löschen");
        item3 = new JMenuItem("Hinzufügen");
        item4 = new JMenuItem("Hinzufügen");
        popup.add(item1);
        popup.add(item4);
        popup.add(item2);
        popup2.add(item3);
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int selected = serverTable.getSelectedRow();
                ServerDO server = (ServerDO) ((ServerTableModel) serverTable.getModel()).
                        getRow(selected);
                ApplejuiceFassade.getInstance().connectToServer(server.getID());
            }
        });
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int selected = serverTable.getSelectedRow();
                ServerDO server = (ServerDO) ((ServerTableModel) serverTable.getModel()).
                        getRow(selected);
                ApplejuiceFassade.getInstance().entferneServer(server.getID());
            }
        });
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                NewServerDialog newServerDialog = new NewServerDialog(AppleJuiceDialog.getApp(), true);
                Dimension appDimension = newServerDialog.getSize();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                newServerDialog.setLocation((screenSize.width - appDimension.width)/2,
                               (screenSize.height - appDimension.height)/2);
                newServerDialog.show();
                if (newServerDialog.isLegal()){
                    ApplejuiceFassade.getInstance().processLink(newServerDialog.getLink());
                }
            }
        };
        item3.addActionListener(actionListener);
        item4.addActionListener(actionListener);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;

        sucheServer.setForeground(Color.blue);
        sucheServer.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                String[] server = PropertiesManager.getOptionsManager().getActualServers();
                ApplejuiceFassade af = ApplejuiceFassade.getInstance();
                for (int i=0; i<server.length; i++){
                    af.processLink(server[i]);
                }
            }
        });
        panel1.add(sucheServer, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(new JLabel(), constraints);
        add(panel1, BorderLayout.NORTH);
        serverTable = new JTable();
        serverTable.setModel(new ServerTableModel());
        serverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SortButtonRenderer renderer = new SortButtonRenderer();
        TableColumnModel model = serverTable.getColumnModel();
        int n = model.getColumnCount();
        for (int i = 0; i < n; i++)
        {
            model.getColumn(i).setHeaderRenderer(renderer);
            model.getColumn(i).setPreferredWidth(model.getColumn(i).getWidth());
        }

        JTableHeader header = serverTable.getTableHeader();
        header.addMouseListener(new HeaderListener(header, renderer));
/*    ToolTipManager.sharedInstance().registerComponent(serverTable);
    serverTable.addMouseMotionListener(new MouseMotionAdapter(){
      public void mouseMoved(MouseEvent e){
        int row = serverTable.rowAtPoint(e.getPoint());
        int column = serverTable.columnAtPoint(e.getPoint());
        Object a = serverTable.getModel().getValueAt(row, column);
        serverTable.setToolTipText((String)a);
      }
    });*/

        TableColumn tc = serverTable.getColumnModel().getColumn(0);
        tc.setCellRenderer(new ServerTableCellRenderer());
        final JScrollPane aScrollPane = new JScrollPane(serverTable);
        MouseAdapter popupMouseAdapter = new MouseAdapter(){
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me))
                {
                    Point p = me.getPoint();
                    int iRow = serverTable.rowAtPoint(p);
                    int iCol = serverTable.columnAtPoint(p);
                    if (iRow!=-1 && iCol!=-1){
                        serverTable.setRowSelectionInterval(iRow, iRow);
                        serverTable.setColumnSelectionInterval(iCol, iCol);
                    }
                }
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger())
                {
                    if (serverTable.getSelectedRowCount()>0){
                        popup.show(aScrollPane, e.getX(), e.getY());
                    }
                    else{
                        popup2.show(aScrollPane, e.getX(), e.getY());
                    }
                }
            }
        };
        aScrollPane.addMouseListener(popupMouseAdapter);
        serverTable.addMouseListener(popupMouseAdapter);
        add(aScrollPane, BorderLayout.CENTER);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
                                                              DataUpdateListener.SERVER_CHANGED);
    }

    public void registerSelected() {
        if (!initizialiced){
            initizialiced = true;
            TableColumnModel headerModel = serverTable.getTableHeader().getColumnModel();
            int columnCount = headerModel.getColumnCount();
            PositionManager pm = PropertiesManager.getPositionManager();
            if (pm.isLegal()){
                int[] widths = pm.getServerWidths();
                for (int i=0; i<columnCount; i++){
                    headerModel.getColumn(i).setPreferredWidth(widths[i]);
                }
            }
            else{
                for (int i=0; i<columnCount; i++){
                    headerModel.getColumn(i).setPreferredWidth(serverTable.getWidth()/columnCount);
                }
            }
            serverTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }

    public void fireContentChanged(int type, Object content) {
        try{
            if (type == DataUpdateListener.SERVER_CHANGED)
            {
                int selected = serverTable.getSelectedRow();
                ((ServerTableModel) serverTable.getModel()).setTable((HashMap) content);
                if (selected != -1 && selected < serverTable.getRowCount())
                {
                    serverTable.setRowSelectionInterval(selected, selected);
                }
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void fireLanguageChanged() {
        try{
            LanguageSelector languageSelector = LanguageSelector.getInstance();
/*            sucheServer.setText("<html><font><u>" +
                                ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                  getFirstAttrbuteByTagName(new String[]{"mainform", "Label11",
                                                                                                         "caption"})) +
                                "</u></font></html>");*/
            String[] columns = new String[4];
            columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"mainform", "serverlist",
                                                                                                  "col0caption"}));
            columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"mainform", "serverlist",
                                                                                                  "col1caption"}));
            columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"mainform", "serverlist",
                                                                                                  "col3caption"}));
            columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"mainform", "serverlist",
                                                                                                  "col5caption"}));
            item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "connserv",
                                                                                                   "caption"})));
            item2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "delserv",
                                                                                                   "caption"})));
            item3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "addserv",
                                                                                                   "caption"})));
            item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "addserv",
                                                                                                   "caption"})));
            TableColumnModel tcm = serverTable.getColumnModel();
            for (int i = 0; i < tcm.getColumnCount(); i++)
            {
                tcm.getColumn(i).setHeaderValue(columns[i]);
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public int[] getColumnWidths(){
        TableColumnModel tcm = serverTable.getColumnModel();
        int[] widths = new int[tcm.getColumnCount()];
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            widths[i] = tcm.getColumn(i).getWidth();
        }
        return widths;
    }
}