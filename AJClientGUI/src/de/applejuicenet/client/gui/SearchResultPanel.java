package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.shared.SortButtonRenderer;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.search.SearchNode;
import de.applejuicenet.client.gui.tables.search.SearchResultTableModel;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.Search.SearchEntry;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SearchResultPanel.java,v 1.13 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SearchResultPanel.java,v $
 * Revision 1.13  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.12  2004/01/25 08:31:11  maj0r
 * Icons eingebaut.
 *
 * Revision 1.11  2004/01/12 14:20:31  maj0r
 * Sortierung eingebaut.
 *
 * Revision 1.10  2004/01/08 07:47:11  maj0r
 * 98%-CPU-Last Bug durch Suche gefixt.
 *
 * Revision 1.9  2004/01/07 14:51:39  maj0r
 * Bug #23 gefixt (Danke an computer.ist.org)
 * Suche laesst sich nun korrekt abrechen.
 *
 * Revision 1.8  2003/12/30 13:08:32  maj0r
 * Suchanzeige korrigiert
 * Es kann passieren, dass nicht alle gefundenen Suchergebnisse beim Core ankommen, die Ausgabe wurde entsprechend korrigiert.
 *
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/12/16 14:51:46  maj0r
 * Suche kann nun GUI-seitig abgebrochen werden.
 *
 * Revision 1.5  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.4  2003/10/01 16:52:53  maj0r
 * Suche weiter gefuehrt.
 * Version 0.32
 *
 * Revision 1.3  2003/10/01 14:45:40  maj0r
 * Suche fortgesetzt.
 *
 * Revision 1.2  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 * Revision 1.1  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 *
 */

