/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Share;
import de.applejuicenet.client.gui.components.dragndrop.DndTargetAdapter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.TableColumnSettings;
import de.applejuicenet.client.gui.share.table.FileListTableModel;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.tablecellrenderer.SizeTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Copyright: General Public License
 */
public class FileListDialog extends JDialog {
    private static final javax.swing.border.Border IDLE_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    private static final javax.swing.border.Border HOVER_BORDER = BorderFactory.createLineBorder(Color.black);
    private JLabel saveText = new JLabel();
    private JLabel copyAjfsp = new JLabel();
    private JLabel copyAjl = new JLabel();
    private JTable table = new JTable();
    private JLabel text = new JLabel();
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem remove = new JMenuItem();
    private JButton sort = new JButton();
    private final Logger logger;

    public FileListDialog(Frame parent, boolean modal) {
        this(parent, modal, new ShareNode[0]);
    }

    public FileListDialog(Frame parent, boolean modal, ShareNode... initialNodes) {
        super(parent, modal);
        logger = LoggerFactory.getLogger(getClass());
        init();
        FileListTableModel model = (FileListTableModel) table.getModel();
        for (ShareNode node : initialNodes) {
            model.addNodes(node);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            DialogLocation.alignRight(this);
        }
        super.setVisible(visible);
    }

    private void removeSelectedColumn() {
        int[] selected = table.getSelectedRows();

        if (selected.length > 0) {
            FileListTableModel model = (FileListTableModel) table.getModel();

            for (int i = selected.length - 1; i >= 0; i--) {
                model.removeRow(selected[i]);
            }
        }
    }

