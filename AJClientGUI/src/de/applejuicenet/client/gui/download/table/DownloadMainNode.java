package de.applejuicenet.client.gui.download.table;

import java.util.ArrayList;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.FileTypeHelper;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadMainNode.java,v 1.1 2004/10/15 15:54:32 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class DownloadMainNode
    implements Node, DownloadNode, LanguageListener, DownloadColumnComponent {
    public static final int ROOT_NODE = -1;
    public static final int LOADING_DOWNLOADS = 0;
    public static final int WAITING_DOWNLOADS = 1;
    public static final int REST_DOWNLOADS = 2;

    private static String columns[];

    private int type;
    private String text = "";

    private DownloadMainNode[] children;
    private DownloadDO downloadDO;
    private JProgressBar progress;
    private JLabel progressbarLabel;
    private JLabel versionLabel;

    private static Icon waitingIcon = IconManager.getInstance().getIcon("cool");
    private static Icon loadingIcon = IconManager.getInstance().getIcon(
        "download");
    private static Icon restIcon = IconManager.getInstance().getIcon("eek");
    private Icon rootIcon = null;

    public DownloadMainNode(DownloadDO downloadDO) {
        type = ROOT_NODE;
        this.downloadDO = downloadDO;
        children = new DownloadMainNode[3];
        children[0] = new DownloadMainNode(downloadDO, LOADING_DOWNLOADS);
        children[1] = new DownloadMainNode(downloadDO, WAITING_DOWNLOADS);
        children[2] = new DownloadMainNode(downloadDO, REST_DOWNLOADS);
        init();
    }

    public DownloadMainNode(DownloadDO downloadDO, int type) {
        super();
        this.type = type;
        this.downloadDO = downloadDO;
        if (type == LOADING_DOWNLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
        else if (type == WAITING_DOWNLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
        else if (type == REST_DOWNLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
        init();
    }

    public static void setColumnTitles(String[] newTitles){
        columns = newTitles;
    }

    public static String[] getColumnTitles(){
        return columns;
    }

    private void init(){
        progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progress.setStringPainted(true);
        progress.setOpaque(false);
        progressbarLabel = new JLabel();
        progressbarLabel.setOpaque(true);
        versionLabel = new JLabel();
        versionLabel.setOpaque(true);
    }

    public Icon getConvenientIcon() {
        if (type == LOADING_DOWNLOADS) {
            return loadingIcon;
        }
        else if (type == WAITING_DOWNLOADS) {
            return waitingIcon;
        }
        else if (type == REST_DOWNLOADS) {
            return restIcon;
        }
        else if (type == ROOT_NODE) {
        	if (rootIcon == null){
        		String fileType = FileTypeHelper.calculatePossibleFileType(downloadDO.getFilename());
        		rootIcon = IconManager.getInstance().getIcon(fileType);
        	}
        	return rootIcon;
        }
        else {
            return null;
        }
    }

    public int getChildCount() {
        Object[] obj = getChildren();
        if (obj == null) {
            return 0;
        }
        else {
            return obj.length;
        }
    }

    public boolean isLeaf() {
        return (getChildCount() == 0) ? true : false;
    }

    public Object[] getChildren() {
        switch (type) {
            case ROOT_NODE: {
                if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
                    return children;
                }
                else {
                    boolean childFound = false;
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].getChildCount() > 0) {
                            childFound = true;
                            break;
                        }
                    }
                    if (childFound) {
                        return children;
                    }
                    else {
                        return null;
                    }
                }
            }
            case LOADING_DOWNLOADS: {
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i = 0; i < downloadSourceDO.length; i++) {
                    if (downloadSourceDO[i].getStatus() ==
                        DownloadSourceDO.UEBERTRAGUNG) {
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[
                    kinder.size()]);
            }
            case WAITING_DOWNLOADS: {
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i = 0; i < downloadSourceDO.length; i++) {
                    if (downloadSourceDO[i].getStatus() == DownloadSourceDO.IN_WARTESCHLANGE
                        || downloadSourceDO[i].getStatus() == DownloadSourceDO.WARTESCHLANGE_VOLL) {
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[
                    kinder.size()]);
            }
            case REST_DOWNLOADS: {
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i = 0; i < downloadSourceDO.length; i++) {
                    if (downloadSourceDO[i].getStatus() !=
                        DownloadSourceDO.UEBERTRAGUNG
                        &&
                        downloadSourceDO[i].getStatus() != DownloadSourceDO.IN_WARTESCHLANGE
                        && downloadSourceDO[i].getStatus() != DownloadSourceDO.WARTESCHLANGE_VOLL) {
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[
                    kinder.size()]);
            }
            default: {
                return null;
            }
        }
    }

    public DownloadDO getDownloadDO() {
        return downloadDO;
    }

    public String toString() {
        if (type == ROOT_NODE) {
            return downloadDO.getFilename();
        }
        else {
            return text + " (" + getChildCount() + ")";
        }
    }

    public int getType() {
        return type;
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        if (type == LOADING_DOWNLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.ladendedownloads"));
        }
        else if (type == WAITING_DOWNLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.wartendedownloads"));
        }
        else if (type == REST_DOWNLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.dreckigerrest"));
        }
    }

    public Component getProgressbarComponent(JTable table, Object value) {
        if (type == DownloadMainNode.ROOT_NODE
            && (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN
                || downloadDO.getStatus() == DownloadDO.PAUSIERT)) {
            String prozent = downloadDO.getProzentGeladenAsString();
            String wert = null;
            int i;
            if ( (i = prozent.indexOf(".")) != -1) {
                wert = prozent.substring(0, i);
            }
            else {
                wert = prozent;
            }
            progress.setValue(Integer.parseInt(wert));
            progress.setString(prozent + " %");
            return progress;
        }
        else {
            progressbarLabel.setFont(table.getFont());
            progressbarLabel.setText( (String) value);
            return progressbarLabel;
        }
    }

    public Component getVersionComponent(JTable table, Object value) {
        versionLabel.setFont(table.getFont());
        versionLabel.setText( (String) value);
        return versionLabel;
    }
}
