package de.applejuicenet.client.shared;

import java.util.Vector;
import java.util.HashMap;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/Search.java,v 1.4 2003/12/30 13:08:32 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: Search.java,v $
 * Revision 1.4  2003/12/30 13:08:32  maj0r
 * Suchanzeige korrigiert
 * Es kann passieren, dass nicht alle gefundenen Suchergebnisse beim Core ankommen, die Ausgabe wurde entsprechend korrigiert.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/12/16 14:51:46  maj0r
 * Suche kann nun GUI-seitig abgebrochen werden.
 *
 * Revision 1.1  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 *
 */

public class Search {
    private int id;
    private String suchText;
    private int offeneSuchen;
    private int gefundenDateien;
    private int durchsuchteClients;
    private HashMap searchEntries = new HashMap();

    public static int currentSearchCount = 0;

    public Search(int id) {
        this.id = id;
    }

    public String getSuchText() {
        return suchText;
    }

    public void setSuchText(String suchText) {
        this.suchText = suchText;
    }

    public int getOffeneSuchen() {
        return offeneSuchen;
    }

    public void setOffeneSuchen(int offeneSuchen) {
        this.offeneSuchen = offeneSuchen;
    }

    public int getGefundenDateien() {
        return gefundenDateien;
    }

    public void setGefundenDateien(int gefundenDateien) {
        this.gefundenDateien = gefundenDateien;
    }

    public int getDurchsuchteClients() {
        return durchsuchteClients;
    }

    public void setDurchsuchteClients(int durchsuchteClients) {
        this.durchsuchteClients = durchsuchteClients;
    }

    public int getId() {
        return id;
    }

    public void addSearchEntry(SearchEntry searchEntry){
        MapSetStringKey key = new MapSetStringKey(searchEntry.getId());
        if (!searchEntries.containsKey(key)){
            searchEntries.put(key, searchEntry);
        }
        else{
            SearchEntry searchEntryOld = (SearchEntry)searchEntries.get(key);
            FileName[] fileNames = searchEntry.getFileNames();
            for (int i=0; i<fileNames.length; i++){
                searchEntryOld.addFileName(fileNames[i]);
            }
        }
    }

    public SearchEntry[] getSearchEntries(){
        return (SearchEntry[]) searchEntries.values().toArray(new SearchEntry[searchEntries.size()]);
    }

    public class SearchEntry{
        private int id;
        private String checksumme;
        private long groesse;
        private HashMap fileNames = new HashMap();

        public SearchEntry(int id, String checksumme, long groesse) {
            this.id = id;
            this.checksumme = checksumme;
            this.groesse = groesse;
        }

        public int getId() {
            return id;
        }

        public String getChecksumme() {
            return checksumme;
        }

        public long getGroesse() {
            return groesse;
        }

        public void addFileName(FileName fileName){
            MapSetStringKey key = new MapSetStringKey(fileName.getDateiName());
            if (!fileNames.containsKey(key)){
                fileNames.put(key, fileName);
            }
            else{
                FileName fileNameOld = (FileName)fileNames.get(key);
                fileNameOld.setHaeufigkeit(fileName.getHaeufigkeit());
            }
        }

        public FileName[] getFileNames(){
            return (FileName[]) fileNames.values().toArray(new FileName[fileNames.size()]);
        }

        public class FileName{
            private String dateiName;
            private int haeufigkeit;

            public FileName(String dateiName, int haeufigkeit) {
                this.dateiName = dateiName;
                this.haeufigkeit = haeufigkeit;
            }

            public String getDateiName() {
                return dateiName;
            }

            public int getHaeufigkeit() {
                return haeufigkeit;
            }

            public void setHaeufigkeit(int haeufigkeit){
                this.haeufigkeit = haeufigkeit;
            }
        }
    }
}