public class SearchResultPanel
    extends JPanel {
    private Logger logger;
    private JTreeTable searchResultTable;
    private SearchResultTableModel tableModel;
    private Search search;
    private JButton sucheAbbrechen = new JButton();
    private JButton schliessen = new JButton("X");
    private static String offeneSuchen = "%i offene Suchen";
    private static String gefundeneDateien = "%i gefundene Dateien";
    private static String durchsuchteClients = "%i durchsuchte Clients";
    private static String linkLaden = "Link";
    private static String sucheStoppen = "Suche stoppen";
    private static String[] columns;
    private JLabel label1 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label3 = new JLabel();
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem item1 = new JMenuItem();
    private boolean activeSearch = true;
    private SearchPanel parentSearchPanel;
    private int searchHitsCount;

    private TableColumn[] tableColumns = new TableColumn[3];

    public SearchResultPanel(Search aSearch, SearchPanel parent) {
        search = aSearch;
        parentSearchPanel = parent;
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

    public Search getSearch() {
        return search;
    }

    public void setActiveSearch(boolean active) {
        activeSearch = active;
        schliessen.setEnabled(!active);
        sucheAbbrechen.setEnabled(active);
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();
        item1.setText(linkLaden);
        item1.setIcon(im.getIcon("download"));
        sucheAbbrechen.setText(sucheStoppen);
        sucheAbbrechen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sucheAbbrechen.setEnabled(false);
                ApplejuiceFassade.getInstance().cancelSearch(search.getId());
            }
        });
        schliessen.setEnabled(false);
        schliessen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        parentSearchPanel.close(SearchResultPanel.this);
                    }
                });
            }
        });

        popup.add(item1);
        setLayout(new BorderLayout());
        updateZahlen();
        tableModel = new SearchResultTableModel(search);
        searchResultTable = new JTreeTable(tableModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(searchResultTable), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel textPanel = new JPanel(new FlowLayout());
        textPanel.add(label1);
        textPanel.add(label2);
        textPanel.add(label3);

        JPanel panel1 = new JPanel(new FlowLayout());
        panel1.add(schliessen);
        panel1.add(sucheAbbrechen);
        southPanel.add(panel1, BorderLayout.WEST);
        southPanel.add(textPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        MouseAdapter popupMouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    Point p = me.getPoint();
                    int iRow = searchResultTable.rowAtPoint(p);
                    int iCol = searchResultTable.columnAtPoint(p);
                    if (iRow != -1 && iCol != -1) {
                        searchResultTable.setRowSelectionInterval(iRow, iRow);
                        searchResultTable.setColumnSelectionInterval(iCol, iCol);
                    }
                }
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int sel = searchResultTable.getSelectedRow();
                    if (sel != -1) {
                        Object result = ( (TreeTableModelAdapter)
                                         searchResultTable.getModel()).
                            nodeForRow(sel);
                        if (result.getClass() == SearchNode.class ||
                            result.getClass() == Search.SearchEntry.FileName.class) {
                            popup.show(searchResultTable, e.getX(), e.getY());
                        }
                    }
                }
            }
        };
        searchResultTable.addMouseListener(popupMouseAdapter);
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int sel = searchResultTable.getSelectedRow();
                if (sel != -1) {
                    Object result = ( (TreeTableModelAdapter) searchResultTable.
                                     getModel()).nodeForRow(sel);
                    if (result.getClass() == SearchNode.class) {
                        Search.SearchEntry entry = (Search.SearchEntry) ( (
                            SearchNode) result).getValueObject();
                        StringBuffer toCopy = new StringBuffer();
                        toCopy.append("ajfsp://file|");
                        toCopy.append(searchResultTable.getValueAt(sel, 0) +
                                      "|" + entry.getChecksumme() + "|" +
                                      entry.getGroesse() + "/");
                        String link = toCopy.toString();
                        ApplejuiceFassade.getInstance().processLink(link);
                    }
                    else if (result.getClass() == Search.SearchEntry.FileName.class) {
                        String dateiname = ( (Search.SearchEntry.FileName)
                                            result).getDateiName();
                        int i = sel;
                        Object temp = null;
                        while (i > 0) {
                            i--;
                            temp = ( (TreeTableModelAdapter) searchResultTable.
                                    getModel()).nodeForRow(i);
                            if (temp.getClass() == SearchNode.class) {
                                break;
                            }
                        }
                        if (temp != null && temp.getClass() == SearchNode.class) {
                            Search.SearchEntry entry = (Search.SearchEntry) ( (
                                SearchNode) temp).getValueObject();
                            StringBuffer toCopy = new StringBuffer();
                            toCopy.append("ajfsp://file|");
                            toCopy.append(dateiname + "|" + entry.getChecksumme() +
                                          "|" +
                                          entry.getGroesse() + "/");
                            String link = toCopy.toString();
                            ApplejuiceFassade.getInstance().processLink(link);
                        }
                    }
                }
            }
        });
        TableColumnModel model = searchResultTable.getColumnModel();
        SortButtonRenderer renderer = new SortButtonRenderer();
        for (int i = 0; i < tableColumns.length; i++) {
            tableColumns[i] = model.getColumn(i);
            tableColumns[i].setHeaderRenderer(renderer);
        }
        JTableHeader header = searchResultTable.getTableHeader();
        header.addMouseListener(new SortMouseAdapter(header, renderer));
    }

    public static void setTexte(String[] texte, String[] tableColumns) {
        offeneSuchen = texte[0];
        gefundeneDateien = texte[1];
        durchsuchteClients = texte[2];
        linkLaden = texte[3];
        sucheStoppen = texte[4];
        columns = tableColumns;
    }

    public void updateSearchContent() {
        try {
            if (search.isChanged()) {
                searchResultTable.updateUI();
                updateZahlen();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void updateZahlen() {
        SearchEntry[] searchEntries = search.getSearchEntries();
        searchHitsCount = 0;
        for (int i = 0; i < searchEntries.length; i++) {
            searchHitsCount += searchEntries[i].getFileNames().length;
        }
        label1.setText(offeneSuchen.replaceFirst("%i",
                                                 Integer.toString(search.getOffeneSuchen())));
        label2.setText(gefundeneDateien.replaceFirst("%i",
            Integer.toString(searchHitsCount)));
        label3.setText(durchsuchteClients.replaceFirst("%i",
            Integer.toString(search.getDurchsuchteClients())));
    }

    public void aendereSprache() {
        try {
            item1.setText(linkLaden);
            sucheAbbrechen.setText(sucheStoppen);
            label1.setText(offeneSuchen.replaceFirst("%i",
                Integer.toString(search.getOffeneSuchen())));
            label2.setText(gefundeneDateien.replaceFirst("%i",
                Integer.toString(search.getGefundenDateien())));
            label3.setText(durchsuchteClients.replaceFirst("%i",
                Integer.toString(search.getDurchsuchteClients())));
            TableColumnModel tcm = searchResultTable.getColumnModel();
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

    class SortMouseAdapter
        extends MouseAdapter {
        private JTableHeader header;
        private SortButtonRenderer renderer;

        public SortMouseAdapter(JTableHeader header,
                                SortButtonRenderer renderer) {
            this.header = header;
            this.renderer = renderer;
        }

        public void mousePressed(MouseEvent e) {
            if (e.getButton() != MouseEvent.BUTTON1) {
                return;
            }
            int col = header.columnAtPoint(e.getPoint());
            TableColumn pressedColumn = searchResultTable.getColumnModel().
                getColumn(col);
            renderer.setPressedColumn(col);
            renderer.setSelectedColumn(col);
            header.repaint();

            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }

            boolean isAscent;
            if (SortButtonRenderer.UP == renderer.getState(col)) {
                isAscent = true;
            }
            else {
                isAscent = false;
            }

            SearchNode rootNode = ( (SearchNode) tableModel.
                                   getRoot());

            if (pressedColumn == tableColumns[0]) {
                rootNode.setSortCriteria(SearchNode.SORT_FILENAME,
                                         isAscent);
            }
            else if (pressedColumn == tableColumns[1]) {
                rootNode.setSortCriteria(SearchNode.SORT_GROESSE,
                                         isAscent);
            }
            else if (pressedColumn == tableColumns[2]) {
                rootNode.setSortCriteria(SearchNode.SORT_ANZAHL,
                                         isAscent);
            }
            searchResultTable.updateUI();
        }

        public void mouseReleased(MouseEvent e) {
            renderer.setPressedColumn( -1);
            header.repaint();
        }
    }
}
