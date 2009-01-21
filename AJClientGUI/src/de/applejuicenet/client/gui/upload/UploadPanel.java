/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.upload.table.UploadActiveTableModel;
import de.applejuicenet.client.gui.upload.table.UploadTableDateCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableFilenameCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTablePercentCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTablePrioCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableSpeedCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableVersionCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableWaitingStatusCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadWaitingTableModel;
import de.applejuicenet.client.shared.IconManager;

import info.clearthought.layout.TableLayout;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/UploadPanel.java,v
 * 1.48 2004/06/23 13:31:24 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class UploadPanel extends TklPanel implements RegisterI
{
   private JLabel                  uploadListeLabel               = new JLabel("0 Clients in Deiner Uploadliste");
   private JPopupMenu              popupMenu                      = new JPopupMenu();
   private JMenuItem               itemCopyToClipboard;
   private JPopupMenu              columnActivePopup              = new JPopupMenu();
   private TableColumn[]           columnsActiveUploads           = new TableColumn[7];
   private JCheckBoxMenuItem[]     columnPopupItemsActiveUploads  = new JCheckBoxMenuItem[columnsActiveUploads.length];
   private JPopupMenu              columnWaitingPopup             = new JPopupMenu();
   private TableColumn[]           columnsWaitingUploads          = new TableColumn[7];
   private JCheckBoxMenuItem[]     columnPopupItemsWaitingUploads = new JCheckBoxMenuItem[columnsActiveUploads.length];
   private JTable                  uploadActiveTable;
   private UploadActiveTableModel  uploadActiveTableModel;
   private JTable                  uploadWaitingTable;
   private UploadWaitingTableModel uploadWaitingTableModel;

   public UploadPanel(GuiController guiController)
   {
      super(guiController);
      try
      {
         init();
      }
      catch(Exception ex)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
         }
      }
   }

   private void init() throws Exception
   {
      double     p     = TableLayout.PREFERRED;
      double     f     = TableLayout.FILL;
      double[][] sizes = 
                         {
                            {5, f, 5},
                            {5, f, f, p}
                         };

      TableLayout tableLayout = new TableLayout(sizes);

      setLayout(tableLayout);
      uploadActiveTableModel = new UploadActiveTableModel();
      uploadActiveTable      = new JTable(uploadActiveTableModel);

      uploadActiveTable.getColumnModel().getColumn(2).setCellRenderer(new UploadTableSpeedCellRenderer());
      uploadActiveTable.getColumnModel().getColumn(3).setCellRenderer(new UploadTablePercentCellRenderer());
      uploadActiveTable.getColumnModel().getColumn(4).setCellRenderer(new UploadTablePercentCellRenderer());
      uploadActiveTable.getColumnModel().getColumn(5).setCellRenderer(new UploadTablePrioCellRenderer());

      uploadActiveTable.setDefaultRenderer(Version.class, new UploadTableVersionCellRenderer());
      uploadActiveTable.setDefaultRenderer(Upload.class, new UploadTableFilenameCellRenderer());

      TableColumnModel modelActive = uploadActiveTable.getColumnModel();

      for(int i = 0; i < columnsActiveUploads.length; i++)
      {
         columnsActiveUploads[i]          = modelActive.getColumn(i);
         columnPopupItemsActiveUploads[i] = new JCheckBoxMenuItem((String) columnsActiveUploads[i].getHeaderValue());
         final int x                      = i;

         columnPopupItemsActiveUploads[i].addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  if(columnPopupItemsActiveUploads[x].isSelected())
                  {
                     uploadActiveTable.getColumnModel().addColumn(columnsActiveUploads[x]);
                     PositionManagerImpl.getInstance().setUploadColumnVisible(x, true);
                     PositionManagerImpl.getInstance()
                     .setUploadColumnIndex(x,
                                           uploadActiveTable.getColumnModel().getColumnIndex(columnsActiveUploads[x].getIdentifier()));
                  }
                  else
                  {
                     uploadActiveTable.getColumnModel().removeColumn(columnsActiveUploads[x]);
                     PositionManagerImpl.getInstance().setUploadColumnVisible(x, false);
                     for(int y = 0; y < columnsActiveUploads.length; y++)
                     {
                        try
                        {
                           PositionManagerImpl.getInstance()
                           .setUploadColumnIndex(y,
                                                 uploadActiveTable.getColumnModel()
                                                 .getColumnIndex(columnsActiveUploads[y].getIdentifier()));
                        }
                        catch(IllegalArgumentException niaE)
                        {
                           ;

                           //nix zu tun
                        }
                     }
                  }
               }
            });
         columnActivePopup.add(columnPopupItemsActiveUploads[i]);
      }

      columnPopupItemsActiveUploads[0].setEnabled(false);
      columnPopupItemsActiveUploads[0].setSelected(true);

      SortButtonRenderer renderer = new SortButtonRenderer();
      TableColumnModel   model = uploadActiveTable.getColumnModel();
      int                n     = model.getColumnCount();

      for(int i = 0; i < n; i++)
      {
         model.getColumn(i).setHeaderRenderer(renderer);
         model.getColumn(i).setPreferredWidth(model.getColumn(i).getWidth());
      }

      JTableHeader header = uploadActiveTable.getTableHeader();

      header.addMouseListener(new HeaderListener(header, renderer));

      uploadWaitingTableModel = new UploadWaitingTableModel();
      uploadWaitingTable      = new JTable(uploadWaitingTableModel);

      uploadWaitingTable.getColumnModel().getColumn(1).setCellRenderer(new UploadTableWaitingStatusCellRenderer());
      uploadWaitingTable.getColumnModel().getColumn(3).setCellRenderer(new UploadTablePercentCellRenderer());
      uploadWaitingTable.getColumnModel().getColumn(4).setCellRenderer(new UploadTablePrioCellRenderer());

      uploadWaitingTable.setDefaultRenderer(Version.class, new UploadTableVersionCellRenderer());
      uploadWaitingTable.setDefaultRenderer(Date.class, new UploadTableDateCellRenderer());

      model    = uploadWaitingTable.getColumnModel();
      n        = model.getColumnCount();
      renderer = new SortButtonRenderer();
      for(int i = 0; i < n; i++)
      {
         model.getColumn(i).setHeaderRenderer(renderer);
         model.getColumn(i).setPreferredWidth(model.getColumn(i).getWidth());
      }

      header = uploadWaitingTable.getTableHeader();

      header.addMouseListener(new HeaderListener(header, renderer));

      TableColumnModel modelWaiting = uploadWaitingTable.getColumnModel();

      for(int i = 0; i < columnsWaitingUploads.length; i++)
      {
         columnsWaitingUploads[i]          = modelWaiting.getColumn(i);
         columnPopupItemsWaitingUploads[i] = new JCheckBoxMenuItem((String) columnsWaitingUploads[i].getHeaderValue());
         final int x                       = i;

         columnPopupItemsWaitingUploads[i].addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  if(columnPopupItemsWaitingUploads[x].isSelected())
                  {
                     uploadWaitingTable.getColumnModel().addColumn(columnsWaitingUploads[x]);
                     PositionManagerImpl.getInstance().setUploadWaitingColumnVisible(x, true);
                     PositionManagerImpl.getInstance()
                     .setUploadWaitingColumnIndex(x,
                                                  uploadWaitingTable.getColumnModel()
                                                  .getColumnIndex(columnsWaitingUploads[x].getIdentifier()));
                  }
                  else
                  {
                     uploadWaitingTable.getColumnModel().removeColumn(columnsWaitingUploads[x]);
                     PositionManagerImpl.getInstance().setUploadWaitingColumnVisible(x, false);
                     for(int y = 0; y < columnsWaitingUploads.length; y++)
                     {
                        try
                        {
                           PositionManagerImpl.getInstance()
                           .setUploadWaitingColumnIndex(y,
                                                        uploadWaitingTable.getColumnModel()
                                                        .getColumnIndex(columnsWaitingUploads[y].getIdentifier()));
                        }
                        catch(IllegalArgumentException niaE)
                        {
                           ;

                           //nix zu tun
                        }
                     }
                  }
               }
            });
         columnWaitingPopup.add(columnPopupItemsWaitingUploads[i]);
      }

      columnPopupItemsWaitingUploads[0].setEnabled(false);
      columnPopupItemsWaitingUploads[0].setSelected(true);

      JScrollPane aScrollPaneActive = new JScrollPane(uploadActiveTable);

      aScrollPaneActive.setBackground(uploadActiveTable.getBackground());
      aScrollPaneActive.getViewport().setOpaque(false);
      add(aScrollPaneActive, "1, 1");

      JScrollPane aScrollPaneWaiting = new JScrollPane(uploadWaitingTable);

      aScrollPaneWaiting.setBackground(uploadWaitingTable.getBackground());
      aScrollPaneWaiting.getViewport().setOpaque(false);
      add(aScrollPaneWaiting, "1, 2");

      add(uploadListeLabel, "1, 3, L, T");
      itemCopyToClipboard = new JMenuItem();
      itemCopyToClipboard.setIcon(IconManager.getInstance().getIcon("clipboard"));
      popupMenu.add(itemCopyToClipboard);
   }

   public int[] getColumnActiveWidths()
   {
      int[] widths = new int[columnsActiveUploads.length];

      for(int i = 0; i < columnsActiveUploads.length; i++)
      {
         widths[i] = columnsActiveUploads[i].getWidth();
      }

      return widths;
   }

   public int[] getColumnWaitingWidths()
   {
      int[] widths = new int[columnsWaitingUploads.length];

      for(int i = 0; i < columnsWaitingUploads.length; i++)
      {
         widths[i] = columnsWaitingUploads[i].getWidth();
      }

      return widths;
   }

   public JTable getUploadWaitingTable()
   {
      return uploadWaitingTable;
   }

   public UploadWaitingTableModel getUploadWaitingTableModel()
   {
      return uploadWaitingTableModel;
   }

   public JTable getUploadActiveTable()
   {
      return uploadActiveTable;
   }

   public UploadActiveTableModel getUploadActiveTableModel()
   {
      return uploadActiveTableModel;
   }

   public TableColumn[] getTableActiveColumns()
   {
      return columnsActiveUploads;
   }

   public TableColumn[] getTableWaitingColumns()
   {
      return columnsWaitingUploads;
   }

   public JCheckBoxMenuItem[] getColumnActivePopupItems()
   {
      return columnPopupItemsActiveUploads;
   }

   public JCheckBoxMenuItem[] getColumnWaitingPopupItems()
   {
      return columnPopupItemsWaitingUploads;
   }

   public JPopupMenu getColumnActivePopup()
   {
      return columnActivePopup;
   }

   public JPopupMenu getColumnWaitingPopup()
   {
      return columnWaitingPopup;
   }

   public JPopupMenu getPopup()
   {
      return popupMenu;
   }

   public JLabel getUploadListeLabel()
   {
      return uploadListeLabel;
   }

   public JMenuItem getMnuCopyToClipboard()
   {
      return itemCopyToClipboard;
   }
}
