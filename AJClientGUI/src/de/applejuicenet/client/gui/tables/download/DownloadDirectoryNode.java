package de.applejuicenet.client.gui.tables.download;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadDirectoryNode.java,v 1.14 2004/06/09 11:41:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

public class DownloadDirectoryNode
    implements Node, DownloadNode, DownloadColumnValue, DownloadColumnComponent {

    public static int SORT_NO_SORT = -1;
    public static int SORT_DOWNLOADNAME = 0;
    public static int SORT_GROESSE = 1;
    public static int SORT_BEREITS_GELADEN = 2;
    public static int SORT_RESTZEIT = 3;
    public static int SORT_PROZENT = 4;
    public static int SORT_PWDL = 5;
    public static int SORT_REST_ZU_LADEN = 6;
    public static int SORT_GESCHWINDIGKEIT = 7;
    public static int SORT_STATUS = 7;

    private static int sort = SORT_DOWNLOADNAME;
    private static boolean isAscent = true;

    private int speziellSort = sort;
    private boolean speziellIsAscent = isAscent;

    private Object[] sortedChildNodes;

    private static Map downloads;
    private String verzeichnis;
    private List children = new ArrayList();

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

    private boolean shouldSort(DownloadDO downloadDO, List oldNodes){
        if (downloadDO.getTargetDirectory().compareTo(verzeichnis) == 0) {
            boolean found = false;
            for (int i = 0; i < children.size(); i++) {
                if ( ( (DownloadMainNode) children.get(i)).
                    getDownloadDO().getId()
                    == downloadDO.getId()) {
                    oldNodes.remove(children.get(i));
                    found = true;
                    break;
                }
            }
            if (!found) {
                children.add(new DownloadMainNode(downloadDO));
                return true;
            }
        }
        return false;
    }

    public Object[] getChildren() {
        if (downloads == null) {
            return null;
        }
        DownloadDO downloadDO;
        boolean newSort = false;
        synchronized (this) {
            Iterator it = downloads.values().iterator();
            ArrayList oldNodes = new ArrayList();
            for (int i = 0; i < children.size(); i++) {
                oldNodes.add(children.get(i));
            }
            while (it.hasNext()) {
                downloadDO = (DownloadDO) it.next();
                if (shouldSort(downloadDO, oldNodes)) {
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

    public int getChildCount() {
        Object[] obj = getChildren();
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

    public String getColumn0() {
        return getVerzeichnis();
    }

    public String getColumn1() {
        return "";
    }

    public String getColumn2() {
        return "";
    }

    public String getColumn3() {
        return "";
    }

    public String getColumn4() {
        return "";
    }

    public String getColumn5() {
        return "";
    }

    public String getColumn6() {
        return "";
    }

    public String getColumn7() {
        return "";
    }

    public String getColumn8() {
        return "";
    }

    public String getColumn9() {
        return "";
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
            o1 = ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                getFilename();
            o2 = ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                getFilename();
        }
        else if (sort == SORT_GROESSE) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getGroesse());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getGroesse());
        }
        else if (sort == SORT_BEREITS_GELADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getBereitsGeladen());
        }
        else if (sort == SORT_RESTZEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getRestZeit());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getRestZeit());
        }
        else if (sort == SORT_PROZENT) {
            o1 = new Double( ( (DownloadMainNode) childNodes[row1]).
                            getDownloadDO().getProzentGeladen());
            o2 = new Double( ( (DownloadMainNode) childNodes[row2]).
                            getDownloadDO().getProzentGeladen());
        }
        else if (sort == SORT_PWDL) {
            o1 = new Integer( ( (DownloadMainNode) childNodes[row1]).
                             getDownloadDO().getPowerDownload());
            o2 = new Integer( ( (DownloadMainNode) childNodes[row2]).
                             getDownloadDO().getPowerDownload());
        }
        else if (sort == SORT_REST_ZU_LADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getBereitsGeladen());
        }
        else if (sort == SORT_GESCHWINDIGKEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getSpeedInBytes());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getSpeedInBytes());
        }
        else if (sort == SORT_STATUS) {
            o1 = ( (DownloadMainNode)
                  childNodes[row1]).getDownloadDO().getStatusAsString();
            o2 = ( (DownloadMainNode)
                  childNodes[row2]).getDownloadDO().getStatusAsString();
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
