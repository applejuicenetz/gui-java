package de.applejuicenet.client.gui.download;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/Attic/DownloadDOOverviewPanel.java,v 1.5 2004/12/06 10:29:53 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;

public class DownloadDOOverviewPanel
    extends JPanel
    implements LanguageListener{
	private DownloadPartListPanel actualDlOverviewTable;
    private JLabel actualDLDateiName = new JLabel();
    private JLabel label5 = new JLabel();
    private JLabel label4 = new JLabel();
    private JLabel label3 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label1 = new JLabel();
    private Logger logger;
    private JButton holeListe = new JButton();
    private PartListWorkerThread partListWorkerThread = null;
    private DownloadPanel downloadPanel;
    private String verfuegbar;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public DownloadDOOverviewPanel(DownloadPanel parent) {
        logger = Logger.getLogger(getClass());
        try {
            downloadPanel = parent;
            actualDlOverviewTable = DownloadPartListPanel.getInstance();
            init();
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void enableHoleListButton(boolean enable) {
        holeListe.setEnabled(enable);
    }
    
    public JButton getBtnHoleListe(){
    	return holeListe;
    }

    private void init() {
        holeListe.setEnabled(false);
        setLayout(new BorderLayout());
        JPanel tempPanel1 = new JPanel();
        tempPanel1.setLayout(new FlowLayout());

        JLabel gelb = new JLabel("     ");
        gelb.setOpaque(true);
        gelb.setBackground(Color.YELLOW);
        tempPanel1.add(gelb);
        tempPanel1.add(label5);

        JLabel blau = new JLabel("     ");
        blau.setOpaque(true);
        blau.setBackground(Color.BLUE);
        tempPanel1.add(blau);
        tempPanel1.add(label4);

        JLabel red = new JLabel("     ");
        red.setOpaque(true);
        red.setBackground(Color.RED);
        tempPanel1.add(red);
        tempPanel1.add(label3);

        JLabel black = new JLabel("     ");
        black.setOpaque(true);
        black.setBackground(Color.BLACK);
        tempPanel1.add(black);
        tempPanel1.add(label2);

        JLabel green = new JLabel("     ");
        green.setOpaque(true);
        green.setBackground(Color.GREEN);
        tempPanel1.add(green);
        tempPanel1.add(label1);

        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(holeListe, BorderLayout.WEST);
        panel3.add(tempPanel1, BorderLayout.CENTER);

        add(panel3, BorderLayout.NORTH);
        actualDLDateiName.setPreferredSize(new Dimension(actualDLDateiName.
            getPreferredSize().width, 17));
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(actualDLDateiName, BorderLayout.NORTH);
        panel1.add(actualDlOverviewTable, BorderLayout.CENTER);
        add(panel1, BorderLayout.CENTER);
    }

    public void setDownloadDO(DownloadDO downloadDO) {
        try {
            if (partListWorkerThread == null){
	            partListWorkerThread = new PartListWorkerThread();
	            partListWorkerThread.start();
	        }
            partListWorkerThread.setDownloadDO(downloadDO);
        }
        catch (Exception e) {
            if (partListWorkerThread != null){
            	partListWorkerThread.cancel();
                partListWorkerThread = null;
            }
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void setDownloadSourceDO(DownloadSourceDO downloadSourceDO) {
        try {
            if (partListWorkerThread == null){
	            partListWorkerThread = new PartListWorkerThread();
	            partListWorkerThread.start();
	        }
            partListWorkerThread.setDownloadSourceDO(downloadSourceDO);
        }
        catch (Exception e) {
            if (partListWorkerThread != null){
            	partListWorkerThread.cancel();
                partListWorkerThread = null;
            }
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            label5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.aktiveuebertragung")));
            label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label4.caption")));
            label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label3.caption")));
            label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label2.caption")));
            label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label1.caption")));
            holeListe.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.partlisteanzeigen")));
            verfuegbar = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(".root.javagui.downloadform.verfuegbar"));
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }
    
    private class PartListWorkerThread extends Thread {
    	private Object objectDO = null;
    	private boolean paused = true;
    	
        public void run() {
        	while(true && !interrupted()){
        		if (paused){
        			try{
	        			sleep(2000);
	        			continue;
	                }
	                catch (InterruptedException iE) {
	                    interrupt();
	                }
        		}
            	if (objectDO == null){
            		paused = true;
            		continue;
            	}
            	boolean shortPause = false;
            	if (objectDO.getClass() == DownloadDO.class){
            		shortPause = workDownloadDO((DownloadDO)objectDO);
            	}
            	else{
            		shortPause = workDownloadSourceDO((DownloadSourceDO)objectDO);
            		paused = true;
            	}
            	if (shortPause){
        			try{
	        			sleep(2000);
	        			continue;
	                }
	                catch (InterruptedException iE) {
	                    interrupt();
	                }
            	}
        	}
        }
        
        private boolean workDownloadDO(DownloadDO downloadDO){
        	if (downloadDO.getStatus() != DownloadDO.FERTIGSTELLEN 
        			&& downloadDO.getStatus() != DownloadDO.FERTIG){
            	String dateiNameText = " " + downloadDO.getFilename() +
            	" (" + downloadDO.getTemporaryFileNumber() + ".data) ";
	            PartListDO partList = null;
                try{
                    partList = ApplejuiceFassade.getInstance().
                        getPartList(downloadDO);
                    if (paused){
                    	// wurde moeglicherweise waehrend des Ziehens der Partliste gestoppt
                    	return false;
                    }
                }
                catch(WebSiteNotFoundException wsnfE){
                    // Core ist wahrscheinlich zurzeit ueberlastet
                    partList = null;
                }
                if (isInterrupted()){
                    return false;
                }
                if (partList == null) {
                    actualDLDateiName.setText("");
                    actualDlOverviewTable.setPartList(null, null);
                }
                else {
                    String tmp = verfuegbar.replaceFirst("%s", 
                    		decimalFormat.format(partList.getProzentVerfuegbar()));
                    actualDLDateiName.setText(dateiNameText + " - " + tmp);
                    actualDlOverviewTable.setPartList(partList, new Integer(downloadDO.getId()));                                
                }
    			return true;
        	}
        	else{
        		paused = true;
    			return false;
        	}
        }

        private boolean workDownloadSourceDO(DownloadSourceDO downloadSoureDO) {
			PartListDO partList;
			String tmp = downloadSoureDO.getFilename() + " (" +
				downloadSoureDO.getNickname() + ")";
			actualDLDateiName.setText(tmp);
			try {
				partList = ApplejuiceFassade.getInstance().getPartList(
						downloadSoureDO);
                if (paused){
                	// wurde moeglicherweise waehrend des Ziehens der Partliste gestoppt
                	return false;
                }
			} catch (WebSiteNotFoundException ex) {
				// Core ist wahrscheinlich zurzeit ueberlastet
				partList = null;
			}
			if (partList == null) {
				actualDLDateiName.setText("");
			} else {
				actualDLDateiName.setText(tmp + " - " + verfuegbar.replaceFirst("%s", decimalFormat
						.format(partList.getProzentVerfuegbar())));
				actualDlOverviewTable.setPartList(partList, new Integer(downloadSoureDO.getId()));
			}
			return false;
		}
        
        public void setDownloadDO(DownloadDO downloadDO){
        	paused = true;
        	if (downloadDO == null){
        		clear();
        	}
        	if (objectDO != downloadDO){
	        	objectDO = downloadDO;
            	if (objectDO == null){
            		clear();
            	}
            	else{
            		paused = false;
            	}
        	}
        }

        public void setDownloadSourceDO(DownloadSourceDO downloadSoureDO){
        	paused = true;
        	if (objectDO != downloadSoureDO){
	        	objectDO = downloadSoureDO;
            	if (objectDO == null){
            		clear();
            	}
            	else{
            		paused = false;            		
            	}
        	}
        }
        
        private void clear(){
        	paused = true;
            actualDLDateiName.setText("");
            actualDlOverviewTable.setPartList(null, null);
        }
        
        public void pause(){
        	paused = true;
        }
        
        public void cancel(){
        	paused = true;
        	interrupt();
        	clear();
        }
    }
}
