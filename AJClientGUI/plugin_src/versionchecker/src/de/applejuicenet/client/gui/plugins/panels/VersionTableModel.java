package de.applejuicenet.client.gui.plugins.panels;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/panels/Attic/VersionTableModel.java,v 1.2 2004/01/27 15:49:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: VersionTableModel.java,v $
 * Revision 1.2  2004/01/27 15:49:17  maj0r
 * CVS-Header eingefuegt.
 *
 *
 */

public class VersionTableModel
    extends AbstractTableModel {
    final static String[] COL_NAMES = {
        "", "Windows", "Linux", "Mac", "Solaris", "OS/2", "FreeBSD", "NetWare", "?"};

    private VersionHolder[] versions = null;

    public Object getRow(int row) {
        if ( (versions != null) && (row < versions.length)) {
            return versions[row];
        }
        return null;
    }

    public Object getValueAt(int row, int column) {
        if ( (versions == null) || (row >= versions.length)) {
            return "";
        }

        VersionHolder versionHolder = versions[row];
        if (versionHolder == null) {
            return "";
        }

        switch (column) {
            case 0:
                return versionHolder.versionsNr;
            case 1:
                return Integer.toString(versionHolder.countWin);
            case 2:
                return Integer.toString(versionHolder.countLinux);
            case 3:
                return Integer.toString(versionHolder.countMac);
            case 4:
                return Integer.toString(versionHolder.countSolaris);
            case 5:
                return Integer.toString(versionHolder.countOS2);
            case 6:
                return Integer.toString(versionHolder.countFreeBSD);
            case 7:
                return Integer.toString(versionHolder.countNetWare);
            case 8:
                return Integer.toString(versionHolder.countSonstige);
            default:
                return "";
        }
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public int getRowCount() {
        if (versions == null) {
            return 0;
        }
        return versions.length;
    }

    public Class getClass(int index) {
        return String.class;
    }

    public void setTable(HashMap changedContent) {
        versions = order((VersionHolder[])changedContent.values().toArray(new VersionHolder[changedContent.size()]));
        this.fireTableDataChanged();
    }

    private VersionHolder[] order(VersionHolder[] versionHolder){
        int n = versionHolder.length;
        VersionHolder tmp;
        for (int i = 0; i < n - 1; i++) {
            int k = i;
            for (int j = i + 1; j < n; j++) {
                if (compare(versionHolder, j, k) > 0) {
                    k = j;
                }
            }
            tmp = versionHolder[i];
            versionHolder[i] = versionHolder[k];
            versionHolder[k] = tmp;
        }
        return versionHolder;
    }

    private int compare(VersionHolder[] childNodes, int row1, int row2) {
        String version1 = childNodes[row1].versionsNr;
        String version2 = childNodes[row2].versionsNr;
        StringTokenizer tokenizerVersion1 = new StringTokenizer(version1, ".");
        StringTokenizer tokenizerVersion2 = new StringTokenizer(version2, ".");
        return compareVersion(tokenizerVersion1, tokenizerVersion2);
    }

    private int compareVersion(StringTokenizer version1, StringTokenizer version2){
        int part1 = Integer.parseInt(version1.nextToken());
        int part2 = Integer.parseInt(version2.nextToken());
        if (part2 != part1){
            if (part2 > part1){
                return 1;
            }
            else{
                return -1;
            }
        }
        else{
            if (version1.hasMoreTokens()){
                return compareVersion(version1, version2);
            }
            else{
                return 0;
            }
        }
    }
}