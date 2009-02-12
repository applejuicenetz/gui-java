/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.download.table.DownloadSourcesTableModel;
import de.applejuicenet.client.gui.download.table.DownloadsTableModel;
import de.applejuicenet.client.gui.options.IncomingDirSelectionDialog;
import de.applejuicenet.client.gui.upload.HeaderPopupListener;
import de.applejuicenet.client.gui.upload.UploadMouseMotionListener;
import de.applejuicenet.client.shared.DesktopTools;
import de.applejuicenet.client.shared.ReleaseInfoDialog;
import de.applejuicenet.client.shared.SoundPlayer;

public class DownloadController extends GuiController
{
   private static DownloadController instance                        = null;
   private static final int          ABBRECHEN                       = 0;
   private static final int          COPY_TO_CLIPBOARD               = 1;
   private static final int          COPY_TO_CLIPBOARD_WITH_SOURCES  = 2;
   private static final int          OPEN_WITH_PROGRAM               = 3;
   private static final int          OPEN_WITH_DEFAULT_PROGRAM       = 16;
   private static final int          PAUSE                           = 4;
   private static final int          FORTSETZEN                      = 5;
   private static final int          UMBENENNEN                      = 6;
   private static final int          ZIELORDNER_AENDERN              = 7;
   private static final int          FERTIGE_ENTFERNEN               = 8;
   private static final int          PARTLISTE_ANZEIGEN              = 9;
   private static final int          START_DOWNLOAD                  = 10;
   private static final int          PARTLISTE_ANZEIGEN_PER_BUTTON   = 11;
   private static final int          START_POWERDOWNLOAD             = 12;
   private static final int          HEADER_DOWNLOAD_POPUP           = 13;
   private static final int          HEADER_DOWNLOAD_SOURCES_POPUP   = 14;
   private static final int          HEADER_DOWNLOAD_DRAGGED         = 15;
   private static final int          HEADER_DOWNLOAD_SOURCES_DRAGGED = 17;
   private static final int          RELEASE_INFO                    = 18;
   private DownloadPanel             downloadPanel;
   private boolean                   initialized                     = false;
   private String                    dialogTitel;
   private String                    downloadAbbrechen;
   private DownloadPartListWatcher   downloadPartListWatcher;
   private boolean                   firstUpdate                     = true;
   private boolean                   isFirstDownloadPropertyChanged  = true;
   private boolean                   selected                        = false;
   private String                    alreadyLoaded;
   private String                    invalidLink;
   private String                    linkFailure;

   private DownloadController()
   {
      super();
      downloadPanel = new DownloadPanel(this);
      try
      {
         init();
         AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.DOWNLOAD_CHANGED);

      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public static synchronized DownloadController getInstance()
   {
      if(null == instance)
      {
         instance = new DownloadController();
      }

      return instance;
   }

   private void init()
   {
      downloadPanel.getMnuAbbrechen().addActionListener(new GuiControllerActionListener(this, ABBRECHEN));
      downloadPanel.getMnuCopyToClipboard().addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
      downloadPanel.getMnuCopyToClipboardWithSources()
      .addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_WITH_SOURCES));
      downloadPanel.getMnuPause().addActionListener(new GuiControllerActionListener(this, PAUSE));
      downloadPanel.getMnuFortsetzen().addActionListener(new GuiControllerActionListener(this, FORTSETZEN));
      downloadPanel.getMnuUmbenennen().addActionListener(new GuiControllerActionListener(this, UMBENENNEN));
      downloadPanel.getMnuZielordner().addActionListener(new GuiControllerActionListener(this, ZIELORDNER_AENDERN));
      downloadPanel.getMnuFertigeEntfernen().addActionListener(new GuiControllerActionListener(this, FERTIGE_ENTFERNEN));
      downloadPanel.getMnuReleaseInfo().addActionListener(new GuiControllerActionListener(this, RELEASE_INFO));
      downloadPanel.getBtnPowerDownload().addActionListener(new GuiControllerActionListener(this, START_POWERDOWNLOAD));
      if(AppleJuiceClient.getAjFassade().isLocalhost())
      {
         downloadPanel.getMnuOpenWithProgram().addActionListener(new GuiControllerActionListener(this, OPEN_WITH_PROGRAM));
         downloadPanel.getMnuOpenWithProgram().setVisible(true);
         if(DesktopTools.isAdvancedSupported())
         {
            downloadPanel.getMnuOpenWithDefaultProgram()
            .addActionListener(new GuiControllerActionListener(this, OPEN_WITH_DEFAULT_PROGRAM));
         }
         else
         {
            downloadPanel.getMnuOpenWithDefaultProgram().setVisible(false);
         }
      }
      else
      {
         downloadPanel.getMnuOpenWithProgram().setEnabled(false);
         downloadPanel.getMnuOpenWithDefaultProgram().setVisible(false);
      }

