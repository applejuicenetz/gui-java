package de.applejuicenet.client.gui.tablerenderer;

import java.io.File;
import java.util.Date;
import javax.swing.tree.DefaultMutableTreeNode;
import de.applejuicenet.client.shared.DownloadDO;
import java.util.HashSet;

/**
 * FileSystemModel is a TreeTableModel representing a hierarchical file
 * system. Nodes in the FileSystemModel are FileNodes which, when they
 * are directory nodes, cache their children to avoid repeatedly querying
 * the real file system.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class DownloadModel extends AbstractTreeTableModel
                             implements TreeTableModel {

    // Names of the columns.
    static protected String[]  cNames = {"Name", "Size", "Type", "Modified"};

    // Types of the columns.
    static protected Class[]  cTypes = {TreeTableModel.class, Integer.class, String.class, Date.class};

    // The the returned file length for directories.
    public static final Integer ZERO = new Integer(0);

    public DownloadModel(DownloadDO downloads) {
	super(new DownloadNode(downloads));
    }

    //
    // Some convenience methods.
    //

    protected DownloadDO getDO(Object node) {
	DownloadNode fileNode = ((DownloadNode)node);
	return fileNode.getDO();
    }

    protected Object[] getChildren(Object node) {
	DownloadNode fileNode = ((DownloadNode)node);
	return fileNode.getChildren();
    }

    //
    // The TreeModel interface
    //

    public int getChildCount(Object node) {
	Object[] children = getChildren(node);
	return (children == null) ? 0 : children.length;
    }

    public Object getChild(Object node, int i) {
	return getChildren(node)[i];
    }

    // The superclass's implementation would work, but this is more efficient.
//    public boolean isLeaf(Object node) { return getFile(node).isFile(); }

    //
    //  The TreeTableNode interface.
    //

    public int getColumnCount() {
	return cNames.length;
    }

    public String getColumnName(int column) {
	return cNames[column];
    }

    public Class getColumnClass(int column) {
	return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
	DownloadDO download = getDO(node);
	try {
	    switch(column) {
	    case 0:
		return download.getDateiname();
	    case 1:
		return download.getStatus();
	    case 2:
		return download.getGroesse();
	    case 3:
		return download.getBereitsGeladen();
	    }
	}
	catch  (SecurityException se) { }

	return null;
    }
}

/* A FileNode is a derivative of the File class - though we delegate to
 * the File object rather than subclassing it. It is used to maintain a
 * cache of a directory's children and therefore avoid repeated access
 * to the underlying file system during rendering.
 */
class DownloadNode{
    DownloadDO download;
    HashSet children;

    public DownloadNode(DownloadDO download) {
	this.download = download;
        children = new HashSet();
        for (int i=0; i<download.getSources().length; i++){
          children.add(download.getSources()[i]);
        }
    }

    public void addChild(DownloadDO download){
      if (!(children.contains(download)))
        children.add(download);
    }

    public void removeChild(DownloadDO download){
      if (children.contains(download))
        children.remove(download);
    }

    // Used to sort the file names.
    static private MergeSort  fileMS = new MergeSort() {
	public int compareElementsAt(int a, int b) {
	    return ((String)toSort[a]).compareTo((String)toSort[b]);
	}
    };

    public DownloadDO getDO(){
      return download;
    }
    /**
     * Returns the the string to be used to display this leaf in the JTree.
     */
    public String toString() {
	return download.getDateiname();
    }

    /**
     * Loads the children, caching the results in the children ivar.
     */
    protected Object[] getChildren() {
	return children.toArray(new DownloadDO[children.size()]);
    }
}


