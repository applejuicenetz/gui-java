package de.applejuicenet.client.gui.share.table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.FileTypeHelper;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.ShareDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/ShareNode.java,v 1.1 2004/10/15 13:39:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class ShareNode
    implements Node {
    private ImageIcon leafIcon = null;
    private static ImageIcon treeIcon;

    static {
        IconManager im = IconManager.getInstance();
        treeIcon = im.getIcon("tree");
    }

    private ShareDO shareDO;
    private Map children = new HashMap();
    private ShareNode parent;
    private String path;
    private Object[] sortedChildren = null;

    public ShareNode(ShareNode parent, ShareDO shareDO) {
        this.parent = parent;
        path = "";
        if (parent != null) {
            String bisherigerPath = getCompletePath();
            String restPath = shareDO.getFilename();
            while (restPath.indexOf(ApplejuiceFassade.separator) == 0) {
                restPath = restPath.substring(1);
            }
            if (bisherigerPath.length() != 0 &&
                restPath.substring(0, bisherigerPath.
                                   length()).compareTo(bisherigerPath) == 0) {
                restPath = restPath.substring(bisherigerPath.
                                              length());
            }
            if (restPath.substring(0, 1).compareTo(ApplejuiceFassade.separator) ==
                0) {
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
            if (parentPath.length() == 0) {
                return path;
            }
            else {
                return parentPath + ApplejuiceFassade.separator + path;
            }
        }
        else {
            return path;
        }
    }

    public Icon getConvenientIcon() {
        if (isLeaf()) {
        	if (leafIcon == null){
        		String fileType = FileTypeHelper.calculatePossibleFileType(shareDO.getShortfilename());
        		leafIcon = IconManager.getInstance().getIcon(fileType);
        	}
            return leafIcon;
        }
        return treeIcon;
    }

    public ShareNode addChild(ShareDO shareDOtoAdd) {
        String bisherigerPath = getCompletePath();
        String restPath = shareDOtoAdd.getFilename();
        while (restPath.indexOf(ApplejuiceFassade.separator) == 0) {
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
            String aKey = tmpPath;
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
            String key = Integer.toString(shareDOtoAdd.getId());
            if (children.containsKey(key)) {
                childNode = (ShareNode) children.get(key);
                ShareDO shareDO = childNode.getDO();
                shareDO.setPrioritaet(shareDOtoAdd.getPrioritaet());
            }
            else {
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

    public Map getChildrenMap() {
        return children;
    }

    public void removeAllChildren() {
        children.clear();
        sortedChildren = null;
    }

    protected Object[] getChildren() {
        if (sortedChildren == null) {
            ShareNode[] shareNodes = (ShareNode[]) children.values().toArray(new
                ShareNode[children.size()]);
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

    private int compare(ShareNode shareNode1, ShareNode shareNode2) {
        if (shareNode1.getDO() == null && shareNode2.getDO() != null) {
            return -1;
        }
        else if (shareNode1.getDO() != null && shareNode2.getDO() == null) {
            return 1;
        }
        else {
            return shareNode1.toString().compareToIgnoreCase(shareNode2.
                toString());
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
