package de.applejuicenet.client.fassade.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.applejuicenet.client.fassade.shared.Search.SearchEntry.FileName;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/shared/Attic/Search.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class Search {
    private int id;
    private String suchText;
    private int offeneSuchen;
    private int gefundenDateien;
    private int durchsuchteClients;
    private Map mapping = new HashMap();
    private List entries = new ArrayList();
    private boolean changed = true;
    private long creationTime;
    private boolean running;

    public static int currentSearchCount = 0;

    private Set filter = new HashSet();
    
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

    public void addFilter(String newFilter){
        filter.add(newFilter);
        if (filter.size()==FileTypeHelper.getAllTypes().length){
            clearFilter();
        }
    }

    public void removeFilter(String newFilter){
        if (filter.size()==0){
        	String[] allTypes = FileTypeHelper.getAllTypes();
            for (int i=0; i<allTypes.length; i++){
                filter.add(allTypes[i]);
            }
        }
        filter.remove(newFilter);
    }

    public void clearFilter(){
        filter.clear();
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

    public SearchEntry[] getAllSearchEntries() {
        return (SearchEntry[]) entries.toArray(new SearchEntry[entries.size()]);
    }

    public SearchEntry getSearchEntryById(int id){
        SearchEntry searchEntry;
        for (int i=0; i<entries.size(); i++){
            searchEntry = (SearchEntry)entries.get(i);
            if (searchEntry.getId() == id){
                return searchEntry;
            }
        }
        return null;
    }

    public SearchEntry[] getSearchEntries() {
        if (filter.size() == 0){
            return (SearchEntry[]) entries.toArray(new SearchEntry[entries.size()]);
        }
        ArrayList neededEntries = new ArrayList();
        for (int i=0; i<entries.size(); i++){
            if (!filter.contains(((SearchEntry)entries.get(i)).getFileType())){
                neededEntries.add(entries.get(i));
            }
        }
        return (SearchEntry[]) neededEntries.toArray(new SearchEntry[neededEntries.size()]);
    }

    public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	public class SearchEntry {
        private int id;
        private int searchId;
        private String checksumme;
        private long groesse;
        private String groesseAsString = null;
        private List fileNames = new ArrayList();
        private Set keys = new HashSet();

        private String type = FileTypeHelper.TYPE_UNKNOWN;

        public SearchEntry(int id, int searchId, String checksumme, long groesse) {
            this.id = id;
            this.searchId = searchId;
            this.checksumme = checksumme;
            this.groesse = groesse;
        }

        public int getId() {
            return id;
        }

        public String getFileType() {
            return type;
        }

        public int getSearchId(){
            return searchId;
        }

        public String getChecksumme() {
            return checksumme;
        }

        public long getGroesse() {
            return groesse;
        }

        private void recalculatePossibleFileType(){
            FileName[] fileNames = getFileNames();
            int pdf = 0;
            int image = 0;
            int movie = 0;
            int iso = 0;
            int text = 0;
            int sound = 0;
            int archive = 0;
            int currentMax = 0;
            for (int i=0; i<fileNames.length; i++){
                String fileNameType = fileNames[i].getFileType();
                if (fileNameType.equals(FileTypeHelper.TYPE_UNKNOWN)){
                    continue;
                }
                else if (fileNameType.equals(FileTypeHelper.TYPE_PDF)){
                    pdf++;
                    if (pdf>currentMax){
                        currentMax = pdf;
                        type = FileTypeHelper.TYPE_PDF;
                    }
                }
                else if (fileNameType.equals(FileTypeHelper.TYPE_IMAGE)){
                    image++;
                    if (image>currentMax){
                        currentMax = image;
                        type = FileTypeHelper.TYPE_IMAGE;
                    }
                }
                else if (fileNameType.equals(FileTypeHelper.TYPE_MOVIE)){
                    movie++;
                    if (movie>currentMax){
                        currentMax = movie;
                        type = FileTypeHelper.TYPE_MOVIE;
                    }
                }
                else if (fileNameType.equals(FileTypeHelper.TYPE_ISO)){
                    iso++;
                    if (iso>currentMax){
                        currentMax = iso;
                        type = FileTypeHelper.TYPE_ISO;
                    }
                }
                else if (fileNameType.equals(FileTypeHelper.TYPE_TEXT)){
                    text++;
                    if (text>currentMax){
                        currentMax = text;
                        type = FileTypeHelper.TYPE_TEXT;
                    }
                }
                else if (fileNameType.equals(FileTypeHelper.TYPE_SOUND)){
                    sound++;
                    if (sound>currentMax){
                        currentMax = sound;
                        type = FileTypeHelper.TYPE_SOUND;
                    }
                }
                else if (fileNameType.equals(FileTypeHelper.TYPE_ARCHIVE)){
                    archive++;
                    if (archive>currentMax){
                        currentMax = archive;
                        type = FileTypeHelper.TYPE_ARCHIVE;
                    }
                }
            }
            if (pdf == image && movie == iso && image == movie
                && movie == text && text == sound && text == archive){
                    type = FileTypeHelper.TYPE_UNKNOWN;
            }
        }

        public void addFileName(FileName fileName) {
            String key = fileName.getDateiName();
            if (!keys.contains(key)) {
                keys.add(key);
                fileNames.add(fileName);
                recalculatePossibleFileType();
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
            private String fileType = FileTypeHelper.TYPE_UNKNOWN;

            public FileName(String dateiName, int haeufigkeit) {
                this.dateiName = dateiName;
                this.haeufigkeit = haeufigkeit;
                fileType = FileTypeHelper.calculatePossibleFileType(dateiName);
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

            public String getFileType(){
                return fileType;
            }
        }
    }
}
