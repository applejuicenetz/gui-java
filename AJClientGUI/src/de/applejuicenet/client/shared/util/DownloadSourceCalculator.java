package de.applejuicenet.client.shared.util;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.fassade.shared.Version;
import de.applejuicenet.client.gui.download.table.DownloadModel;
import de.applejuicenet.client.shared.IconManager;

public abstract class DownloadSourceCalculator {

	public static String getStatusAsString(DownloadSourceDO downloadSourceDO) {
        switch (downloadSourceDO.getStatus()) {
            case DownloadSourceDO.UNGEFRAGT:
                return DownloadModel.ungefragt;
            case DownloadSourceDO.VERSUCHE_ZU_VERBINDEN:
                return DownloadModel.versucheZuVerbinden;
            case DownloadSourceDO.GEGENSTELLE_HAT_ZU_ALTE_VERSION:
                return DownloadModel.ggstZuAlteVersion;
            case DownloadSourceDO.GEGENSTELLE_KANN_DATEI_NICHT_OEFFNEN:
                return DownloadModel.kannDateiNichtOeffnen;
            case DownloadSourceDO.IN_WARTESCHLANGE: {
                String temp = DownloadModel.position;
                temp = temp.replaceFirst("%d",
                		Integer.toString(downloadSourceDO.getQueuePosition()));
                return temp;
            }
            case DownloadSourceDO.KEINE_BRAUCHBAREN_PARTS:
                return DownloadModel.keineBrauchbarenParts;
            case DownloadSourceDO.UEBERTRAGUNG:
                return DownloadModel.uebertragung;
            case DownloadSourceDO.NICHT_GENUEGEND_PLATZ_AUF_DER_PLATTE:
                return DownloadModel.nichtGenugPlatz;
            case DownloadSourceDO.FERTIGGESTELLT:
                return DownloadModel.fertiggestellt;
            case DownloadSourceDO.KEINE_VERBINDUNG_MOEGLICH:
                return DownloadModel.keineVerbindungMoeglich;
            case DownloadSourceDO.PAUSIERT:
                return DownloadModel.pausiert;
            case DownloadSourceDO.VERSUCHE_INDIREKT:
                return DownloadModel.versucheIndirekt;
            case DownloadSourceDO.WARTESCHLANGE_VOLL:
                return DownloadModel.warteschlangeVoll;
            case DownloadSourceDO.EIGENES_LIMIT_ERREICHT:
                return DownloadModel.eigenesLimitErreicht;
            case DownloadSourceDO.INDIREKTE_VERBINDUNG_ABGELEHNT:
                return DownloadModel.indirekteVerbindungAbgelehnt;

            default:
                return "";
        }
    }
	
	private static JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
	private static JLabel progressbarLabel = new JLabel();
	private static JLabel versionLabel = new JLabel();
	static {
		progressBar.setStringPainted(true);
		progressBar.setOpaque(false);
		progressbarLabel.setOpaque(true);
		versionLabel.setOpaque(true);
	}
	
	public static Component getProgressbarComponent(DownloadSourceDO downloadSourceDO) {
        if (downloadSourceDO.getStatus() == DownloadSourceDO.UEBERTRAGUNG) {
            String prozent = downloadSourceDO.getDownloadPercentAsString();
            int pos = prozent.indexOf('.');
            String balken = prozent;
            if (pos != -1) {
                balken = balken.substring(0, pos);
            }
            progressBar.setValue(Integer.parseInt(balken));
            progressBar.setString(prozent + " %");
            return progressBar;
        }
        else {
            return progressbarLabel;
        }
    }
	
	public static Component getVersionComponent(DownloadSourceDO downloadSourceDO, JTable table) {
        if (downloadSourceDO.getVersion() == null) {
            versionLabel.setIcon(null);
            versionLabel.setText("");
        }
        else {
            versionLabel.setFont(table.getFont());
            versionLabel.setIcon(getVersionIcon(downloadSourceDO));
            versionLabel.setText(downloadSourceDO.getVersion().getVersion());
        }
        return versionLabel;
    }
	
	public static ImageIcon getVersionIcon(DownloadSourceDO downloadSourceDO) {
        switch (downloadSourceDO.getVersion().getBetriebsSystem()) {
	        case Version.WIN32: {
	            return IconManager.getInstance().getIcon("winsymbol");
	        }
	        case Version.LINUX: {
	            return IconManager.getInstance().getIcon("linuxsymbol");
	        }
	        case Version.FREEBSD: {
	            return IconManager.getInstance().getIcon("freebsdsymbol");
	        }
	        case Version.MACINTOSH: {
	            return IconManager.getInstance().getIcon("macsymbol");
	        }
	        case Version.SOLARIS: {
	            return IconManager.getInstance().getIcon("sunossymbol");
	        }
	        case Version.NETWARE: {
	            return IconManager.getInstance().getIcon("netwaresymbol");
	        }
	        case Version.OS2: {
	            return IconManager.getInstance().getIcon("os2symbol");
	        }
	        default: {
	            return IconManager.getInstance().getIcon("unbekanntsymbol");
	        }
        }
    }
}
