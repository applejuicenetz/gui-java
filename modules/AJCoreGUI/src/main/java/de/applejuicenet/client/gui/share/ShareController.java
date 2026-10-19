/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share;


import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Information;
import de.applejuicenet.client.facade.entity.Server;
import de.applejuicenet.client.facade.entity.Share;
import de.applejuicenet.client.facade.entity.ShareEntry;
import de.applejuicenet.client.facade.entity.ShareEntry.SHAREMODE;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.TableColumnSettings;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.tree.DirectoryNode;
import de.applejuicenet.client.gui.share.tree.DirectoryTree;
import de.applejuicenet.client.gui.share.tree.ShareSelectionTreeModel;
import de.applejuicenet.client.gui.share.tree.ShareTreeLocator;
import de.applejuicenet.client.shared.DesktopTools;
import de.applejuicenet.client.shared.ReleaseInfo;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Copyright: General Public License
 */
public class ShareController extends GuiController
{
   private static final int       CLEAR_PRIORITY            = 0;
   private static final int       SET_PRIORITY              = 1;
   private static final int       REFRESH                        = 2;
   private static final int       STOP_SHARECHECK                = 99;
   private static final String    STOP_SHARECHECK_MIN_VERSION    = "0.35.186.1";
   private static final int       SHARE_REFRESH                 = 3;
   private static final int       NOT_SHARED                     = 4;
   private static final int       SHARE_WITHOUT_SUB              = 5;
   private static final int       SHARE_WITH_SUB                 = 6;
   private static final int       COPY_TO_CLIPBOARD              = 7;
   private static final int       COPY_TO_CLIPBOARD_WITH_SOURCES = 8;
   private static final int       COPY_TO_CLIPBOARD_AS_UBB_CODE  = 9;
   private static final int       NEW_LIST                     = 10;
   private static final int       OPEN_WITH_STANDARD_PROGRAM     = 12;
   private static final int       RELEASE_INFO                   = 13;
   private static final int       FILELIST_FROM_FOLDER           = 14;
   private static ShareController instance                       = null;
   private SharePanel             sharePanel;
   private String                 fileSize;
   private String                 entriesText;
   private int                    priorityTotal                           = 0;
   private int                    fileCount                  = 0;
   private boolean                initialized                    = false;
   private boolean                treeInitialized              = false;
   private ShareTreeMouseAdapter  shareTreeMouseAdapter;

