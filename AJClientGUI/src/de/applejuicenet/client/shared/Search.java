package de.applejuicenet.client.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.Icon;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/Search.java,v 1.14 2004/02/29 19:40:10 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: Search.java,v $
 * Revision 1.14  2004/02/29 19:40:10  maj0r
 * Dateityp Archiv hinzugefuegt.
 *
 * Revision 1.13  2004/02/28 15:05:54  maj0r
 * Da hatte ich in der vorherigen Version Mist eingecheckt.
 *
 * Revision 1.11  2004/02/27 20:39:24  maj0r
 * Icons weiter ausgebaut.
 *
 * Revision 1.10  2004/02/27 16:48:27  maj0r
 * Suchergebnisse werden nun, wenn moeglich mit einem sprechenden Icon angezeigt.
 *
 * Revision 1.9  2004/02/18 17:24:21  maj0r
 * Von DOM auf SAX umgebaut.
 *
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

    private HashSet filter = new HashSet();

    public static final String TYPE_PDF = "pdf";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_MOVIE = "movie";
    public static final String TYPE_ISO = "iso";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_SOUND = "sound";
    public static final String TYPE_ARCHIVE = "archive";
    public static final String TYPE_UNKNOWN = "treeRoot";

    public static final String[] allTypes = new String[]
        {TYPE_PDF, TYPE_IMAGE, TYPE_MOVIE, TYPE_ISO, TYPE_TEXT, TYPE_SOUND, TYPE_ARCHIVE, TYPE_UNKNOWN};

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
        if (filter.size()==allTypes.length){
            clearFilter();
        }
    }

    public void removeFilter(String newFilter){
        if (filter.size()==0){
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

    public class SearchEntry {
        private int id;
        private int searchId;
        private String checksumme;
        private long groesse;
        private String groesseAsString = null;
        private ArrayList fileNames = new ArrayList();
        private HashSet keys = new HashSet();

        private String type = TYPE_UNKNOWN;

        public SearchEntry(int id, int searchId, String checksumme, long groesse) {
            this.id = id;
            this.searchId = searchId;
            this.checksumme = checksumme;
            this.groesse = groesse;
        }

        public int getId() {
            return id;
        }

        public Icon getTypeIcon(){
            return IconManager.getInstance().getIcon(type);
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

        public String getGroesseAsString() {
            if (groesseAsString == null) {
                groesseAsString = DownloadModel.parseGroesse(groesse);
            }
            return groesseAsString;
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
                if (fileNameType.equals(TYPE_UNKNOWN)){
                    continue;
                }
                else if (fileNameType.equals(TYPE_PDF)){
                    pdf++;
                    if (pdf>currentMax){
                        currentMax = pdf;
                        type = TYPE_PDF;
                    }
                }
                else if (fileNameType.equals(TYPE_IMAGE)){
                    image++;
                    if (image>currentMax){
                        currentMax = image;
                        type = TYPE_IMAGE;
                    }
                }
                else if (fileNameType.equals(TYPE_MOVIE)){
                    movie++;
                    if (movie>currentMax){
                        currentMax = movie;
                        type = TYPE_MOVIE;
                    }
                }
                else if (fileNameType.equals(TYPE_ISO)){
                    iso++;
                    if (iso>currentMax){
                        currentMax = iso;
                        type = TYPE_ISO;
                    }
                }
                else if (fileNameType.equals(TYPE_TEXT)){
                    text++;
                    if (text>currentMax){
                        currentMax = text;
                        type = TYPE_TEXT;
                    }
                }
                else if (fileNameType.equals(TYPE_SOUND)){
                    sound++;
                    if (sound>currentMax){
                        currentMax = sound;
                        type = TYPE_SOUND;
                    }
                }
                else if (fileNameType.equals(TYPE_ARCHIVE)){
                    archive++;
                    if (archive>currentMax){
                        currentMax = archive;
                        type = TYPE_ARCHIVE;
                    }
                }
            }
            if (pdf == image && movie == iso && image == movie
                && movie == text && text == sound && text == archive){
                    type = TYPE_UNKNOWN;
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

        public class FileName implements Node{
            private String dateiName;
            private int haeufigkeit;
            private String fileType = TYPE_UNKNOWN;

            public FileName(String dateiName, int haeufigkeit) {
                this.dateiName = dateiName;
                this.haeufigkeit = haeufigkeit;
                calculatePossibleFileType();
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

            public Icon getConvenientIcon() {
                return IconManager.getInstance().getIcon(fileType);
            }

            private void calculatePossibleFileType(){
                String lower = dateiName.toLowerCase();
                if (lower.endsWith(".pdf")){
                    fileType = TYPE_PDF;
                }
                else if (lower.endsWith(".bmp") || lower.endsWith(".jpg")
                         || lower.endsWith(".tif") || lower.endsWith(".png")
                         || lower.endsWith(".pcx") || lower.endsWith(".jpeg")
                         || lower.endsWith(".jpe")){
                    fileType = TYPE_IMAGE;
                }
                else if (lower.endsWith(".mpg") || lower.endsWith(".avi")
                         || lower.endsWith(".mov") || lower.endsWith(".mpeg")
                         || lower.endsWith(".dat") || lower.endsWith(".ra")
                         || lower.endsWith(".vob") || lower.endsWith(".rm")
                         || lower.endsWith(".divx")){
                    fileType = TYPE_MOVIE;
                }
                else if (lower.endsWith(".iso") || lower.endsWith(".bin")
                         || lower.endsWith(".cue") || lower.endsWith(".dao")
                         || lower.endsWith(".img") || lower.endsWith(".cif")
                         || lower.endsWith(".nrg") || lower.endsWith(".c2d")
                         || lower.endsWith(".bwt") || lower.endsWith(".pdi")
                         || lower.endsWith(".b5t") || lower.endsWith(".cdi")
                         || lower.endsWith(".ccd") || lower.endsWith(".tao")){
                    fileType = TYPE_ISO;
                }
                else if (lower.endsWith(".txt") || lower.endsWith(".nfo")
                         || lower.endsWith(".doc")){
                    fileType = TYPE_TEXT;
                }
                else if (lower.endsWith(".mp3") || lower.endsWith(".wav")
                         || lower.endsWith(".mid") || lower.endsWith(".mp2")
                         || lower.endsWith(".m2v") || lower.endsWith(".wma")
                         || lower.endsWith(".midi") || lower.endsWith(".ogg")
                         || lower.endsWith(".mmf")){
                    fileType = TYPE_SOUND;
                }
                else if (lower.endsWith(".zip") || lower.endsWith(".rar")
                         || lower.endsWith(".ace") || lower.endsWith(".arj")
                         || lower.endsWith(".gz2") || lower.endsWith(".cab")
                         || lower.endsWith(".lzh") || lower.endsWith(".tag")
                         || lower.endsWith(".gzip") || lower.endsWith(".uue")
                         || lower.endsWith(".bz2") || lower.endsWith(".jar")){
                    fileType = TYPE_ARCHIVE;
                }
            }
        }
    }
}