      downloadPanel.getDownloadTable().addMouseListener(new MouseAdapter()
         {
            @Override
            public void mouseClicked(MouseEvent e)
            {
               if(SwingUtilities.isLeftMouseButton(e))
               {
                  int selected = downloadPanel.getDownloadTable().getSelectedRow();

                  if(selected == -1)
                  {
                     return;
                  }

                  Download download = downloadPanel.getDownloadTableModel().getRow(selected);

                  downloadClicked(download);
                  if(downloadPanel.getDownloadSourcesTableModel().setDownload(download))
                  {
                     downloadPanel.getDownloadSourcesTableModel().fireTableDataChanged();
                  }
               }
               else if(SwingUtilities.isRightMouseButton(e))
               {
                  int row = downloadPanel.getDownloadTable().rowAtPoint(e.getPoint());

                  if(!downloadPanel.getDownloadTable().getSelectionModel().isSelectedIndex(row))
                  {
                     downloadPanel.getDownloadTable().getSelectionModel().setSelectionInterval(row, row);
                  }

                  maybeShowDownloadPopup(e);
               }
            }
         });
      downloadPanel.getDownloadTable().addKeyListener(new KeyAdapter()
         {
            public void keyPressed(KeyEvent ke)
            {
               switch(ke.getKeyCode())
               {

                  case KeyEvent.VK_F2:
                  {
                     renameDownload();
                     break;
                  }

                  case KeyEvent.VK_F3:
                  {
                     changeTargetDir();
                     break;
                  }

                  case KeyEvent.VK_F5:
                  {
                     pausieren();
                     break;
                  }

                  case KeyEvent.VK_F6:
                  {
                     fortsetzen();
                     break;
                  }

                  default:
                     break;
               }
            }
         });
      downloadPanel.getDownloadSourceTable().addMouseListener(new MouseAdapter()
         {
            @Override
            public void mouseClicked(MouseEvent e)
            {
               if(SwingUtilities.isLeftMouseButton(e))
               {
                  int selected = downloadPanel.getDownloadSourceTable().getSelectedRow();

                  if(selected == -1)
                  {
                     return;
                  }

                  DownloadSource downloadSource = downloadPanel.getDownloadSourcesTableModel().getRow(selected);

                  downloadSourceClicked(downloadSource);
               }
            }
         });
      downloadPartListWatcher = new DownloadPartListWatcher(this);

      JTableHeader header = downloadPanel.getDownloadTable().getTableHeader();

      header.addMouseListener(new HeaderPopupListener(this, HEADER_DOWNLOAD_POPUP));
      header.addMouseMotionListener(new UploadMouseMotionListener(this, HEADER_DOWNLOAD_DRAGGED));

      header = downloadPanel.getDownloadSourceTable().getTableHeader();

      header.addMouseListener(new HeaderPopupListener(this, HEADER_DOWNLOAD_SOURCES_POPUP));
      header.addMouseMotionListener(new UploadMouseMotionListener(this, HEADER_DOWNLOAD_SOURCES_DRAGGED));

