package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.exception.NodeAlreadyExistsException;
import de.applejuicenet.client.gui.tables.Node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.util.HashMap;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadNode.java,v 1.3 2003/07/03 19:11:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadNode.java,v $
 * Revision 1.3  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.2  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 *
 */

public class DownloadNode implements Node {
  private static ImageIcon leafIcon;
  private static ImageIcon uebertrageIcon;
  private static HashMap directoryNodes = new HashMap();

  public static final int ROOT_NODE = 0;
  public static final int DIRECTORY_NODE = 1;
  public static final int DOWNLOAD_NODE = 2;
  public static final int SOURCE_NODE = 3;

  private int nodetype;

  private DownloadDO downloadDO;
  private DownloadSourceDO downloadSourceDO = null;
  private HashMap children;
  private String pfad = "";

  public DownloadNode(DownloadDO downloadDO) {
    if (downloadDO==null){
        return;
    }
    nodetype = DOWNLOAD_NODE;
    initIcons();
    this.downloadDO = downloadDO;
    children = new HashMap();
    String pfad = downloadDO.getTargetDirectory();
    DownloadNode parent = null;
    if (pfad==null || pfad.length()==0){
        parent = (DownloadNode)directoryNodes.get(new MapSetStringKey("rootPfad"));
        parent.addChild(this);
    }
    else{
        parent = (DownloadNode)directoryNodes.get(new MapSetStringKey("pfad"));
        if (parent==null){      //Verzeichnis noch nicht vorhanden
            parent = new DownloadNode(pfad);
        }
        parent.addChild(this);
    }
    DownloadSourceDO[] sources = downloadDO.getSources();
    for (int i=0; i<sources.length; i++){
        DownloadNode child = new DownloadNode(sources[i]);
        addChild(child);
    }
  }

  private DownloadNode(DownloadSourceDO downloadSource){
      nodetype = SOURCE_NODE;
      downloadSourceDO = downloadSource;
      this.downloadDO = null;
  }

  public int getNodeType(){
      return nodetype;
  }

  private DownloadNode(String pfad){
      nodetype = DIRECTORY_NODE;
      directoryNodes.put(new MapSetStringKey(pfad), this);
      children = new HashMap();
      this.pfad = pfad;
      DownloadNode parent = (DownloadNode)directoryNodes.get(new MapSetStringKey("rootPfad"));
      parent.addChild(this);
      this.downloadDO = null;
  }

  public DownloadNode() {       // nur zum Erstellen des Root-Nodes
      nodetype = ROOT_NODE;
      initIcons();
      this.pfad = "rootPfad";
      directoryNodes.put(new MapSetStringKey(pfad), this);
      children = new HashMap();
      this.downloadDO = null;
  }

  private void initIcons() {
    if (leafIcon == null) {
      IconManager im = IconManager.getInstance();
      leafIcon = im.getIcon("treeRoot");
      uebertrageIcon = im.getIcon("treeUebertrage");
    }
  }

  private void addChild(DownloadNode child){
      children.put(new MapSetStringKey(child.getId()), child);
  }

  public boolean isLeaf(){
    if (nodetype==SOURCE_NODE){
        return true;
    }
    else{
        return false;
    }
  }

  public Icon getConvenientIcon() {
      if (isLeaf()){
          return uebertrageIcon;
      }
      else{
        return leafIcon;
      }
  }

  public String getId(){
      if (nodetype==SOURCE_NODE){
          return downloadSourceDO.getId();
      }
      else if (nodetype==DOWNLOAD_NODE){
          return downloadDO.getId();
      }
      return "";
  }

  public DownloadDO getDownloadDO() {
    return downloadDO;
  }

  public DownloadSourceDO getDownloadSourceDO() {
    return downloadSourceDO;
  }

  public String getPfad(){
      return pfad;
  }

  public String toString() {
    if (nodetype==DOWNLOAD_NODE){
        return downloadDO.getFilename();
    }
    if (nodetype==SOURCE_NODE){
        return downloadSourceDO.getFilename() + "(" + downloadSourceDO.getNickname() + ")";
    }
    return pfad;
  }

  protected Object[] getChildren() {
    if (downloadSourceDO!=null){
        return null;
    }
    else{
        return children.values().toArray(new DownloadNode[children.size()]);
    }
  }
}
