/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.entity.ShareEntry;
import de.applejuicenet.client.fassade.entity.ShareEntry.SHAREMODE;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.TableColumnSettings;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.tree.DirectoryNode;
import de.applejuicenet.client.gui.share.tree.ShareSelectionTreeModel;
import de.applejuicenet.client.shared.DesktopTools;
import de.applejuicenet.client.shared.ReleaseInfo;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/ShareController.java,v 1.24 2009/02/12 09:11:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class ShareController extends GuiController
{
   private static final int       PRIORITAET_AUFHEBEN            = 0;
   private static final int       PRIORITAET_SETZEN              = 1;
   private static final int       REFRESH                        = 2;
   private static final int       SHARE_ERNEUERN                 = 3;
   private static final int       NOT_SHARED                     = 4;
   private static final int       SHARE_WITHOUT_SUB              = 5;
   private static final int       SHARE_WITH_SUB                 = 6;
   private static final int       COPY_TO_CLIPBOARD              = 7;
   private static final int       COPY_TO_CLIPBOARD_WITH_SOURCES = 8;
   private static final int       COPY_TO_CLIPBOARD_AS_UBB_CODE  = 9;
   private static final int       NEUE_LISTE                     = 10;
   private static final int       OPEN_WITH_PROGRAM              = 11;
   private static final int       OPEN_WITH_STANDARD_PROGRAM     = 12;
   private static final int       RELEASE_INFO                   = 13;
   private static ShareController instance                       = null;
   private SharePanel             sharePanel;
   private String                 dateiGroesse;
   private String                 eintraege;
   private int                    prio                           = 0;
   private int                    anzahlDateien                  = 0;
   private boolean                initialized                    = false;
   private boolean                treeInitialisiert              = false;
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
      sharePanel.getBtnPrioritaetAufheben().addActionListener(new GuiControllerActionListener(this, PRIORITAET_AUFHEBEN));
      sharePanel.getBtnPrioritaetSetzen().addActionListener(new GuiControllerActionListener(this, PRIORITAET_SETZEN));
      sharePanel.getBtnRefresh().addActionListener(new GuiControllerActionListener(this, REFRESH));
      sharePanel.getBtnNeuLaden().addActionListener(new GuiControllerActionListener(this, SHARE_ERNEUERN));
      sharePanel.getMnuNotShared().addActionListener(new GuiControllerActionListener(this, NOT_SHARED));
      sharePanel.getMnuSharedWithoutSub().addActionListener(new GuiControllerActionListener(this, SHARE_WITHOUT_SUB));
      sharePanel.getMnuSharedWithSub().addActionListener(new GuiControllerActionListener(this, SHARE_WITH_SUB));
      sharePanel.getMnuCopyToClipboard().addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
      sharePanel.getMnuReleaseInfo().addActionListener(new GuiControllerActionListener(this, RELEASE_INFO));
      sharePanel.getMnuCopyToClipboardWithSources()
      .addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_WITH_SOURCES));
      sharePanel.getMnuCopyToClipboardAsUBBCode()
      .addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_AS_UBB_CODE));
      sharePanel.getBtnNeueListe().addActionListener(new GuiControllerActionListener(this, NEUE_LISTE));

      shareTreeMouseAdapter = new ShareTreeMouseAdapter(sharePanel.getDirectoryTree(), sharePanel.getPopupMenu(),
                                                        sharePanel.getMnuSharedWithSub(), sharePanel.getMnuSharedWithoutSub(),
                                                        sharePanel.getMnuNotShared());
      sharePanel.getShareTable().addMouseListener(new ShareTableMouseAdapter(sharePanel.getShareTable(), sharePanel.getPopupMenu2()));

      if(AppleJuiceClient.getAjFassade().isLocalhost())
      {
         if(DesktopTools.isAdvancedSupported())
         {
            sharePanel.getMnuOpenWithStandardProgram()
            .addActionListener(new GuiControllerActionListener(this, OPEN_WITH_STANDARD_PROGRAM));
            sharePanel.getMnuOpenWithProgram().setVisible(false);
         }
         else
         {
            sharePanel.getMnuOpenWithStandardProgram().setVisible(false);
            sharePanel.getMnuOpenWithProgram().addActionListener(new GuiControllerActionListener(this, OPEN_WITH_PROGRAM));
         }
      }
      else
      {
         sharePanel.getMnuOpenWithProgram().setVisible(false);
      }
   }

   public void fireAction(int actionId, Object source)
   {
      switch(actionId)
      {

         case PRIORITAET_AUFHEBEN:
         {
            prioritaetAufheben();
            break;
         }

         case PRIORITAET_SETZEN:
         {
            prioritaetSetzen();
            break;
         }

         case REFRESH:
         {
            refresh();
            break;
         }

         case SHARE_ERNEUERN:
         {
            shareNeuLaden();
            break;
         }

         case NOT_SHARED:
         {
            nichtSharen();
            break;
         }

         case SHARE_WITHOUT_SUB:
         {
            ohneUnterverzeichnisSharen();
            break;
         }

         case SHARE_WITH_SUB:
         {
            mitUnterverzeichnisSharen();
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

         case NEUE_LISTE:
         {
            neueListe();
            break;
         }

         case OPEN_WITH_PROGRAM:
         {
            mitProgrammOeffnen();
            break;
         }

         case OPEN_WITH_STANDARD_PROGRAM:
         {
            mitStandardProgrammOeffnen();
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
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share share = ((ShareNode) obj[0]).getShare();

         ReleaseInfo.handle(share.getShortfilename(), share.getCheckSum(), (long) share.getSize());
      }
   }

   private void mitStandardProgrammOeffnen()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share  share    = ((ShareNode) obj[0]).getShare();
         String filename = share.getFilename();

         DesktopTools.open(new File(filename));
      }
   }

   private void mitProgrammOeffnen()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share  share            = ((ShareNode) obj[0]).getShare();
         String filename         = share.getFilename();
         String programToExecute = OptionsManagerImpl.getInstance().getOpenProgram();

         if(programToExecute.length() != 0)
         {
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
   }

   private void neueListe()
   {
      DateiListeDialog dateiListeDialog = new DateiListeDialog(AppleJuiceDialog.getApp(), false);

      sharePanel.getShareTable().setDragEnabled(true);
      dateiListeDialog.setVisible(true);
   }

   private void copyToClipboardWithSources()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share        share  = ((ShareNode) obj[0]).getShare();
         Clipboard    cb     = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuffer toCopy = new StringBuffer();

         toCopy.append("ajfsp://file|");
         toCopy.append(share.getShortfilename());
         toCopy.append("|");
         toCopy.append(share.getCheckSum());
         toCopy.append("|");
         toCopy.append(share.getSize());
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

   private void copyToClipboardAsUBBCode()
   {
      Object[] obj = sharePanel.getShareTable().getSelectedItems();

      if(((ShareNode) obj[0]).isLeaf())
      {
         Share        share        = ((ShareNode) obj[0]).getShare();
         Clipboard    cb           = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuffer toCopy       = new StringBuffer();
         StringBuffer tempFilename = new StringBuffer(share.getShortfilename());

         for(int i = 0; i < tempFilename.length(); i++)
         {
            if(tempFilename.charAt(i) == ' ')
            {
               tempFilename.setCharAt(i, '.');
            }
         }

         String encodedFilename = "";

         try
         {
            encodedFilename = URLEncoder.encode(tempFilename.toString(), "ISO-8859-1");
         }
         catch(UnsupportedEncodingException ex)
         {
            ;

            //gibbet, also nix zu behandeln...
         }

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
         StringBuffer toCopy = new StringBuffer();

         toCopy.append("ajfsp://file|");
         toCopy.append(shareDO.getShortfilename() + "|" + shareDO.getCheckSum() + "|" + shareDO.getSize() + "/");
         StringSelection contents = new StringSelection(toCopy.toString());

         cb.setContents(contents, null);
      }
   }

   private void mitUnterverzeichnisSharen()
   {
      DirectoryNode node = (DirectoryNode) sharePanel.getDirectoryTree().getLastSelectedPathComponent();

      if(node != null)
      {
         String       path    = node.getDirectory().getPath();
         List<String> entries = new Vector<String>();

         entries.add(path);
         AppleJuiceClient.getAjFassade().addShareEntry(entries, SHAREMODE.SUBDIRECTORY);
         DirectoryNode.setShareDirs(AppleJuiceClient.getAjFassade().getAJSettings().getShareDirs());
         sharePanel.getDirectoryTree().repaint();
      }
   }

   private void ohneUnterverzeichnisSharen()
   {
      DirectoryNode node = (DirectoryNode) sharePanel.getDirectoryTree().getLastSelectedPathComponent();

      if(node != null)
      {
         String       path    = node.getDirectory().getPath();
         List<String> entries = new Vector<String>();

         entries.add(path);
         AppleJuiceClient.getAjFassade().addShareEntry(entries, SHAREMODE.SINGLEDIRECTORY);
         DirectoryNode.setShareDirs(AppleJuiceClient.getAjFassade().getAJSettings().getShareDirs());
         sharePanel.getDirectoryTree().repaint();
      }
   }

   private void nichtSharen()
   {
      Set<ShareEntry> shares = AppleJuiceClient.getAjFassade().getAJSettings().getShareDirs();
      DirectoryNode   node = (DirectoryNode) sharePanel.getDirectoryTree().getLastSelectedPathComponent();

      if(node != null)
      {
         String       path    = node.getDirectory().getPath();
         List<String> entries = new Vector<String>();

         entries.add(path);
         AppleJuiceClient.getAjFassade().removeShareEntry(entries);
         DirectoryNode.setShareDirs(AppleJuiceClient.getAjFassade().getAJSettings().getShareDirs());
         sharePanel.getDirectoryTree().repaint();
      }
   }

   private void prioritaetAufheben()
   {
      changePriority(1);
   }

   private void prioritaetSetzen()
   {
      changePriority((Integer) sharePanel.getCmbPrioritaet().getSelectedItem());
   }

   private void changePriority(int priority)
   {
      Object[] values = sharePanel.getShareTable().getSelectedItems();
      if(values == null)
      {
         return;
      }
      sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
      sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
      sharePanel.getBtnNeuLaden().setEnabled(false);
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
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            finally
            {
               shareNeuLaden();
            }
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
               Set<ShareEntry> shares = AppleJuiceClient.getAjFassade().getAJSettings().getShareDirs();

               AppleJuiceClient.getAjFassade().setShare(shares);
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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

   private void shareNeuLaden()
   {
      if(!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(this::shareNeuLaden);
         return;
      }
      sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
      sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
      sharePanel.getBtnNeuLaden().setEnabled(false);
      final javax.swing.SwingWorker<List<Share>, Void> worker = new javax.swing.SwingWorker<List<Share>, Void>()
      {
         protected List<Share> doInBackground()
         {
            try
            {
               return new ArrayList<Share>(AppleJuiceClient.getAjFassade().getShare(true).values());
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
               anzahlDateien = 0;
               long size = 0;

               prio = 0;
               for(Share curShare : shares)
               {
                  size += curShare.getSize();
                  if(curShare.getPrioritaet() > 1)
                  {
                     prio += curShare.getPrioritaet();
                  }

                  anzahlDateien++;
               }

               dateiGroesse = ShareSizeFormatter.format(size);
               String temp = eintraege;

               temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
               temp = temp.replaceFirst("%s", dateiGroesse);
               StringBuffer tmp = new StringBuffer(temp);

               tmp.append(" - Prio: ");
               tmp.append(prio);
               tmp.append("/1000");
               sharePanel.getLblDateien().setText(tmp.toString());
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            finally
            {
               sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
               sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
               sharePanel.getBtnNeuLaden().setEnabled(true);
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

         if(!treeInitialisiert)
         {
            treeInitialisiert = true;
            new javax.swing.SwingWorker<AJSettings, Void>()
            {
                  protected AJSettings doInBackground()
                  {
                     return AppleJuiceClient.getAjFassade().getAJSettings();
                  }

                  protected void done()
                  {
                     try
                     {
                        DirectoryNode.setShareDirs(get().getShareDirs());
                        initShareSelectionTree();
                     }
                     catch(Exception e)
                     {
                        treeInitialisiert = false;
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                     }
                     sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
                     sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
                     sharePanel.getBtnNeuLaden().setEnabled(true);
                     sharePanel.getBtnRefresh().setEnabled(true);
                     if(treeInitialisiert)
                     {
                        shareNeuLaden();
                     }
                  }
            }.execute();
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private void initShareSelectionTree()
   {
      sharePanel.getDirectoryTree().removeMouseListener(shareTreeMouseAdapter);
      javax.swing.SwingWorker<ShareSelectionTreeModel, Void> worker2 = new javax.swing.SwingWorker<ShareSelectionTreeModel, Void>()
      {
         protected ShareSelectionTreeModel doInBackground()
         {
            return new ShareSelectionTreeModel();
         }

         protected void done()
         {
            try
            {
               sharePanel.getDirectoryTree().setModel(get());
               sharePanel.getDirectoryTree().setRootVisible(false);
               sharePanel.getDirectoryTree().addMouseListener(shareTreeMouseAdapter);
            }
            catch(Exception e)
            {
               treeInitialisiert = false;
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
         }
      };

      worker2.execute();
   }

   public void componentLostSelection()
   {

      // nix zu tun
   }

   protected void languageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      sharePanel.getFolderTreeBolder().setTitle(languageSelector.getFirstAttrbuteByTagName("mainform.dirssheet.caption"));
      sharePanel.getMainPanelBolder().setTitle(languageSelector.getFirstAttrbuteByTagName("mainform.filessheet.caption"));
      sharePanel.getMnuSharedWithSub().setText(languageSelector.getFirstAttrbuteByTagName("mainform.addwsubdirsbtn.caption"));
      sharePanel.getMnuSharedWithoutSub().setText(languageSelector.getFirstAttrbuteByTagName("mainform.addosubdirsbtn.caption"));
      sharePanel.getMnuNotShared().setText(languageSelector.getFirstAttrbuteByTagName("mainform.deldirbtn.caption"));
      sharePanel.getMnuReleaseInfo().setText(languageSelector.getFirstAttrbuteByTagName("releaseinfo.menu"));
      sharePanel.getMnuCopyToClipboard().setText(languageSelector.getFirstAttrbuteByTagName("mainform.getlink1.caption"));
      sharePanel.getMnuCopyToClipboardAsUBBCode()
      .setText(languageSelector.getFirstAttrbuteByTagName("javagui.shareform.linkalsubbcode"));
      sharePanel.getMnuCopyToClipboardWithSources()
      .setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.getlinkwithsources"));
      sharePanel.getMnuOpenWithProgram().setText("VLC");
      sharePanel.getMnuOpenWithStandardProgram()
      .setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.startemitstandard"));
      sharePanel.getBtnRefresh().setText(languageSelector.getFirstAttrbuteByTagName("mainform.startsharecheck.caption"));
      sharePanel.getBtnRefresh().setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.startsharecheck.hint"));
      sharePanel.getBtnNeueListe().setText(languageSelector.getFirstAttrbuteByTagName("mainform.newfilelist.caption"));
      sharePanel.getBtnNeueListe().setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.newfilelist.hint"));
      sharePanel.getBtnNeuLaden().setText(languageSelector.getFirstAttrbuteByTagName("mainform.sharereload.caption"));
      sharePanel.getBtnNeuLaden().setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.sharereload.hint"));
      sharePanel.getBtnPrioritaetSetzen().setText(languageSelector.getFirstAttrbuteByTagName("mainform.setprio.caption"));
      sharePanel.getBtnPrioritaetSetzen().setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.setprio.hint"));
      sharePanel.getBtnPrioritaetAufheben().setText(languageSelector.getFirstAttrbuteByTagName("mainform.clearprio.caption"));
      sharePanel.getBtnPrioritaetAufheben().setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.clearprio.hint"));

      String[] tableColumns = new String[6];

      tableColumns[0] = languageSelector.getFirstAttrbuteByTagName("mainform.sfiles.col0caption");
      tableColumns[1] = languageSelector.getFirstAttrbuteByTagName("mainform.sfiles.col1caption");
      tableColumns[2] = languageSelector.getFirstAttrbuteByTagName("mainform.sfiles.col2caption");
      tableColumns[3] = languageSelector.getFirstAttrbuteByTagName("javagui.shareform.letzteanfrage");
      tableColumns[4] = languageSelector.getFirstAttrbuteByTagName("javagui.shareform.downloadanfragen");
      tableColumns[5] = languageSelector.getFirstAttrbuteByTagName("javagui.shareform.suchanfragen");

      TableColumnModel tcm = sharePanel.getShareTable().getColumnModel();

      for(int i = 0; i < tableColumns.length; i++)
      {
         tcm.getColumn(i).setHeaderValue(tableColumns[tcm.getColumn(i).getModelIndex()]);
      }

      eintraege = languageSelector.getFirstAttrbuteByTagName("javagui.shareform.anzahlShare");
      if(anzahlDateien > 0)
      {
         String temp = eintraege;

         temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
         temp = temp.replaceFirst("%s", dateiGroesse);
         StringBuffer tmp = new StringBuffer(temp);

         tmp.append(" - Prio: ");
         tmp.append(prio);
         tmp.append("/1000");
         sharePanel.getLblDateien().setText(tmp.toString());
      }
      else
      {
         sharePanel.getLblDateien().setText("");
      }
   }

   protected void contentChanged(DATALISTENER_TYPE type, Object content)
   {

      // nix zu tun
   }
}
