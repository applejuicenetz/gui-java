package de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.MapSetStringKey;

import java.util.HashMap;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/powerdownload/Attic/StandardAutomaticPwdlPolicy.java,v 1.2 2003/11/17 14:44:10 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: StandardAutomaticPwdlPolicy.java,v $
 * Revision 1.2  2003/11/17 14:44:10  maj0r
 * Erste funktionierende Version des automatischen Powerdownloads eingebaut.
 *
 * Revision 1.1  2003/11/17 07:32:30  maj0r
 * Automatischen Pwdl begonnen.
 *
 *
 */

public class StandardAutomaticPwdlPolicy extends AutomaticPowerdownloadPolicy {
    private HashMap downloads = null;
    private int currentId = -1;
    private DownloadDO downloadDO = null;

    public StandardAutomaticPwdlPolicy() {}

    public void doAction() throws Exception {
        downloads = applejuiceFassade.getDownloadsSnapshot();
        synchronized (downloads)
        {
            if (currentId != -1)
            {
                MapSetStringKey key = new MapSetStringKey(currentId);
                downloadDO = (DownloadDO) downloads.get(key);
                if (downloadDO == null || downloadDO.getStatus() != DownloadDO.SUCHEN_LADEN)
                {
                    currentId = -1;
                }
            }
            if (currentId == -1)
            {
                int[] ids = new int[downloads.size()];
                Iterator it = downloads.values().iterator();
                int temp = 0;
                double maxProzent = -1;
                int maxProzentId = -1;
                while (it.hasNext())
                {
                    downloadDO = (DownloadDO) it.next();
                    if (downloadDO.getProzentGeladen() > maxProzent
                            && (downloadDO.getStatus() == DownloadDO.PAUSIERT
                            || downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN))
                    {
                        maxProzent = downloadDO.getProzentGeladen();
                        maxProzentId = downloadDO.getId();
                    }
                    ids[temp] = downloadDO.getId();
                    temp++;
                }
                applejuiceFassade.pauseDownload(ids);
                applejuiceFassade.setPowerDownload(ids, 0);
                applejuiceFassade.setPowerDownload(new int[]{maxProzentId}, 16);
                applejuiceFassade.resumeDownload(new int[]{maxProzentId});
            }
        }
        sleep(30000);
    }

    public String getVersion() {
        return "1.0";
    }

    public String getDescription() {
        String description =
                "Powerdownload wird fuer den prozentual weitesten Download aktiviert.\r\n" +
                "Wenn dieser fertig ist, wird der Naechste aktiviert." +
                "Die restlichen Downloads werden pausiert.";
        return description;
    }

    public String getAuthor() {
        return "Maj0r";
    }

    public String toString() {
        return "StandardPwdlPolicy Vers. " + getVersion();
    }
}
