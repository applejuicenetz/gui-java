package de.applejuicenet.client.gui.powerdownload;

import java.awt.Frame;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.gui.AppleJuiceDialog;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/pwdl_policy_src/standardpwdlpolicy/src/de/applejuicenet/client/gui/powerdownload/StandardAutomaticPwdlPolicy.java,v 1.11 2005/02/15 16:39:33 loevenwong Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class StandardAutomaticPwdlPolicy extends AutomaticPowerdownloadPolicy {
    private int pwdlValue;
    private int anzahlDownloads;
    private List<Download> resumedDownloads = new Vector();
    private List<Download> pausedDownloads = new Vector();

    public StandardAutomaticPwdlPolicy(ApplejuiceFassade applejuiceFassade) {
    	super(applejuiceFassade);
    	anzahlDownloads = 2;
    }

    public StandardAutomaticPwdlPolicy(ApplejuiceFassade applejuiceFassade, int anzahlDownloads, int pwdlValue) {
    	super(applejuiceFassade);
    	this.pwdlValue = pwdlValue;
    	this.anzahlDownloads = anzahlDownloads;
    }

    public boolean initAction() {
        shouldPause = false;
        double wert = 0;
        boolean correctInput = false;
        JPanel abfrage = new JPanel();
        JTextField pwdlWert = new JTextField("2,6");
        JTextField pwdlCount = new JTextField("2");
        abfrage.add(new JLabel("Pwdl-Wert:"));
        abfrage.add(pwdlWert);
        abfrage.add(new JLabel("# DL:"));
        abfrage.add(pwdlCount);
        while (!correctInput){
            JOptionPane.showInputDialog(AppleJuiceDialog.getApp(), abfrage);
            try {
            	String result = pwdlWert.getText();
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
            	Integer counter = null;
            	try {
            		counter = new Integer(pwdlCount.getText());
            	}
            	catch (NumberFormatException ex) {
            		continue;
            	}
              anzahlDownloads = counter;
            	
            	correctInput = true;
            }
            catch (Exception e) {
            	continue;
            }
        }
        return true;
    }

    public void doAction() throws Exception {
    		Map downloads = applejuiceFassade.getDownloadsSnapshot();
        int downloadSize = downloads.size();
        if (downloadSize == 0) {
        	return;
        }
        synchronized (downloads)
        {
          TreeMap<Sortierkriterium,Download> naechsteDownloads = new TreeMap(new ProzentGeladenComparator());
        	if (downloadSize <= anzahlDownloads) {
        		// alle auf pd setzen und resuemen...
        		List power = new Vector(downloads.values());
        		applejuiceFassade.setPowerDownload(power, new Integer(pwdlValue));
        		applejuiceFassade.resumeDownload(power);
        	}
        	else {
        		Iterator<Download> it = downloads.values().iterator();
        		while (it.hasNext()) {
        			Download current = it.next();
        			if (current.getStatus() == Download.PAUSIERT || current.getStatus() == Download.SUCHEN_LADEN) {
        				naechsteDownloads.put(new Sortierkriterium(current), current);
        			}
        		}
        		int pos = 0;
        		List<Download> downloads2Start = new Vector();
        		List<Download> downloads2Stop = new Vector();
        		for (Download cur : naechsteDownloads.values()) {
        			if (pos < anzahlDownloads) {
        				downloads2Start.add(cur);
        			}
        			else {
        				downloads2Stop.add(cur);
        			}
      				pos++;
        		}
        		if (changed(downloads2Start, downloads2Stop)) {
        			applejuiceFassade.setPowerDownload(downloads2Start, pwdlValue);
        			applejuiceFassade.resumeDownload(downloads2Start);
        			applejuiceFassade.pauseDownload(downloads2Stop);
        		}
        	}
        }
    }

		private boolean changed(List<Download> downloads2Start, List<Download> downloads2Stop)
		{
			boolean changed = false;
			if (downloads2Start.size() != resumedDownloads.size()) {
				changed = true;
			}
			if (downloads2Stop.size() != pausedDownloads.size()) {
				changed = true;
			}
			if (!changed) {
				if (!listsEquals(downloads2Start, resumedDownloads)) {
					changed = true;
				}
			}
			if (!changed) {
				if (!listsEquals(downloads2Stop, pausedDownloads)) {
					changed = true;
				}
			}
			resumedDownloads = downloads2Start;
			pausedDownloads = downloads2Stop;
			return changed;
		}

		private boolean listsEquals(List<Download> erste, List<Download> zweite)
		{
			for (int i=0; i<erste.size(); i++) {
				if (!downloadEquals(erste.get(i), zweite.get(i))) {
					return false;
				}
			}
			return true;
		}

		private boolean downloadEquals(Download download, Download download2)
		{
			if (download.getId() != download2.getId()) {
				return false;
			}
			if (download.getPowerDownload() != download2.getPowerDownload()) {
				return false;
			}
			return true;
		}

		public void informPaused() {
    }

    public String getVersion() {
        return "1.3";
    }

    public String getDescription() {
        String description =
                "Powerdownload wird fuer die prozentual weitesten Downloads aktiviert." +
                "Wenn einer fertig ist, wird der Naechste aktiviert." +
                "Der Pwdl-Wert ist frei einstellbar.";
        return description;
    }

    public String getAuthor() {
        return "Maj0r & loevenwong";
    }

    public String toString() {
        return "StandardPwdlPolicy Vers. " + getVersion();
    }
    
    private class Sortierkriterium {
    	private Double prozentGeladen;
    	private Long groesse;
    	private Integer id;
    	
    	public Sortierkriterium(Double prozentGeladen, Long groesse, Integer id) {
    		this.prozentGeladen = prozentGeladen;
    		this.groesse = groesse;
    		this.id = id;
    	}

    	public Sortierkriterium(Download current)
			{
    		this.prozentGeladen = new Double(current.getProzentGeladen());
    		this.groesse = current.getGroesse();
    		this.id = current.getId();
			}

			public int compareTo(Sortierkriterium comparable)
			{
				int compareResult = this.prozentGeladen.compareTo(comparable.prozentGeladen) * -1;
				if (compareResult == 0) {
					compareResult = this.groesse.compareTo(comparable.groesse) * -1;
				}
				if (compareResult == 0) {
					compareResult = this.id.compareTo(comparable.id);
				}
				return compareResult;
			}
    }
    
    private class ProzentGeladenComparator implements Comparator {
			public int compare(Object arg0, Object arg1)
			{
				return ((Sortierkriterium)arg0).compareTo((Sortierkriterium)arg1);
			}
    }
}
