package de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.MapSetStringKey;

import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JOptionPane;
import de.applejuicenet.client.gui.AppleJuiceDialog;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/powerdownload/Attic/StandardAutomaticPwdlPolicy.java,v 1.3 2003/11/19 17:05:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: StandardAutomaticPwdlPolicy.java,v $
 * Revision 1.3  2003/11/19 17:05:20  maj0r
 * Autom. Pwdl ueberarbeitet.
 *
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
    private int[] maxProzentId = new int[]{-1, -1};
    double[] maxProzent = new double[]{-1, -1};
    private DownloadDO[] downloadDO = new DownloadDO[2];
    private int pwdlValue;

    public StandardAutomaticPwdlPolicy() {}

    public boolean initAction() {
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
                    wert = Double.parseDouble(result);
                    pwdlValue = (int)((wert -1) * 10);
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
                MapSetStringKey key = new MapSetStringKey(maxProzentId[0]);
                MapSetStringKey key2 = new MapSetStringKey(maxProzentId[1]);
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
            }
            if (maxProzentId[0] == -1 || maxProzentId[1] == -1)
            {
                int[] ids = new int[downloads.size()];
                Iterator it = downloads.values().iterator();
                int temp = 0;
                DownloadDO tmpDownloadDO;
                while (it.hasNext())
                {
                    tmpDownloadDO = (DownloadDO) it.next();
                    if ((tmpDownloadDO.getProzentGeladen() > maxProzent[0] ||
                        tmpDownloadDO.getProzentGeladen() > maxProzent[1] )
                            && (tmpDownloadDO.getStatus() == DownloadDO.PAUSIERT
                            || tmpDownloadDO.getStatus() == DownloadDO.SUCHEN_LADEN))
                    {
                        if (maxProzent[0]==-1){
                            zuordnen(0, tmpDownloadDO);
                        }
                        else if (maxProzent[1]==-1){
                            zuordnen(1, tmpDownloadDO);
                        }
                        else if (maxProzent[0]>maxProzent[1]){
                            if (tmpDownloadDO.getProzentGeladen() > maxProzent[1]){
                                zuordnen(1, tmpDownloadDO);
                            }
                            else{
                                zuordnen(0, tmpDownloadDO);
                            }
                        }
                        else{
                            if (tmpDownloadDO.getProzentGeladen() > maxProzent[0]){
                                zuordnen(0, tmpDownloadDO);
                            }
                            else{
                                zuordnen(1, tmpDownloadDO);
                            }
                        }
                    }
                    ids[temp] = tmpDownloadDO.getId();
                    temp++;
                }
                if (ids.length!=0){
                    applejuiceFassade.pauseDownload(ids);
                    applejuiceFassade.setPowerDownload(ids, 0);
                    applejuiceFassade.setPowerDownload(new int[] {maxProzentId[
                        0], maxProzentId[1]}
                        , pwdlValue);
                    applejuiceFassade.resumeDownload(new int[] {maxProzentId[0],
                        maxProzentId[1]});
                }
            }
        }
        sleep(30000);
    }

    private void zuordnen(int id, DownloadDO downloadDO){
        maxProzent[id] = downloadDO.getProzentGeladen();
        maxProzentId[id] = downloadDO.getId();
    }

    public void informPaused() {
        maxProzentId[0] = -1;
        maxProzentId[1] = -1;
    }

    public String getVersion() {
        return "1.1";
    }

    public String getDescription() {
        String description =
                "Powerdownload wird fuer die beiden prozentual weitesten Downloads aktiviert." +
                "Wenn einer fertig ist, wird der Naechste aktiviert." +
                "Die restlichen Downloads werden pausiert." +
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
