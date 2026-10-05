/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.gui.components.dragndrop.DndTargetAdapter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.TableColumnSettings;
import de.applejuicenet.client.gui.share.table.DateiListeTableModel;
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
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/DateiListeDialog.java,v 1.8 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class DateiListeDialog extends JDialog {
    private static final javax.swing.border.Border IDLE_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    private static final javax.swing.border.Border HOVER_BORDER = BorderFactory.createLineBorder(Color.black);
    private JLabel speicherTxt = new JLabel();
    private JLabel kopierenAjfsp = new JLabel();
    private JLabel kopierenAjl = new JLabel();
    private JTable table = new JTable();
    private JLabel text = new JLabel();
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem entfernen = new JMenuItem();
    private JButton sortieren = new JButton();
    private final Logger logger;

    public DateiListeDialog(Frame parent, boolean modal) {
        this(parent, modal, new ShareNode[0]);
    }

    public DateiListeDialog(Frame parent, boolean modal, ShareNode... initialNodes) {
        super(parent, modal);
        logger = LoggerFactory.getLogger(getClass());
        init();
        DateiListeTableModel model = (DateiListeTableModel) table.getModel();
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
            DateiListeTableModel model = (DateiListeTableModel) table.getModel();

            for (int i = selected.length - 1; i >= 0; i--) {
                model.removeRow(selected[i]);
            }
        }
    }

    private void init() {
        try {
            entfernen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    removeSelectedColumn();
                }
            });
            popup.add(entfernen);
            table.setModel(new DateiListeTableModel());
            table.setDefaultRenderer(Long.class, new SizeTableCellRenderer());
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            getContentPane().setLayout(new GridBagLayout());
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            IconManager im = IconManager.getInstance();

            speicherTxt.setIcon(im.getIcon("speichern"));
            speicherTxt.addMouseListener(new SpeichernMouseAdapter());
            kopierenAjfsp.setIcon(im.getIcon("clipboard"));
            kopierenAjfsp.addMouseListener(new KopierenMouseAdapter(false));
            kopierenAjl.setIcon(im.getIcon("clipboard"));
            kopierenAjl.addMouseListener(new KopierenMouseAdapter(true));
            panel1.add(speicherTxt);
            panel1.add(kopierenAjfsp);
            panel1.add(kopierenAjl);
            for (JLabel button : new JLabel[]{speicherTxt, kopierenAjfsp, kopierenAjl}) {
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
            sortieren.addActionListener(e -> {
                DateiListeTableModel model = (DateiListeTableModel) table.getModel();
                model.setDescending(!model.isDescending());
                updateSortButton();
            });
            JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            sortPanel.add(sortieren);
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

            scroll.setDropTarget(new DropTarget(scroll, new ListeDndTargetAdapter()));
            table.setDropTarget(new DropTarget(table, new ListeDndTargetAdapter()));
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
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    private void updateSortButton() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        boolean descending = ((DateiListeTableModel) table.getModel()).isDescending();

        sortieren.setText(languageSelector.getFirstAttrbuteByTagName(
                descending ? "javagui.dateiliste.sortierenabsteigend" : "javagui.dateiliste.sortierenaufsteigend"));
    }

    public void initLanguage() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        setTitle(languageSelector.getFirstAttrbuteByTagName("linklist.caption"));
        text.setText(languageSelector.getFirstAttrbuteByTagName("linklist.Label1.caption"));
        entfernen.setText(languageSelector.getFirstAttrbuteByTagName("javagui.dateiliste.entfernen"));
        speicherTxt.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.dateiliste.exportajl"));
        kopierenAjfsp.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.dateiliste.kopierenajfsp"));
        kopierenAjl.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.dateiliste.kopierenajl"));
        sortieren.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.dateiliste.sortieren"));
        updateSortButton();
        String[] tableColumns = new String[2];

        tableColumns[0] = languageSelector.getFirstAttrbuteByTagName("linklist.files.col0caption");
        tableColumns[1] = languageSelector.getFirstAttrbuteByTagName("linklist.files.col1caption");
        TableColumnModel tcm = table.getColumnModel();

        for (int i = 0; i < tableColumns.length; i++) {
            tcm.getColumn(i).setHeaderValue(tableColumns[i]);
        }
    }

    class KopierenMouseAdapter extends MouseAdapter {
        private final boolean ajl;

        KopierenMouseAdapter(boolean ajl) {
            this.ajl = ajl;
        }

        public void mouseEntered(MouseEvent e) {
            ((JLabel) e.getSource()).setBorder(HOVER_BORDER);
        }

        public void mouseExited(MouseEvent e) {
            ((JLabel) e.getSource()).setBorder(IDLE_BORDER);
        }

        public void mouseClicked(MouseEvent e) {
            DateiListeTableModel model = (DateiListeTableModel) table.getModel();
            Share[] shares = model.getShares();
            String content = ajl ? ShareListFormat.ajl(shares, model.isDescending())
                    : ShareListFormat.ajfsp(shares, model.isDescending());

            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);
            } catch (Exception ex) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    class SpeichernMouseAdapter extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();

            source.setBorder(HOVER_BORDER);
        }

        public void mouseClicked(MouseEvent e) {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setFileFilter(new TxtFileFilter());

            int i = fileChooser.showSaveDialog(DialogLocation.getReference(DateiListeDialog.this));

            if (i == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                StringBuffer text = new StringBuffer();
                Share[] share = ((DateiListeTableModel) table.getModel()).getShares();

                if (!file.getPath().toLowerCase().endsWith(".ajl")) {
                    file = new File(file.getPath() + ".ajl");
                }
                text.append(ShareListFormat.ajl(share,
                        ((DateiListeTableModel) table.getModel()).isDescending()));

                try {
                    Files.writeString(file.toPath(), text.toString(), StandardCharsets.UTF_8);
                } catch (Exception ex) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                }
            }
        }

        private Share[] sortShares(Share[] share) {
            return ShareListFormat.sorted(share,
                    ((DateiListeTableModel) table.getModel()).isDescending());
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





    private class ListeDndTargetAdapter extends DndTargetAdapter {
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
                        DateiListeTableModel model = (DateiListeTableModel) table.getModel();

                        for (int i = 0; i < transfer.length; i++) {
                            model.addNodes((ShareNode) transfer[i]);
                        }
                    }
                } catch (Exception e) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);

                    event.getDropTargetContext().dropComplete(false);
                }
            }

            event.getDropTargetContext().dropComplete(true);
        }
    }
}
