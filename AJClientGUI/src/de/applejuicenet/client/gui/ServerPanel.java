package de.applejuicenet.client.gui;

import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
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
import de.applejuicenet.client.gui.shared.HeaderListener;
import de.applejuicenet.client.gui.shared.SortButtonRenderer;
import de.applejuicenet.client.gui.tables.server.ServerTableCellRenderer;
import de.applejuicenet.client.gui.tables.server.ServerTableModel;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ServerDO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import de.applejuicenet.client.shared.Information;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ServerPanel.java,v 1.46 2004/01/08 07:48:22 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ServerPanel.java,v $
 * Revision 1.46  2004/01/08 07:48:22  maj0r
 * Wenn das Panel nicht selektiert ist, wird die Tabelle nun nicht mehr aktualisiert.
 *
 * Revision 1.45  2004/01/07 16:15:20  maj0r
 * Warnmeldung bezueglich 30-Minuten-Sperre bei manuellem Serverwechsel eingebaut.
 *
 * Revision 1.44  2004/01/02 16:48:30  maj0r
 * Serverliste holen geaendert.
 *
 * Revision 1.43  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.42  2003/12/29 09:39:21  maj0r
 * Alte BugIDs entfernt, da auf neuen Bugtracker auf bugs.applejuicenet.de umgestiegen wurde.
 *
 * Revision 1.41  2003/12/27 14:02:15  maj0r
 * Legende fuer die Servertabelle eingebaut (Danke an muhviestarr).
 *
 * Revision 1.40  2003/12/26 19:26:40  maj0r
 * Gridlines werden nun in der Servertabelle nicht mehr angezeigt (Danke an muhviestarr).
 *
 * Revision 1.39  2003/12/17 16:42:16  maj0r
 * Verhalten des Popupmenues der Servertabelle ueberarbeitet.
 * Muell entfernt.
 *
 * Revision 1.38  2003/12/17 11:06:29  maj0r
 * RegisterI erweitert, um auf Verlassen eines Tabs reagieren zu koennen.
 *
 * Revision 1.37  2003/12/05 11:18:02  maj0r
 * Workaround fürs Setzen der Hintergrundfarben der Scrollbereiche ausgebaut.
 *
 * Revision 1.36  2003/11/30 17:01:33  maj0r
 * Hintergrundfarbe aller Scrollbereiche an ihre Tabellen angepasst.
 *
 * Revision 1.35  2003/10/31 11:31:45  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt. Kommen noch mehr.
 *
 * Revision 1.34  2003/10/18 19:19:26  maj0r
 * Mehrfachselektion in der Servertabelle nun moeglich.
 *
 * Revision 1.33  2003/10/06 12:08:36  maj0r
 * Server holen in Thread ausgelagert.
 *
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
    private JButton sucheServer = new JButton();
    private JPopupMenu popup = new JPopupMenu();
    private JPopupMenu popup2 = new JPopupMenu();
    private JPopupMenu popup3 = new JPopupMenu();
    private JMenuItem item1;
    private JMenuItem item2;
    private JMenuItem item3;
    private JMenuItem item4;
    private JMenuItem item5;
    private JMenuItem item6;
    private JLabel verbunden = new JLabel();
    private JLabel versucheZuVerbinden = new JLabel();
    private JLabel aelter24h = new JLabel();
    private JLabel juenger24h = new JLabel();
    private Logger logger;
    private boolean initizialiced = false;
    private String warnungTitel = "";
    private String warnungNachricht = "";

    public ServerPanel() {
        _this = this;
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        LanguageSelector.getInstance().addLanguageListener(this);

        sucheServer.setForeground(Color.BLUE);
        item1 = new JMenuItem("Verbinden");
        item5 = new JMenuItem("Hinzufügen");
        item2 = new JMenuItem("Löschen");
        item3 = new JMenuItem("Hinzufügen");
        item4 = new JMenuItem("Hinzufügen");
        item6 = new JMenuItem("Löschen");
        popup.add(item1);
        popup.add(item4);
        popup.add(item2);
        popup2.add(item3);
        popup3.add(item5);
        popup3.add(item6);
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int selected = serverTable.getSelectedRow();
                ServerDO server = (ServerDO) ( (ServerTableModel) serverTable.
                                              getModel()).
                    getRow(selected);
                if (ApplejuiceFassade.getInstance().getInformation().getVerbindungsStatus() == Information.VERBUNDEN){
                    int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.getApp(), warnungNachricht, warnungTitel,
                                  JOptionPane.YES_NO_OPTION);
                    if (result != JOptionPane.YES_OPTION){
                        return;
                    }
                }
                ApplejuiceFassade.getInstance().connectToServer(server.
                    getID());
                SoundPlayer.getInstance().playSound(SoundPlayer.VERBINDEN);
            }
        });
        ActionListener loescheServerListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int selected[] = serverTable.getSelectedRows();
                if (selected.length > 0) {
                    ServerDO server = null;
                    for (int i = 0; i < selected.length; i++) {
                        server = (ServerDO) ( (ServerTableModel) serverTable.
                                             getModel()).
                            getRow(selected[i]);
                        if (server != null) {
                            ApplejuiceFassade.getInstance().entferneServer(
                                server.getID());
                        }
                    }
                }
            }
        };
        item2.addActionListener(loescheServerListener);
        item6.addActionListener(loescheServerListener);
        ActionListener newServerListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                NewServerDialog newServerDialog = new NewServerDialog(
                    AppleJuiceDialog.getApp(), true);
                Dimension appDimension = newServerDialog.getSize();
                Dimension screenSize = Toolkit.getDefaultToolkit().
                    getScreenSize();
                newServerDialog.setLocation( (screenSize.width -
                                              appDimension.width) / 2,
                                            (screenSize.height -
                                             appDimension.height) / 2);
                newServerDialog.show();
                if (newServerDialog.isLegal()) {
                    ApplejuiceFassade.getInstance().processLink(newServerDialog.
                        getLink());
                }
            }
        };
        item3.addActionListener(newServerListener);
        item4.addActionListener(newServerListener);
        item5.addActionListener(newServerListener);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;

        sucheServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Thread worker = new Thread() {
                    public void run() {
                        ApplejuiceFassade af = ApplejuiceFassade.getInstance();
                        String[] server = af.getNetworkKnownServers();
                        if (server == null || server.length == 0){
                            return;
                        }
                        for (int i = 0; i < server.length; i++) {
                            af.processLink(server[i]);
                        }
                    }
                };
                worker.start();
            }
        });
        panel1.add(sucheServer, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(new JLabel(), constraints);
        add(panel1, BorderLayout.NORTH);
        serverTable = new JTable();
        serverTable.setModel(new ServerTableModel());
        serverTable.setShowGrid(false);
        serverTable.setSelectionMode(ListSelectionModel.
                                     MULTIPLE_INTERVAL_SELECTION);
        SortButtonRenderer renderer = new SortButtonRenderer();
        TableColumnModel model = serverTable.getColumnModel();
        int n = model.getColumnCount();
        for (int i = 0; i < n; i++) {
            model.getColumn(i).setHeaderRenderer(renderer);
            model.getColumn(i).setPreferredWidth(model.getColumn(i).getWidth());
        }

        JTableHeader header = serverTable.getTableHeader();
        header.addMouseListener(new HeaderListener(header, renderer));

        TableColumn tc = serverTable.getColumnModel().getColumn(0);
        tc.setCellRenderer(new ServerTableCellRenderer());
        final JScrollPane aScrollPane = new JScrollPane(serverTable);
        aScrollPane.setBackground(serverTable.getBackground());
        serverTable.getTableHeader().setBackground(serverTable.getBackground());
        aScrollPane.getViewport().setOpaque(false);
        MouseAdapter popupMouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                super.mouseReleased(me);
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int selectedRow = serverTable.rowAtPoint(e.getPoint());
                    if (selectedRow != -1) {
                        if (serverTable.getSelectedRowCount() == 0) {
                            serverTable.setRowSelectionInterval(selectedRow,
                                selectedRow);
                        }
                        else {
                            int[] currentSelectedRows = serverTable.
                                getSelectedRows();
                            for (int i = 0; i < currentSelectedRows.length; i++) {
                                if (currentSelectedRows[i] == selectedRow) {
                                    selectedRow = -1;
                                    break;
                                }
                            }
                            if (selectedRow != -1) {
                                serverTable.setRowSelectionInterval(selectedRow,
                                    selectedRow);
                            }
                        }
                    }
                    if (serverTable.getSelectedRowCount() == 1) {
                        popup.show(aScrollPane, e.getX(), e.getY());
                    }
                    else if (serverTable.getSelectedRowCount() > 1) {
                        popup3.show(aScrollPane, e.getX(), e.getY());
                    }
                    else {
                        popup2.show(aScrollPane, e.getX(), e.getY());
                    }
                }
            }
        };
        aScrollPane.addMouseListener(popupMouseAdapter);
        serverTable.addMouseListener(popupMouseAdapter);
        add(aScrollPane, BorderLayout.CENTER);
        JPanel legende = new JPanel(new FlowLayout());
        IconManager im = IconManager.getInstance();
        ImageIcon icon1 = im.getIcon("serververbunden");
        ImageIcon icon2 = im.getIcon("serverversuche");
        ImageIcon icon3 = im.getIcon("aelter24h");
        ImageIcon icon4 = im.getIcon("juenger24h");
        JLabel label1 = new JLabel(icon1);
        JLabel label2 = new JLabel(icon2);
        JLabel label3 = new JLabel(icon3);
        JLabel label4 = new JLabel(icon4);
        legende.add(label1);
        legende.add(verbunden);
        legende.add(label2);
        legende.add(versucheZuVerbinden);
        legende.add(label3);
        legende.add(aelter24h);
        legende.add(label4);
        legende.add(juenger24h);
        add(legende, BorderLayout.SOUTH);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
            DataUpdateListener.SERVER_CHANGED);
    }

    public void registerSelected() {
        try {
            if (!initizialiced) {
                initizialiced = true;
                TableColumnModel headerModel = serverTable.getTableHeader().
                    getColumnModel();
                int columnCount = headerModel.getColumnCount();
                PositionManager pm = PropertiesManager.getPositionManager();
                if (pm.isLegal()) {
                    int[] widths = pm.getServerWidths();
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(widths[i]);
                    }
                }
                else {
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(serverTable.
                            getWidth() / columnCount);
                    }
                }
                serverTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.SERVER_CHANGED) {
                int[] selected = serverTable.getSelectedRows();
                ( (ServerTableModel) serverTable.getModel()).setTable( (HashMap)
                    content);
                if (selected.length != 0) {
                    for (int i = 0; i < selected.length; i++) {
                        serverTable.getSelectionModel().addSelectionInterval(
                            selected[i], selected[i]);
                    }
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            sucheServer.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                 getFirstAttrbuteByTagName(new String[]{"mainform", "Label11",
                 "caption"})));
            String[] columns = new String[4];
            columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "serverlist",
                                          "col0caption"}));
            columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "serverlist",
                                          "col1caption"}));
            columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "serverlist",
                                          "col3caption"}));
            columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "serverlist",
                                          "col5caption"}));
            item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "connserv",
                                          "caption"})));
            item2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "delserv",
                                          "caption"})));
            item3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "addserv",
                                          "caption"})));
            item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "addserv",
                                          "caption"})));
            item5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "addserv",
                                          "caption"})));
            item6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "delserv",
                                          "caption"})));
            verbunden.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "serverform",
                                          "verbunden"})));
            versucheZuVerbinden.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "serverform",
                                          "verbinden"})));
            aelter24h.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "serverform",
                                          "aelter24h"})));
            juenger24h.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "serverform",
                                          "juenger24h"})));
            warnungTitel = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "caption"}));
            warnungNachricht = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "serverform",
                                          "warnungnachricht"}));

            TableColumnModel tcm = serverTable.getColumnModel();
            for (int i = 0; i < tcm.getColumnCount(); i++) {
                tcm.getColumn(i).setHeaderValue(columns[i]);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public int[] getColumnWidths() {
        TableColumnModel tcm = serverTable.getColumnModel();
        int[] widths = new int[tcm.getColumnCount()];
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            widths[i] = tcm.getColumn(i).getWidth();
        }
        return widths;
    }

    public void lostSelection() {
    }
}