package de.applejuicenet.client.gui.share.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Directory;
import de.applejuicenet.client.fassade.entity.ShareEntry;
import de.applejuicenet.client.fassade.entity.ShareEntry.SHAREMODE;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.components.tree.ApplejuiceNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/tree/DirectoryNode.java,v 1.6 2005/02/21 17:37:14 maj0r Exp $
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
	public static final int NOT_SHARED = 0;
    public static final int SHARED_WITH_SUB = 1;
    public static final int SHARED_WITHOUT_SUB = 2;
    public static final int SHARED_SOMETHING = 3;
    public static final int SHARED_SUB = 4;
    
    private static Logger logger = Logger.getLogger(DirectoryNode.class);

    private static Set<ShareEntry> shareDirs = new HashSet<ShareEntry>();
    private static Map<String, Icon> icons = new HashMap<String, Icon>();
    private static boolean initialized = false;

    private Directory directory;
    private List children = null;
    private DirectoryNode parent;

    public DirectoryNode(DirectoryNode parent, Directory directory) {
        this.parent = parent;
        this.directory = directory;
    }

    public DirectoryNode() {
        this.parent = null;
        this.directory = null;
        try {
        	children = new ArrayList();
			List<Directory> directories = AppleJuiceClient.getAjFassade().getDirectories(null);
			for (Directory curDirectory : directories){
				children.add(new DirectoryNode(this, curDirectory));
			}
		} catch (IllegalArgumentException e) {
			logger.error(e);
		}
    }

    public static void setShareDirs(Set<ShareEntry> newShareDirs) {
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
        if (directory == null) {
            return NOT_SHARED;
        }
        int shareMode = NOT_SHARED;
        for (ShareEntry curShareEntry : shareDirs) {
        	if (directory.getName().indexOf("crypto") != -1){
        		int i = 0;
        	}
            if (curShareEntry.getDir().toLowerCase().startsWith(directory.getPath().
                toLowerCase())) {
                if (directory.getPath().length() < curShareEntry.getDir().length()
                    &&
                    directory.getPath().lastIndexOf(ApplejuiceFassade.separator) ==
                    	curShareEntry.getDir().lastIndexOf(ApplejuiceFassade.separator)) {
                    continue;
                }
                if (curShareEntry.getShareMode() == SHAREMODE.SUBDIRECTORY) {
                    if (directory.getPath().toLowerCase().startsWith(curShareEntry.
                        getDir().toLowerCase())) {
                        return SHARED_WITH_SUB;
                    }
                    else {
                        return SHARED_SOMETHING;
                    }
                }
                else {
                    if (directory.getPath().toLowerCase().startsWith(curShareEntry.
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
                return icons.get("sharedwsub");
            case SHARED_SUB:
                return icons.get("sharedwsub");
            case SHARED_WITHOUT_SUB:
                return icons.get("sharedwosub");
            case SHARED_SOMETHING:
                return icons.get("somethingshared");
            default:
                return icons.get("notshared");
        }
    }

    public Icon getConvenientIcon() {
        if (directory == null) {
            return null; //rootNode
        }
        if (!initialized){
            initializeImages();
        }
        switch (directory.getType()) {
            case Directory.TYPE_DESKTOP:
                return icons.get("server");
            case Directory.TYPE_DISKETTE:
                return icons.get("diskette");
            case Directory.TYPE_LAUFWERK:
                return icons.get("laufwerk");
            case Directory.TYPE_ORDNER:
                return icons.get("tree");
            case Directory.TYPE_RECHNER:
                return icons.get("server");
            default:
                return null;
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public String toString() {
        if (directory == null) {
            return "rootNode";
        }
        return directory.getName();
    }

    public ApplejuiceNode addChild(Directory childDirectory) {
        if (children == null) {
            children = new ArrayList();
        }
        DirectoryNode childNode = new DirectoryNode(this, childDirectory);
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
                if (!((DirectoryNode)children.get(j)).getDirectory().isFileSystem()){
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
            try {
    			List<Directory> directories = AppleJuiceClient.getAjFassade().
					getDirectories(directory.getPath());
    			for (Directory curDirectory : directories){
    				children.add(new DirectoryNode(this, curDirectory));
    			}
			} catch (IllegalArgumentException e) {
				logger.error(e);
			}
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
        int maxHeight = 0;
        for (Icon curIcon : icons.values()){
            if (curIcon.getIconHeight() > maxHeight){
                maxHeight = curIcon.getIconHeight();
            }
        }
        return maxHeight;
    }
}
