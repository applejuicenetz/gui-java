package de.applejuicenet.client.gui;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;

import de.applejuicenet.client.gui.tables.search.SearchResultTableModel;
import de.applejuicenet.client.gui.tables.search.SearchNode;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.shared.Search;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SearchResultPanel.java,v 1.4 2003/10/01 16:52:53 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchResultPanel.java,v $
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

public class SearchResultPanel extends JPanel{
    private Logger logger;
    private JTreeTable searchResultTable;
    private SearchResultTableModel tableModel;
    private Search search;
    private JButton sucheAbbrechen = new JButton();
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

    public SearchResultPanel(Search aSearch) {
        search = aSearch;
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() throws Exception {
        item1.setText(linkLaden);
        sucheAbbrechen.setText(sucheStoppen);

        /**
         * @ToDo: zu implementieren, sobald der Core es kann
         */
        sucheAbbrechen.setEnabled(false);
        sucheAbbrechen.setToolTipText("Der Core unterstützt noch nicht das Abbrechen einer Suche");

        popup.add(item1);
        setLayout(new BorderLayout());
        label1.setText(offeneSuchen.replaceFirst("%i", Integer.toString(search.getOffeneSuchen())));
        label2.setText(gefundeneDateien.replaceFirst("%i", Integer.toString(search.getGefundenDateien())));
        label3.setText(durchsuchteClients.replaceFirst("%i", Integer.toString(search.getDurchsuchteClients())));
        tableModel = new SearchResultTableModel(search);
        searchResultTable = new JTreeTable(tableModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(searchResultTable), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel textPanel = new JPanel(new FlowLayout());
        textPanel.add(label1);
        textPanel.add(label2);
        textPanel.add(label3);

        southPanel.add(sucheAbbrechen, BorderLayout.WEST);
        southPanel.add(textPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        MouseAdapter popupMouseAdapter = new MouseAdapter(){
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me))
                {
                    Point p = me.getPoint();
                    int iRow = searchResultTable.rowAtPoint(p);
                    int iCol = searchResultTable.columnAtPoint(p);
                    if (iRow!=-1 && iCol!=-1){
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
                if (e.isPopupTrigger())
                {
                    int sel = searchResultTable.getSelectedRow();
                    if (sel!=-1){
                        Object result = ((TreeTableModelAdapter) searchResultTable.getModel()).nodeForRow(sel);
                        if (result.getClass()==SearchNode.class ||
                                result.getClass()==Search.SearchEntry.FileName.class){
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
                if (sel!=-1){
                    Object result = ((TreeTableModelAdapter) searchResultTable.getModel()).nodeForRow(sel);
                    if (result.getClass()==SearchNode.class){
                        Search.SearchEntry entry = (Search.SearchEntry)((SearchNode)result).getValueObject();
                        StringBuffer toCopy = new StringBuffer();
                        toCopy.append("ajfsp://file|");
                        toCopy.append(searchResultTable.getValueAt(sel, 0) + "|" + entry.getChecksumme() + "|" + entry.getGroesse() + "/");
                        String link = toCopy.toString();
                        ApplejuiceFassade.getInstance().processLink(link);
                    }
                    else if (result.getClass()==Search.SearchEntry.FileName.class){
                        String dateiname = ((Search.SearchEntry.FileName)result).getDateiName();
                        int i = sel;
                        Object temp = null;
                        while (i>0){
                            i--;
                            temp = ((TreeTableModelAdapter) searchResultTable.getModel()).nodeForRow(i);
                            if (temp.getClass()==SearchNode.class){
                                break;
                            }
                        }
                        if (temp!=null && temp.getClass()==SearchNode.class){
                            Search.SearchEntry entry = (Search.SearchEntry)((SearchNode)temp).getValueObject();
                            StringBuffer toCopy = new StringBuffer();
                            toCopy.append("ajfsp://file|");
                            toCopy.append(dateiname + "|" + entry.getChecksumme() + "|" +
                                          entry.getGroesse() + "/");
                            String link = toCopy.toString();
                            ApplejuiceFassade.getInstance().processLink(link);
                        }
                    }
                }
            }
        });
    }

    public static void setTexte(String[] texte, String[] tableColumns){
        offeneSuchen = texte[0];
        gefundeneDateien = texte[1];
        durchsuchteClients = texte[2];
        linkLaden = texte[3];
        sucheStoppen = texte[4];
        columns = tableColumns;
    }

    public void updateSearchContent(){
        try{
            searchResultTable.updateUI();
            label1.setText(offeneSuchen.replaceFirst("%i", Integer.toString(search.getOffeneSuchen())));
            label2.setText(gefundeneDateien.replaceFirst("%i", Integer.toString(search.getGefundenDateien())));
            label3.setText(durchsuchteClients.replaceFirst("%i", Integer.toString(search.getDurchsuchteClients())));
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void aendereSprache(){
        try{
            item1.setText(linkLaden);
            sucheAbbrechen.setText(sucheStoppen);
            label1.setText(offeneSuchen.replaceFirst("%i", Integer.toString(search.getOffeneSuchen())));
            label2.setText(gefundeneDateien.replaceFirst("%i", Integer.toString(search.getGefundenDateien())));
            label3.setText(durchsuchteClients.replaceFirst("%i", Integer.toString(search.getDurchsuchteClients())));
            TableColumnModel tcm = searchResultTable.getColumnModel();
            for (int i = 0; i < tcm.getColumnCount(); i++) {
                tcm.getColumn(i).setHeaderValue(columns[i]);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }
}