   private ShareController()
   {
      super();
      sharePanel = new SharePanel(this);
      init();
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   public static synchronized ShareController getInstance()
   {
      if(null == instance)
      {
         instance = new ShareController();
      }

      return instance;
   }

   public JComponent getComponent()
   {
      return sharePanel;
   }

   public Value[] getCustomizedValues()
   {
      return null;
   }

   private void init()
   {
      sharePanel.getBtnClearPriority().addActionListener(new GuiControllerActionListener(this, CLEAR_PRIORITY));
      sharePanel.getBtnSetPriority().addActionListener(new GuiControllerActionListener(this, SET_PRIORITY));
      sharePanel.getBtnRefresh().addActionListener(new GuiControllerActionListener(this, REFRESH));
      sharePanel.getBtnStopShareCheck().addActionListener(new GuiControllerActionListener(this, STOP_SHARECHECK));
      sharePanel.getBtnReload().addActionListener(new GuiControllerActionListener(this, SHARE_REFRESH));
      sharePanel.getMnuNotShared().addActionListener(new GuiControllerActionListener(this, NOT_SHARED));
      sharePanel.getMnuSharedWithoutSub().addActionListener(new GuiControllerActionListener(this, SHARE_WITHOUT_SUB));
      sharePanel.getMnuSharedWithSub().addActionListener(new GuiControllerActionListener(this, SHARE_WITH_SUB));
      sharePanel.getMnuCopyToClipboard().addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
      sharePanel.getMnuReleaseInfo().addActionListener(new GuiControllerActionListener(this, RELEASE_INFO));
      sharePanel.getMnuCopyToClipboardWithSources()
      .addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_WITH_SOURCES));
      sharePanel.getMnuCopyToClipboardAsUBBCode()
      .addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_AS_UBB_CODE));
      sharePanel.getBtnNewList().addActionListener(new GuiControllerActionListener(this, NEW_LIST));
      sharePanel.getMnuCreateFileList().addActionListener(new GuiControllerActionListener(this, FILELIST_FROM_FOLDER));

      shareTreeMouseAdapter = new ShareTreeMouseAdapter(sharePanel.getDirectoryTree(), sharePanel.getPopupMenu(),
                                                        sharePanel.getMnuSharedWithSub(), sharePanel.getMnuSharedWithoutSub(),
                                                        sharePanel.getMnuNotShared());
      sharePanel.getShareTable().addMouseListener(new ShareTableMouseAdapter(sharePanel.getShareTable(),
            sharePanel.getPopupMenu2(), sharePanel.getFolderPopupMenu()));

      if(AppleJuiceClient.getAjFacade().isLocalhost())
      {
         if(DesktopTools.isAdvancedSupported())
         {
            sharePanel.getMnuOpenWithStandardProgram()
            .addActionListener(new GuiControllerActionListener(this, OPEN_WITH_STANDARD_PROGRAM));
         }
         else
         {
            sharePanel.getMnuOpenWithStandardProgram().setVisible(false);
         }
      }
      else
      {
         sharePanel.getMnuOpenWithStandardProgram().setEnabled(false);
      }
   }

   public void fireAction(int actionId, Object source)
   {
      switch(actionId)
      {

         case CLEAR_PRIORITY:
         {
            clearPriority();
            break;
         }

         case SET_PRIORITY:
         {
            applyPriority();
            break;
         }

         case REFRESH:
         {
            refresh();
            break;
         }

         case STOP_SHARECHECK:
         {
            stopShareCheck();
            break;
         }

         case SHARE_REFRESH:
         {
            reloadShare();
            break;
         }

         case NOT_SHARED:
         {
            doNotShare();
            break;
         }

         case SHARE_WITHOUT_SUB:
         {
            shareWithoutSubdirectory();
            break;
         }

         case SHARE_WITH_SUB:
         {
            shareWithSubdirectory();
            break;
         }

         case COPY_TO_CLIPBOARD:
         {
            copyToClipboard();
            break;
         }

         case COPY_TO_CLIPBOARD_WITH_SOURCES:
         {
            copyToClipboardWithSources();
            break;
         }

         case COPY_TO_CLIPBOARD_AS_UBB_CODE:
         {
            copyToClipboardAsUBBCode();
            break;
         }

         case NEW_LIST:
         {
            newList();
            break;
         }

         case FILELIST_FROM_FOLDER:
         {
            Object[] selected = sharePanel.getShareTable().getSelectedItems();
            if(selected != null && selected.length == 1 && !((ShareNode) selected[0]).isLeaf())
            {
               newList((ShareNode) selected[0]);
            }
            break;
         }

         case OPEN_WITH_STANDARD_PROGRAM:
         {
            openWithDefaultProgram();
            break;
         }

         case RELEASE_INFO:
         {
            showReleaseInfo();
            break;
         }

         default:
            logger.error("Unregistered event id {}", actionId);
      }
   }

   private void showReleaseInfo()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share share = ((ShareNode) obj[0]).getShare();

         ReleaseInfo.handle(share.getShortfilename(), share.getCheckSum(), share.getSize());
      }
   }

   private void openWithDefaultProgram()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share  share    = ((ShareNode) obj[0]).getShare();
         String filename = share.getFilename();

         DesktopTools.open(new File(filename));
      }
   }

   private void newList(ShareNode... initialNodes)
   {
      FileListDialog fileListDialog = new FileListDialog(AppleJuiceDialog.getApp(), false, initialNodes);

      sharePanel.getShareTable().setDragEnabled(true);
      fileListDialog.setVisible(true);
   }

   private void copyToClipboardWithSources()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share        share  = ((ShareNode) obj[0]).getShare();
         Clipboard    cb     = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuilder toCopy = new StringBuilder();

         toCopy.append("ajfsp://file|");
         toCopy.append(share.getShortfilename());
         toCopy.append("|");
         toCopy.append(share.getCheckSum());
         toCopy.append("|");
         toCopy.append(share.getSize());
         long        port        = AppleJuiceClient.getAjFacade().getAJSettings().getPort();
         Information information = AppleJuiceClient.getAjFacade().getInformation();

         toCopy.append("|");
         toCopy.append(information.getExternalIP());
         toCopy.append(":");
         toCopy.append(port);
         if(information.getConnectionStatus() == Information.CONNECTED)
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

   private void copyToClipboardAsUBBCode()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share        share        = ((ShareNode) obj[0]).getShare();
         Clipboard    cb           = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuilder toCopy       = new StringBuilder();
         StringBuilder tempFilename = new StringBuilder(share.getShortfilename());

         for(int i = 0; i < tempFilename.length(); i++)
         {
            if(tempFilename.charAt(i) == ' ')
            {
               tempFilename.setCharAt(i, '.');
            }
         }

         String encodedFilename = URLEncoder.encode(tempFilename.toString(), StandardCharsets.ISO_8859_1);

         toCopy.append("[URL=ajfsp://file|");
         toCopy.append(encodedFilename + "|" + share.getCheckSum() + "|" + share.getSize());
         toCopy.append("/]" + share.getShortfilename() + "[/URL]");
         StringSelection contents = new StringSelection(toCopy.toString());

         cb.setContents(contents, null);
      }
   }

   private void copyToClipboard()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share        shareDO = ((ShareNode) obj[0]).getShare();
         Clipboard    cb     = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuilder toCopy = new StringBuilder();

         toCopy.append("ajfsp://file|");
         toCopy.append(shareDO.getShortfilename() + "|" + shareDO.getCheckSum() + "|" + shareDO.getSize() + "/");
         StringSelection contents = new StringSelection(toCopy.toString());

         cb.setContents(contents, null);
      }
   }

   private void shareWithSubdirectory()
   {
      DirectoryNode node = (DirectoryNode) sharePanel.getDirectoryTree().getLastSelectedPathComponent();

      if(node != null)
      {
         String       path    = node.getDirectory().getPath();
         List<String> entries = new Vector<String>();

         entries.add(path);
         AppleJuiceClient.getAjFacade().addShareEntry(entries, SHAREMODE.SUBDIRECTORY);
         DirectoryNode.setShareDirs(AppleJuiceClient.getAjFacade().getAJSettings().getShareDirs());
         sharePanel.getDirectoryTree().repaint();
      }
   }

   private void shareWithoutSubdirectory()
   {
      DirectoryNode node = (DirectoryNode) sharePanel.getDirectoryTree().getLastSelectedPathComponent();

      if(node != null)
      {
         String       path    = node.getDirectory().getPath();
         List<String> entries = new Vector<String>();

         entries.add(path);
         AppleJuiceClient.getAjFacade().addShareEntry(entries, SHAREMODE.SINGLEDIRECTORY);
         DirectoryNode.setShareDirs(AppleJuiceClient.getAjFacade().getAJSettings().getShareDirs());
         sharePanel.getDirectoryTree().repaint();
      }
   }

   private void doNotShare()
   {
      Set<ShareEntry> shares = AppleJuiceClient.getAjFacade().getAJSettings().getShareDirs();
      DirectoryNode   node = (DirectoryNode) sharePanel.getDirectoryTree().getLastSelectedPathComponent();

      if(node != null)
      {
         String       path    = node.getDirectory().getPath();
         List<String> entries = new Vector<String>();

         entries.add(path);
         AppleJuiceClient.getAjFacade().removeShareEntry(entries);
         DirectoryNode.setShareDirs(AppleJuiceClient.getAjFacade().getAJSettings().getShareDirs());
         sharePanel.getDirectoryTree().repaint();
      }
   }

   private void clearPriority()
   {
      changePriority(1);
   }

   private void applyPriority()
   {
      changePriority((Integer) sharePanel.getCmbPriority().getSelectedItem());
   }

   private void changePriority(int priority)
   {
      Object[] values = sharePanel.getShareTable().getSelectedItems();
      if(values == null)
      {
         return;
      }
      sharePanel.getBtnClearPriority().setEnabled(false);
      sharePanel.getBtnSetPriority().setEnabled(false);
      sharePanel.getBtnReload().setEnabled(false);
      new javax.swing.SwingWorker<Void, Void>()
      {
         protected Void doInBackground()
         {
            for(Object value : values)
            {
               ((ShareNode) value).setPriority(priority);
            }
            return null;
         }

         protected void done()
         {
            try
            {
               get();
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            }
            finally
            {
               reloadShare();
            }
         }
      }.execute();
   }

   private boolean isStopShareCheckSupported()
   {
      try
      {
         de.applejuicenet.client.facade.entity.Version version = AppleJuiceClient.getAjFacade().getCoreVersion();

         return version != null && version.compareTo(STOP_SHARECHECK_MIN_VERSION) >= 0;
      }
      catch(Exception e)
      {
         return false;
      }
   }

   private void stopShareCheck()
   {
      new javax.swing.SwingWorker<Void, Void>()
      {
         protected Void doInBackground()
         {
            try
            {
               AppleJuiceClient.getAjFacade().stopShareCheck();
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            }

            return null;
         }
      }.execute();
   }

   private void refresh()
   {
      sharePanel.getBtnRefresh().setEnabled(false);
      final javax.swing.SwingWorker<Void, Void> worker = new javax.swing.SwingWorker<Void, Void>()
      {
         protected Void doInBackground()
         {
            try
            {
               Set<ShareEntry> shares = AppleJuiceClient.getAjFacade().getAJSettings().getShareDirs();

               AppleJuiceClient.getAjFacade().setShare(shares);
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            }

            return null;
         }

         protected void done()
         {
            sharePanel.getBtnRefresh().setEnabled(true);
         }
      };

      worker.execute();
   }

   private void reloadShare()
   {
      if(!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(this::reloadShare);
         return;
      }
      sharePanel.getBtnClearPriority().setEnabled(false);
      sharePanel.getBtnSetPriority().setEnabled(false);
      sharePanel.getBtnReload().setEnabled(false);
      final javax.swing.SwingWorker<List<Share>, Void> worker = new javax.swing.SwingWorker<List<Share>, Void>()
      {
         protected List<Share> doInBackground()
         {
            try
            {
               return new ArrayList<Share>(AppleJuiceClient.getAjFacade().getShare(true).values());
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
               return null;
            }
         }

         protected void done()
         {
            try
            {
               List<Share> shares = get();
               if(shares == null)
               {
                  return;
               }
               sharePanel.getShareTable().setShares(shares);
               fileCount = 0;
               long size = 0;

               priorityTotal = 0;
               for(Share curShare : shares)
               {
                  size += curShare.getSize();
                  if(curShare.getPriority() > 1)
                  {
                     priorityTotal += curShare.getPriority();
                  }

                  fileCount++;
               }

               fileSize = ShareSizeFormatter.format(size);
               String temp = entriesText;

               temp = temp.replaceFirst("%i", Integer.toString(fileCount));
               temp = temp.replaceFirst("%s", fileSize);
               StringBuilder tmp = new StringBuilder(temp);

               tmp.append(" - Prio: ");
               tmp.append(priorityTotal);
               tmp.append("/1000");
               sharePanel.getLblFiles().setText(tmp.toString());
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            }
            finally
            {
               sharePanel.getBtnClearPriority().setEnabled(true);
               sharePanel.getBtnSetPriority().setEnabled(true);
               sharePanel.getBtnReload().setEnabled(true);
            }
         }
      };

      worker.execute();
   }

   public void componentSelected()
   {
      if(!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(this::componentSelected);
         return;
      }
      try
      {
         if(!initialized)
         {
            initialized = true;
            sharePanel.getShareTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumnModel headerModel = sharePanel.getShareTable().getTableHeader().getColumnModel();
            int              columnCount = headerModel.getColumnCount();
            PositionManager  pm          = PositionManagerImpl.getInstance();

            if(pm.isLegal())
            {
               int[] widths = pm.getShareWidths();

               for(int i = 0; i < columnCount; i++)
               {
                  headerModel.getColumn(i).setPreferredWidth(widths[i]);
               }
            }
            TableColumn[] columns = new TableColumn[columnCount];
            for(int index = 0; index < columnCount; index++)
            {
               columns[index] = headerModel.getColumn(index);
            }
            TableColumnSettings.installCompact(sharePanel.getShareTable(), "share", columns);
         }

         if(!treeInitialized)
         {
            treeInitialized = true;
            new javax.swing.SwingWorker<AJSettings, Void>()
            {
                  protected AJSettings doInBackground()
                  {
                     return AppleJuiceClient.getAjFacade().getAJSettings();
                  }

                  protected void done()
                  {
                     try
                     {
                        DirectoryNode.setShareDirs(get().getShareDirs());
                        initShareSelectionTree(get().getIncomingDir());
                     }
                     catch(Exception e)
                     {
                        treeInitialized = false;
                        logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                     }
                     sharePanel.getBtnClearPriority().setEnabled(true);
                     sharePanel.getBtnSetPriority().setEnabled(true);
                     sharePanel.getBtnReload().setEnabled(true);
                     sharePanel.getBtnRefresh().setEnabled(true);
                     sharePanel.getBtnStopShareCheck().setEnabled(isStopShareCheckSupported());
                     if(treeInitialized)
                     {
                        reloadShare();
                     }
                  }
            }.execute();
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
      }
   }

   private void initShareSelectionTree(String incomingDir)
   {
      sharePanel.getDirectoryTree().removeMouseListener(shareTreeMouseAdapter);
      javax.swing.SwingWorker<DirectoryTreeInit, Void> worker2 = new javax.swing.SwingWorker<DirectoryTreeInit, Void>()
      {
         protected DirectoryTreeInit doInBackground()
         {
            ShareSelectionTreeModel model = new ShareSelectionTreeModel();
            TreePath incomingPath = ShareTreeLocator.locate(model, incomingDir, ApplejuiceFacade.separator,
                  node -> ((DirectoryNode) node).getDirectory() == null ? null
                        : ((DirectoryNode) node).getDirectory().getPath());

            return new DirectoryTreeInit(model, incomingPath);
         }

         protected void done()
         {
            try
            {
               DirectoryTreeInit init = get();
               DirectoryTree tree = sharePanel.getDirectoryTree();

               tree.setModel(init.model());
               tree.setRootVisible(false);
               tree.addMouseListener(shareTreeMouseAdapter);
               openIncomingDirectory(tree, init.incomingPath());
            }
            catch(Exception e)
            {
               treeInitialized = false;
               logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            }
         }
      };

      worker2.execute();
   }

   /** Opens the way to the incoming directory and selects it, so its files are shown right away. */
   private static void openIncomingDirectory(DirectoryTree tree, TreePath incomingPath)
   {
      if(incomingPath == null)
      {
         return;
      }

      if(incomingPath.getParentPath() != null)
      {
         tree.expandPath(incomingPath.getParentPath());
      }

      tree.setSelectionPath(incomingPath);
      tree.scrollPathToVisible(incomingPath);
   }

   private record DirectoryTreeInit(ShareSelectionTreeModel model, TreePath incomingPath)
   {
   }

   public void componentLostSelection()
   {

      // nix zu tun
   }

   protected void languageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      sharePanel.getFolderTreeBolder().setTitle(languageSelector.getFirstAttributeByTagName("mainform.dirssheet.caption"));
      sharePanel.getMainPanelBolder().setTitle(languageSelector.getFirstAttributeByTagName("mainform.filessheet.caption"));
      sharePanel.getMnuSharedWithSub().setText(languageSelector.getFirstAttributeByTagName("mainform.addwsubdirsbtn.caption"));
      sharePanel.getMnuSharedWithoutSub().setText(languageSelector.getFirstAttributeByTagName("mainform.addosubdirsbtn.caption"));
      sharePanel.getMnuNotShared().setText(languageSelector.getFirstAttributeByTagName("mainform.deldirbtn.caption"));
      sharePanel.getMnuReleaseInfo().setText(languageSelector.getFirstAttributeByTagName("releaseinfo.menu"));
      sharePanel.getMnuCopyToClipboard().setText(languageSelector.getFirstAttributeByTagName("mainform.getlink1.caption"));
      sharePanel.getMnuCopyToClipboardAsUBBCode()
      .setText(languageSelector.getFirstAttributeByTagName("javagui.shareform.linkasubbcode"));
      sharePanel.getMnuCopyToClipboardWithSources()
      .setText(languageSelector.getFirstAttributeByTagName("javagui.downloadform.getlinkwithsources"));
      sharePanel.getMnuOpenWithStandardProgram()
      .setText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.startwithdefault"));
      sharePanel.getMnuOpenWithStandardProgram().setToolTipText(AppleJuiceClient.getAjFacade().isLocalhost() ? null
            : languageSelector.getFirstAttributeByTagName("javagui.remotecore.hint"));
      sharePanel.getBtnRefresh().setText(languageSelector.getFirstAttributeByTagName("mainform.startsharecheck.caption"));
      sharePanel.getBtnRefresh().setToolTipText(languageSelector.getFirstAttributeByTagName("mainform.startsharecheck.hint"));
      sharePanel.getBtnStopShareCheck().setText(languageSelector.getFirstAttributeByTagName("mainform.stopsharecheck.caption"));
      sharePanel.getBtnStopShareCheck().setToolTipText(languageSelector.getFirstAttributeByTagName("mainform.stopsharecheck.hint"));
      sharePanel.getBtnNewList().setText(languageSelector.getFirstAttributeByTagName("mainform.newfilelist.caption"));
      sharePanel.getMnuCreateFileList().setText(languageSelector.getFirstAttributeByTagName("mainform.createfilelist.caption"));
      sharePanel.getBtnNewList().setToolTipText(languageSelector.getFirstAttributeByTagName("mainform.newfilelist.hint"));
      sharePanel.getBtnReload().setText(languageSelector.getFirstAttributeByTagName("mainform.sharereload.caption"));
      sharePanel.getBtnReload().setToolTipText(languageSelector.getFirstAttributeByTagName("mainform.sharereload.hint"));
      sharePanel.getBtnSetPriority().setText(languageSelector.getFirstAttributeByTagName("mainform.setprio.caption"));
      sharePanel.getBtnSetPriority().setToolTipText(languageSelector.getFirstAttributeByTagName("mainform.setprio.hint"));
      sharePanel.getBtnClearPriority().setText(languageSelector.getFirstAttributeByTagName("mainform.clearprio.caption"));
      sharePanel.getBtnClearPriority().setToolTipText(languageSelector.getFirstAttributeByTagName("mainform.clearprio.hint"));
      sharePanel.getSearchField().setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.shareform.filter.hint"));
      sharePanel.getSearchField().putClientProperty("JTextField.placeholderText", languageSelector.getFirstAttributeByTagName("javagui.shareform.filter.hint"));

      String[] tableColumns = new String[6];

      tableColumns[0] = languageSelector.getFirstAttributeByTagName("mainform.sfiles.col0caption");
      tableColumns[1] = languageSelector.getFirstAttributeByTagName("mainform.sfiles.col1caption");
      tableColumns[2] = languageSelector.getFirstAttributeByTagName("mainform.sfiles.col2caption");
      tableColumns[3] = languageSelector.getFirstAttributeByTagName("javagui.shareform.lastrequest");
      tableColumns[4] = languageSelector.getFirstAttributeByTagName("javagui.shareform.downloadrequests");
      tableColumns[5] = languageSelector.getFirstAttributeByTagName("javagui.shareform.searchrequests");

      TableColumnModel columnModel = sharePanel.getShareTable().getColumnModel();

      for(int i = 0; i < tableColumns.length; i++)
      {
         columnModel.getColumn(i).setHeaderValue(tableColumns[columnModel.getColumn(i).getModelIndex()]);
      }

      entriesText = languageSelector.getFirstAttributeByTagName("javagui.shareform.sharecount");
      if(fileCount > 0)
      {
         String temp = entriesText;

         temp = temp.replaceFirst("%i", Integer.toString(fileCount));
         temp = temp.replaceFirst("%s", fileSize);
         StringBuilder tmp = new StringBuilder(temp);

         tmp.append(" - Prio: ");
         tmp.append(priorityTotal);
         tmp.append("/1000");
         sharePanel.getLblFiles().setText(tmp.toString());
      }
      else
      {
         sharePanel.getLblFiles().setText("");
      }
   }

   protected void contentChanged(DATALISTENER_TYPE type, Object content)
   {

      // nix zu tun
   }
}
