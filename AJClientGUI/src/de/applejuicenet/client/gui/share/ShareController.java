/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.share;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import java.io.File;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.text.DecimalFormat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.entity.ShareEntry;
import de.applejuicenet.client.fassade.entity.ShareEntry.SHAREMODE;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.tree.DirectoryNode;
import de.applejuicenet.client.gui.share.tree.ShareSelectionTreeModel;
import de.applejuicenet.client.shared.DesktopTools;
import de.applejuicenet.client.shared.SwingWorker;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/ShareController.java,v 1.20 2009/01/11 21:32:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
   private static ShareController instance                       = null;
   private static DecimalFormat   formatter                      = new DecimalFormat("###,##0.00");
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
      sharePanel                                                 = new SharePanel(this);
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
            shareNeuLaden(true);
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

         default:
            logger.error("Unregistrierte EventId " + actionId);
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
         Clipboard    cb      = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuffer toCopy  = new StringBuffer();

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
         sharePanel.getDirectoryTree().updateUI();
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
         sharePanel.getDirectoryTree().updateUI();
      }
   }

   private void nichtSharen()
   {
      Set<ShareEntry> shares = AppleJuiceClient.getAjFassade().getAJSettings().getShareDirs();
      DirectoryNode   node   = (DirectoryNode) sharePanel.getDirectoryTree().getLastSelectedPathComponent();

      if(node != null)
      {
         String       path    = node.getDirectory().getPath();
         List<String> entries = new Vector<String>();

         entries.add(path);
         AppleJuiceClient.getAjFassade().removeShareEntry(entries);
         DirectoryNode.setShareDirs(AppleJuiceClient.getAjFassade().getAJSettings().getShareDirs());
         sharePanel.getDirectoryTree().updateUI();
      }
   }

   private void prioritaetAufheben()
   {
      new Thread()
         {
            public void run()
            {
               try
               {
                  Object[] values = sharePanel.getShareTable().getSelectedItems();

                  if(values != null)
                  {
                     sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
                     sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
                     sharePanel.getBtnNeuLaden().setEnabled(false);
                     synchronized(values)
                     {
                        ShareNode shareNode = null;

                        for(int i = 0; i < values.length; i++)
                        {
                           shareNode = (ShareNode) values[i];
                           shareNode.setPriority(1);
                        }
                     }

                     shareNeuLaden(false);
                  }
               }
               catch(Exception e)
               {
                  logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
               }
            }
         }.start();
   }

   private void prioritaetSetzen()
   {
      new Thread()
         {
            public void run()
            {
               try
               {
                  int      prio   = ((Integer) sharePanel.getCmbPrioritaet().getSelectedItem()).intValue();
                  Object[] values = sharePanel.getShareTable().getSelectedItems();

                  if(values != null)
                  {
                     sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
                     sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
                     sharePanel.getBtnNeuLaden().setEnabled(false);
                     synchronized(values)
                     {
                        ShareNode shareNode = null;

                        for(int i = 0; i < values.length; i++)
                        {
                           shareNode = (ShareNode) values[i];
                           shareNode.setPriority(prio);
                        }
                     }

                     shareNeuLaden(false);
                  }
               }
               catch(Exception e)
               {
                  logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
               }
            }
         }.start();
   }

   private void refresh()
   {
      sharePanel.getBtnRefresh().setEnabled(false);
      final SwingWorker worker = new SwingWorker()
      {
         public Object construct()
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

         public void finished()
         {
            sharePanel.getBtnRefresh().setEnabled(true);
         }
      };

      worker.start();
   }

   private void shareNeuLaden(final boolean komplettNeu)
   {
      sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
      sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
      sharePanel.getBtnNeuLaden().setEnabled(false);
      final SwingWorker worker = new SwingWorker()
      {
         public Object construct()
         {
            try
            {
               ShareNode rootNode = sharePanel.getShareModel().getRootNode();

               if(komplettNeu)
               {
                  rootNode.removeAllChildren();
               }

               Map<String, Share> shares = AppleJuiceClient.getAjFassade().getShare(true);

               anzahlDateien = 0;
               double size = 0;

               prio = 0;
               for(Share curShare : shares.values())
               {
                  rootNode.addChild(curShare);
                  size += curShare.getSize();
                  if(curShare.getPrioritaet() > 1)
                  {
                     prio += curShare.getPrioritaet();
                  }

                  anzahlDateien++;
               }

               size         = size / 1048576;

               dateiGroesse = formatter.format(size) + " MB";
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

            return null;
         }

         public void finished()
         {
            sharePanel.getShareTable().updateUI();
            sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
            sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
            sharePanel.getBtnNeuLaden().setEnabled(true);
         }
      };

      worker.start();
   }

   public void componentSelected()
   {
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
            else
            {
               for(int i = 0; i < columnCount; i++)
               {
                  headerModel.getColumn(i).setPreferredWidth(sharePanel.getShareTable().getWidth() / columnCount);
               }
            }
         }

         if(!treeInitialisiert)
         {
            treeInitialisiert = true;
            new Thread()
               {
                  public void run()
                  {
                     AJSettings ajSettings = AppleJuiceClient.getAjFassade().getAJSettings();

                     DirectoryNode.setShareDirs(ajSettings.getShareDirs());
                     sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
                     sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
                     sharePanel.getBtnNeuLaden().setEnabled(true);
                     sharePanel.getBtnRefresh().setEnabled(true);
                     SwingUtilities.invokeLater(new Runnable()
                        {
                           public void run()
                           {
                              initShareSelectionTree();
                           }
                        });
                  }
               }.start();
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
      SwingWorker worker2 = new SwingWorker()
      {
         public Object construct()
         {
            ShareSelectionTreeModel treeModel = new ShareSelectionTreeModel();

            sharePanel.getDirectoryTree().setModel(treeModel);
            sharePanel.getDirectoryTree().setRootVisible(false);
            sharePanel.getDirectoryTree().addMouseListener(shareTreeMouseAdapter);
            return null;
         }
      };

      worker2.start();
   }

   public void componentLostSelection()
   {

      // nix zu tun
   }

   protected void languageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      sharePanel.getFolderTreeBolder()
      .setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.dirssheet.caption")));
      sharePanel.getMainPanelBolder()
      .setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.filessheet.caption")));
      sharePanel.getMnuSharedWithSub()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.addwsubdirsbtn.caption")));
      sharePanel.getMnuSharedWithoutSub()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.addosubdirsbtn.caption")));
      sharePanel.getMnuNotShared()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.deldirbtn.caption")));
      sharePanel.getMnuCopyToClipboard()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));
      sharePanel.getMnuCopyToClipboardAsUBBCode()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.linkalsubbcode")));
      sharePanel.getMnuCopyToClipboardWithSources()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.getlinkwithsources")));
      sharePanel.getMnuOpenWithProgram().setText("VLC");
      sharePanel.getMnuOpenWithStandardProgram()
      .setText(languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.standard.startemitstandard"));
      sharePanel.getBtnRefresh()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.startsharecheck.caption")));
      sharePanel.getBtnRefresh()
      .setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.startsharecheck.hint")));
      sharePanel.getBtnNeueListe()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.newfilelist.caption")));
      sharePanel.getBtnNeueListe()
      .setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.newfilelist.hint")));
      sharePanel.getBtnNeuLaden()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.sharereload.caption")));
      sharePanel.getBtnNeuLaden()
      .setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.sharereload.hint")));
      sharePanel.getBtnPrioritaetSetzen()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.setprio.caption")));
      sharePanel.getBtnPrioritaetSetzen()
      .setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.setprio.hint")));
      sharePanel.getBtnPrioritaetAufheben()
      .setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.clearprio.caption")));
      sharePanel.getBtnPrioritaetAufheben()
      .setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.clearprio.hint")));

      String[] tableColumns = new String[6];

      tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.sfiles.col0caption"));
      tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.sfiles.col1caption"));
      tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.sfiles.col2caption"));
      tableColumns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.letzteanfrage"));
      tableColumns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.downloadanfragen"));
      tableColumns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.suchanfragen"));

      TableColumnModel tcm = sharePanel.getShareTable().getColumnModel();

      for(int i = 0; i < tableColumns.length; i++)
      {
         tcm.getColumn(i).setHeaderValue(tableColumns[i]);
      }

      eintraege = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.anzahlShare"));
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
