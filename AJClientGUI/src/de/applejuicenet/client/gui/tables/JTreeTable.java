package de.applejuicenet.client.gui.tables;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.StringTokenizer;
import java.util.Vector;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalToolTipUI;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.tables.download.DownloadDirectoryNode;
import de.applejuicenet.client.gui.tables.download.DownloadMainNode;
import de.applejuicenet.client.gui.tables.download.IconGetter;
import de.applejuicenet.client.gui.tables.upload.MainNode;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.UploadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/JTreeTable.java,v 1.30 2004/10/06 12:29:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class JTreeTable
    extends JTable {
    private static final long serialVersionUID = -2358030317266014942L;

	protected TreeTableCellRenderer tree;

    protected JTable thisTable;

    public JTreeTable(TreeTableModel treeTableModel) {
        super();
        thisTable = this;
        tree = new TreeTableCellRenderer(treeTableModel);

        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        ListToTreeSelectionModelWrapper selectionWrapper = new
            ListToTreeSelectionModelWrapper();
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

        setShowGrid(false);

        setIntercellSpacing(new Dimension(0, 0));

        if (tree.getRowHeight() < 1) {
            // Metal looks better like this.
            setRowHeight(18);
        }
    }

    public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip(new DownloadToolTipUI());
        tip.setComponent(this);
        return tip;
    }

    public String getToolTipText(MouseEvent me){
        if (!Settings.getSettings().isToolTipEnabled()) {
            return null;
        }
        Point p = me.getPoint();
        int column = columnAtPoint(p);
        if (column == 0){
            int row = rowAtPoint(p);
            Object value = ( (TreeTableModelAdapter) getModel()).
                nodeForRow(row);
            if (value.getClass() == DownloadSourceDO.class) {
                return getToolTipForDownloadSourceDO( (DownloadSourceDO) value);
            }
            else if (value.getClass() == DownloadMainNode.class
                &&
                ( (DownloadMainNode) value).getType() ==
                DownloadMainNode.ROOT_NODE) {
                return getToolTipForDownloadDO(( (DownloadMainNode) value).getDownloadDO());
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    private String getToolTipForDownloadDO(DownloadDO downloadDO){
        String[] columns = DownloadMainNode.getColumnTitles();
        StringBuffer toolTip = new StringBuffer();
        toolTip.append(downloadDO.getColumn0());
        toolTip.append("| |" + columns[1] + ": ");
        toolTip.append(downloadDO.getColumn1());
        if (downloadDO.getStatus()==DownloadDO.SUCHEN_LADEN ||
            downloadDO.getStatus()==DownloadDO.PAUSIERT){
            toolTip.append("|" + columns[2] + ": ");
            toolTip.append(downloadDO.getColumn2());
            toolTip.append("|" + columns[3] + ": ");
            toolTip.append(downloadDO.getColumn3());
            if (downloadDO.getStatus()==DownloadDO.SUCHEN_LADEN ){
                toolTip.append("|" + columns[4] + ": ");
                toolTip.append(downloadDO.getColumn4());
                toolTip.append("|" + columns[5] + ": ");
                toolTip.append(downloadDO.getColumn5());
            }
            toolTip.append("|" + columns[6] + ": ");
            toolTip.append(downloadDO.getProzentGeladenAsString());
            toolTip.append("|" + columns[7] + ": ");
            toolTip.append(downloadDO.getColumn7());
            toolTip.append("|" + columns[8] + ": ");
            toolTip.append(downloadDO.getColumn8());
        }
        return toolTip.toString();
    }

    private String getToolTipForDownloadSourceDO(DownloadSourceDO downloadSourceDO){
        String[] columns = DownloadMainNode.getColumnTitles();
        StringBuffer toolTip = new StringBuffer();
        toolTip.append(downloadSourceDO.getColumn0());
        toolTip.append("| |" + columns[1] + ": ");
        toolTip.append(downloadSourceDO.getColumn1());
        toolTip.append("|" + columns[2] + ": ");
        toolTip.append(downloadSourceDO.getColumn2());
        toolTip.append("|" + columns[3] + ": ");
        toolTip.append(downloadSourceDO.getColumn3());
        toolTip.append("|" + columns[4] + ": ");
        toolTip.append(downloadSourceDO.getColumn4());
        toolTip.append("|" + columns[5] + ": ");
        toolTip.append(downloadSourceDO.getColumn5());
        toolTip.append("|" + columns[6] + ": ");
        toolTip.append(downloadSourceDO.getDownloadPercentAsString());
        toolTip.append("|" + columns[7] + ": ");
        toolTip.append(downloadSourceDO.getColumn7());
        toolTip.append("|" + columns[8] + ": ");
        toolTip.append(downloadSourceDO.getColumn8());
        toolTip.append("|" + columns[9] + ": ");
        toolTip.append(downloadSourceDO.getColumn9());
        return toolTip.toString();
    }

    public void updateUI() {
        super.updateUI();
        if (tree != null) {
            tree.updateUI();
        }
        LookAndFeel.installColorsAndFont(this, "Tree.background",
                                         "Tree.foreground", "Tree.font");
    }

    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
            editingRow;
    }

    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if (tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight());
        }
    }

    public JTree getTree() {
        return tree;
    }

    public class TreeTableCellRenderer
        extends JTree
        implements
        TableCellRenderer, DataUpdateListener {

        private static final long serialVersionUID = -461045856373187978L;
		protected int visibleRow;
        private Settings settings;

        public TreeTableCellRenderer(TreeModel model) {
            super(model);
            settings = Settings.getSettings();
            this.setCellRenderer(new IconNodeRenderer());
            OptionsManagerImpl.getInstance().addSettingsListener(this);
        }

        public void updateUI() {
            super.updateUI();
            TreeCellRenderer tcr = getCellRenderer();
            if (tcr instanceof DefaultTreeCellRenderer) {
                DefaultTreeCellRenderer dtcr = ( (DefaultTreeCellRenderer) tcr);
                dtcr.setTextSelectionColor(UIManager.getColor
                                           ("Table.selectionForeground"));
                dtcr.setBackgroundSelectionColor(UIManager.getColor
                                                 ("Table.selectionBackground"));
            }
        }

        public void setRowHeight(int rowHeight) {
            if (rowHeight > 0) {
                super.setRowHeight(rowHeight);
                if (JTreeTable.this != null &&
                    JTreeTable.this.getRowHeight() != rowHeight) {
                    JTreeTable.this.setRowHeight(getRowHeight());
                }
            }
        }

        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, 0, w, JTreeTable.this.getHeight());
        }

        public void paint(Graphics g) {
            g.translate(0, -visibleRow * getRowHeight());
            super.paint(g);
        }

        public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row, int column) {
            Object node = ( (TreeTableModelAdapter) table.getModel()).
                nodeForRow(row);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            }
            else {
                if (settings.isFarbenAktiv()) {
                    if (node.getClass() == DownloadSourceDO.class) {
                        setBackground(settings.getQuelleHintergrundColor());
                    }
                    else if (node.getClass() == DownloadMainNode.class &&
                             ( (DownloadMainNode) node).getType() ==
                             DownloadMainNode.ROOT_NODE &&
                             ( (DownloadMainNode) node).getDownloadDO().
                             getStatus() == DownloadDO.FERTIG) {
                        setBackground(settings.
                                      getDownloadFertigHintergrundColor());
                    }
                    else {
                        setBackground(table.getBackground());
                    }
                }
                else {
                    setBackground(table.getBackground());
                }
            }

            visibleRow = row;
            return this;
        }

        public void fireContentChanged(int type, Object content) {
            if (type == DataUpdateListener.SETTINGS_CHANGED) {
                settings = (Settings) content;
            }
        }
    }

    public class TreeTableCellEditor
        extends AbstractCellEditor
        implements
        TableCellEditor {
        public Component getTableCellEditorComponent(JTable table,
            Object value,
            boolean isSelected,
            int r, int c) {
            return tree;
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                for (int counter = getColumnCount() - 1; counter >= 0;
                     counter--) {
                    if (getColumnClass(counter) == TreeTableModel.class) {
                        MouseEvent me = (MouseEvent) e;
                        MouseEvent newME = new MouseEvent(tree, me.getID(),
                            me.getWhen(), me.getModifiers(),
                            me.getX() - getCellRect(0, counter, true).x,
                            me.getY(), me.getClickCount(),
                            me.isPopupTrigger());
                        tree.dispatchEvent(newME);
                        break;
                    }
                }
            }
            return false;
        }
    }

    class ListToTreeSelectionModelWrapper
        extends DefaultTreeSelectionModel {

        private static final long serialVersionUID = 3201094989681091040L;
		protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            super();
            getListSelectionModel().addListSelectionListener
                (createListSelectionListener());
        }

        ListSelectionModel getListSelectionModel() {
            return listSelectionModel;
        }

        public void resetRowSelection() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    super.resetRowSelection();
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
        }

        protected ListSelectionListener createListSelectionListener() {
            return new ListSelectionHandler();
        }

        protected void updateSelectedPathsFromSelectedRows() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    int min = listSelectionModel.getMinSelectionIndex();
                    int max = listSelectionModel.getMaxSelectionIndex();

                    clearSelection();
                    if (min != -1 && max != -1) {
                        for (int counter = min; counter <= max; counter++) {
                            if (listSelectionModel.isSelectedIndex(counter)) {
                                TreePath selPath = tree.getPathForRow
                                    (counter);

                                if (selPath != null) {
                                    addSelectionPath(selPath);
                                }
                            }
                        }
                    }
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
        }

        class ListSelectionHandler
            implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                updateSelectedPathsFromSelectedRows();
            }
        }
    }

    public class IconNodeRenderer
        extends DefaultTreeCellRenderer
        implements DataUpdateListener {

        private static final long serialVersionUID = 4819527202533954019L;
		private Settings settings;

        public IconNodeRenderer() {
            super();
            settings = Settings.getSettings();
            OptionsManagerImpl.getInstance().addSettingsListener(this);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {

            Component c = null;
            if (value.getClass() == DownloadSourceDO.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (DownloadSourceDO) value).getNickname()
                    + " (" + ( (DownloadSourceDO) value).getFilename() + ")",
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == DownloadMainNode.class) {
                c = super.getTreeCellRendererComponent(tree, value.toString(),
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == DownloadDirectoryNode.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (DownloadDirectoryNode) value).getVerzeichnis(),
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == FileName.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (FileName) value).getDateiName(),
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == MainNode.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (MainNode) value).toString() + " (" + ( (MainNode) value).getChildCount() + ")",
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == UploadDO.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (UploadDO) value).getDateiName(),
                    sel, expanded, leaf, row, hasFocus);
            }
            else {
                c = super.getTreeCellRendererComponent(tree, value,
                    sel, expanded, leaf, row, hasFocus);
            }
            if (c.getClass() == IconNodeRenderer.class) {
                ( (IconNodeRenderer) c).setOpaque(true);
                if (sel) {
                    c.setBackground(thisTable.getSelectionBackground());
                    c.setForeground(thisTable.getSelectionForeground());
                }
                else {
                    if (settings.isFarbenAktiv()) {
                        if (value.getClass() == DownloadSourceDO.class) {
                            c.setBackground(settings.getQuelleHintergrundColor());
                        }
                        else if (value.getClass() == DownloadMainNode.class &&
                                 ( (DownloadMainNode) value).getType() ==
                                 DownloadMainNode.ROOT_NODE &&
                                 ( (DownloadMainNode) value).getDownloadDO().
                                 getStatus() == DownloadDO.FERTIG) {
                            c.setBackground(settings.
                                            getDownloadFertigHintergrundColor());
                        }
                        else {
                            c.setBackground(tree.getBackground());
                        }
                    }
                }
            }
            if (value instanceof Node) {
                Icon icon = ( (Node) value).getConvenientIcon();
                if (icon != null) {
                    setIcon(icon);
                }
            }
            else {
                Icon icon = IconGetter.getConvenientIcon(value);
                if (icon != null) {
                    setIcon(icon);
                }
            }
            return this;
        }

        public void fireContentChanged(int type, Object content) {
            if (type == DataUpdateListener.SETTINGS_CHANGED) {
                settings = (Settings) content;
            }
        }
    }

    private class DownloadToolTipUI
        extends MetalToolTipUI {
        private String[] strs;

        public void paint(Graphics g, JComponent c) {
            if (strs != null) {
                Font normalFont = c.getFont();
                FontMetrics normalMetrics = c.getFontMetrics(normalFont);
                Font titleFont = new Font(c.getFont().getName(), Font.BOLD,
                                     c.getFont().getSize());
                FontMetrics titleMetrics = c.getFontMetrics(titleFont);
                Dimension size = c.getSize();
                g.setColor(c.getBackground());
                g.fillRect(0, 0, size.width, size.height);
                g.setColor(c.getForeground());
                int length = strs.length;
                int maxWidth = titleMetrics.stringWidth(strs[0]);
                int tmp;
                for (int a=1; a<strs.length; a++){
                    tmp = normalMetrics.stringWidth(strs[a]);
                    if (tmp > maxWidth){
                        maxWidth = tmp;
                    }
                }
                g.setFont(titleFont);
                int y = normalMetrics.getHeight();
                g.drawString(strs[0], 3, y);
                g.drawLine(3, y + y/2, maxWidth - 3, y + y/2);
                g.setFont(normalFont);
                for (int i = 1; i < length; i++) {
                    g.drawString(strs[i], 3, y * (i + 1));
                }
            }
        }

        public Dimension getPreferredSize(JComponent c) {
            Font font = new Font(c.getFont().getName(), Font.BOLD,
                                 c.getFont().getSize());
            FontMetrics metrics = c.getFontMetrics(font);
            String tipText = ( (JToolTip) c).getTipText();
            if (tipText == null) {
                tipText = "";
            }
            StringTokenizer st = new StringTokenizer(tipText, "|");
            int maxWidth = 0;
            Vector v = new Vector();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int width = SwingUtilities.computeStringWidth(metrics, token);
                maxWidth = (maxWidth < width) ? width : maxWidth;
                v.addElement(token);
            }
            int lines = v.size();
            if (lines < 1) {
                strs = null;
                lines = 1;
            }
            else {
                strs = new String[lines];
                int i = 0;
                for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
                    strs[i] = (String) e.nextElement();
                }
            }
            int height = metrics.getHeight() * lines;
            return new Dimension(maxWidth + 6, height + 4);
        }
    }
}
