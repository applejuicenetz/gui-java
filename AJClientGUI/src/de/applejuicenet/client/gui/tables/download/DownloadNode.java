package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.exception.NodeAlreadyExistsException;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.controller.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadNode.java,v 1.7 2003/08/11 15:34:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadNode.java,v $
 * Revision 1.7  2003/08/11 15:34:45  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.6  2003/08/09 10:56:38  maj0r
 * DownloadTabelle weitergeführt.
 *
 * Revision 1.5  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.4  2003/07/04 06:43:51  maj0r
 * Diverse Änderungen am DownloadTableModel.
 *
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
  private static ImageIcon downloadIcon;
  private static ImageIcon directoryIcon;

  private static ImageIcon direktVerbundenIcon;
  private static ImageIcon verbindungUnbekanntIcon;
  private static ImageIcon indirektVerbundenIcon;

  private static HashMap directoryNodes = new HashMap();

  public static final int ROOT_NODE = 0;
  public static final int DIRECTORY_NODE = 1;
  public static final int DOWNLOAD_NODE = 2;
  public static final int SOURCE_NODE = 3;

  public static final Color SOURCE_NODE_COLOR = new Color(255, 255, 150);
  public static final Color DOWNLOAD_FERTIG_COLOR = Color.GREEN;

  private int nodetype;

  private DownloadDO downloadDO;
  private DownloadSourceDO downloadSourceDO = null;
  private HashMap children;
  private String pfad = "";

  public DownloadNode(DownloadDO downloadDO) {
    if (downloadDO==null){
        return;
    }
    DownloadNode existent = (DownloadNode)directoryNodes.get(new MapSetStringKey(downloadDO.getId()));
    if (existent==null){
        nodetype = DOWNLOAD_NODE;
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
        DownloadNode child = null;
        for (int i=0; i<sources.length; i++){
            child = new DownloadNode(sources[i]);
            addChild(child);
        }
    }
    else{
        //Werte werden per Referenz automatisch aktualisiert
        DownloadSourceDO[] sources = downloadDO.getSources();
        DownloadNode child = null;
        for (int i=0; i<sources.length; i++){
            if (!existent.containsChild(
                    new MapSetStringKey(
                            sources[i].getId()))){
                //neue Source
                child = new DownloadNode(sources[i]);
                existent.addChild(child);
            }
            else{
                //Werte werden per Referenz automatisch aktualisiert
            }
        }

    }
  }

  public static void clearOldNodes(){
      ArrayList toRemove = new ArrayList();
      Iterator it = directoryNodes.values().iterator();
      MapSetStringKey key = null;
      HashMap downloads = DataManager.getInstance().getDownloads();
      DownloadNode node = null;
      while(it.hasNext()){
          node = (DownloadNode)it.next();
          if (node.getNodeType()==DownloadNode.DOWNLOAD_NODE){
              key = new MapSetStringKey(node.getId());
              if (!(downloads.containsKey(key))){
                  toRemove.add(key);
              }
          }
      }
      int size = toRemove.size();
      for (int i=0; i<size; i++){
          directoryNodes.remove(toRemove.get(i));
      }

      //(DownloadNode)directoryNodes.get(new MapSetStringKey(downloadDO.getId()));
  }

  private DownloadNode(DownloadSourceDO downloadSource){
      nodetype = SOURCE_NODE;
      downloadSourceDO = downloadSource;
      this.downloadDO = null;
  }

  public boolean containsChild(MapSetStringKey key){
      if (children.containsKey(key)){
          return true;
      }
      else{
          return false;
      }
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
      initIcons();
      nodetype = ROOT_NODE;
      this.pfad = "rootPfad";
      directoryNodes.put(new MapSetStringKey(pfad), this);
      children = new HashMap();
      this.downloadDO = null;
  }

  private void initIcons() {
    if (downloadIcon == null) {
      IconManager im = IconManager.getInstance();
      downloadIcon = im.getIcon("treeRoot");
      directoryIcon = im.getIcon("tree");
      direktVerbundenIcon = im.getIcon("treeUebertrage");
      verbindungUnbekanntIcon = im.getIcon("treeIndirekt");
      indirektVerbundenIcon = im.getIcon("treeWarteschlange");
    }
  }

  public int getNodeType(){
      return nodetype;
  }

  private void addChild(DownloadNode child){
      MapSetStringKey key = new MapSetStringKey(child.getId());
      children.put(key, child);
      if (child.getNodeType()==DOWNLOAD_NODE || child.getNodeType()==SOURCE_NODE){
          directoryNodes.put(key, child);
      }
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
          switch(downloadSourceDO.getDirectstate()){
              case DownloadSourceDO.UNBEKANNT:
                  return verbindungUnbekanntIcon;
              case DownloadSourceDO.DIREKTE_VERBINDUNG:
                  return direktVerbundenIcon;
              case DownloadSourceDO.INDIREKTE_VERBINDUNG:
                  return indirektVerbundenIcon;
              default:
                  return verbindungUnbekanntIcon;
              }
      }
      else{
          switch (nodetype){
              case DIRECTORY_NODE:
                  return directoryIcon;
              case DOWNLOAD_NODE:
                  return downloadIcon;
              default:
                  return downloadIcon;
          }
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
        return downloadSourceDO.getNickname() + " (" + downloadSourceDO.getFilename() + ")";
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
