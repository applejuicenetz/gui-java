package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;

interface DownloadInternal {

    public void addSource(DownloadSourceDO downloadSourceDO);

    public void setShareId(int shareId);

    public void setHash(String hash);

    public void setGroesse(long groesse);

    public void setStatus(int newStatus);

    public void setFilename(String newFilename);

    public void setTargetDirectory(String newTargetDirectory);

    public void setPowerDownload(int newPowerDownload);

    public void setTemporaryFileNumber(int temporaryFileNumber);

    public void setReady(long newReady);    
}
