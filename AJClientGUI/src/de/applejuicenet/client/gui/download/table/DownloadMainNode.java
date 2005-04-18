package de.applejuicenet.client.gui.download.table;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.shared.FileTypeHelper;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadMainNode.java,v 1.9 2005/04/18 14:54:39 maj0r Exp $
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
    private Download download;
    private JProgressBar progress;
    private JLabel progressbarLabel;
    private JLabel versionLabel;

    private static Icon waitingIcon = IconManager.getInstance().getIcon("cool");
    private static Icon loadingIcon = IconManager.getInstance().getIcon(
        "download");
    private static Icon restIcon = IconManager.getInstance().getIcon("eek");
    private Icon rootIcon = null;

    public DownloadMainNode(Download download) {
        type = ROOT_NODE;
        this.download = download;
        children = new DownloadMainNode[3];
        children[0] = new DownloadMainNode(download, LOADING_DOWNLOADS);
        children[1] = new DownloadMainNode(download, WAITING_DOWNLOADS);
        children[2] = new DownloadMainNode(download, REST_DOWNLOADS);
        init();
    }

    public DownloadMainNode(Download download, int type) {
        super();
        this.type = type;
        this.download = download;
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
        		String fileType = FileTypeHelper.calculatePossibleFileType(download.getFilename());
        		rootIcon = IconManager.getInstance().getIcon(fileType);
        	}
        	return rootIcon;
        }
        else {
            return null;
        }
    }

    public int getChildCount(boolean sort) {
        Object[] obj = getChildren();
        if (obj == null) {
            return 0;
        }
        else {
            return obj.length;
        }
    }

    public boolean isLeaf() {
        return (getChildCount(false) == 0) ? true : false;
    }

    public Object[] getChildren() {
        switch (type) {
            case ROOT_NODE: {
                if (download.getStatus() == Download.SUCHEN_LADEN) {
                    return children;
                }
                else {
                    boolean childFound = false;
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].getChildCount(false) > 0) {
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
                ArrayList<DownloadSource> kinder = new ArrayList<DownloadSource>();
                DownloadSource[] downloadSource = download.getSources();
                for (int i = 0; i < downloadSource.length; i++) {
                    if (downloadSource[i].getStatus() ==
                        DownloadSource.UEBERTRAGUNG) {
                        kinder.add(downloadSource[i]);
                    }
                }
                return (DownloadSource[]) kinder.toArray(new DownloadSource[
                    kinder.size()]);
            }
            case WAITING_DOWNLOADS: {
                ArrayList<DownloadSource> kinder = new ArrayList<DownloadSource>();
                DownloadSource[] downloadSource = download.getSources();
                for (int i = 0; i < downloadSource.length; i++) {
                    if (downloadSource[i].getStatus() == DownloadSource.IN_WARTESCHLANGE
                        || downloadSource[i].getStatus() == DownloadSource.WARTESCHLANGE_VOLL) {
                        kinder.add(downloadSource[i]);
                    }
                }
                return (DownloadSource[]) kinder.toArray(new DownloadSource[
                    kinder.size()]);
            }
            case REST_DOWNLOADS: {
                ArrayList<DownloadSource> kinder = new ArrayList<DownloadSource>();
                DownloadSource[] downloadSource = download.getSources();
                for (int i = 0; i < downloadSource.length; i++) {
                    if (downloadSource[i].getStatus() !=
                        DownloadSource.UEBERTRAGUNG
                        &&
                        downloadSource[i].getStatus() != DownloadSource.IN_WARTESCHLANGE
                        && downloadSource[i].getStatus() != DownloadSource.WARTESCHLANGE_VOLL) {
                        kinder.add(downloadSource[i]);
                    }
                }
                return (DownloadSource[]) kinder.toArray(new DownloadSource[
                    kinder.size()]);
            }
            default: {
                return null;
            }
        }
    }

    public Download getDownload() {
        return download;
    }

    public String toString() {
        if (type == ROOT_NODE) {
            return download.getFilename();
        }
        else {
            return text + " (" + getChildCount(false) + ")";
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
            && (download.getStatus() == Download.SUCHEN_LADEN
                || download.getStatus() == Download.PAUSIERT)) {
            String prozent = download.getProzentGeladenAsString();
            String wert = null;
            int i;
            if ( (i = prozent.indexOf(",")) != -1) {
                wert = prozent.substring(0, i);
            }
            else {
                wert = prozent;
            }
            try{
                progress.setValue(Integer.parseInt(wert));
            }
            catch(NumberFormatException nfE){
                progress.setValue(0);
            }
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
