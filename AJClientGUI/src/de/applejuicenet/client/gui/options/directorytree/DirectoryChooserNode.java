package de.applejuicenet.client.gui.options.directorytree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.entity.Directory;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.components.tree.ApplejuiceNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/directorytree/DirectoryChooserNode.java,v 1.6 2005/03/07 13:41:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DirectoryChooserNode
    extends DefaultMutableTreeNode
    implements Node, ApplejuiceNode {
	private Directory directory;
    private List<DirectoryChooserNode> children = null;
    private DirectoryChooserNode parent;

    public DirectoryChooserNode(DirectoryChooserNode parent,
                                Directory directory) {
        this.parent = parent;
        this.directory = directory;
    }

    public DirectoryChooserNode() {
        this.parent = null;
        this.directory = null;
        children = new ArrayList<DirectoryChooserNode>();
        try {
			List<Directory> directories = AppleJuiceClient.getAjFassade().getDirectories(null);
            for (Directory curDirectory : directories){
                addChild(curDirectory);
            }
		} catch (IllegalArgumentException e) {
            // sollte nicht passieren
			throw new RuntimeException(e);
		}
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

    public Icon getConvenientIcon() {
        if (directory == null) {
            return null; //rootNode
        }

        IconManager im = IconManager.getInstance();
        switch (directory.getType()) {
            case Directory.TYPE_DESKTOP:
                return im.getIcon("server");
            case Directory.TYPE_DISKETTE:
                return im.getIcon("diskette");
            case Directory.TYPE_LAUFWERK:
                return im.getIcon("laufwerk");
            case Directory.TYPE_ORDNER:
                return im.getIcon("tree");
            case Directory.TYPE_RECHNER:
                return im.getIcon("server");
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
            children = new ArrayList<DirectoryChooserNode>();
        }
        DirectoryChooserNode childNode = new DirectoryChooserNode(this,
            childDirectory);
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
                if (!children.get(j).getDirectory().isFileSystem()){
                    continue;
                }
                if (children.get(j).toString().compareToIgnoreCase(children.get(
                    k).toString()) < 0) {
                    k = j;
                }
            }
            DirectoryChooserNode tmp = children.get(i);
            children.set(i, children.get(k));
            children.set(k, tmp);
        }
    }

    protected Object[] getChildren() {
        if (children == null) {
            children = new ArrayList<DirectoryChooserNode>();
            List<Directory> directories;
			try {
				directories = AppleJuiceClient.getAjFassade().getDirectories(directory.getPath());
                for (Directory curDirectory : directories){
                    addChild(curDirectory);
                }
                sortChildren();
			} catch (IllegalArgumentException e) {
                // sollte nicht passieren
                throw new RuntimeException(e);
			}
        }
        return children.toArray(new DirectoryChooserNode[children.size()]);
    }

}
