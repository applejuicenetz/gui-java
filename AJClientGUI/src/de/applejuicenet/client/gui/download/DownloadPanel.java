/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.download.table.DownloadSourcesTableModel;
import de.applejuicenet.client.gui.download.table.DownloadTableDownloadFilenameCellRenderer;
import de.applejuicenet.client.gui.download.table.DownloadTableFilenameCellRenderer;
import de.applejuicenet.client.gui.download.table.DownloadTablePowerdownloadCellRenderer;
import de.applejuicenet.client.gui.download.table.DownloadsTableModel;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.tablecellrenderer.ProgressTableCellRenderer;
import de.applejuicenet.client.shared.tablecellrenderer.SizeTableCellRenderer;
import de.applejuicenet.client.shared.tablecellrenderer.SpeedTableCellRenderer;
import de.applejuicenet.client.shared.tablecellrenderer.StringTableCellRenderer;
import de.applejuicenet.client.shared.tablecellrenderer.VersionTableCellRenderer;

import de.tklsoft.gui.controls.TKLTextField;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/DownloadPanel.java,v
 * 1.109 2004/06/23 12:39:15 maj0r Exp $
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
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class DownloadPanel extends TklPanel
{
   private DownloadOverviewPanel     downloadOverviewPanel;
   private PowerDownloadPanel        powerDownloadPanel;
   private DownloadsTableModel       downloadActiveTableModel;
   private JTable                    downloadActiveTable;
   private DownloadSourcesTableModel downloadSourceTableModel;
   private JTable                    downloadSourceTable;
   private JPopupMenu                popup                           = new JPopupMenu();
   private JScrollPane               aScrollPane;
   private JMenuItem                 abbrechen;
   private JMenuItem                 pause;
   private JMenuItem                 fortsetzen;
   private JMenuItem                 umbenennen;
   private JMenuItem                 zielordner;
   private JMenuItem                 fertigEntfernen;
   private JMenuItem                 itemReleaseInfo             = new JMenuItem();
   private JMenuItem                 itemCopyToClipboard             = new JMenuItem();
   private JMenuItem                 itemCopyToClipboardWithSources  = new JMenuItem();
   private JMenuItem                 itemOpenWithProgram             = new JMenuItem();
   private JMenuItem                 itemOpenWithDefaultProgram      = new JMenuItem();
   private JSplitPane                splitPane;
   private Logger                    logger;
   private TableColumn[]             downloadColumns                 = new TableColumn[DownloadsTableModel.CLASS_TYPES.length];
   private TableColumn[]             downloadSourceColumns           = new TableColumn[DownloadSourcesTableModel.CLASS_TYPES.length];
   private JPopupMenu                columnDownloadPopup             = new JPopupMenu();
   private JCheckBoxMenuItem[]       columnDownloadPopupItems        = new JCheckBoxMenuItem[downloadColumns.length];
   private JPopupMenu                columnDownloadSourcesPopup      = new JPopupMenu();
   private JCheckBoxMenuItem[]       columnDownloadSourcesPopupItems = new JCheckBoxMenuItem[downloadSourceColumns.length];
   private JPopupMenu                menu;
   private JScrollPane               downloadSourcesScrollPane;

   public DownloadPanel(GuiController guiController)
   {
      super(guiController);
      try
      {
         downloadOverviewPanel = new DownloadOverviewPanel(this);
         powerDownloadPanel    = new PowerDownloadPanel((DownloadController) guiController);
         init();
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public JTable getDownloadTable()
   {
      return downloadActiveTable;
   }

   public JTable getDownloadSourceTable()
   {
      return downloadSourceTable;
   }

   public TableColumn[] getDownloadTableColumns()
   {
      return downloadColumns;
   }

   public TableColumn[] getDownloadSourcesTableColumns()
   {
      return downloadSourceColumns;
   }

   public DownloadOverviewPanel getDownloadOverviewPanel()
   {
      return downloadOverviewPanel;
   }

   public JScrollPane getScrollPane()
   {
      return aScrollPane;
   }

   public JSplitPane getSplitPane()
   {
      return splitPane;
   }

   public PowerDownloadPanel getPowerDownloadPanel()
   {
      return powerDownloadPanel;
   }

   public DownloadsTableModel getDownloadTableModel()
   {
      return downloadActiveTableModel;
   }

   public DownloadSourcesTableModel getDownloadSourcesTableModel()
   {
      return downloadSourceTableModel;
   }

   public JButton getBtnPowerDownload()
   {
      return powerDownloadPanel.getBtnPdl();
   }

   public JRadioButton getBtnPowerDownloadInaktiv()
   {
      return powerDownloadPanel.btnInaktiv;
   }

   public JRadioButton getBtnPowerDownloadAktiv()
   {
      return powerDownloadPanel.btnAktiv;
   }

   public TKLTextField getRatioField()
   {
      return powerDownloadPanel.ratio;
   }

   public JMenuItem getMnuAbbrechen()
   {
      return abbrechen;
   }

   public JMenuItem getMnuPause()
   {
      return pause;
   }

   public JMenuItem getMnuFortsetzen()
   {
      return fortsetzen;
   }

   public JMenuItem getMnuUmbenennen()
   {
      return umbenennen;
   }

   public JMenuItem getMnuZielordner()
   {
      return zielordner;
   }

   public JMenuItem getMnuFertigeEntfernen()
   {
      return fertigEntfernen;
   }

   public JMenuItem getMnuReleaseInfo()
   {
      return itemReleaseInfo;
   }
   
   public JMenuItem getMnuOpenWithProgram()
   {
      return itemOpenWithProgram;
   }

   public JMenuItem getMnuOpenWithDefaultProgram()
   {
      return itemOpenWithDefaultProgram;
   }

   public JMenuItem getMnuCopyToClipboard()
   {
      return itemCopyToClipboard;
   }

   public JMenuItem getMnuCopyToClipboardWithSources()
   {
      return itemCopyToClipboardWithSources;
   }

   public JPopupMenu getPopup()
   {
      return popup;
   }

   private void init() throws Exception
   {
      setLayout(new BorderLayout());
      JPanel topPanel = new JPanel();

      topPanel.setLayout(new GridBagLayout());
      JPanel bottomPanel = new JPanel();

      bottomPanel.setLayout(new BorderLayout());

      abbrechen       = new JMenuItem();
      pause           = new JMenuItem();
      fortsetzen      = new JMenuItem();
      umbenennen      = new JMenuItem();
      zielordner      = new JMenuItem();
      fertigEntfernen = new JMenuItem();

      menu = new JPopupMenu();

      IconManager im = IconManager.getInstance();

      abbrechen.setIcon(im.getIcon("abbrechen"));
      pause.setIcon(im.getIcon("pause"));
      umbenennen.setIcon(im.getIcon("umbenennen"));
      zielordner.setIcon(im.getIcon("zielordner"));
      fertigEntfernen.setIcon(im.getIcon("bereinigen"));
      fortsetzen.setIcon(im.getIcon("pause"));
      itemReleaseInfo.setIcon(im.getIcon("pause"));
      itemCopyToClipboard.setIcon(im.getIcon("clipboard"));
      itemCopyToClipboardWithSources.setIcon(im.getIcon("clipboard"));

      popup.add(fortsetzen);
      popup.add(pause);
      popup.add(abbrechen);
      popup.add(new JSeparator());
      popup.add(umbenennen);
      popup.add(zielordner);
      popup.add(new JSeparator());
      popup.add(fertigEntfernen);
      popup.add(new JSeparator());
      popup.add(itemCopyToClipboard);
      popup.add(itemCopyToClipboardWithSources);
      popup.add(new JSeparator());
      popup.add(itemReleaseInfo);
      popup.add(itemOpenWithProgram);
      itemOpenWithProgram.setIcon(im.getIcon("vlc"));
      popup.add(itemOpenWithDefaultProgram);

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor     = GridBagConstraints.NORTH;
      constraints.fill       = GridBagConstraints.BOTH;
      constraints.gridx      = 0;
      constraints.gridy      = 0;
      constraints.gridwidth  = 3;
      constraints.gridheight = 1;

      GridBagConstraints constraints2 = new GridBagConstraints();

      constraints2.anchor = GridBagConstraints.NORTH;
      constraints2.fill   = GridBagConstraints.BOTH;
      constraints2.gridx  = 0;
      constraints2.gridy  = 0;

      constraints.gridwidth = 3;
      constraints.gridx     = 0;
      constraints.gridy     = 1;
      constraints.weighty   = 0.4;
      constraints.weightx   = 1;

      downloadActiveTableModel = new DownloadsTableModel();
      downloadActiveTable      = new JTable(downloadActiveTableModel);
      downloadActiveTable.setDefaultRenderer(String.class, new StringTableCellRenderer());
      downloadActiveTable.getColumnModel().getColumn(0).setCellRenderer(new DownloadTableDownloadFilenameCellRenderer());
      downloadActiveTable.getColumnModel().getColumn(2).setCellRenderer(new SizeTableCellRenderer());
      downloadActiveTable.getColumnModel().getColumn(3).setCellRenderer(new SizeTableCellRenderer());
      downloadActiveTable.getColumnModel().getColumn(4).setCellRenderer(new SpeedTableCellRenderer());
      downloadActiveTable.getColumnModel().getColumn(6).setCellRenderer(new ProgressTableCellRenderer());
      downloadActiveTable.getColumnModel().getColumn(7).setCellRenderer(new SizeTableCellRenderer());
      downloadActiveTable.getColumnModel().getColumn(8).setCellRenderer(new DownloadTablePowerdownloadCellRenderer());

      TableColumnModel   model    = downloadActiveTable.getColumnModel();
      SortButtonRenderer renderer = new SortButtonRenderer();

      JTableHeader       header = downloadActiveTable.getTableHeader();

      header.setDefaultRenderer(renderer);
      header.addMouseListener(new HeaderListener(header, renderer));
      for(int i = 0; i < model.getColumnCount(); i++)
      {
         downloadColumns[i]          = model.getColumn(i);
         columnDownloadPopupItems[i] = new JCheckBoxMenuItem((String) downloadColumns[i].getHeaderValue());
         final int x                 = i;

         columnDownloadPopupItems[i].addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  if(columnDownloadPopupItems[x].isSelected())
                  {
                     downloadActiveTable.getColumnModel().addColumn(downloadColumns[x]);
                     PositionManagerImpl.getInstance().setDownloadColumnVisible(x, true);
                     PositionManagerImpl.getInstance()
                     .setDownloadColumnIndex(x,
                                             downloadActiveTable.getColumnModel().getColumnIndex(downloadColumns[x].getIdentifier()));
                  }
                  else
                  {
                     downloadActiveTable.getColumnModel().removeColumn(downloadColumns[x]);
                     PositionManagerImpl.getInstance().setDownloadColumnVisible(x, false);
                     for(int y = 0; y < downloadColumns.length; y++)
                     {
                        try
                        {
                           PositionManagerImpl.getInstance()
                           .setDownloadColumnIndex(y,
                                                   downloadActiveTable.getColumnModel()
                                                   .getColumnIndex(downloadColumns[y].getIdentifier()));
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
         columnDownloadPopup.add(columnDownloadPopupItems[i]);
      }

      columnDownloadPopupItems[0].setEnabled(false);

      aScrollPane = new JScrollPane(downloadActiveTable);
      aScrollPane.setBackground(downloadActiveTable.getBackground());
      downloadActiveTable.getTableHeader().setBackground(downloadActiveTable.getBackground());
      aScrollPane.getViewport().setOpaque(false);
      topPanel.add(aScrollPane, constraints);

      downloadSourceTableModel = new DownloadSourcesTableModel();
      downloadSourceTable      = new JTable(downloadSourceTableModel);
      downloadSourceTable.setDefaultRenderer(String.class, new StringTableCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(0).setCellRenderer(new DownloadTableFilenameCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(3).setCellRenderer(new SizeTableCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(4).setCellRenderer(new SizeTableCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(5).setCellRenderer(new SpeedTableCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(7).setCellRenderer(new ProgressTableCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(8).setCellRenderer(new SizeTableCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(9).setCellRenderer(new DownloadTablePowerdownloadCellRenderer());
      downloadSourceTable.getColumnModel().getColumn(10).setCellRenderer(new VersionTableCellRenderer());

      renderer = new SortButtonRenderer();

      header = downloadSourceTable.getTableHeader();

      header.setDefaultRenderer(renderer);
      header.addMouseListener(new HeaderListener(header, renderer));
      model = downloadSourceTable.getColumnModel();
      for(int i = 0; i < model.getColumnCount(); i++)
      {
         downloadSourceColumns[i]           = model.getColumn(i);
         columnDownloadSourcesPopupItems[i] = new JCheckBoxMenuItem((String) downloadSourceColumns[i].getHeaderValue());
         final int x                        = i;

         columnDownloadSourcesPopupItems[i].addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  if(columnDownloadSourcesPopupItems[x].isSelected())
                  {
                     downloadSourceTable.getColumnModel().addColumn(downloadSourceColumns[x]);
                     PositionManagerImpl.getInstance().setDownloadSourcesColumnVisible(x, true);
                     PositionManagerImpl.getInstance()
                     .setDownloadSourcesColumnIndex(x,
                                                    downloadSourceTable.getColumnModel()
                                                    .getColumnIndex(downloadSourceColumns[x].getIdentifier()));
                  }
                  else
                  {
                     downloadSourceTable.getColumnModel().removeColumn(downloadSourceColumns[x]);
                     PositionManagerImpl.getInstance().setDownloadSourcesColumnVisible(x, false);
                     for(int y = 0; y < downloadSourceColumns.length; y++)
                     {
                        try
                        {
                           PositionManagerImpl.getInstance()
                           .setDownloadSourcesColumnIndex(y,
                                                          downloadSourceTable.getColumnModel()
                                                          .getColumnIndex(downloadSourceColumns[y].getIdentifier()));
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
         columnDownloadSourcesPopup.add(columnDownloadSourcesPopupItems[i]);
      }

      downloadSourcesScrollPane = new JScrollPane(downloadSourceTable);

      downloadSourcesScrollPane.setBackground(downloadSourceTable.getBackground());
      downloadSourceTable.getTableHeader().setBackground(downloadSourceTable.getBackground());
      downloadSourcesScrollPane.getViewport().setOpaque(false);

      constraints.gridwidth = 3;
      constraints.gridx     = 0;
      constraints.gridy     = 2;
      constraints.weighty   = 0.6;
      constraints.weightx   = 1;

      topPanel.add(downloadSourcesScrollPane, constraints);

      constraints.gridx   = 0;
      constraints.gridy   = 0;
      constraints.weighty = 1;

      bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);
      bottomPanel.add(downloadOverviewPanel, BorderLayout.CENTER);

      splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
      splitPane.setBorder(null);
      add(splitPane, BorderLayout.CENTER);

      downloadActiveTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
   }

   public JScrollPane getDownloadSourcesScrollPane()
   {
      return downloadSourcesScrollPane;
   }

   public int[] getDownloadColumnWidths()
   {
      int[] widths = new int[downloadColumns.length];

      for(int i = 0; i < downloadColumns.length; i++)
      {
         widths[i] = downloadColumns[i].getWidth();
      }

      return widths;
   }

   public int[] getDownloadSourcesColumnWidths()
   {
      int[] widths = new int[downloadSourceColumns.length];

      for(int i = 0; i < downloadSourceColumns.length; i++)
      {
         widths[i] = downloadSourceColumns[i].getWidth();
      }

      return widths;
   }

   public JCheckBoxMenuItem[] getColumnDownloadPopupItems()
   {
      return columnDownloadPopupItems;
   }

   public JPopupMenu getColumnDownloadPopup()
   {
      return columnDownloadPopup;
   }

   public JPopupMenu getColumnDownloadSourcesPopup()
   {
      return columnDownloadSourcesPopup;
   }

   public JCheckBoxMenuItem[] getColumnDownloadSourcesPopupItems()
   {
      return columnDownloadSourcesPopupItems;
   }
}
