package de.applejuicenet.client.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/Search.java,v 1.8 2004/02/12 21:16:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: Search.java,v $
 * Revision 1.8  2004/02/12 21:16:51  maj0r
 * Bug #23 gefixt (Danke an computer.ist.org)
 * Suche abbrechen korrigiert.
 *
 * Revision 1.7  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.6  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.5  2004/01/08 07:47:17  maj0r
 * 98%-CPU-Last Bug durch Suche gefixt.
 *
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
    private HashMap mapping = new HashMap();
    private ArrayList entries = new ArrayList();
    private boolean changed = true;
    private long creationTime;

    public static int currentSearchCount = 0;

    public Search(int id) {
        this.id = id;
        creationTime = System.currentTimeMillis();
    }

    public long getCreationTime(){
        return creationTime;
    }

    public boolean isChanged() {
        return changed;
    }

    public synchronized void setChanged(boolean changed) {
        this.changed = changed;
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

    public void addSearchEntry(SearchEntry searchEntry) {
        String key = Integer.toString(searchEntry.getId());
        if (!mapping.containsKey(key)) {
            mapping.put(key, searchEntry);
            entries.add(searchEntry);
            setChanged(true);
        }
        else {
            SearchEntry oldSearchEntry = (SearchEntry) mapping.get(key);
            FileName[] fileNames = searchEntry.getFileNames();
            for (int i = 0; i < fileNames.length; i++) {
                oldSearchEntry.addFileName(fileNames[i]);
            }
        }
    }

    public SearchEntry[] getSearchEntries() {
        return (SearchEntry[]) entries.toArray(new SearchEntry[entries.size()]);
    }

    public class SearchEntry {
        private int id;
        private String checksumme;
        private long groesse;
        private String groesseAsString = null;
        private ArrayList fileNames = new ArrayList();
        private HashSet keys = new HashSet();

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

        public String getGroesseAsString() {
            if (groesseAsString == null) {
                groesseAsString = DownloadModel.parseGroesse(groesse);
            }
            return groesseAsString;
        }

        public void addFileName(FileName fileName) {
            String key = fileName.getDateiName();
            if (!keys.contains(key)) {
                keys.add(key);
                fileNames.add(fileName);
                setChanged(true);
            }
            else {
                FileName oldFileName = null;
                int size = fileNames.size();
                for (int i = 0; i < size; i++) {
                    oldFileName = (FileName) fileNames.get(i);
                    if (oldFileName.getDateiName().compareToIgnoreCase(fileName.
                        getDateiName()) == 0) {
                        oldFileName.setHaeufigkeit(fileName.getHaeufigkeit());
                    }
                }
            }
        }

        public FileName[] getFileNames() {
            return (FileName[]) fileNames.toArray(new FileName[fileNames.size()]);
        }

        public class FileName {
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

            public void setHaeufigkeit(int haeufigkeit) {
                this.haeufigkeit = haeufigkeit;
            }
        }
    }
}
