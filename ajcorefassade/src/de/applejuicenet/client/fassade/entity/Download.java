package de.applejuicenet.client.fassade.entity;

import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;

public interface Download {
    
    // Status - IDs
    public static final int SUCHEN_LADEN = 0;
    public static final int NICHT_GENUG_PLATZ_FEHLER = 1;
    public static final int FERTIGSTELLEN = 12;
    public static final int FEHLER_BEIM_FERTIGSTELLEN = 13;
    public static final int FERTIG = 14;
    public static final int ABBRECHEN = 15;
    public static final int DATA_WIRD_ERSTELLT = 16;
    public static final int ABGEGROCHEN = 17;
    public static final int PAUSIERT = 18;
    
    public String getProzentGeladenAsString();

    public double getProzentGeladen();

    public DownloadSourceDO getSourceById(int sourceId);

    public DownloadSourceDO[] getSources();

    public int getShareId();

    public String getHash();

    public long getGroesse();

    public int getStatus();

    public String getFilename();

    public String getTargetDirectory();

    public int getPowerDownload();

    public int getId();

    public int getTemporaryFileNumber();

    public long getReady();

    public long getRestZeit();

    public String getRestZeitAsString();

    public long getSpeedInBytes();

    public long getBereitsGeladen();
}
