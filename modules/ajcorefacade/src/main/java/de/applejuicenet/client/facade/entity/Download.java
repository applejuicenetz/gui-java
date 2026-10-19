package de.applejuicenet.client.facade.entity;

import java.util.Map;

public interface Download extends IdOwner{
    
    // Status - IDs
    static final int SEARCHING_LOADING = 0;
    static final int NOT_ENOUGH_SPACE_ERROR = 1;
    static final int FINISHING = 12;
    static final int ERROR_ON_COMPLETING = 13;
    static final int FINISHED = 14;
    static final int CANCEL = 15;
    static final int DATA_BEING_CREATED = 16;
    static final int ABORTED_BY_PEER = 17;
    static final int PAUSED = 18;
    
    String getPercentLoadedAsString();

    double getPercentLoaded();

    DownloadSource getSourceById(int sourceId);

    DownloadSource[] getSources();
    
    Map<Integer, DownloadSource> getSourcesMap();

    int getShareId();

    String getHash();

    long getSize();

    int getStatus();

    String getFilename();

    String getTargetDirectory();

    int getPowerDownload();

    int getId();

    int getTemporaryFileNumber();

    long getReady();

    long getRemainingTime();

    String getRemainingTimeAsString();

    int getSpeedInBytes();

    long getAlreadyLoaded();
}
