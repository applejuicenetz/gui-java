package de.applejuicenet.client.gui.share;

import java.io.File;
import java.io.FileWriter;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.components.dragndrop.DndTargetAdapter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.share.table.DateiListeTableModel;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/DateiListeDialog.java,v 1.3 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class DateiListeDialog
    extends JDialog {
    private static final long serialVersionUID = 5114504658143608456L;
	private JLabel speicherTxt = new JLabel();
    private JLabel speicherHtml = new JLabel();
    private JTable table = new JTable();
    private JLabel text = new JLabel();
    private JPopupMenu popup = new JPopupMenu();
    private Logger logger;

    public DateiListeDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        init();
    }

    private void removeSelectedColumn(){
        int[] selected = table.getSelectedRows();
        if (selected.length > 0) {
            DateiListeTableModel model = (DateiListeTableModel)
                table.getModel();
            for (int i = selected.length-1; i >=0; i--) {
                model.removeRow(selected[i]);
            }
        }
    }

    private void init() {
        try {
            JMenuItem entfernen = new JMenuItem();
            entfernen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    removeSelectedColumn();
                }
            });
            popup.add(entfernen);
            table.setModel(new DateiListeTableModel());
            table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            getContentPane().setLayout(new GridBagLayout());
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            IconManager im = IconManager.getInstance();
            speicherTxt.setIcon(im.getIcon("speichern"));
            speicherTxt.addMouseListener(new SpeichernMouseAdapter());
            speicherHtml.setIcon(im.getIcon("web"));
            speicherHtml.addMouseListener(new SpeichernMouseAdapter());
            panel1.add(speicherTxt);
            panel1.add(speicherHtml);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1;
            getContentPane().add(panel1, constraints);
            constraints.weightx = 0;
            constraints.gridy = 1;
            getContentPane().add(text, constraints);
            constraints.gridy = 2;
            constraints.weighty = 1;
            JScrollPane scroll = new JScrollPane(table);
            scroll.setDropTarget(new DropTarget(scroll, new ListeDndTargetAdapter()));
            table.setDropTarget(new DropTarget(table, new ListeDndTargetAdapter()));
            table.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
                        removeSelectedColumn();
                    }
                    else {
                        super.keyPressed(ke);
                    }
                }
            });
            table.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent me) {
                    if (SwingUtilities.isRightMouseButton(me)) {
                        Point p = me.getPoint();
                        int iRow = table.rowAtPoint(p);
                        int iCol = table.columnAtPoint(p);
                        table.setRowSelectionInterval(iRow, iRow);
                        table.setColumnSelectionInterval(iCol, iCol);
                    }
                    maybeShowPopup(me);
                }

                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger() && table.getSelectedRowCount() > 0) {
                        popup.show(table, e.getX(), e.getY());
                    }
                }
            });
            getContentPane().add(scroll, constraints);
            constraints.weighty = 0;
            initLanguage();
            pack();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public void initLanguage() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.linklist.caption")));
        text.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.linklist.Label1.caption")));
        String[] tableColumns = new String[2];
        tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.linklist.files.col0caption"));
        tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.linklist.files.col1caption"));
        TableColumnModel tcm = table.getColumnModel();
        for (int i = 0; i < tableColumns.length; i++) {
            tcm.getColumn(i).setHeaderValue(tableColumns[i]);
        }
    }

    class SpeichernMouseAdapter
        extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            if (source == speicherTxt) {
                fileChooser.setFileFilter(new TxtFileFilter());
            }
            else {
                fileChooser.setFileFilter(new HtmlFileFilter());
            }
            int i = fileChooser.showSaveDialog(DateiListeDialog.this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                StringBuffer text = new StringBuffer();
                ShareDO[] shareDO = ( (DateiListeTableModel) table.getModel()).
                    getShareDOs();
                if (source != speicherTxt) {
                    if (!file.getPath().toLowerCase().endsWith(".htm")
                        && !file.getPath().toLowerCase().endsWith(".html")) {
                        file = new File(file.getPath() + ".html");
                    }
                    text.append("<html><head><title>appleJuice Linklist</title></head><body bgcolor=#000080 text=#ffffff "
                                + "link=#ffffff vlink=#ffffff><table align=center border=0><tr><td><b>appleJuice Dateien</b></td></tr><br>" +
                                "\r\n");
                    ShareDO[] sortedShareDOs = sortShareDOs(shareDO);
                    for (int x = 0; x < shareDO.length; x++) {
                        text.append("<tr><td><a href=\"ajfsp://file|");
                        text.append(sortedShareDOs[x].getShortfilename() + "|" +
                                    sortedShareDOs[x].getCheckSum() + "|" +
                                    sortedShareDOs[x].getSize() + "/\">");
                        text.append(sortedShareDOs[x].getShortfilename());
                        text.append("</a></td></tr>" + "\r\n");
                    }
                    text.append("</table></body></html>");
                }
                else {
                    if (!file.getPath().toLowerCase().endsWith(".ajl")) {
                        file = new File(file.getPath() + ".ajl");
                    }
                    text.append("\r\n" + "Du benoetigst ein appleJuice-GUI, um diese Datei zu oeffnen. Das gibts z.B. hier "
                                +
                        "http://developer.berlios.de/projects/applejuicejava/" +
                                "\r\n\r\n");
                    text.append("Diese Datei darf nicht modifiziert werden!" +
                                "\r\n" + "-----\r\n100\r\n");
                    ShareDO[] sortedShareDOs = sortShareDOs(shareDO);
                    for (int x = 0; x < sortedShareDOs.length; x++) {
                        text.append(sortedShareDOs[x].getShortfilename() + "\r\n");
                        text.append(sortedShareDOs[x].getCheckSum() + "\r\n");
                        text.append(sortedShareDOs[x].getSize() + "\r\n");
                    }
                }
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(text.toString());
                    fileWriter.close();
                }
                catch (Exception ex) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                    }
                }
            }
        }

        private ShareDO[] sortShareDOs(ShareDO[] shareDO){
            ShareDO[] sortedDOs = shareDO;
            int n = sortedDOs.length;
            ShareDO tmp;
            for (int i = 0; i < n - 1; i++) {
                int k = i;
                for (int j = i + 1; j < n; j++) {
                    if (sortedDOs[j].getShortfilename().compareToIgnoreCase(
                        sortedDOs[k].getShortfilename()) < 0) {
                        k = j;
                    }
                }
                tmp = sortedDOs[i];
                sortedDOs[i] = sortedDOs[k];
                sortedDOs[k] = tmp;
            }
            return sortedDOs;
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }

    class TxtFileFilter
        extends FileFilter {
        public boolean accept(File file) {
            if (!file.isFile()) {
                return true;
            }
            else {
                String name = file.getName();
                return (name.toLowerCase().endsWith(".ajl"));
            }
        }

        public String getDescription() {
            return "AJL-Dateien";
        }
    }

    class HtmlFileFilter
        extends FileFilter {
        public boolean accept(File file) {
            if (!file.isFile()) {
                return true;
            }
            else {
                String name = file.getName();
                return (name.toLowerCase().endsWith(".htm") ||
                        name.toLowerCase().endsWith(".html"));
            }
        }

        public String getDescription() {
            return "HTML-Dateien";
        }
    }

    private class ListeDndTargetAdapter extends DndTargetAdapter{
        protected Object getTarget(Point point) {
            return this;
        }

        public void drop(DropTargetDropEvent event) {
            Transferable tr = event.getTransferable();
            if (tr.isDataFlavorSupported(new DataFlavor(DataFlavor.
                javaJVMLocalObjectMimeType, "ShareNodesTransferer"))) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Object[] transfer = (Object[]) tr.getTransferData(
                        new DataFlavor(DataFlavor.
                                       javaJVMLocalObjectMimeType,
                                       "ShareNodesTransferer"));
                    if (transfer != null && transfer.length != 0) {
                        DateiListeTableModel model = (
                            DateiListeTableModel) table.getModel();
                        for (int i = 0; i < transfer.length; i++) {
                            model.addNodes( (ShareNode) transfer[i]);
                        }
                    }
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    }
                    event.getDropTargetContext().dropComplete(false);
                }
            }
            event.getDropTargetContext().dropComplete(true);
        }
    }
}