      LanguageSelector.getInstance().addLanguageListener(this);
   }

   public Value[] getCustomizedValues()
   {
      return new Value[0];
   }

   public void fireAction(int actionId, Object source)
   {
      switch(actionId)
      {

         case HEADER_DOWNLOAD_DRAGGED:
         {
            headerDownloadsDragged();
            break;
         }

         case HEADER_DOWNLOAD_SOURCES_DRAGGED:
         {
            headerDownloadSourcesDragged();
            break;
         }

         case HEADER_DOWNLOAD_POPUP:
         {
            headerDownloadsPopup((MouseEvent) source);
            break;
         }

         case HEADER_DOWNLOAD_SOURCES_POPUP:
         {
            headerDownloadSourcesPopup((MouseEvent) source);
            break;
         }

         case ABBRECHEN:
         {
            downloadAbbrechen();
            break;
         }

         case COPY_TO_CLIPBOARD:
         {
            copyDownloadLinkToClipboard();
            break;
         }

         case COPY_TO_CLIPBOARD_WITH_SOURCES:
         {
            copyDownloadLinkToClipboardWithSources();
            break;
         }

         case OPEN_WITH_PROGRAM:
         {
            openWithProgram();
            break;
         }

         case OPEN_WITH_DEFAULT_PROGRAM:
         {
            openWithDefaultProgram();
            break;
         }

         case PAUSE:
         {
            pausieren();
            break;
         }

         case FORTSETZEN:
         {
            fortsetzen();
            break;
         }

         case UMBENENNEN:
         {
            renameDownload();
            break;
         }

         case ZIELORDNER_AENDERN:
         {
            changeTargetDir();
            break;
         }

         case FERTIGE_ENTFERNEN:
         {
            clearReadyDownloads();
            break;
         }

         case START_POWERDOWNLOAD:
         {
            startPowerDownload();
            break;
         }

         case RELEASE_INFO:
         {
            showReleaseInfo();
            break;
         }

         default:
            logger.error("Unregistrierte EventId " + actionId);
      }
   }

   private void showReleaseInfo()
   {
      Download[] selectedItems = getSelectedDownloads();

      if(selectedItems == null || selectedItems.length < 1)
      {
         return;
      }

      Download curDownload = selectedItems[0];

      ReleaseInfoDialog.showReleaseInfo(curDownload.getHash());
   }

   private void headerDownloadsDragged()
   {
      PositionManager  pm          = PositionManagerImpl.getInstance();
      TableColumnModel columnModel = downloadPanel.getDownloadTable().getColumnModel();
      TableColumn[]    columns     = downloadPanel.getDownloadTableColumns();

      for(int i = 0; i < columns.length; i++)
      {
         pm.setDownloadColumnIndex(i, columnModel.getColumnIndex(columns[i].getIdentifier()));
      }
   }

   private void headerDownloadSourcesDragged()
   {
      PositionManager  pm          = PositionManagerImpl.getInstance();
      TableColumnModel columnModel = downloadPanel.getDownloadSourceTable().getColumnModel();
      TableColumn[]    columns     = downloadPanel.getDownloadSourcesTableColumns();

      for(int i = 0; i < columns.length; i++)
      {
         pm.setDownloadSourcesColumnIndex(i, columnModel.getColumnIndex(columns[i].getIdentifier()));
      }
   }

   private void headerDownloadsPopup(MouseEvent e)
   {
      TableColumn[]       columns          = downloadPanel.getDownloadTableColumns();
      JCheckBoxMenuItem[] columnPopupItems = downloadPanel.getColumnDownloadPopupItems();
      TableColumnModel    tableColumnModel = downloadPanel.getDownloadTable().getColumnModel();

      for(int i = 1; i < columns.length; i++)
      {
         try
         {
            tableColumnModel.getColumnIndex(columns[i].getIdentifier());
            columnPopupItems[i].setSelected(true);
         }
         catch(java.lang.IllegalArgumentException iaE)
         {
            columnPopupItems[i].setSelected(false);
         }
      }

      downloadPanel.getColumnDownloadPopup().show(downloadPanel.getDownloadTable().getTableHeader(), e.getX(), e.getY());
   }

   private void headerDownloadSourcesPopup(MouseEvent e)
   {
      TableColumn[]       columns          = downloadPanel.getDownloadSourcesTableColumns();
      JCheckBoxMenuItem[] columnPopupItems = downloadPanel.getColumnDownloadSourcesPopupItems();
      TableColumnModel    tableColumnModel = downloadPanel.getDownloadSourceTable().getColumnModel();

      for(int i = 1; i < columns.length; i++)
      {
         try
         {
            tableColumnModel.getColumnIndex(columns[i].getIdentifier());
            columnPopupItems[i].setSelected(true);
         }
         catch(java.lang.IllegalArgumentException iaE)
         {
            columnPopupItems[i].setSelected(false);
         }
      }

      downloadPanel.getColumnDownloadSourcesPopup().show(downloadPanel.getDownloadSourceTable().getTableHeader(), e.getX(), e.getY());
   }

   public JComponent getComponent()
   {
      return downloadPanel;
   }

   private boolean handleDownloadDataPropertyChangeEvent(DownloadDataPropertyChangeEvent event)
   {
      return false;
   }

   private void clearReadyDownloads()
   {
      new Thread()
         {
            @Override
            public void run()
            {
               AppleJuiceClient.getAjFassade().cleanDownloadList();
            }
         }.start();
      downloadPanel.getPowerDownloadPanel().getBtnPdl().setEnabled(false);
      downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
      downloadPanel.getDownloadTable().getSelectionModel().clearSelection();
   }

   private void downloadSourceClicked(DownloadSource downloadSource)
   {
      tryGetPartList(downloadSource);
   }

   private void downloadClicked(Download download)
   {
      tryGetPartList(download);

      if(!downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive())
      {
         downloadPanel.getPowerDownloadPanel().getBtnPdl().setEnabled(true);
         if(download.getStatus() == Download.SUCHEN_LADEN || download.getStatus() == Download.PAUSIERT)
         {
            downloadPanel.getPowerDownloadPanel().setPwdlValue(download.getPowerDownload());
         }
         else
         {
            downloadPanel.getPowerDownloadPanel().getBtnPdl().setEnabled(false);
            downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
         }
      }
   }

   private void startPowerDownload()
   {
      try
      {
         Download[] selectedDownloads = getSelectedDownloads();

         if(selectedDownloads != null && selectedDownloads.length != 0)
         {
            int powerDownload = 0;

            if(!downloadPanel.getBtnPowerDownloadInaktiv().isSelected())
            {
               String temp  = downloadPanel.getRatioField().getText();
               double power = 2.2;

               try
               {
                  power = Double.parseDouble(temp);
               }
               catch(NumberFormatException nfE)
               {
                  if(logger.isEnabledFor(Level.ERROR))
                  {
                     logger.error(ApplejuiceFassade.ERROR_MESSAGE, nfE);
                  }

                  downloadPanel.getRatioField().setText("2.2");
               }

               powerDownload = (int) (power * 10 - 10);
            }

            List<Download> temp = new ArrayList<Download>();

            for(Download curDownload : selectedDownloads)
            {
               if(curDownload.getStatus() == Download.PAUSIERT || curDownload.getStatus() == Download.SUCHEN_LADEN)
               {
                  temp.add(curDownload);
               }
            }

            AppleJuiceClient.getAjFassade().setPowerDownload(temp, new Integer(powerDownload));
            if(downloadPanel.getBtnPowerDownloadAktiv().isSelected())
            {
               SoundPlayer.getInstance().playSound(SoundPlayer.POWER);
            }
         }
      }
      catch(Exception ex)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
         }
      }
   }

   private void maybeShowDownloadPopup(MouseEvent e)
   {
      int[] selected = downloadPanel.getDownloadTable().getSelectedRows();

      if(null == selected || selected.length == 0)
      {
         return;
      }

      Download[] selectedDownloads = new Download[selected.length];

      for(int i = 0; i < selected.length; i++)
      {
         selectedDownloads[i] = downloadPanel.getDownloadTableModel().getRow(selected[i]);
      }

      boolean pausiert = false;
      boolean laufend = false;

      downloadPanel.getMnuZielordner().setEnabled(true);
      for(Download curDownload : selectedDownloads)
      {
         if(curDownload.getStatus() == Download.SUCHEN_LADEN)
         {
            laufend = true;
         }
         else if(curDownload.getStatus() == Download.PAUSIERT)
         {
            pausiert = true;
         }
      }

      downloadPanel.getMnuUmbenennen().setEnabled(selected.length == 1);
      downloadPanel.getMnuCopyToClipboard().setEnabled(selected.length == 1);
      downloadPanel.getMnuCopyToClipboardWithSources().setEnabled(selected.length == 1);
      downloadPanel.getMnuPause().setEnabled(laufend);
      downloadPanel.getMnuFortsetzen().setEnabled(pausiert);
      downloadPanel.getPopup().show(downloadPanel.getDownloadTable(), e.getX(), e.getY());
   }

   private void tryGetPartList(DownloadSource downloadSource)
   {
      downloadPartListWatcher.setDownloadNode(downloadSource);
   }

   private void tryGetPartList(Download download)
   {
      downloadPartListWatcher.setDownloadNode(download);
   }

   private Download[] getSelectedDownloads()
   {
      try
      {
         int[] selected = downloadPanel.getDownloadTable().getSelectedRows();

         if(null == selected || selected.length == 0)
         {
            return new Download[0];
         }

         Download[] selectedDownloads = new Download[selected.length];

         for(int i = 0; i < selected.length; i++)
         {
            selectedDownloads[i] = downloadPanel.getDownloadTableModel().getRow(selected[i]);
         }

         return selectedDownloads;
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }

         return new Download[0];
      }
   }

   private void renameDownload()
   {
      Download[] selectedItems = getSelectedDownloads();

      if(selectedItems != null && selectedItems.length == 1)
      {
         Download             curDownload          = selectedItems[0];
         RenameDownloadDialog renameDownloadDialog = new RenameDownloadDialog(AppleJuiceDialog.getApp(), curDownload);

         renameDownloadDialog.setVisible(true);
         String neuerName = renameDownloadDialog.getNewName();

         if(neuerName == null)
         {
            return;
         }
         else
         {
            if(curDownload.getFilename().compareTo(neuerName) != 0)
            {
               try
               {
                  AppleJuiceClient.getAjFassade().renameDownload(curDownload, neuerName);
               }
               catch(IllegalArgumentException e)
               {
                  logger.error(e);
               }
            }
         }
      }
   }

   private void pausieren()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads != null && selectedDownloads.length != 0 &&
            !downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive())
      {
         final List<Download> pausieren = new ArrayList<Download>();

         for(Download curDownload : selectedDownloads)
         {
            if(curDownload.getStatus() == Download.SUCHEN_LADEN)
            {
               pausieren.add(curDownload);
            }
         }

         if(pausieren.size() > 0)
         {
            new Thread()
               {
                  public void run()
                  {
                     try
                     {
                        AppleJuiceClient.getAjFassade().pauseDownload(pausieren);
                     }
                     catch(IllegalArgumentException e)
                     {
                        logger.error(e);
                     }
                  }
               }.start();
         }
      }
   }

   private void fortsetzen()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads != null && selectedDownloads.length != 0 &&
            !downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive())
      {
         final List<Download> fortsetzen = new ArrayList<Download>();

         for(Download curDownload : selectedDownloads)
         {
            if(curDownload.getStatus() == Download.PAUSIERT)
            {
               fortsetzen.add(curDownload);
            }
         }

         if(fortsetzen.size() > 0)
         {
            new Thread()
               {
                  public void run()
                  {
                     try
                     {
                        AppleJuiceClient.getAjFassade().resumeDownload(fortsetzen);
                     }
                     catch(IllegalArgumentException e)
                     {
                        logger.error(e);
                     }
                  }
               }.start();
         }
      }
   }

   private void changeTargetDir()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads == null || selectedDownloads.length == 0)
      {
         return;
      }

      String selectedDir = null;

      for(Download curDownload : selectedDownloads)
      {
         selectedDir = curDownload.getTargetDirectory();
         if(null != selectedDir && selectedDir.trim().length() > 0)
         {
            break;
         }
      }

      String[]                   dirs                       = AppleJuiceClient.getAjFassade().getCurrentIncomingDirs();
      IncomingDirSelectionDialog incomingDirSelectionDialog = new IncomingDirSelectionDialog(AppleJuiceDialog.getApp(), dirs,
                                                                                             selectedDir);

      incomingDirSelectionDialog.setVisible(true);
      String neuerName = incomingDirSelectionDialog.getSelectedIncomingDir();

      if(neuerName == null)
      {
         return;
      }
      else
      {
         neuerName = neuerName.trim();
         if(neuerName.indexOf(File.separator) == 0 || neuerName.indexOf(ApplejuiceFassade.separator) == 0)
         {
            neuerName = neuerName.substring(1);
         }
      }

      List<Download> toChange = new ArrayList<Download>();

      for(Download curDownload : selectedDownloads)
      {
         if(!neuerName.equals(curDownload.getTargetDirectory()))
         {
            toChange.add(curDownload);
         }
      }

      if(toChange.size() > 0)
      {
         try
         {
            AppleJuiceClient.getAjFassade().setTargetDir(toChange, neuerName);
         }
         catch(IllegalArgumentException e)
         {
            logger.error(e);
         }
      }
   }

   private void openWithProgram()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads != null && selectedDownloads.length == 1)
      {
         String programToExecute = OptionsManagerImpl.getInstance().getOpenProgram();

         if(programToExecute.length() != 0)
         {
            for(Download curDownload : selectedDownloads)
            {
               Integer shareId = new Integer(curDownload.getShareId());

               try
               {
                  Share share = (Share) AppleJuiceClient.getAjFassade().getObjectById(shareId);

                  if(share != null)
                  {
                     String filename = share.getFilename();

                     try
                     {
                        Runtime.getRuntime().exec(new String[] {programToExecute, filename});
                     }
                     catch(Exception ex)
                     {

                        //nix zu tun
                     }
                  }
               }
               catch(IllegalArgumentException e)
               {
                  logger.error(e);
               }
            }
         }
      }
   }

   private void openWithDefaultProgram()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads != null && selectedDownloads.length == 1)
      {
         for(Download curDownload : selectedDownloads)
         {
            Integer shareId = new Integer(curDownload.getShareId());

            try
            {
               Share share = (Share) AppleJuiceClient.getAjFassade().getObjectById(shareId);

               if(share != null)
               {
                  String filename = share.getFilename();

                  DesktopTools.open(new File(filename));
               }
            }
            catch(IllegalArgumentException e)
            {
               logger.error(e);
            }
         }
      }
   }

   private void copyDownloadLinkToClipboardWithSources()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads != null && selectedDownloads.length == 1)
      {
         Download     curDownload = selectedDownloads[0];
         Clipboard    cb     = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuffer toCopy = new StringBuffer();

         toCopy.append("ajfsp://file|");
         boolean copyToClipboard = false;

         toCopy.append(curDownload.getFilename() + "|" + curDownload.getHash() + "|" + curDownload.getGroesse());
         copyToClipboard = true;

         if(copyToClipboard)
         {
            long        port        = AppleJuiceClient.getAjFassade().getAJSettings().getPort();
            Information information = AppleJuiceClient.getAjFassade().getInformation();

            toCopy.append("|");
            toCopy.append(information.getExterneIP());
            toCopy.append(":");
            toCopy.append(port);
            if(information.getVerbindungsStatus() == Information.VERBUNDEN)
            {
               Server server = information.getServer();

               if(server != null)
               {
                  toCopy.append(":");
                  toCopy.append(server.getHost());
                  toCopy.append(":");
                  toCopy.append(server.getPort());
               }
            }

            toCopy.append("/");
            StringSelection contents = new StringSelection(toCopy.toString());

            cb.setContents(contents, null);
         }
      }
   }

   private void copyDownloadLinkToClipboard()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads != null && selectedDownloads.length == 1)
      {
         Download     curDownload = selectedDownloads[0];
         Clipboard    cb     = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuffer toCopy = new StringBuffer();

         toCopy.append("ajfsp://file|");
         toCopy.append(curDownload.getFilename() + "|" + curDownload.getHash() + "|" + curDownload.getGroesse() + "/");
         StringSelection contents = new StringSelection(toCopy.toString());

         cb.setContents(contents, null);
      }
   }

   private void downloadAbbrechen()
   {
      Download[] selectedDownloads = getSelectedDownloads();

      if(selectedDownloads != null && selectedDownloads.length != 0)
      {
         int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.getApp(), downloadAbbrechen, dialogTitel,
                                                    JOptionPane.YES_NO_OPTION);

         if(result == JOptionPane.YES_OPTION)
         {
            final List<Download> abbrechen = new ArrayList<Download>();

            for(Download curDownload : selectedDownloads)
            {
               abbrechen.add(curDownload);
            }

            if(abbrechen.size() > 0)
            {
               new Thread()
                  {
                     public void run()
                     {
                        try
                        {
                           AppleJuiceClient.getAjFassade().cancelDownload(abbrechen);
                           SoundPlayer.getInstance().playSound(SoundPlayer.ABGEBROCHEN);
                        }
                        catch(IllegalArgumentException e)
                        {
                           logger.error(e);
                        }
                     }
                  }.start();
            }
         }
      }
   }

   public void componentSelected()
   {
      try
      {
         selected = true;
         if(!initialized)
         {
            initialized = true;
            firstUpdate = false;
            int             width = downloadPanel.getScrollPane().getWidth() - 18;
            PositionManager pm = PositionManagerImpl.getInstance();

            TableColumn[]   columnsDownload        = downloadPanel.getDownloadTableColumns();
            TableColumn[]   columnsDownloadSources = downloadPanel.getDownloadSourcesTableColumns();
            int[]           sortDownloads          = null;
            int[]           sortDownloadSources    = null;

            if(pm.isLegal())
            {
               int[]                  widths         = pm.getDownloadWidths();
               boolean[]              visibilies     = pm.getDownloadColumnVisibilities();
               int[]                  indizes        = pm.getDownloadColumnIndizes();
               ArrayList<TableColumn> visibleColumns = new ArrayList<TableColumn>();

               for(int i = 0; i < columnsDownload.length; i++)
               {
                  columnsDownload[i].setPreferredWidth(widths[i]);
                  downloadPanel.getDownloadTable().removeColumn(columnsDownload[i]);
                  if(visibilies[i])
                  {
                     visibleColumns.add(columnsDownload[i]);
                  }
               }

               int pos = -1;

               for(int i = 0; i < visibleColumns.size(); i++)
               {
                  for(int x = 0; x < columnsDownload.length; x++)
                  {
                     if(visibleColumns.contains(columnsDownload[x]) && indizes[x] == pos + 1)
                     {
                        downloadPanel.getDownloadTable().addColumn(columnsDownload[x]);
                        pos++;
                        break;
                     }
                  }
               }

               widths        = pm.getDownloadSourcesWidths();
               visibilies    = pm.getDownloadSourcesColumnVisibilities();
               indizes       = pm.getDownloadSourcesColumnIndizes();
               sortDownloads = pm.getDownloadSort();

               visibleColumns = new ArrayList<TableColumn>();

               for(int i = 0; i < columnsDownloadSources.length; i++)
               {
                  columnsDownloadSources[i].setPreferredWidth(widths[i]);
                  downloadPanel.getDownloadSourceTable().removeColumn(columnsDownloadSources[i]);
                  if(visibilies[i])
                  {
                     visibleColumns.add(columnsDownloadSources[i]);
                  }
               }

               pos = -1;

               sortDownloadSources = pm.getDownloadSourcesSort();
               for(int i = 0; i < visibleColumns.size(); i++)
               {
                  for(int x = 0; x < columnsDownloadSources.length; x++)
                  {
                     if(visibleColumns.contains(columnsDownloadSources[x]) && indizes[x] == pos + 1)
                     {
                        downloadPanel.getDownloadSourceTable().addColumn(columnsDownloadSources[x]);
                        pos++;
                        break;
                     }
                  }
               }
            }
            else
            {
               for(int i = 0; i < columnsDownload.length; i++)
               {
                  columnsDownload[i].setPreferredWidth(width / columnsDownload.length);
               }

               for(int i = 0; i < columnsDownloadSources.length; i++)
               {
                  columnsDownloadSources[i].setPreferredWidth(width / columnsDownloadSources.length);
               }
            }

            downloadPanel.getDownloadTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            downloadPanel.getDownloadSourceTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            int loc = (int) ((downloadPanel.getSplitPane().getHeight() - downloadPanel.getSplitPane().getDividerSize() -
                      downloadPanel.getPowerDownloadPanel().getPreferredSize().height));

            downloadPanel.getSplitPane().setDividerLocation(loc);
            if(null != sortDownloads)
            {
               for(MouseListener curMl : downloadPanel.getDownloadTable().getTableHeader().getMouseListeners())
               {
                  if(curMl instanceof HeaderListener)
                  {
                     ((HeaderListener) curMl).sort(sortDownloads[0], sortDownloads[1] == 1);
                  }
               }
            }

            if(null != sortDownloadSources)
            {
               for(MouseListener curMl : downloadPanel.getDownloadSourceTable().getTableHeader().getMouseListeners())
               {
                  if(curMl instanceof HeaderListener)
                  {
                     ((HeaderListener) curMl).sort(sortDownloadSources[0], sortDownloadSources[1] == 1);
                  }
               }
            }
         }

         downloadPanel.getDownloadTable().updateUI();
         if(downloadPanel.getDownloadSourcesScrollPane().isVisible())
         {
            downloadPanel.getDownloadSourceTable().updateUI();
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public void componentLostSelection()
   {
      selected = false;
      downloadPartListWatcher.setDownloadNode((Download) null);
   }

   protected void languageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String           text = languageSelector.getFirstAttrbuteByTagName("mainform.Label14.caption");

      dialogTitel       = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
      downloadAbbrechen = languageSelector.getFirstAttrbuteByTagName("mainform.msgdlgtext5");
      String[] tableColumns = new String[DownloadsTableModel.CLASS_TYPES.length];

      tableColumns[0] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col0caption");
      tableColumns[1] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col1caption");
      tableColumns[2] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col2caption");
      tableColumns[3] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col3caption");
      tableColumns[4] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col4caption");
      tableColumns[5] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col5caption");
      tableColumns[6] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col6caption");
      tableColumns[7] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col7caption");
      tableColumns[8] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col8caption");
      tableColumns[9] = "Zielverzeichnis";
      TableColumn[]       columns = downloadPanel.getDownloadTableColumns();
      JCheckBoxMenuItem[] columnPopupItems = downloadPanel.getColumnDownloadPopupItems();

      for(int i = 0; i < columns.length; i++)
      {
         columns[i].setHeaderValue(tableColumns[i]);
         columnPopupItems[i].setText(tableColumns[i]);
      }

      tableColumns = new String[DownloadSourcesTableModel.CLASS_TYPES.length];

      tableColumns[0]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col0caption");
      tableColumns[1]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col1caption");
      tableColumns[2]  = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col1caption");
      tableColumns[3]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col2caption");
      tableColumns[4]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col3caption");
      tableColumns[5]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col4caption");
      tableColumns[6]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col5caption");
      tableColumns[7]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col6caption");
      tableColumns[8]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col7caption");
      tableColumns[9]  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col8caption");
      tableColumns[10] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col9caption");

      columns          = downloadPanel.getDownloadSourcesTableColumns();
      columnPopupItems = downloadPanel.getColumnDownloadSourcesPopupItems();
      for(int i = 0; i < columns.length; i++)
      {
         columns[i].setHeaderValue(tableColumns[i]);
         columnPopupItems[i].setText(tableColumns[i]);
      }

      downloadPanel.getMnuReleaseInfo().setText(languageSelector.getFirstAttrbuteByTagName("releaseinfo.menu"));
      downloadPanel.getMnuAbbrechen().setText(languageSelector.getFirstAttrbuteByTagName("mainform.canceldown.caption"));
      downloadPanel.getMnuPause().setText(languageSelector.getFirstAttrbuteByTagName("mainform.pausedown.caption") + " [F5]");
      downloadPanel.getMnuFortsetzen().setText(languageSelector.getFirstAttrbuteByTagName("mainform.resumedown.caption") + " [F6]");
      downloadPanel.getMnuUmbenennen().setText(languageSelector.getFirstAttrbuteByTagName("mainform.renamefile.caption") + " [F2]");
      downloadPanel.getMnuZielordner().setText(languageSelector.getFirstAttrbuteByTagName("mainform.changetarget.caption") +
                                               " [F3]");
      downloadPanel.getMnuFertigeEntfernen()
      .setText(languageSelector.getFirstAttrbuteByTagName("mainform.Clearfinishedentries1.caption"));
      downloadPanel.getMnuCopyToClipboard().setText(languageSelector.getFirstAttrbuteByTagName("mainform.getlink1.caption"));
      downloadPanel.getMnuCopyToClipboardWithSources()
      .setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.getlinkwithsources"));
      downloadPanel.getMnuOpenWithProgram().setText("VLC");
      downloadPanel.getMnuOpenWithDefaultProgram()
      .setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.startemitstandard"));
      alreadyLoaded = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.bereitsgeladen");
      invalidLink   = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.falscherlink");
      linkFailure   = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.sonstigerlinkfehlerlang");
   }

   @SuppressWarnings("unchecked")
   protected void contentChanged(DATALISTENER_TYPE type, final Object content)
   {
      Map<Integer, Download> downloads = (Map<Integer, Download>) content;

      final boolean          downloadChanged = downloadPanel.getDownloadTableModel().setDownloads(downloads);
      Download               curDownload     = downloadPanel.getDownloadSourcesTableModel().getDownload();

      boolean                sourcesChangedTmp = false;

      if(null != curDownload)
      {
         Download freshDownload = downloads.get(curDownload.getId());

         sourcesChangedTmp = downloadPanel.getDownloadSourcesTableModel().setDownload(freshDownload);
      }

      final boolean sourcesChanged = sourcesChangedTmp;

      if(selected && (downloadChanged || sourcesChanged))
      {
         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  if(downloadChanged)
                  {
                     downloadPanel.getDownloadTableModel().forceResort();
                     downloadPanel.getDownloadTable().updateUI();
                  }

                  if(sourcesChanged)
                  {
                     downloadPanel.getDownloadSourcesTableModel().forceResort();
                     downloadPanel.getDownloadSourceTable().updateUI();
                  }
               }
            });
      }
   }
}
