package de.applejuicenet.client.shared.util;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.dac.DownloadDO;
import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.gui.download.table.DownloadModel;

public abstract class DownloadDOCalculator {
	private static Logger logger = Logger.getLogger(DownloadDOCalculator.class); 

	public static String getStatusAsString(DownloadDO downloadDO) {
        try {
            switch (downloadDO.getStatus()) {
                case DownloadDO.PAUSIERT:
                    return DownloadModel.pausiert;
                case DownloadDO.ABBRECHEN:
                    return DownloadModel.abbrechen;
                case DownloadDO.ABGEGROCHEN:
                    return DownloadModel.abgebrochen;
                case DownloadDO.FERTIG:
                    return DownloadModel.fertig;
                case DownloadDO.FEHLER_BEIM_FERTIGSTELLEN:
                    return DownloadModel.fehlerBeimFertigstellen;
                case DownloadDO.NICHT_GENUG_PLATZ_FEHLER:
                    return DownloadModel.keinPlatz;
                case DownloadDO.DATA_WIRD_ERSTELLT:
                    return DownloadModel.dataWirdErstellt;
                case DownloadDO.SUCHEN_LADEN: {
                    DownloadSourceDO[] sources = downloadDO.getSources();
                    String result = "";
                    int uebertragung = 0;
                    int warteschlange = 0;
                    int status;
                    for (int i = 0; i < sources.length; i++) {
                        status = sources[i].getStatus();
                        if (status == DownloadSourceDO.UEBERTRAGUNG) {
                            uebertragung++;
                            result = DownloadModel.laden;
                        }
                        else if (status == DownloadSourceDO.IN_WARTESCHLANGE
                                 || status == DownloadSourceDO.WARTESCHLANGE_VOLL) {
                            warteschlange++;
                        }
                    }
                    if (result.length() == 0) {
                        result = DownloadModel.suchen;
                    }
                    return result + " " + (warteschlange + uebertragung) + "/" +
                        sources.length + " (" + uebertragung + ")";
                }
                case DownloadDO.FERTIGSTELLEN:
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
