/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.upload;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.NormalHeaderRenderer;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.upload.table.UploadActiveTableModel;
import de.applejuicenet.client.gui.upload.table.UploadTableFilenameCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTablePercentCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableSpeedCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableVersionCellRenderer;
import de.applejuicenet.client.shared.IconManager;

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
   private JLabel                 uploadListeLabel    = new JLabel("0 Clients in Deiner Uploadliste");
   private JPopupMenu             popupMenu           = new JPopupMenu();
   private JMenuItem              itemCopyToClipboard;
   private JPopupMenu             columnPopup         = new JPopupMenu();
   private TableColumn[]          columns             = new TableColumn[7];
   private JCheckBoxMenuItem[]    columnPopupItems    = new JCheckBoxMenuItem[columns.length];
   private JTable                 uploadTable;
   private UploadActiveTableModel uploadTableModel;

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

   public JTable getUploadTable()
   {
      return uploadTable;
   }

   public UploadActiveTableModel getUploadTableModel()
   {
      return uploadTableModel;
   }

   public TableColumn[] getTableColumns()
   {
      return columns;
   }

   public JCheckBoxMenuItem[] getColumnPopupItems()
   {
      return columnPopupItems;
   }

   public JPopupMenu getColumnPopup()
   {
      return columnPopup;
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

   private void init() throws Exception
   {
      setLayout(new BorderLayout());
      uploadTableModel                                = new UploadActiveTableModel();
      uploadTable                                     = new JTable(uploadTableModel);

      uploadTable.getColumnModel().getColumn(2).setCellRenderer(new UploadTableSpeedCellRenderer());
      uploadTable.getColumnModel().getColumn(3).setCellRenderer(new UploadTablePercentCellRenderer());
      uploadTable.getColumnModel().getColumn(4).setCellRenderer(new UploadTablePercentCellRenderer());

      uploadTable.setDefaultRenderer(Version.class, new UploadTableVersionCellRenderer());
      uploadTable.setDefaultRenderer(Upload.class, new UploadTableFilenameCellRenderer());

      TableColumnModel model = uploadTable.getColumnModel();

      for(int i = 0; i < columns.length; i++)
      {
         columns[i]          = model.getColumn(i);
         columnPopupItems[i] = new JCheckBoxMenuItem((String) columns[i].getHeaderValue());
         final int x         = i;

         columnPopupItems[i].addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  if(columnPopupItems[x].isSelected())
                  {
                     uploadTable.getColumnModel().addColumn(columns[x]);
                     PositionManagerImpl.getInstance().setUploadColumnVisible(x, true);
                     PositionManagerImpl.getInstance()
                     .setUploadColumnIndex(x, uploadTable.getColumnModel().getColumnIndex(columns[x].getIdentifier()));
                  }
                  else
                  {
                     uploadTable.getColumnModel().removeColumn(columns[x]);
                     PositionManagerImpl.getInstance().setUploadColumnVisible(x, false);
                     for(int y = 0; y < columns.length; y++)
                     {
                        try
                        {
                           PositionManagerImpl.getInstance()
                           .setUploadColumnIndex(y, uploadTable.getColumnModel().getColumnIndex(columns[y].getIdentifier()));
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
         columnPopup.add(columnPopupItems[i]);
      }

      columnPopupItems[0].setEnabled(false);
      columnPopupItems[0].setSelected(true);

      JScrollPane aScrollPane = new JScrollPane(uploadTable);

      aScrollPane.setBackground(uploadTable.getBackground());
      aScrollPane.getViewport().setOpaque(false);
      add(aScrollPane, BorderLayout.CENTER);

      int               n        = model.getColumnCount();
      TableCellRenderer renderer = new NormalHeaderRenderer();

      for(int i = 0; i < n; i++)
      {
         model.getColumn(i).setHeaderRenderer(renderer);
      }

      JPanel panel = new JPanel();

      panel.setLayout(new FlowLayout());
      panel.add(uploadListeLabel);
      JPanel panel2 = new JPanel();

      panel2.setLayout(new BorderLayout());
      panel2.add(panel, BorderLayout.WEST);
      add(panel2, BorderLayout.SOUTH);
      itemCopyToClipboard = new JMenuItem();
      itemCopyToClipboard.setIcon(IconManager.getInstance().getIcon("clipboard"));
      popupMenu.add(itemCopyToClipboard);
   }

   public int[] getColumnWidths()
   {
      int[] widths = new int[columns.length];

      for(int i = 0; i < columns.length; i++)
      {
         widths[i] = columns[i].getWidth();
      }

      return widths;
   }
}
