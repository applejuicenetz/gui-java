package de.applejuicenet.client.gui.share;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.SharePanel;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.shared.GuiController;
import de.applejuicenet.client.gui.shared.GuiControllerActionListener;
import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ShareDO;

public class ShareController implements GuiController{

	private static final int PRIORITAET_AUFHEBEN = 0;
	private static final int PRIORITAET_SETZEN = 1;
	private static final int REFRESH = 2;
	private static final int SHARE_ERNEUERN = 3;
	
    private Logger logger;
	private SharePanel sharePanel;
    private String dateiGroesse;
    private String eintraege;
    private int anzahlDateien = 0;
	
	public ShareController(){
        logger = Logger.getLogger(getClass());
		sharePanel = SharePanel.getInstance();
		init();
	}

	public JComponent getComponent() {
		return sharePanel;
	}
	
	private void init(){
		sharePanel.getBtnPrioritaetAufheben().addActionListener(
				new GuiControllerActionListener(this, PRIORITAET_AUFHEBEN));
		sharePanel.getBtnPrioritaetSetzen().addActionListener(
				new GuiControllerActionListener(this, PRIORITAET_SETZEN));
		sharePanel.getBtnRefresh().addActionListener(
				new GuiControllerActionListener(this, REFRESH));
		sharePanel.getBtnNeuLaden().addActionListener(
				new GuiControllerActionListener(this, SHARE_ERNEUERN));
	}

	public void fireAction(int actionId) {
		switch(actionId){
			case PRIORITAET_AUFHEBEN:{
				prioritaetAufheben();
				break;
			}
			case PRIORITAET_SETZEN:{
				prioritaetSetzen();
				break;
			}
			case REFRESH:{
				refresh();
				break;
			}
			case SHARE_ERNEUERN:{
				shareNeuLaden(true);
				break;
			}
			default:{
				logger.error("Unregistrierte EventId " + actionId);
			}
		}
	}
	
	private void prioritaetAufheben(){
		sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
		sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
		sharePanel.getBtnNeuLaden().setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Object[] values = sharePanel.getShareTable().getSelectedItems();
                    if (values == null) {
                        return null;
                    }
                    ShareNode shareNode = null;
                    for (int i = 0; i < values.length; i++) {
                        shareNode = (ShareNode) values[i];
                        shareNode.setPriority(1);
                    }
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    }
                }
                return null;
            }

            public void finished() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                    	sharePanel.getShareTable().updateUI();
                        sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
                        sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
                        sharePanel.getBtnNeuLaden().setEnabled(true);
                    }
                });
            }
        };
        worker.start();
	}
	
	private void prioritaetSetzen(){
		sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
		sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
        sharePanel.getBtnNeuLaden().setEnabled(false);
        new Thread() {
            public void run() {
                try {
                    int prio = ( (Integer) sharePanel.getCmbPrioritaet().getSelectedItem()).
                        intValue();
                    Object[] values = sharePanel.getShareTable().getSelectedItems();
                    if (values != null){
                        synchronized (values) {
                            if (values == null) {
                                return;
                            }
                            ShareNode shareNode = null;
                            for (int i = 0; i < values.length; i++) {
                                shareNode = (ShareNode) values[i];
                                shareNode.setPriority(prio);
                            }
                        }
                        shareNeuLaden(false);
                    }
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    }
                }
            }
        }
        .start();
	}
	
	private void refresh(){
		sharePanel.getBtnRefresh().setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Set shares = ApplejuiceFassade.getInstance().
                    	getAJSettings().getShareDirs();
                    ApplejuiceFassade.getInstance().setShare(shares);
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    }
                }
                return null;
            }

            public void finished() {
            	sharePanel.getBtnRefresh().setEnabled(true);
            }
        };
        worker.start();
	}
	
    private void shareNeuLaden(final boolean komplettNeu) {
    	sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
    	sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
    	sharePanel.getBtnNeuLaden().setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    ShareNode rootNode = sharePanel.getShareModel().getRootNode();
                    if (komplettNeu) {
                        rootNode.removeAllChildren();
                    }
                    Map shares = ApplejuiceFassade.getInstance().getShare(true);
                    Iterator iterator = shares.values().iterator();
                    int anzahlDateien = 0;
                    double size = 0;
                    ShareDO shareDO;
                    while (iterator.hasNext()) {
                        shareDO = (ShareDO) iterator.next();
                        rootNode.addChild(shareDO);
                        size += shareDO.getSize();
                        anzahlDateien++;
                    }
                    size = size / 1048576;
                    dateiGroesse = Double.toString(size);
                    if (dateiGroesse.indexOf(".") + 3 < dateiGroesse.length()) {
                        dateiGroesse = dateiGroesse.substring(0,
                            dateiGroesse.indexOf(".") + 3) + " MB";
                    }
                    String temp = eintraege;
                    temp = temp.replaceFirst("%i",
                                             Integer.toString(anzahlDateien));
                    temp = temp.replaceFirst("%s", dateiGroesse);
                    sharePanel.getLblDateien().setText(temp);
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    }
                }
                return null;
            }

            public void finished() {
            	sharePanel.getShareTable().updateUI();
                sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
                sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
                sharePanel.getBtnNeuLaden().setEnabled(true);
            }
        };
        worker.start();
    }

	public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        eintraege = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.shareform.anzahlShare"));
        if (anzahlDateien > 0) {
            String temp = eintraege;
            temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
            temp = temp.replaceFirst("%s", dateiGroesse);
            sharePanel.getLblDateien().setText(temp);
        }
        else {
        	sharePanel.getLblDateien().setText("");
        }
	}
}
