package de.applejuicenet.client.gui.tables.share;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.exception.NodeAlreadyExistsException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.util.HashMap;
import java.io.File;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/ShareNode.java,v 1.1 2003/07/02 13:54:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareNode.java,v $
 * Revision 1.1  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett �berarbeitet.
 *
 *
 */

public class ShareNode implements Node {
  private static ImageIcon leafIcon;
  private static ImageIcon treeIcon;
  private static HashMap directoryNodes = new HashMap();

  private ShareDO shareDO;
  private HashMap children = new HashMap();
  private ShareNode parent;
  private String path;

  public ShareNode(ShareNode parent, ShareDO shareDO) {
    initIcons();
    path = "";
    this.parent = parent;
    this.shareDO = shareDO;
  }

  public ShareNode(ShareNode parent, String path) throws NodeAlreadyExistsException{
    initIcons();
    this.parent = parent;
    shareDO = null;
    this.path = path;
    MapSetStringKey key = new MapSetStringKey(path);
    if (directoryNodes.containsKey(key)){
        throw new NodeAlreadyExistsException("ShareNode mit diesem Pfad bereits vorhanden");
    }
    else{
        directoryNodes.put(key, this);
    }
  }

  public static ShareNode getNodeByPath(String path){
      ShareNode result = (ShareNode)directoryNodes.get(new MapSetStringKey(path));
      return result;
  }

  public ShareNode getParent(){
      return parent;
  }

  private void initIcons() {
    if (leafIcon == null) {
      IconManager im = IconManager.getInstance();
      leafIcon = im.getIcon("treeRoot");
      treeIcon = im.getIcon("tree");
    }
  }

  public boolean isLeaf(){
    if (children.size()==0){
        return true;
    }
    else{
        return false;
    }
  }

  public String getPath(){
      return path;
  }

  public Icon getConvenientIcon() {
    if (path.length()==0) {
      return leafIcon;
    }
    return treeIcon;
  }

  public ShareNode addChild(ShareDO shareDOtoAdd) {
    ShareNode childNode = new ShareNode(this, shareDOtoAdd);
    children.put(new MapSetStringKey(shareDOtoAdd.getId()), childNode);
    return childNode;
  }

  public void addDirectory(ShareNode shareNodeToAdd) {
    shareNodeToAdd.setParent(this);
    children.put(new MapSetStringKey(shareNodeToAdd.getPath()), shareNodeToAdd);
  }

  public ShareDO getDO() {
    return shareDO;
  }

  public void setParent(ShareNode parentNode){
    parent = parentNode;
  }

  public String toString() {
    if (isLeaf() && parent!=null){
        return getDO().getShortfilename();
    }
    else if (parent!=null){
        return path;
    }
    else{
        return "";
    }
  }

  public HashMap getChildrenMap() {
    return children;
  }

  public void removeChild(ShareNode toRemove){
      children.remove(new MapSetStringKey(toRemove.getDO().getId()));
  }

  public void removeAllChildren(){
      if (parent==null){
          directoryNodes.clear();
          directoryNodes.put("/", this);
      }
      children.clear();
  }
  protected Object[] getChildren() {
    return children.values().toArray(new ShareNode[children.size()]);
  }

	protected void nodeChanged() {
	    /*ShareNode parent = getParent();

	    if (parent != null) {
		FileNode[]   path = parent.getPath();
		int[]        index = { getIndexOfChild(parent, this) };
		Object[]     children = { this };

		fireTreeNodesChanged(FileSystemModel2.this, path,  index,
				     children);
	    }*/
	}
}