    private void init() {
        try {
            remove.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    removeSelectedColumn();
                }
            });
            popup.add(remove);
            table.setModel(new FileListTableModel());
            table.setDefaultRenderer(Long.class, new SizeTableCellRenderer());
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            getContentPane().setLayout(new GridBagLayout());
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            IconManager im = IconManager.getInstance();

            saveText.setIcon(im.getIcon("speichern"));
            saveText.addMouseListener(new SaveMouseAdapter());
            copyAjfsp.setIcon(im.getIcon("clipboard"));
            copyAjfsp.addMouseListener(new CopyMouseAdapter(false));
            copyAjl.setIcon(im.getIcon("clipboard"));
            copyAjl.addMouseListener(new CopyMouseAdapter(true));
            panel1.add(saveText);
            panel1.add(copyAjfsp);
            panel1.add(copyAjl);
            for (JLabel button : new JLabel[]{saveText, copyAjfsp, copyAjl}) {
                button.setBorder(IDLE_BORDER);
            }
            GridBagConstraints constraints = new GridBagConstraints();

            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1;
            JPanel toolbar = new JPanel(new BorderLayout());
            toolbar.add(panel1, BorderLayout.WEST);
            sort.addActionListener(e -> {
                FileListTableModel model = (FileListTableModel) table.getModel();
                model.setDescending(!model.isDescending());
                updateSortButton();
            });
            JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            sortPanel.add(sort);
            toolbar.add(sortPanel, BorderLayout.EAST);
            getContentPane().add(toolbar, constraints);
            constraints.weightx = 0;
            constraints.gridy = 1;
            getContentPane().add(text, constraints);
            constraints.gridy = 2;
            constraints.weighty = 1;
            JScrollPane scroll = new JScrollPane(table);
            TableColumn[] columns = new TableColumn[table.getColumnCount()];
            for (int index = 0; index < columns.length; index++) {
                columns[index] = table.getColumnModel().getColumn(index);
            }
            TableColumnSettings.installCompact(table, "filelist", columns);

            scroll.setDropTarget(new DropTarget(scroll, new ListDndTargetAdapter()));
            table.setDropTarget(new DropTarget(table, new ListDndTargetAdapter()));
            table.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
                        removeSelectedColumn();
                    } else {
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

                        if (iRow >= 0 && iCol >= 0 && !table.isRowSelected(iRow)) {
                            table.setRowSelectionInterval(iRow, iRow);
                            table.setColumnSelectionInterval(iCol, iCol);
                        }
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
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
        }
    }

    private void updateSortButton() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        boolean descending = ((FileListTableModel) table.getModel()).isDescending();

        sort.setText(languageSelector.getFirstAttributeByTagName(
                descending ? "javagui.filelist.sortdescending" : "javagui.filelist.sortascending"));
    }

    public void initLanguage() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        setTitle(languageSelector.getFirstAttributeByTagName("linklist.caption"));
        text.setText(languageSelector.getFirstAttributeByTagName("linklist.Label1.caption"));
        remove.setText(languageSelector.getFirstAttributeByTagName("javagui.filelist.remove"));
        saveText.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.filelist.exportajl"));
        copyAjfsp.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.filelist.copyajfsp"));
        copyAjl.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.filelist.copyajl"));
        sort.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.filelist.sort"));
        updateSortButton();
        String[] tableColumns = new String[2];

        tableColumns[0] = languageSelector.getFirstAttributeByTagName("linklist.files.col0caption");
        tableColumns[1] = languageSelector.getFirstAttributeByTagName("linklist.files.col1caption");
        TableColumnModel columnModel = table.getColumnModel();

        for (int i = 0; i < tableColumns.length; i++) {
            columnModel.getColumn(i).setHeaderValue(tableColumns[i]);
        }
    }

    class CopyMouseAdapter extends MouseAdapter {
        private final boolean ajl;

        CopyMouseAdapter(boolean ajl) {
            this.ajl = ajl;
        }

        public void mouseEntered(MouseEvent e) {
            ((JLabel) e.getSource()).setBorder(HOVER_BORDER);
        }

        public void mouseExited(MouseEvent e) {
            ((JLabel) e.getSource()).setBorder(IDLE_BORDER);
        }

        public void mouseClicked(MouseEvent e) {
            FileListTableModel model = (FileListTableModel) table.getModel();
            Share[] shares = model.getShares();
            String content = ajl ? ShareListFormat.ajl(shares, model.isDescending())
                    : ShareListFormat.ajfsp(shares, model.isDescending());

            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);
            } catch (Exception ex) {
                logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
                showError("javagui.filelist.copyerror", ex);
            }
        }
    }

    private void showError(String key, Exception ex) {
        String detail = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        JOptionPane.showMessageDialog(DialogLocation.getReference(this),
                GuiText.text(key).replace("%s", detail), GuiText.text("javagui.error"), JOptionPane.ERROR_MESSAGE);
    }

    class SaveMouseAdapter extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();

            source.setBorder(HOVER_BORDER);
        }

        public void mouseClicked(MouseEvent e) {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setFileFilter(new TxtFileFilter());

            int i = fileChooser.showSaveDialog(DialogLocation.getReference(FileListDialog.this));

            if (i == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                StringBuilder text = new StringBuilder();
                Share[] share = ((FileListTableModel) table.getModel()).getShares();

                if (!file.getPath().toLowerCase().endsWith(".ajl")) {
                    file = new File(file.getPath() + ".ajl");
                }
                text.append(ShareListFormat.ajl(share,
                        ((FileListTableModel) table.getModel()).isDescending()));

                try {
                    Files.writeString(file.toPath(), text.toString(), StandardCharsets.UTF_8);
                } catch (Exception ex) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
                    showError("javagui.filelist.saveerror", ex);
                }
            }
        }

        private Share[] sortShares(Share[] share) {
            return ShareListFormat.sorted(share,
                    ((FileListTableModel) table.getModel()).isDescending());
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();

            source.setBorder(IDLE_BORDER);
        }
    }


    class TxtFileFilter extends FileFilter {
        public boolean accept(File file) {
            if (!file.isFile()) {
                return true;
            } else {
                String name = file.getName();

                return (name.toLowerCase().endsWith(".ajl"));
            }
        }

        public String getDescription() {
            return GuiText.text("javagui.filefilter.ajl");
        }
    }





    private class ListDndTargetAdapter extends DndTargetAdapter {
        protected Object getTarget(Point point) {
            return this;
        }

        public void drop(DropTargetDropEvent event) {
            Transferable tr = event.getTransferable();

            if (tr.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "ShareNodesTransferer"))) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Object[] transfer = (Object[]) tr.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType,
                            "ShareNodesTransferer"));

                    if (transfer != null && transfer.length != 0) {
                        FileListTableModel model = (FileListTableModel) table.getModel();

                        for (int i = 0; i < transfer.length; i++) {
                            model.addNodes((ShareNode) transfer[i]);
                        }
                    }
                } catch (Exception e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);

                    event.getDropTargetContext().dropComplete(false);
                }
            }

            event.getDropTargetContext().dropComplete(true);
        }
    }
}
