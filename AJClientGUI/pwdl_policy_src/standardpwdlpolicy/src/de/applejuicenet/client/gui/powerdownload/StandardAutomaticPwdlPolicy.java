package de.applejuicenet.client.gui.powerdownload;

import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.shared.dac.DownloadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/pwdl_policy_src/standardpwdlpolicy/src/de/applejuicenet/client/gui/powerdownload/StandardAutomaticPwdlPolicy.java,v 1.3 2004/06/15 06:22:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class StandardAutomaticPwdlPolicy extends AutomaticPowerdownloadPolicy {
    private Map downloads = null;
    private int[] maxProzentId = new int[]{-1, -1};
    double[] maxProzent = new double[]{-1, -1};
    private DownloadDO[] downloadDO = new DownloadDO[2];
    private int pwdlValue;

    public StandardAutomaticPwdlPolicy() {}

    public boolean initAction() {
        shouldPause = false;
        double wert = 0;
        boolean correctInput = false;
        while (!correctInput){
            String result = JOptionPane.showInputDialog(AppleJuiceDialog.getApp(), "Pwdl-Wert:", "2,6");
            if (result==null){
                return false;
            }
            else{
                try {
                    result = result.replace(',', '.');
                    if (result.length()>result.lastIndexOf('.')+2){
                        result = result.substring(0, result.lastIndexOf('.')+2);
                    }
                    wert = Double.parseDouble(result) - 1;
                    wert = ((double) Math.round(wert * 100.0))/100.0;
                    pwdlValue = (int)((wert) * 10);
                    if (pwdlValue<12 || pwdlValue>490){
                        continue;
                    }
                    correctInput = true;
                }
                catch (Exception e) {
                    continue;
                }
            }
        }
        return true;
    }

    public void doAction() throws Exception {
        downloads = applejuiceFassade.getDownloadsSnapshot();
        synchronized (downloads)
        {
            if (maxProzentId[0] != -1 || maxProzentId[1] != -1)
            {
                if (maxProzentId[0]==maxProzentId[1]){
                    maxProzentId[0]=-1;
                }
                String key = Integer.toString(maxProzentId[0]);
                String key2 = Integer.toString(maxProzentId[1]);
                downloadDO[0] = (DownloadDO) downloads.get(key);
                downloadDO[1] = (DownloadDO) downloads.get(key2);
                for (int i=0; i<maxProzentId.length; i++){
                    if (downloadDO[i] == null || (downloadDO[i].getStatus() != DownloadDO.SUCHEN_LADEN)
                        && downloadDO[i].getStatus() != DownloadDO.PAUSIERT)
                    {
                        maxProzentId[i] = -1;
                        maxProzent[i] = -1;
                    }
                }
                if (maxProzentId[0] == -1 && maxProzentId[1] != -1){
                    maxProzent[0] = maxProzent[1];
                    maxProzentId[0] = maxProzentId[1];
                    maxProzent[1] = -1;
                    maxProzentId[1] = -1;
                }
            }
            if (maxProzentId[0] == -1 || (
                maxProzentId[1] == -1 && downloads.size()!=1))
            {
                int downloadSize = downloads.size();
                int[] ids = new int[downloadSize];
                Iterator it = downloads.values().iterator();
                DownloadDO tmpDownloadDO;
                if (downloadSize==0){
                    maxProzent[0] = -1;
                    maxProzentId[0] = -1;
                    maxProzent[1] = -1;
                    maxProzentId[1] = -1;
                }
                else if (downloadSize==1){
                    tmpDownloadDO = (DownloadDO) it.next();
                    maxProzent[0] = tmpDownloadDO.getProzentGeladen();
                    maxProzentId[0] = tmpDownloadDO.getId();
                    maxProzent[1] = -1;
                    maxProzentId[1] = -1;
                    applejuiceFassade.setPowerDownload(new int[] {
                        maxProzentId[0]}, pwdlValue);
                    applejuiceFassade.resumeDownload(new int[] {
                        maxProzentId[0]});
                }
                else{
                    int temp = 0;
                    while (it.hasNext()) {
                        tmpDownloadDO = (DownloadDO) it.next();
                        if (tmpDownloadDO.getProzentGeladen() > maxProzent[1]
                            &&
                            (tmpDownloadDO.getStatus() == DownloadDO.PAUSIERT
                             ||
                             tmpDownloadDO.getStatus() == DownloadDO.SUCHEN_LADEN)) {
                            if (tmpDownloadDO.getProzentGeladen() >
                                maxProzent[0]) {
                                maxProzent[1] = maxProzent[0];
                                maxProzentId[1] = maxProzentId[0];
                                maxProzent[0] = tmpDownloadDO.getProzentGeladen();
                                maxProzentId[0] = tmpDownloadDO.getId();
                            }
                            else {
                                maxProzent[1] = tmpDownloadDO.getProzentGeladen();
                                maxProzentId[1] = tmpDownloadDO.getId();
                            }
                        }
                        ids[temp] = tmpDownloadDO.getId();
                        temp++;
                    }
                    if (ids.length != 0) {
//                        applejuiceFassade.pauseDownload(ids);
                        applejuiceFassade.setPowerDownload(ids, 0);
                        applejuiceFassade.setPowerDownload(new int[] {
                            maxProzentId[
                            0], maxProzentId[1]}
                            , pwdlValue);
                        applejuiceFassade.resumeDownload(new int[] {
                            maxProzentId[0],
                            maxProzentId[1]});
                    }
                }
            }
        }
        sleep(30000);
    }

    public void informPaused() {
        maxProzentId[0] = -1;
        maxProzentId[1] = -1;
        maxProzent[0] = -1;
        maxProzent[1] = -1;
    }

    public String getVersion() {
        return "1.2";
    }

    public String getDescription() {
        String description =
                "Powerdownload wird fuer die beiden prozentual weitesten Downloads aktiviert." +
                "Wenn einer fertig ist, wird der Naechste aktiviert." +
                "Der Pwdl-Wert ist frei einstellbar.";
        return description;
    }

    public String getAuthor() {
        return "Maj0r";
    }

    public String toString() {
        return "StandardPwdlPolicy Vers. " + getVersion();
    }
}
