package de.applejuicenet.client.gui.trees.share;

import de.applejuicenet.client.shared.dac.DirectoryDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.trees.ApplejuiceNode;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/DirectoryNode.java,v 1.11 2003/12/18 14:50:37 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DirectoryNode.java,v $
 * Revision 1.11  2003/12/18 14:50:37  maj0r
 * Bug im Sharebaum behoben.
 *
 * Revision 1.10  2003/09/03 11:12:00  maj0r
 * Eintraege werden nun nach Verzeichnisname sortiert.
 *
 * Revision 1.9  2003/08/28 06:58:14  maj0r
 * Plattformunabhaengigkeit wieder hergestellt.
 *
 * Revision 1.8  2003/08/26 09:49:01  maj0r
 * ShareTree weitgehend fertiggestellt.
 *
 * Revision 1.7  2003/08/26 06:20:10  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.6  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.5  2003/08/17 16:13:11  maj0r
 * Erstellen des DirectoryNode-Baumes korrigiert.
 *
 * Revision 1.4  2003/08/16 20:53:40  maj0r
 * Kleinen Fehler korrigiert
 *
 * Revision 1.3  2003/08/15 17:53:54  maj0r
 * Tree fuer Shareauswahl fortgefuehrt, aber noch nicht fertiggestellt.
 *
 * Revision 1.2  2003/08/15 14:44:20  maj0r
 * DirectoryTree eingefuegt, aber noch nicht fertiggestellt.
 *
 *
 */

public class DirectoryNode extends DefaultMutableTreeNode implements Node, ApplejuiceNode{
    public static final int NOT_SHARED = 0;
    public static final int SHARED_WITH_SUB = 1;
    public static final int SHARED_WITHOUT_SUB = 2;
    public static final int SHARED_SOMETHING = 3;
    public static final int SHARED_SUB = 4;

    private static HashSet shareDirs = new HashSet();

    private DirectoryDO directoryDO;
    private ArrayList children = null;
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

    public static void setShareDirs(HashSet newShareDirs){
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

    public int getShareMode(){
        if (directoryDO==null)
            return NOT_SHARED;
        Iterator it = shareDirs.iterator();
        int shareMode = NOT_SHARED;
        while (it.hasNext()){
            ShareEntry value = (ShareEntry)it.next();
            if (value.getDir().toLowerCase().startsWith(directoryDO.getPath().toLowerCase())){
                if (directoryDO.getPath().length()<value.getDir().length()
                    && directoryDO.getPath().lastIndexOf(ApplejuiceFassade.separator) ==
                        value.getDir().lastIndexOf(ApplejuiceFassade.separator)){
                        continue;
                }
                if (value.getShareMode()==ShareEntry.SUBDIRECTORY){
                    if (directoryDO.getPath().toLowerCase().startsWith(value.getDir().toLowerCase()))
                        return SHARED_WITH_SUB;
                    else
                        return SHARED_SOMETHING;
                }
                else{
                    if (directoryDO.getPath().toLowerCase().startsWith(value.getDir().toLowerCase()))
                        return SHARED_WITHOUT_SUB;
                    else
                        return SHARED_SOMETHING;
                }
            }
        }
        int parentShareMode = parent.getShareMode();
        if (parentShareMode==SHARED_WITH_SUB || parentShareMode==SHARED_SUB)
            shareMode = SHARED_SUB;
        return shareMode;
    }

    public Icon getShareModeIcon(){
        IconManager im = IconManager.getInstance();
        switch (getShareMode()){
            case SHARED_WITH_SUB:
                    return im.getIcon("sharedwsub");
            case SHARED_SUB:
                    return im.getIcon("sharedwsub");
            case SHARED_WITHOUT_SUB:
                    return im.getIcon("sharedwosub");
            case SHARED_SOMETHING:
                    return im.getIcon("somethingshared");
            default:
                return im.getIcon("notshared");
        }
    }


    public Icon getConvenientIcon() {
        if (directoryDO==null)
            return null;      //rootNode
        IconManager im = IconManager.getInstance();
        switch (directoryDO.getType()){
            case DirectoryDO.TYPE_DESKTOP:
                    return im.getIcon("server");
            case DirectoryDO.TYPE_DISKETTE:
                    return im.getIcon("diskette");
            case DirectoryDO.TYPE_LAUFWERK:
                    return im.getIcon("laufwerk");
            case DirectoryDO.TYPE_ORDNER:
                    return im.getIcon("tree");
            case DirectoryDO.TYPE_RECHNER:
                    return im.getIcon("server");
            default:
                return null;
        }
    }

    public DirectoryDO getDO() {
        return directoryDO;
    }

    public String toString() {
        if (directoryDO==null)
            return "rootNode";
        return directoryDO.getName();
    }

    public ApplejuiceNode addChild(DirectoryDO childDirectoryDO){
        if (children==null){
            children = new ArrayList();
        }
        DirectoryNode childNode = new DirectoryNode(this, childDirectoryDO);
        children.add(childNode);
        return childNode;
    }

    private void sortChildren(){
        if (children==null || children.size()<2){
            return;
        }
        int n = children.size();
        int k;
        for (int i = 0; i < n - 1; i++) {
          k = i;
          for (int j = i + 1; j < n; j++) {
              if (children.get(j).toString().compareToIgnoreCase(children.get(k).toString()) < 0) {
                k = j;
              }
          }
          Object tmp = children.get(i);
          children.set(i, children.get(k));
          children.set(k, tmp);
        }
    }

    protected Object[] getChildren() {
        if (children==null){
            children = new ArrayList();
            ApplejuiceFassade.getInstance().getDirectory(directoryDO.getPath(), this);
            sortChildren();
        }
        return children.toArray(new DirectoryNode[children.size()]);
    }
}
