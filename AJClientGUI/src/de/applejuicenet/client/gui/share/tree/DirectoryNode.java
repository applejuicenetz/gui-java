package de.applejuicenet.client.gui.share.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.applejuicenet.client.gui.components.tree.ApplejuiceNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.shared.dac.DirectoryDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/tree/DirectoryNode.java,v 1.1 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class DirectoryNode
    extends DefaultMutableTreeNode
    implements Node, ApplejuiceNode {
    private static final long serialVersionUID = 5049239281851486091L;
	public static final int NOT_SHARED = 0;
    public static final int SHARED_WITH_SUB = 1;
    public static final int SHARED_WITHOUT_SUB = 2;
    public static final int SHARED_SOMETHING = 3;
    public static final int SHARED_SUB = 4;

    private static Set shareDirs = new HashSet();
    private static Map icons = new HashMap();
    private static boolean initialized = false;

    private DirectoryDO directoryDO;
    private List children = null;
    private DirectoryNode parent;

    public DirectoryNode(DirectoryNode parent, DirectoryDO directoryDO) {
        this.parent = parent;
        this.directoryDO = directoryDO;
    }

    public DirectoryNode() {
        this.parent = null;
        this.directoryDO = null;
        children = new ArrayList();
        ApplejuiceFassade.getInstance().getDirectory(null, this);
    }

    public static void setShareDirs(Set newShareDirs) {
        shareDirs = newShareDirs;
    }

    public int getChildCount() {
        return getChildren().length;
    }

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return false;
    }

    public int getShareMode() {
        if (directoryDO == null) {
            return NOT_SHARED;
        }
        Iterator it = shareDirs.iterator();
        int shareMode = NOT_SHARED;
        while (it.hasNext()) {
            ShareEntry value = (ShareEntry) it.next();
            if (value.getDir().toLowerCase().startsWith(directoryDO.getPath().
                toLowerCase())) {
                if (directoryDO.getPath().length() < value.getDir().length()
                    &&
                    directoryDO.getPath().lastIndexOf(ApplejuiceFassade.separator) ==
                    value.getDir().lastIndexOf(ApplejuiceFassade.separator)) {
                    continue;
                }
                if (value.getShareMode() == ShareEntry.SUBDIRECTORY) {
                    if (directoryDO.getPath().toLowerCase().startsWith(value.
                        getDir().toLowerCase())) {
                        return SHARED_WITH_SUB;
                    }
                    else {
                        return SHARED_SOMETHING;
                    }
                }
                else {
                    if (directoryDO.getPath().toLowerCase().startsWith(value.
                        getDir().toLowerCase())) {
                        return SHARED_WITHOUT_SUB;
                    }
                    else {
                        return SHARED_SOMETHING;
                    }
                }
            }
        }
        int parentShareMode = parent.getShareMode();
        if (parentShareMode == SHARED_WITH_SUB || parentShareMode == SHARED_SUB) {
            shareMode = SHARED_SUB;
        }
        return shareMode;
    }

    public Icon getShareModeIcon() {
        if (!initialized){
            initializeImages();
        }
        switch (getShareMode()) {
            case SHARED_WITH_SUB:
                return (Icon)icons.get("sharedwsub");
            case SHARED_SUB:
                return (Icon)icons.get("sharedwsub");
            case SHARED_WITHOUT_SUB:
                return (Icon)icons.get("sharedwosub");
            case SHARED_SOMETHING:
                return (Icon)icons.get("somethingshared");
            default:
                return (Icon)icons.get("notshared");
        }
    }

    public Icon getConvenientIcon() {
        if (directoryDO == null) {
            return null; //rootNode
        }
        if (!initialized){
            initializeImages();
        }
        switch (directoryDO.getType()) {
            case DirectoryDO.TYPE_DESKTOP:
                return (Icon)icons.get("server");
            case DirectoryDO.TYPE_DISKETTE:
                return (Icon)icons.get("diskette");
            case DirectoryDO.TYPE_LAUFWERK:
                return (Icon)icons.get("laufwerk");
            case DirectoryDO.TYPE_ORDNER:
                return (Icon)icons.get("tree");
            case DirectoryDO.TYPE_RECHNER:
                return (Icon)icons.get("server");
            default:
                return null;
        }
    }

    public DirectoryDO getDO() {
        return directoryDO;
    }

    public String toString() {
        if (directoryDO == null) {
            return "rootNode";
        }
        return directoryDO.getName();
    }

    public ApplejuiceNode addChild(DirectoryDO childDirectoryDO) {
        if (children == null) {
            children = new ArrayList();
        }
        DirectoryNode childNode = new DirectoryNode(this, childDirectoryDO);
        children.add(childNode);
        return childNode;
    }

    private void sortChildren() {
        if (children == null || children.size() < 2) {
            return;
        }
        int n = children.size();
        int k;
        for (int i = 0; i < n - 1; i++) {
            k = i;
            for (int j = i + 1; j < n; j++) {
                if (!((DirectoryNode)children.get(j)).getDO().isFileSystem()){
                    continue;
                }
                if (children.get(j).toString().compareToIgnoreCase(children.get(
                    k).toString()) < 0) {
                    k = j;
                }
            }
            Object tmp = children.get(i);
            children.set(i, children.get(k));
            children.set(k, tmp);
        }
    }

    protected Object[] getChildren() {
        if (children == null) {
            children = new ArrayList();
            ApplejuiceFassade.getInstance().getDirectory(directoryDO.getPath(), this);
            sortChildren();
        }
        return children.toArray(new DirectoryNode[children.size()]);
    }

    private static void initializeImages(){
        initialized = true;
        IconManager im = IconManager.getInstance();
        icons.put("server", im.getIcon("server"));
        icons.put("diskette", im.getIcon("diskette"));
        icons.put("laufwerk", im.getIcon("laufwerk"));
        icons.put("tree", im.getIcon("tree"));
        icons.put("sharedwsub", im.getIcon("sharedwsub"));
        icons.put("sharedwsub", im.getIcon("sharedwsub"));
        icons.put("sharedwosub", im.getIcon("sharedwosub"));
        icons.put("somethingshared", im.getIcon("somethingshared"));
        icons.put("notshared", im.getIcon("notshared"));
        icons.put("warten", im.getIcon("warten"));
    }

    public static int getMaxHeight(){
        if (!initialized){
            initializeImages();
        }
        Iterator it = icons.values().iterator();
        Icon icon = null;
        int maxHeight = 0;
        while (it.hasNext()){
            icon = (Icon)it.next();
            if (icon.getIconHeight() > maxHeight){
                maxHeight = icon.getIconHeight();
            }
        }
        return maxHeight;
    }
}
