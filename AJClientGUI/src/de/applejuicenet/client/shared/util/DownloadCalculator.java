package de.applejuicenet.client.shared.util;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.gui.download.table.DownloadModel;

public abstract class DownloadCalculator {
	private static Logger logger = Logger.getLogger(DownloadCalculator.class); 

	public static String getStatusAsString(Download download) {
        try {
            switch (download.getStatus()) {
                case Download.PAUSIERT:
                    return DownloadModel.pausiert;
                case Download.ABBRECHEN:
                    return DownloadModel.abbrechen;
                case Download.ABGEGROCHEN:
                    return DownloadModel.abgebrochen;
                case Download.FERTIG:
                    return DownloadModel.fertig;
                case Download.FEHLER_BEIM_FERTIGSTELLEN:
                    return DownloadModel.fehlerBeimFertigstellen;
                case Download.NICHT_GENUG_PLATZ_FEHLER:
                    return DownloadModel.keinPlatz;
                case Download.DATA_WIRD_ERSTELLT:
                    return DownloadModel.dataWirdErstellt;
                case Download.SUCHEN_LADEN: {
                    DownloadSource[] sources = download.getSources();
                    String result = "";
                    int uebertragung = 0;
                    int warteschlange = 0;
                    int status;
                    for (int i = 0; i < sources.length; i++) {
                        status = sources[i].getStatus();
                        if (status == DownloadSource.UEBERTRAGUNG) {
                            uebertragung++;
                            result = DownloadModel.laden;
                        }
                        else if (status == DownloadSource.IN_WARTESCHLANGE
                                 || status == DownloadSource.WARTESCHLANGE_VOLL) {
                            warteschlange++;
                        }
                    }
                    if (result.length() == 0) {
                        result = DownloadModel.suchen;
                    }
                    return result + " " + (warteschlange + uebertragung) + "/" +
                        sources.length + " (" + uebertragung + ")";
                }
                case Download.FERTIGSTELLEN:
                    return DownloadModel.fertigstellen;
                default:
                    return "";
            }
        }
        catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            return "";
        }
    }	
}
