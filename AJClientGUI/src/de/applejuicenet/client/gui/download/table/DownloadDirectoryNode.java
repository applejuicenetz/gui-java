package de.applejuicenet.client.gui.download.table;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.util.DownloadCalculator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadDirectoryNode.java,v 1.8 2005/03/23 06:59:58 loevenwong Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DownloadDirectoryNode
    implements Node, DownloadNode, DownloadColumnComponent {

    public static final int SORT_NO_SORT = -1;
    public static final int SORT_DOWNLOADNAME = 0;
    public static final int SORT_GROESSE = 1;
    public static final int SORT_BEREITS_GELADEN = 2;
    public static final int SORT_RESTZEIT = 3;
    public static final int SORT_PROZENT = 4;
    public static final int SORT_PWDL = 5;
    public static final int SORT_REST_ZU_LADEN = 6;
    public static final int SORT_GESCHWINDIGKEIT = 7;
    public static final int SORT_STATUS = 7;

    private static int sort = SORT_DOWNLOADNAME;
    private static boolean isAscent = true;

    private int speziellSort = sort;
    private boolean speziellIsAscent = isAscent;

    private Object[] sortedChildNodes;

    private static Map downloads;
    private String verzeichnis;
    private List<DownloadMainNode> children = new ArrayList<DownloadMainNode>();

    private JLabel progressbarLabel;
    private JLabel versionLabel;

    public DownloadDirectoryNode(String targetDir) {
        verzeichnis = targetDir;
        progressbarLabel = new JLabel();
        progressbarLabel.setOpaque(true);
        versionLabel = new JLabel();
        versionLabel.setOpaque(true);
    }

    public static void setDownloads(Map downloadsMap) {
        if (downloads == null) {
            downloads = downloadsMap;
        }
    }

    private boolean shouldSort(Download download, List oldNodes){
        if (download.getTargetDirectory().compareTo(verzeichnis) == 0) {
            boolean found = false;
            for (int i = 0; i < children.size(); i++) {
                if ( ( (DownloadMainNode) children.get(i)).
                    getDownload().getId()
                    == download.getId()) {
                    oldNodes.remove(children.get(i));
                    found = true;
                    break;
                }
            }
            if (!found) {
                children.add(new DownloadMainNode(download));
                return true;
            }
        }
        return false;
    }

    public Object[] getChildren(){
        if (downloads == null) {
            return null;
        }
        else{
        	return sortedChildNodes;
        }
    }
    
    public Object[] getChildrenWithSort() {
        if (downloads == null) {
            return null;
        }
        Download download;
        boolean newSort = false;
        synchronized (this) {
            Iterator it = downloads.values().iterator();
            ArrayList oldNodes = new ArrayList();
            for (int i = 0; i < children.size(); i++) {
                oldNodes.add(children.get(i));
            }
            while (it.hasNext()) {
                download = (Download) it.next();
                if (shouldSort(download, oldNodes)) {
                    newSort = true;
                }
            }
            for (int i = 0; i < oldNodes.size(); i++) {
                children.remove(oldNodes.get(i));
                newSort = true;
            }
        }
        if (newSort || sortedChildNodes == null || speziellSort != sort
            || speziellIsAscent != isAscent) {
            return sort( (Object[]) children.toArray(new Object[children.size()]));
        }
        else {
            return sortedChildNodes;
        }
    }

    public static void setSortCriteria(int sortCriteria, boolean ascent) {
        sort = sortCriteria;
        isAscent = ascent;
    }

    public int getChildCount(boolean sort) {
    	Object[] obj;
    	if ((sort || sortedChildNodes == null) && downloads != null){
    		obj = getChildrenWithSort();
    	}
    	else{
    		obj = getChildren();
    	}
        if (obj == null) {
            return 0;
        }
        return getChildren().length;
    }

    public String getVerzeichnis() {
        return verzeichnis;
    }

    public boolean isLeaf() {
        return false;
    }

    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("tree");
    }

    private Object[] sort(Object[] childNodes) {
        speziellIsAscent = isAscent;
        speziellSort = sort;
        if (sort == SORT_NO_SORT) {
            sortedChildNodes = childNodes;
            return childNodes;
        }
        else {
            sortedChildNodes = childNodes;
            int n = sortedChildNodes.length;
            Object tmp;
            for (int i = 0; i < n - 1; i++) {
                int k = i;
                for (int j = i + 1; j < n; j++) {
                    if (isAscent) {
                        if (compare(sortedChildNodes, j, k) < 0) {
                            k = j;
                        }
                    }
                    else {
                        if (compare(sortedChildNodes, j, k) > 0) {
                            k = j;
                        }
                    }
                }
                tmp = sortedChildNodes[i];
                sortedChildNodes[i] = sortedChildNodes[k];
                sortedChildNodes[k] = tmp;
            }
            return sortedChildNodes;
        }
    }

    private int compare(Object[] childNodes, int row1, int row2) {
        Object o1 = null;
        Object o2 = null;
        if (sort == SORT_DOWNLOADNAME) {
            o1 = ( (DownloadMainNode) childNodes[row1]).getDownload().
                getFilename();
            o2 = ( (DownloadMainNode) childNodes[row2]).getDownload().
                getFilename();
        }
        else if (sort == SORT_GROESSE) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getGroesse());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getGroesse());
        }
        else if (sort == SORT_BEREITS_GELADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getBereitsGeladen());
        }
        else if (sort == SORT_RESTZEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getRestZeit());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getRestZeit());
        }
        else if (sort == SORT_PROZENT) {
            o1 = new Double( ( (DownloadMainNode) childNodes[row1]).
                            getDownload().getProzentGeladen());
            o2 = new Double( ( (DownloadMainNode) childNodes[row2]).
                            getDownload().getProzentGeladen());
        }
        else if (sort == SORT_PWDL) {
            o1 = new Integer( ( (DownloadMainNode) childNodes[row1]).
                             getDownload().getPowerDownload());
            o2 = new Integer( ( (DownloadMainNode) childNodes[row2]).
                             getDownload().getPowerDownload());
        }
        else if (sort == SORT_REST_ZU_LADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getBereitsGeladen());
        }
        else if (sort == SORT_GESCHWINDIGKEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getSpeedInBytes());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getSpeedInBytes());
        }
        else if (sort == SORT_STATUS) {
            o1 = ( DownloadCalculator.getStatusAsString(((DownloadMainNode)
                  childNodes[row1]).getDownload()));
            o2 = ( DownloadCalculator.getStatusAsString(((DownloadMainNode)
                    childNodes[row2]).getDownload()));
        }

        if (o1 == null && o2 == null) {
            return 0;
        }
        else if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        else {
            if (o1.getClass().getSuperclass() == Number.class) {
                return compare( (Number) o1, (Number) o2);
            }
            else if (o1.getClass() == Boolean.class) {
                return compare( (Boolean) o1, (Boolean) o2);
            }
            else {
                return ( (String) o1).compareToIgnoreCase( (String) o2);
            }
        }
    }

    public int compare(Number o1, Number o2) {
        double n1 = o1.doubleValue();
        double n2 = o2.doubleValue();
        if (n1 < n2) {
            return -1;
        }
        else if (n1 > n2) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public int compare(Boolean o1, Boolean o2) {
        boolean b1 = o1.booleanValue();
        boolean b2 = o2.booleanValue();
        if (b1 == b2) {
            return 0;
        }
        else if (b1) {
            return 1;
        }
        else {
            return -1;
        }
    }

    public Component getProgressbarComponent(JTable table, Object value) {
        progressbarLabel.setFont(table.getFont());
        progressbarLabel.setText( (String) value);
        return progressbarLabel;
    }

    public Component getVersionComponent(JTable table, Object value) {
        versionLabel.setFont(table.getFont());
        versionLabel.setText( (String) value);
        return versionLabel;
    }
}
