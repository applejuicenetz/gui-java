package de.applejuicenet.client.gui.tables.share;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.dac.ShareDO;
import java.util.ArrayList;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/ShareNode.java,v 1.17 2004/01/05 15:30:22 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareNode.java,v $
 * Revision 1.17  2004/01/05 15:30:22  maj0r
 * Bug #43 gefixt (Danke an flabeg)
 * Shareverzeichnis wird bei Prioritaetenaenderung nicht mehr komplett neu geladen, sondern nur aktualsiert.
 *
 * Revision 1.16  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.15  2003/12/29 10:54:57  maj0r
 * Bug #4 gefixt (Danke an muhviestarr).
 * Shareanzeige bei Prioritaetenaenderung gefixt.
 *
 * Revision 1.14  2003/12/18 10:48:03  maj0r
 * Bei der Sortierung werden nun Ordner zuerst gelistet.
 *
 * Revision 1.13  2003/12/17 20:35:54  maj0r
 * Bug beim Sortieren der Sharetabelle behoben.
 *
 * Revision 1.12  2003/12/16 18:30:06  maj0r
 * Nun ist es auch wieder plattformunabhaengig.
 *
 * Revision 1.11  2003/12/16 17:05:54  maj0r
 * Sharetabelle auf vielfachen Wunsch komplett überarbeitet.
 *
 * Revision 1.10  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.9  2003/09/01 15:50:52  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.8  2003/08/27 11:19:30  maj0r
 * Prioritaet setzen und aufheben vollstaendig implementiert.
 * Button für 'Share erneuern' eingefuehrt.
 *
 * Revision 1.7  2003/08/26 06:20:10  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.6  2003/08/25 19:28:52  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.5  2003/08/25 07:23:25  maj0r
 * Kleine Korrekturen.
 *
 * Revision 1.4  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.3  2003/08/15 14:44:48  maj0r
 * Schreibfehler.
 *
 * Revision 1.2  2003/08/14 20:08:42  maj0r
 * Tree fuer Shareauswahl eingefuegt, aber noch nicht fertiggestellt.
 *
 * Revision 1.1  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 *
 */

public class ShareNode
    implements Node {
    private static ImageIcon leafIcon;
    private static ImageIcon treeIcon;

    static {
        IconManager im = IconManager.getInstance();
        leafIcon = im.getIcon("treeRoot");
        treeIcon = im.getIcon("tree");
    }

    private ShareDO shareDO;
    private HashMap children = new HashMap();
    private ShareNode parent;
    private String path;
    private Object[] sortedChildren = null;

    public ShareNode(ShareNode parent, ShareDO shareDO) {
        this.parent = parent;
        path = "";
        if (parent != null) {
            String bisherigerPath = getCompletePath();
            String restPath = shareDO.getFilename();
            while (restPath.indexOf(ApplejuiceFassade.separator)==0){
                restPath = restPath.substring(1);
            }
            if (bisherigerPath.length()!=0 && restPath.substring(0, bisherigerPath.
                length()).compareTo(bisherigerPath)==0){
                restPath = restPath.substring(bisherigerPath.
                    length());
            }
            if (restPath.substring(0, 1).compareTo(ApplejuiceFassade.separator)==0){
                restPath = restPath.substring(1);
            }
            int pos = restPath.indexOf(ApplejuiceFassade.separator);
            if (pos != -1) {
                path = restPath.substring(0, pos);
            }
            else {
                this.shareDO = shareDO;
            }
        }
    }

    public ShareNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return (shareDO != null);
    }

    public String getPath() {
        return path;
    }

    public String getCompletePath() {
        if (parent != null) {
            String parentPath = parent.getCompletePath();
            if (parentPath.length()==0){
                return path;
            }
            else{
                return parentPath + ApplejuiceFassade.separator + path;
            }
        }
        else {
            return path;
        }
    }

    public Icon getConvenientIcon() {
        if (path.length() == 0) {
            return leafIcon;
        }
        return treeIcon;
    }

    public ShareNode addChild(ShareDO shareDOtoAdd) {
        String bisherigerPath = getCompletePath();
        String restPath = shareDOtoAdd.getFilename();
        while (restPath.indexOf(ApplejuiceFassade.separator)==0){
            restPath = restPath.substring(1);
        }
        restPath = restPath.substring(bisherigerPath.length());
        int pos = restPath.indexOf(ApplejuiceFassade.separator);
        while (pos == 0) {
            restPath = restPath.substring(pos + 1);
            pos = restPath.indexOf(ApplejuiceFassade.separator);
        }
        ShareNode childNode = null;
        if (pos != -1) {
            String tmpPath = restPath.substring(0, pos);
            MapSetStringKey aKey = new MapSetStringKey(tmpPath);
            if (children.containsKey(aKey)) {
                childNode = (ShareNode) children.get(aKey);
                childNode.addChild(shareDOtoAdd);
            }
            else {
                childNode = new ShareNode(this, shareDOtoAdd);
                children.put(aKey, childNode);
                childNode.addChild(shareDOtoAdd);
            }
        }
        else {
            MapSetStringKey key = new MapSetStringKey(shareDOtoAdd.getId());
            if (children.containsKey(key)){
                childNode = (ShareNode)children.get(key);
                ShareDO shareDO = childNode.getDO();
                shareDO.setPrioritaet(shareDOtoAdd.getPrioritaet());
            }
            else{
                childNode = new ShareNode(this, shareDOtoAdd);
                children.put(key, childNode);
                sortedChildren = null;
            }
        }
        return childNode;
    }

    public ShareDO getDO() {
        return shareDO;
    }

    public void setParent(ShareNode parentNode) {
        parent = parentNode;
    }

    public String toString() {
        if (isLeaf() && parent != null) {
            return getDO().getShortfilename();
        }
        else if (parent != null) {
            return path;
        }
        else {
            return "";
        }
    }

    public HashMap getChildrenMap() {
        return children;
    }

    public void removeAllChildren() {
        children.clear();
        sortedChildren=null;
    }

    protected Object[] getChildren() {
        if (sortedChildren==null){
            ShareNode[] shareNodes = (ShareNode[]) children.values().toArray(new ShareNode[children.size()]);
            sortedChildren = sort(shareNodes);
        }
        return sortedChildren;
    }

    private Object[] sort(ShareNode[] childNodes) {
        int n = childNodes.length;
        ShareNode tmp;
        for (int i = 0; i < n - 1; i++) {
            int k = i;
            for (int j = i + 1; j < n; j++) {
                if (compare(childNodes[j], childNodes[k]) < 0) {
                    k = j;
                }
            }
            tmp = childNodes[i];
            childNodes[i] = childNodes[k];
            childNodes[k] = tmp;
        }
        return childNodes;
    }

    private int compare(ShareNode shareNode1, ShareNode shareNode2){
        if (shareNode1.getDO()==null && shareNode2.getDO()!=null){
            return -1;
        }
        else if (shareNode1.getDO()!=null && shareNode2.getDO()==null){
            return 1;
        }
        else{
            return shareNode1.toString().compareToIgnoreCase(shareNode2.toString());
        }
    }

    public void setPriority(int prio) {
        if (isLeaf()) {
            ApplejuiceFassade.getInstance().setPrioritaet(shareDO.getId(), prio);
        }
        else {
            Iterator it = children.values().iterator();
            while (it.hasNext()) {
                ( (ShareNode) it.next()).setPriority(prio);
            }
        }
    }
}
