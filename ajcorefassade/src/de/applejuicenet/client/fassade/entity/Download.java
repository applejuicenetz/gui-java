package de.applejuicenet.client.fassade.entity;

import java.util.Map;

public interface Download extends IdOwner{
    
    // Status - IDs
    static final int SUCHEN_LADEN = 0;
    static final int NICHT_GENUG_PLATZ_FEHLER = 1;
    static final int FERTIGSTELLEN = 12;
    static final int FEHLER_BEIM_FERTIGSTELLEN = 13;
    static final int FERTIG = 14;
    static final int ABBRECHEN = 15;
    static final int DATA_WIRD_ERSTELLT = 16;
    static final int ABGEGROCHEN = 17;
    static final int PAUSIERT = 18;
    
    String getProzentGeladenAsString();

    double getProzentGeladen();

    DownloadSource getSourceById(int sourceId);

    DownloadSource[] getSources();
    
    Map<Integer, DownloadSource> getSourcesMap();

    int getShareId();

    String getHash();

    int getGroesse();

    int getStatus();

    String getFilename();

    String getTargetDirectory();

    int getPowerDownload();

    int getId();

    int getTemporaryFileNumber();

    int getReady();

    long getRestZeit();

    String getRestZeitAsString();

    int getSpeedInBytes();

    int getBereitsGeladen();
}
