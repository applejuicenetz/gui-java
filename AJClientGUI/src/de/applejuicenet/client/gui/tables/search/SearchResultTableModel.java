package de.applejuicenet.client.gui.tables.search;

import de.applejuicenet.client.shared.Search;

import javax.swing.table.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/search/Attic/SearchResultTableModel.java,v 1.2 2003/09/30 16:35:11 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchResultTableModel.java,v $
 * Revision 1.2  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SearchResultTableModel
        extends AbstractTableModel {
    final static String[] COL_NAMES = {
        "Dateiname", "Größe", "Anzahl"};

    private Search search;

    public SearchResultTableModel(Search aSearch) {
        super();
        search = aSearch;
    }

    public Object getRow(int row) {
        if ((search != null))
        {
            int anzahl=0;
            Search.SearchEntry[] searchEntries = search.getSearchEntries();
            for (int i=0; i<searchEntries.length; i++){
                int x = searchEntries[i].getFileNames().length;
                if (anzahl+x>=row){
                    if (row==0){
                        return searchEntries[i].getFileNames()[0];
                    }
                    return searchEntries[i].getFileNames()[row-anzahl-1];
                }
                anzahl+=x;
            }
        }
        return null;
    }

    public Object getValueAt(int row, int column) {
        if (search == null)
        {
            return "";
        }
        Search.SearchEntry.FileName result = (Search.SearchEntry.FileName)getRow(row);
        if (result==null){
            return "";
        }

        if (result == null)
        {
            return "";
        }

        String s = new String("");
        switch (column)
        {
            case 0:
                return result.getDateiName();
            case 1:
                return "";
            case 2:
                return Integer.toString(result.getHaeufigkeit());
            default:
                s = new String("Fehler");
        }
        if (s == null)
        {
            s = "";
        }
        return s;
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public int getRowCount() {
        if ((search != null))
        {
            int anzahl=0;
            Search.SearchEntry[] searchEntries = search.getSearchEntries();
            for (int i=0; i<searchEntries.length; i++){
                anzahl += searchEntries[i].getFileNames().length;
            }
            return anzahl;
        }
        return 0;
    }

    public Class getClass(int index) {
        return String.class;
    }

    public void updateTable() {
        this.fireTableDataChanged();
    }
}