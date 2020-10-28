/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

import de.applejuicenet.client.gui.tray.TrayLoader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
import de.applejuicenet.client.gui.about.AboutDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.download.DownloadController;
import de.applejuicenet.client.gui.download.DownloadPanel;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.memorymonitor.MemoryMonitorDialog;
import de.applejuicenet.client.gui.options.IncomingDirSelectionDialog;
import de.applejuicenet.client.gui.options.OptionsDialog;
import de.applejuicenet.client.gui.server.ServerPanel;
import de.applejuicenet.client.gui.share.ShareController;
import de.applejuicenet.client.gui.share.SharePanel;
import de.applejuicenet.client.gui.upload.UploadController;
import de.applejuicenet.client.gui.upload.UploadPanel;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.SoundPlayer;

import de.tklsoft.gui.controls.TKLButton;
import de.tklsoft.gui.controls.TKLFrame;
import de.tklsoft.gui.controls.TKLLabel;
import de.tklsoft.gui.controls.TKLPanel;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/AppleJuiceDialog.java,v
 * 1.125 2004/06/23 14:56:12 maj0r Exp $
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
public class AppleJuiceDialog extends TKLFrame implements LanguageListener, DataUpdateListener
{

   private static final Logger      logger                   = Logger.getLogger(AppleJuiceDialog.class);
   private static Map<String, Skin> themes                   = null;
   public static boolean            rewriteProperties        = false;
   private static AppleJuiceDialog  theApp;
   private static boolean           themesInitialized        = false;
   private static boolean           useTrayIcon              = false;
   private static TrayLoader        trayLoader               = null;
   private Information              information              = null;
   private RegisterPanel            registerPane;
   private TKLLabel[]               statusbar                = new TKLLabel[6];
   private JMenu                    sprachMenu;
   private JMenu                    optionenMenu;
   private JMenu                    themesMenu               = null;
   private JMenu                    coreMenu;
   private JMenuItem                menuItemOptionen         = new JMenuItem();
   private JMenuItem                menuItemDateiliste       = new JMenuItem();
   private JMenuItem                menuItemCheckUpdate      = new JMenuItem();
   private JMenuItem                menuItemCoreBeenden      = new JMenuItem();
   private JMenuItem                menuItemUeber            = new JMenuItem();
   private JMenuItem                menuItemDeaktivieren     = new JMenuItem();
   private JMenuItem                menuItemAktivieren       = new JMenuItem();
   private JMenuItem                popupOptionenMenuItem    = new JMenuItem();
   private JMenuItem                popupAboutMenuItem       = new JMenuItem();
   private JMenuItem                popupShowHideMenuItem    = new JMenuItem();
   private JMenuItem                popupCheckUpdateMenuItem = new JMenuItem();
   private TKLButton                sound                    = new TKLButton();
   private TKLButton                memory                   = new TKLButton();
   private String                   keinServer;
   private boolean                  firstChange              = true;
   private MemoryMonitorDialog      memoryMonitorDialog;
   private String                   themeSupportTitel;
   private String                   themeSupportNachricht;
   private boolean                  automaticPwdlEnabled     = false;
   private String                   titel;
   private String                   bestaetigung;
   private int                      desktopHeight;
   private int                      desktopWidth;
   private boolean                  maximized                = false;
   private Dimension                lastFrameSize;
   private Point                    lastFrameLocation;
   private Icon                     versteckenIcon           = null;
   private Icon                     zeigenIcon               = null;
   private boolean                  firewalled               = false;
   private String                   firewallWarning;
   private String                   alreadyLoaded;
   private String                   invalidLink;
   private String                   linkFailure;
   private String                   dialogTitel;
   private String                   verbunden;
   private String                   verbinden;
   private String                   nichtVerbunden;
   private ImageIcon                firewallIcon;
   private ImageIcon                verbundenIcon;
   private ImageIcon                nichtVerbundenIcon;
   private JPopupMenu               popup;
   private DownloadlinkPanel        linkPane                 = new DownloadlinkPanel();

   public AppleJuiceDialog()
   {
      super();
      try
      {
         enableCloseWindowListener(false);
         theApp = this;
         init();
         pack();
         LanguageSelector.getInstance().addLanguageListener(this);
         initKeyStrokes();

      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public static String getVersion() {
      return AppleJuiceDialog.class.getPackage().getImplementationVersion();
   }

   private void initKeyStrokes()
   {
      int tabCount = registerPane.getTabCount();

      for(int i = 0; i < tabCount; i++)
      {
         int            event       = i < 9 ? KeyEvent.VK_1 + i : KeyEvent.VK_A + i - 9;
         KeyStroke      stroke      = KeyStroke.getKeyStroke(event, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
         final int      index       = i;
         AbstractAction action      = new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               registerPane.setSelectedIndex(index);
            }
         };

         String commandName         = "ctrl_" + ((char) event);

         ((JComponent) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, commandName);
         ((JComponent) getContentPane()).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, commandName);
         ((JComponent) getContentPane()).getActionMap().put(commandName, action);
      }
   }

   @SuppressWarnings("deprecation")
   public static void initThemes()
   {
      try
      {
         themesInitialized = true;
         if(OptionsManagerImpl.getInstance().isThemesSupported())
         {
            HashSet<URL> themesDateien = new HashSet<URL>();
            File         themesPath    = new File(System.getProperty("user.dir") + File.separator + "themes");

            if(!themesPath.isDirectory())
            {
               if(logger.isEnabledFor(Level.INFO))
               {
                  logger.info("Der Ordner" + " fuer die Themes zip-Dateien ist nicht vorhanden." + "\r\nappleJuice wird beendet.");
               }

               closeWithErrormessage("Der Ordner" + " fuer die Themes zip-Dateien ist nicht vorhanden." +
                                     "\r\nappleJuice wird beendet.", false);
            }

            File[] themeFiles = themesPath.listFiles();

            for(int i = 0; i < themeFiles.length; i++)
            {
               if(themeFiles[i].isFile() && themeFiles[i].getName().indexOf(".zip") != -1)
               {

                  //testen, ob es wirklich ein skinfile ist
                  ZipFile  jf    = new ZipFile(themeFiles[i]);
                  ZipEntry entry = jf.getEntry("skinlf-themepack.xml");

                  if(entry != null)
                  {
                     themesDateien.add(themeFiles[i].toURL());
                  }
               }
            }

            Skin   standardSkin = null;
            Skin   aSkin        = null;
            String temp;
            String shortName    = "";
            String defaultTheme = OptionsManagerImpl.getInstance().getDefaultTheme();

            themes              = new HashMap<String, Skin>();
            for(URL curSkinURL : themesDateien)
            {
               temp = curSkinURL.getFile();
               int index1 = temp.lastIndexOf('/');
               int index2 = temp.lastIndexOf(".zip");

               if(index1 == -1 || index2 == -1)
               {
                  continue;
               }

               shortName = temp.substring(index1 + 1, index2);
               aSkin     = SkinLookAndFeel.loadThemePack(curSkinURL);
               themes.put(shortName, aSkin);
               if(shortName.compareToIgnoreCase(defaultTheme) == 0)
               {
                  standardSkin = aSkin;
               }
            }

            if(standardSkin == null)
            {
               standardSkin = aSkin;
            }

            SkinLookAndFeel.setSkin(standardSkin);
            SkinLookAndFeel.enable();
         }
         else
         {
            LookAFeel defaultlookandfeel = OptionsManagerImpl.getInstance().getDefaultLookAndFeel();

            if(defaultlookandfeel != null)
            {
               UIManager.setLookAndFeel(defaultlookandfeel.getClassName());
            }
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public static AppleJuiceDialog getApp()
   {
      return theApp;
   }

   @SuppressWarnings("unchecked")
   private void init() throws Exception
   {
      titel = "appleJuice GUI (" + AppleJuiceDialog.getVersion() + ")";
      IconManager im = IconManager.getInstance();

      firewallIcon       = im.getIcon("firewall");
      verbundenIcon      = im.getIcon("serververbunden");
      nichtVerbundenIcon = im.getIcon("serverversuche");

      Image image        = im.getIcon("applejuice").getImage();

      setTitle(titel);

      setIconImage(image);
      menuItemOptionen.setIcon(im.getIcon("optionen"));
      menuItemUeber.setIcon(im.getIcon("info"));
      menuItemCoreBeenden.setIcon(im.getIcon("skull"));
      menuItemDateiliste.setIcon(im.getIcon("speichern"));
      menuItemCheckUpdate.setIcon(im.getIcon("update"));

      setJMenuBar(createMenuBar());
      if(OptionsManagerImpl.getInstance().isThemesSupported())
      {
         SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
      }

      String path    = System.getProperty("user.dir") + File.separator + "language" + File.separator;
      String sprache = OptionsManagerImpl.getInstance().getSprache();

      if(null == sprache || sprache.trim().length() == 0)
      {
         sprache = "deutsch";
      }

      path += sprache + ".properties";
      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(25, "Initialisiere Sprache...");
      }

      LanguageSelector languageSelector = LanguageSelector.getInstance(path);

      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(30, "Erstelle Register...");
      }

      registerPane = new RegisterPanel(this);
      languageSelector.fireLanguageChanged();
      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(95, "Register erstellt..");
      }

      addWindowListener(new WindowAdapter()
         {
            public void windowClosing(WindowEvent evt)
            {
               closeDialog(evt);
            }
         });

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      desktopWidth  = screenSize.width;
      desktopHeight = screenSize.height;
      addComponentListener(new ComponentAdapter()
         {
            public void componentResized(ComponentEvent e)
            {
               int x = getWidth();
               int y = getHeight();

               if(x == desktopWidth && y == desktopHeight)
               {
                  if(maximized)
                  {
                     demaximize();
                  }
                  else
                  {
                     maximize();
                  }
               }
               else
               {
                  if(!maximized)
                  {
                     lastFrameSize     = getSize();
                     lastFrameLocation = getLocation();
                  }

                  super.componentResized(e);
               }
            }

            public void componentMoved(ComponentEvent e)
            {
               if(!maximized)
               {
                  lastFrameLocation = getLocation();
               }

               super.componentMoved(e);
            }
         });
      useTrayIcon = true;
      popup       = makeSwingPopup();
      try
      {
         TrayLoader trayLoader  = new TrayLoader();

         useTrayIcon = trayLoader.makeTray(titel, this, popupShowHideMenuItem, zeigenIcon, versteckenIcon, popup);
         AppleJuiceClient.getAjFassade().addDataUpdateListener(new DataUpdateListener()
            {
               private Map<Integer, Integer> stati           = new HashMap<Integer, Integer>();
               private List<Integer>         alreadyNotified = new ArrayList<Integer>();

               public void fireContentChanged(DATALISTENER_TYPE type, Object content)
               {
                  Map<Integer, Download> downloads = (Map<Integer, Download>) content;

                  for(Download curDownload : downloads.values())
                  {
                     Integer oldDownloadStatus = stati.get(curDownload.getId());

                     if(null == oldDownloadStatus)
                     {
                        // neu
                        stati.put(curDownload.getId(), curDownload.getStatus());
                     }
                     else if(curDownload.getStatus() == Download.FERTIG && oldDownloadStatus != Download.FERTIG &&
                                !alreadyNotified.contains(curDownload.getId()))
                     {
                        // fertiggestellt und noch nicht benachrichtigt
                        alreadyNotified.add(curDownload.getId());
                        showMessage("Download fertig", curDownload.getFilename() + " abgeschlossen!");
                     }
                  }

                  ArrayList<Integer> toRemove = new ArrayList<Integer>();

                  for(Integer curKey : stati.keySet())
                  {
                     if(!downloads.containsKey(curKey))
                     {
                        // download wurde entfernt
                        toRemove.add(curKey);
                     }
                  }

                  for(Integer curKey : toRemove)
                  {
                     stati.remove(curKey);
                  }
               }
            }, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
      }
      catch(Throwable e)
      {
         useTrayIcon = false;
      }

      getContentPane().setLayout(new BorderLayout());
      linkPane.getBtnStartDownload().addActionListener(e -> uebernehmeLink());

      getContentPane().add(linkPane, BorderLayout.NORTH);
      getContentPane().add(registerPane, BorderLayout.CENTER);

      TKLPanel panel = new TKLPanel(new GridBagLayout());

      for(int i = 0; i < statusbar.length; i++)
      {
         statusbar[i] = new TKLLabel("            ");
         statusbar[i].setHorizontalAlignment(TKLLabel.RIGHT);
         statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
         statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
      }

      memory.setIcon(IconManager.getInstance().getIcon("mmonitor"));
      memory.addActionListener(ae -> {
         if(memoryMonitorDialog == null)
         {
            memoryMonitorDialog = new MemoryMonitorDialog(AppleJuiceDialog.this);
            Point loc = memory.getLocationOnScreen();

            loc.setLocation(loc.getX() - memoryMonitorDialog.getWidth(), loc.getY() - memoryMonitorDialog.getHeight());
            memoryMonitorDialog.setLocation(loc);
         }

         if(!memoryMonitorDialog.isVisible())
         {
            memoryMonitorDialog.setVisible(true);
         }
      });

      sound.addActionListener(new ActionListener()
         {

            {
               if(OptionsManagerImpl.getInstance().isSoundEnabled())
               {
                  sound.setIcon(IconManager.getInstance().getIcon("soundon"));
               }
               else
               {
                  sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
               }
            }

            public void actionPerformed(ActionEvent ae)
            {
               OptionsManager om = OptionsManagerImpl.getInstance();

               om.enableSound(!om.isSoundEnabled());
               if(om.isSoundEnabled())
               {
                  sound.setIcon(IconManager.getInstance().getIcon("soundon"));
               }
               else
               {
                  sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
               }
            }
         });

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor = GridBagConstraints.NORTH;
      constraints.fill   = GridBagConstraints.BOTH;
      constraints.gridx  = 0;
      constraints.gridy  = 0;
      panel.add(statusbar[0], constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      panel.add(statusbar[1], constraints);
      constraints.weightx = 0;
      constraints.gridx   = 2;
      panel.add(statusbar[2], constraints);
      constraints.gridx = 3;
      panel.add(statusbar[3], constraints);
      constraints.gridx = 4;
      panel.add(statusbar[4], constraints);
      constraints.gridx = 5;
      panel.add(statusbar[5], constraints);
      constraints.gridx = 6;
      panel.add(memory, constraints);
      constraints.gridx = 7;
      panel.add(sound, constraints);
      getContentPane().add(panel, BorderLayout.SOUTH);

      //Tooltipps einstellen
      ToolTipManager.sharedInstance().setInitialDelay(1);
      ToolTipManager.sharedInstance().setDismissDelay(50000);
      LanguageSelector.getInstance().addLanguageListener(linkPane);
      fireLanguageChanged();
      ApplejuiceFassade dm = AppleJuiceClient.getAjFassade();

      dm.addDataUpdateListener(this, DATALISTENER_TYPE.INFORMATION_CHANGED);
      dm.addDataUpdateListener(this, DATALISTENER_TYPE.NETINFO_CHANGED);
      dm.startXMLCheck();
   }

   public static synchronized void showMessage(String caption, String message)
   {
      if(null != trayLoader)
      {
         trayLoader.showBallon(caption, message);
      }
   }

   protected void uebernehmeLink()
   {
      if(linkPane.getTxtDownloadLink().isInvalid())
      {
         return;
      }

      final String link = linkPane.getTxtDownloadLink().getText();
      Object       sel  = linkPane.getCmbTargetDir().getSelectedItem();
      String       tmp;

      if(sel != null)
      {
         tmp            = (String) sel;
      }
      else
      {
         tmp = "";
      }

      final String targetDir = tmp;

      if(link.length() != 0)
      {
         linkPane.getTxtDownloadLink().setText("");
         Thread linkThread = new Thread()
         {
            public void run()
            {
               try
               {
                  final String result = AppleJuiceClient.getAjFassade().processLink(link, targetDir);

                  SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
                  if(result.indexOf("ok") != 0)
                  {
                     SwingUtilities.invokeLater(() -> {
                        String message = null;

                        if(result.contains("already downloaded"))
                        {
                           message = alreadyLoaded.replaceAll("%s", link);
                        }
                        else if(result.contains("incorrect link"))
                        {
                           message = invalidLink.replaceAll("%s", link);
                        }
                        else if(result.contains("failure"))
                        {
                           message = linkFailure;
                        }

                        if(message != null)
                        {
                           JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message, dialogTitel, JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                        }
                     });
                  }
               }
               catch(IllegalArgumentException e)
               {
                  logger.error(e);
               }
            }
         };

         linkThread.start();
      }
   }

   private void demaximize()
   {
      setSize(lastFrameSize);
      setLocation(lastFrameLocation);
      maximized = false;
   }

   private void maximize()
   {
      Toolkit   tk         = Toolkit.getDefaultToolkit();
      Dimension screenSize = tk.getScreenSize();
      Insets    insets     = tk.getScreenInsets(getGraphicsConfiguration());

      screenSize.width -= (insets.left + insets.right);
      screenSize.height -= (insets.top + insets.bottom);
      setSize(screenSize);
      setLocation(insets.left, insets.top);
      maximized = true;
   }

   private static void einstellungenSpeichern()
   {
      try
      {
         String sprachText = LanguageSelector.getInstance().getFirstAttrbuteByTagName("Languageinfo.name");

         OptionsManagerImpl.getInstance().setSprache(sprachText);
         int[]           downloadWidths        = ((DownloadPanel) DownloadController.getInstance().getComponent()).getDownloadColumnWidths();
         int[]           downloadSourcesWidths = ((DownloadPanel) DownloadController.getInstance().getComponent()).getDownloadSourcesColumnWidths();
         int[]           uploadWidths          = ((UploadPanel) UploadController.getInstance().getComponent()).getColumnActiveWidths();
         int[]           uploadWaitingWidths   = ((UploadPanel) UploadController.getInstance().getComponent()).getColumnWaitingWidths();
         int[]           serverWidths          = ServerPanel.getInstance().getColumnWidths();
         int[]           shareWidths           = ((SharePanel) ShareController.getInstance().getComponent()).getColumnWidths();
         Dimension       dim                   = AppleJuiceDialog.getApp().getSize();
         Point           p                     = AppleJuiceDialog.getApp().getLocationOnScreen();
         PositionManager pm                    = PositionManagerImpl.getInstance();

         pm.setMainXY(p);
         pm.setMainDimension(dim);
         pm.setDownloadWidths(downloadWidths);
         pm.setDownloadSourcesWidths(downloadSourcesWidths);
         pm.setUploadWidths(uploadWidths);
         pm.setUploadWaitingWidths(uploadWaitingWidths);
         pm.setServerWidths(serverWidths);
         pm.setShareWidths(shareWidths);
         pm.save();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public void informAutomaticPwdlEnabled(boolean enabled)
   {
      if(enabled != automaticPwdlEnabled)
      {
         automaticPwdlEnabled = enabled;
         setTitle(titel);
         repaint();
      }
   }

   private void closeDialog(WindowEvent evt)
   {
      AppleJuiceClient.getAjFassade().stopXMLCheck();
      if(rewriteProperties)
      {
         PropertiesManager.restoreProperties();
      }

      String nachricht = "appleJuice-GUI wird beendet...";

      if(logger.isEnabledFor(Level.INFO))
      {
         logger.info(nachricht);
      }

      System.out.println(nachricht);
      if(!rewriteProperties)
      {
         einstellungenSpeichern();
      }

      setVisible(false);

      System.exit(0);
   }

   public static void closeWithErrormessage(String error, boolean speichereEinstellungen)
   {
      JOptionPane.showMessageDialog(theApp, error, "appleJuice Client", JOptionPane.OK_OPTION);
      if(rewriteProperties)
      {
         PropertiesManager.restoreProperties();
      }
      else
      {
         AppleJuiceClient.getAjFassade().stopXMLCheck();
      }

      String nachricht = "appleJuice-GUI wird beendet...";
      Logger aLogger   = Logger.getLogger(AppleJuiceDialog.class.getName());

      if(aLogger.isEnabledFor(Level.INFO))
      {
         aLogger.info(nachricht);
      }

      System.out.println(nachricht);
      if(speichereEinstellungen && !rewriteProperties)
      {
         einstellungenSpeichern();
      }

      System.out.println("Fehler: " + error);

      System.exit(-1);
   }

   protected JMenuBar createMenuBar()
   {
      try
      {
         if(!themesInitialized)
         {
            AppleJuiceDialog.initThemes();
         }

         String path         = System.getProperty("user.dir") + File.separator + "language" + File.separator;
         File   languagePath = new File(path);

         if(!languagePath.isDirectory())
         {
            if(logger.isEnabledFor(Level.INFO))
            {
               logger.info("Der Ordner " + path + " fuer die Sprachauswahl properties-Dateien ist nicht vorhanden." +
                           "\r\nappleJuice wird beendet.");
            }

            closeWithErrormessage("Der Ordner " + path + " fuer die Sprachauswahl properties-Dateien ist nicht vorhanden." +
                                  "\r\nappleJuice wird beendet.", false);
         }

         String[]        tempListe     = languagePath.list();
         HashSet<String> sprachDateien = new HashSet<String>();

         for(int i = 0; i < tempListe.length; i++)
         {
            if(tempListe[i].contains(".properties"))
            {
               sprachDateien.add(tempListe[i]);
            }
         }

         if(sprachDateien.size() == 0)
         {
            if(logger.isEnabledFor(Level.INFO))
            {
               logger.info("Es sind keine properties-Dateien fuer die Sprachauswahl im Ordner " + path + " vorhanden." +
                           "\r\nappleJuice wird beendet.");
            }

            closeWithErrormessage("Es sind keine properties-Dateien fuer die Sprachauswahl im Ordner " + path + " vorhanden." +
                                  "\r\nappleJuice wird beendet.", false);
         }

         JMenuBar menuBar = new JMenuBar();

         optionenMenu = new JMenu();
         menuItemOptionen.addActionListener(e -> showOptionsDialog());
         menuItemDateiliste.addActionListener(e -> dateiListeImportieren());
         menuItemCheckUpdate.addActionListener(e -> checkAndDisplayUpdate());
         menuItemCoreBeenden.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.getApp(), bestaetigung, "appleJuice Client",
                                                       JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(result == JOptionPane.YES_OPTION)
            {
               AppleJuiceClient.getAjFassade().shutdownCore();
            }
         });
         menuItemUeber.addActionListener(e -> showAboutDialog());
         optionenMenu.add(menuItemOptionen);
         optionenMenu.add(menuItemDateiliste);
         optionenMenu.add(menuItemCheckUpdate);
         optionenMenu.add(menuItemUeber);
         menuBar.add(optionenMenu);

         sprachMenu = new JMenu();
         menuBar.add(sprachMenu);
         ButtonGroup lafGroup = new ButtonGroup();

         for(String curSprachDatei : sprachDateien)
         {
            String            sprachText = LanguageSelector.getInstance(path + curSprachDatei)
                                           .getFirstAttrbuteByTagName("Languageinfo.name");
            JCheckBoxMenuItem rb         = new JCheckBoxMenuItem(sprachText);

            if(OptionsManagerImpl.getInstance().getSprache().equalsIgnoreCase(sprachText))
            {
               rb.setSelected(true);
            }

            Image     img    = Toolkit.getDefaultToolkit().getImage(path + sprachText.toLowerCase() + ".gif");
            ImageIcon result = new ImageIcon();

            result.setImage(img);
            rb.setIcon(result);

            sprachMenu.add(rb);
            rb.addItemListener(ae -> {
               JCheckBoxMenuItem rb2 = (JCheckBoxMenuItem) ae.getSource();

               if(rb2.isSelected())
               {
                  String path1 = System.getProperty("user.dir") + File.separator + "language" + File.separator;
                  String dateiName = path1 + rb2.getText().toLowerCase() + ".properties";

                  LanguageSelector.getInstance(dateiName);
               }
            });
            lafGroup.add(rb);
         }

         themesMenu = new JMenu();
         if(OptionsManagerImpl.getInstance().isThemesSupported())
         {
            HashSet<URL> themesDateien = new HashSet<URL>();
            File         themesPath    = new File(System.getProperty("user.dir") + File.separator + "themes");

            if(!themesPath.isDirectory())
            {
               if(logger.isEnabledFor(Level.INFO))
               {
                  logger.info("Der Ordner " + path + " fuer die Themes zip-Dateien ist nicht vorhanden." +
                              "\r\nappleJuice wird beendet.");
               }

               closeWithErrormessage("Der Ordner " + path + " fuer die Themes zip-Dateien ist nicht vorhanden." +
                                     "\r\nappleJuice wird beendet.", false);
            }

            File[] themeFiles = themesPath.listFiles();

            for(int i = 0; i < themeFiles.length; i++)
            {
               if(themeFiles[i].isFile() && themeFiles[i].getName().endsWith(".zip"))
               {

                  //testen, ob es wirklich ein skinfile ist
                  ZipFile  jf    = new ZipFile(themeFiles[i]);
                  ZipEntry entry = jf.getEntry("skinlf-themepack.xml");

                  if(entry != null)
                  {
                     themesDateien.add(themeFiles[i].toURL());
                  }
               }
            }

            ButtonGroup lafGroup2    = new ButtonGroup();
            String      temp;
            String      shortName;
            String      defaultTheme = OptionsManagerImpl.getInstance().getDefaultTheme();

            for(URL curSkinURL : themesDateien)
            {
               temp = curSkinURL.getFile();
               int index1 = temp.lastIndexOf('/');
               int index2 = temp.lastIndexOf(".zip");

               if(index1 == -1 || index2 == -1)
               {
                  continue;
               }

               shortName = temp.substring(index1 + 1, index2);
               final JCheckBoxMenuItem rb = new JCheckBoxMenuItem(shortName);

               if(shortName.compareToIgnoreCase(defaultTheme) == 0)
               {
                  rb.setSelected(true);
               }

               rb.addItemListener(ae -> {
                  if(rb.isSelected())
                  {
                     activateLaF(rb.getText());
                  }
               });
               lafGroup2.add(rb);
               themesMenu.add(rb);
            }

            themesMenu.add(new JSeparator());
            menuItemDeaktivieren.addActionListener(ce -> activateThemeSupport(false));
            themesMenu.add(menuItemDeaktivieren);
         }
         else
         {
            final LookAFeel[] feels              = OptionsManagerImpl.getInstance().getLookAndFeels();
            LookAFeel         defaultlookandfeel = OptionsManagerImpl.getInstance().getDefaultLookAndFeel();
            ButtonGroup       lafGroup2          = new ButtonGroup();

            for(int i = 0; i < feels.length; i++)
            {
               final JCheckBoxLookAndFeelMenuItem lookAndFeelMenuItem = new JCheckBoxLookAndFeelMenuItem(feels[i]);

               lafGroup2.add(lookAndFeelMenuItem);
               themesMenu.add(lookAndFeelMenuItem);
               if(defaultlookandfeel != null && feels[i].getName().equals(defaultlookandfeel.getName()))
               {
                  lookAndFeelMenuItem.setSelected(true);
               }
               else
               {
                  lookAndFeelMenuItem.setSelected(false);
               }

               lookAndFeelMenuItem.addItemListener(ae -> {
                  if(lookAndFeelMenuItem.isSelected())
                  {
                     activateLaF(lookAndFeelMenuItem.getText());
                  }
               });
            }

            menuItemAktivieren.addActionListener(ce -> activateThemeSupport(true));
            themesMenu.add(new JSeparator());
            themesMenu.add(menuItemAktivieren);
         }

         menuBar.add(themesMenu);
         coreMenu = new JMenu();
         coreMenu.add(menuItemCoreBeenden);
         menuBar.add(coreMenu);
         coreMenu.setText("Core");
         return menuBar;
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         return null;
      }
   }

   private void showOptionsDialog()
   {
      OptionsDialog od           = new OptionsDialog(getApp());
      Dimension     optDimension = od.getSize();
      Dimension     screenSize   = Toolkit.getDefaultToolkit().getScreenSize();

      od.setLocation((screenSize.width - optDimension.width) / 2, (screenSize.height - optDimension.height) / 2);
      od.setVisible(true);
   }

   private void showAboutDialog()
   {
      AboutDialog aboutDialog  = new AboutDialog(getApp(), true);
      Dimension   appDimension = aboutDialog.getSize();
      Dimension   screenSize   = Toolkit.getDefaultToolkit().getScreenSize();

      aboutDialog.setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
      aboutDialog.setVisible(true);
   }

   private void activateThemeSupport(boolean enable)
   {
      int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.this, themeSupportNachricht, themeSupportTitel,
                                                 JOptionPane.YES_NO_OPTION);

      if(result == JOptionPane.YES_OPTION)
      {
         OptionsManagerImpl.getInstance().enableThemeSupport(enable);
         closeDialog(null);
      }
   }

   private void activateLaF(String laf)
   {
      try
      {

         // theme???
         if(themes != null)
         {
            Skin aSkin = (Skin) themes.get(laf);

            if(aSkin != null)
            {
               SkinLookAndFeel.setSkin(aSkin);
               SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
               OptionsManagerImpl.getInstance().setDefaultTheme(laf);
               return;
            }
         }

         // laf???
         final LookAFeel[] feels = OptionsManagerImpl.getInstance().getLookAndFeels();

         if(feels != null && laf != null)
         {
            for(int i = 0; i < feels.length; i++)
            {
               if(laf.equals(feels[i].getName()))
               {
                  OptionsManagerImpl.getInstance().setDefaultLookAndFeel(feels[i]);
                  return;
               }
            }
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public void setTitle(String newTitle)
   {
      if(!automaticPwdlEnabled)
      {
         super.setTitle(newTitle);
      }
      else
      {
         super.setTitle(newTitle + " - Autopilot");
      }

      titel = newTitle;
   }

   private void dateiListeImportieren()
   {
      JFileChooser fileChooser = new JFileChooser();

      fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
      fileChooser.setFileFilter(new TxtFileFilter());
      fileChooser.setDialogTitle(menuItemDateiliste.getText());
      fileChooser.setMultiSelectionEnabled(false);
      int i = fileChooser.showOpenDialog(this);

      if(i == JFileChooser.APPROVE_OPTION)
      {
         final File file = fileChooser.getSelectedFile();

         if(file.isFile())
         {
            String[]                   dirs                       = AppleJuiceClient.getAjFassade().getCurrentIncomingDirs();
            IncomingDirSelectionDialog incomingDirSelectionDialog = new IncomingDirSelectionDialog(AppleJuiceDialog.getApp(), dirs,
                                                                                                   null);

            incomingDirSelectionDialog.setVisible(true);
            String directory = incomingDirSelectionDialog.getSelectedIncomingDir();

            if(directory != null)
            {
               directory = directory.trim();
               if(directory.indexOf(File.separator) == 0 || directory.indexOf(ApplejuiceFassade.separator) == 0)
               {
                  directory = directory.substring(1);
               }
            }
            else
            {
               directory = "";
            }

            final String targetDir = directory;

            new Thread()
               {
                  public void run()
                  {
                     BufferedReader reader = null;

                     try
                     {
                        reader = new BufferedReader(new FileReader(file));
                        String line = "";

                        while((line = reader.readLine()) != null)
                        {
                           if(line.compareTo("100") == 0)
                           {
                              break;
                           }
                        }

                        String             size           = "";
                        String             filename       = "";
                        String             checksum       = "";
                        String             link           = "";
                        ApplejuiceFassade  af             = AppleJuiceClient.getAjFassade();
                        final StringBuffer returnValues   = new StringBuffer();
                        boolean            somethingAdded = false;

                        while((line = reader.readLine()) != null)
                        {
                           filename = line;
                           checksum = reader.readLine();
                           size = reader.readLine();
                           if(size != null && checksum != null)
                           {
                              link = "ajfsp://file|" + filename + "|" + checksum + "|" + size + "/";
                              String result;

                              try
                              {
                                 result = af.processLink(link, targetDir);
                              }
                              catch(IllegalArgumentException e)
                              {
                                 logger.error(e);
                                 return;
                              }

                              if(result.indexOf("ok") == 0)
                              {
                                 returnValues.append("'" + link + "' OK\n");
                                 somethingAdded = true;
                              }
                              else if(result.contains("already downloaded"))
                              {
                                 returnValues.append(alreadyLoaded.replaceAll("%s", link) + "\n");
                                 somethingAdded = true;
                              }
                              else if(result.contains("incorrect link"))
                              {
                                 returnValues.append(invalidLink.replaceAll("%s", link) + "\n");
                                 somethingAdded = true;
                              }
                              else if(result.contains("failure"))
                              {
                                 returnValues.append(linkFailure + "\n");
                                 somethingAdded = true;
                              }
                           }
                        }

                        if(somethingAdded)
                        {
                           SwingUtilities.invokeLater(() -> {
                              JTextPane textArea = new JTextPane();

                              textArea.setPreferredSize(new Dimension(550, 300));
                              textArea.setMaximumSize(new Dimension(550, 300));
                              textArea.setEditable(false);
                              textArea.setBackground(new TKLLabel().getBackground());
                              textArea.setText(returnValues.toString());
                              JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), new JScrollPane(textArea),
                                                            dialogTitel,
                                                            JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                           });
                        }
                     }
                     catch(FileNotFoundException ex)
                     {
                        ;

                        //nix zu tun
                     }
                     catch(IOException ex1)
                     {
                        ;

                        //nix zu tun
                     }
                  }
               }.start();
         }
      }
   }

   public void fireLanguageChanged()
   {
      try
      {
         LanguageSelector languageSelector = LanguageSelector.getInstance();

         verbunden             = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.verbunden");
         verbinden             = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.verbinden");
         nichtVerbunden        = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.nichtverbunden");
         keinServer            = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.keinserver");
         themeSupportTitel     = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
         themeSupportNachricht = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.themesupportnachricht");
         sprachMenu.setText(languageSelector.getFirstAttrbuteByTagName("einstform.languagesheet.caption"));
         menuItemOptionen.setText(languageSelector.getFirstAttrbuteByTagName("mainform.optbtn.caption"));
         menuItemOptionen.setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.optbtn.hint"));
         menuItemCoreBeenden.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.corebeenden"));
         menuItemCoreBeenden.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.corebeendenhint"));
         menuItemUeber.setText(languageSelector.getFirstAttrbuteByTagName("mainform.aboutbtn.caption"));
         menuItemCheckUpdate.setText(languageSelector.getFirstAttrbuteByTagName("mainform.checkupdate.caption"));
         menuItemCheckUpdate.setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.checkupdate.hint"));
         menuItemDeaktivieren.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.deaktivieren"));
         optionenMenu.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.extras"));
         menuItemDateiliste.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.dateiliste"));
         menuItemDateiliste.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.dateilistehint"));
         themesMenu.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.themes"));
         bestaetigung = languageSelector.getFirstAttrbuteByTagName("javagui.menu.bestaetigung");
         menuItemAktivieren.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.aktivieren"));
         menuItemDeaktivieren.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.deaktivieren"));

         firewallWarning = languageSelector.getFirstAttrbuteByTagName("mainform.firewallwarning.caption");
         alreadyLoaded   = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.bereitsgeladen");
         invalidLink     = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.falscherlink");
         linkFailure     = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.sonstigerlinkfehlerkurz");
         dialogTitel     = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
         if(firewalled)
         {
            statusbar[0].setToolTipText(firewallWarning);
         }

         if(useTrayIcon && null != trayLoader)
         {
            trayLoader.setTextVerstecken(languageSelector.getFirstAttrbuteByTagName("javagui.menu.verstecken"));
            trayLoader.setTextZeigen(languageSelector.getFirstAttrbuteByTagName("javagui.menu.zeigen"));
            popupAboutMenuItem.setText(menuItemUeber.getText());
            popupAboutMenuItem.setToolTipText(menuItemUeber.getToolTipText());
            popupOptionenMenuItem.setText(menuItemOptionen.getText());
            popupOptionenMenuItem.setToolTipText(menuItemOptionen.getToolTipText());
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public void fireContentChanged(DATALISTENER_TYPE type, final Object content)
   {
      if(type == DATALISTENER_TYPE.NETINFO_CHANGED)
      {
         SwingUtilities.invokeLater(() -> {
            try
            {
               NetworkInfo netInfo = (NetworkInfo) content;

               if(netInfo.isFirewalled() != firewalled)
               {
                  firewalled = !firewalled;
                  updateFirewall();
                  if(firewalled)
                  {
                     statusbar[0].setToolTipText(firewallWarning);
                  }
                  else
                  {
                     statusbar[0].setToolTipText(null);
                  }

                  updateFirewall();
               }
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
         });
      }
      else if(type == DATALISTENER_TYPE.INFORMATION_CHANGED)
      {
         SwingUtilities.invokeLater(() -> {
            try
            {
               information = (Information) content;
               statusbar[0].setText(getVerbindungsStatusAsString(information));
               if(information.getVerbindungsStatus() == Information.NICHT_VERBUNDEN)
               {
                  statusbar[1].setText(keinServer);
               }
               else
               {
                  String tmp = information.getServerName();

                  if(tmp == null || tmp.length() == 0)
                  {
                     Server server = information.getServer();

                     if(server != null)
                     {
                        tmp = server.getHost() + ":" + server.getPort();
                     }
                  }

                  statusbar[1].setText(tmp);
               }

               statusbar[2].setText(information.getUpDownAsString());
               statusbar[3].setText(information.getUpDownSessionAsString());
               statusbar[4].setText(information.getExterneIP());
               statusbar[5].setText(information.getCreditsAsString());
               updateFirewall();
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
         });
      }
   }

   protected void updateFirewall()
   {
      if(information != null && information.getVerbindungsStatus() != Information.VERBUNDEN)
      {
         statusbar[0].setIcon(nichtVerbundenIcon);
      }
      else
      {
         if(firewalled)
         {
            statusbar[0].setIcon(firewallIcon);
         }
         else
         {
            statusbar[0].setIcon(verbundenIcon);
         }
      }
   }

   public JPopupMenu makeSwingPopup()
   {
      final JPopupMenu popup = new JPopupMenu();

      popupShowHideMenuItem.addActionListener(ae -> {
         if(!isVisible())
         {
            setVisible(true);
            setAlwaysOnTop(true);
            setAlwaysOnTop(false);
            requestFocus();
         }
         else
         {
            if(popup.isVisible())
            {
               popup.setVisible(false);
            }

            setVisible(false);
         }
      });
      popup.add(popupShowHideMenuItem);
      popupOptionenMenuItem.addActionListener(ae -> showOptionsDialog());

      popup.add(popupOptionenMenuItem);
      IconManager im = IconManager.getInstance();

      versteckenIcon = im.getIcon("hide");
      zeigenIcon     = im.getIcon("applejuice");
      Icon aboutIcon = im.getIcon("about");

      popupOptionenMenuItem.setIcon(im.getIcon("optionen"));
      popupAboutMenuItem.setIcon(aboutIcon);
      popupAboutMenuItem.addActionListener(ae -> showAboutDialog());
      popup.add(popupAboutMenuItem);
      new Thread()
         {
            public void run()
            {
               final AJSettings ajSettings = AppleJuiceClient.getAjFassade().getAJSettings();

               if(ajSettings != null)
               {
                  long maxUpload = 50;

                  if(ajSettings.getMaxUploadInKB() > maxUpload)
                  {
                     maxUpload = ajSettings.getMaxUploadInKB() + 20;
                  }

                  final JSlider uploadSlider = new JSlider(JSlider.VERTICAL, 0, (int) maxUpload, (int) ajSettings.getMaxUploadInKB());
                  long          maxDownload  = 300;

                  if(ajSettings.getMaxDownloadInKB() > maxDownload)
                  {
                     maxDownload = ajSettings.getMaxDownloadInKB() + 20;
                  }

                  final JSlider downloadSlider = new JSlider(JSlider.VERTICAL, 0, (int) maxDownload,
                                                             (int) ajSettings.getMaxDownloadInKB());

                  uploadSlider.setPaintLabels(true);
                  uploadSlider.setPaintTicks(true);
                  uploadSlider.setPaintTrack(true);
                  uploadSlider.setSnapToTicks(true);
                  downloadSlider.setPaintLabels(true);
                  downloadSlider.setPaintTicks(true);
                  downloadSlider.setPaintTrack(true);
                  downloadSlider.setSnapToTicks(true);
                  final JMenu    uploadMenu    = new JMenu("Upload");
                  final JMenu    downloadMenu  = new JMenu("Download");
                  TKLPanel       uploadPanel   = new TKLPanel(new BorderLayout());
                  TKLPanel       downloadPanel = new TKLPanel(new BorderLayout());
                  final TKLLabel label1        = new TKLLabel("50 kb/s");
                  final TKLLabel label2        = new TKLLabel("50 kb/s");

                  label1.setText(ajSettings.getMaxUploadInKB() + " kb/s");
                  label2.setText(ajSettings.getMaxDownloadInKB() + " kb/s");
                  uploadPanel.add(label1, BorderLayout.NORTH);
                  uploadPanel.add(uploadSlider, BorderLayout.SOUTH);
                  uploadMenu.add(uploadPanel);
                  downloadPanel.add(label2, BorderLayout.NORTH);
                  downloadPanel.add(downloadSlider, BorderLayout.SOUTH);
                  downloadMenu.add(downloadPanel);
                  uploadSlider.addChangeListener(e -> {
                     JSlider slider = (JSlider) e.getSource();

                     label1.setText(slider.getValue() + " kb/s");
                  });
                  downloadSlider.addChangeListener(e -> {
                     JSlider slider = (JSlider) e.getSource();

                     label2.setText(slider.getValue() + " kb/s");
                  });
                  uploadSlider.addMouseListener(new MouseAdapter()
                     {
                        public void mouseReleased(MouseEvent e)
                        {
                           if(uploadSlider.getValue() < uploadSlider.getMaximum() && uploadSlider.getValue() > 0)
                           {
                              Long down = (long) (downloadSlider.getValue() * 1024);
                              Long up   = (long) (uploadSlider.getValue() * 1024);

                              try
                              {
                                 AppleJuiceClient.getAjFassade().setMaxUpAndDown(up, down);
                              }
                              catch(IllegalArgumentException e1)
                              {
                                 logger.error(e1);
                              }
                           }
                           else
                           {
                              uploadSlider.setValue((int) ajSettings.getMaxUploadInKB());
                           }
                        }
                     });
                  downloadSlider.addMouseListener(new MouseAdapter()
                     {
                        public void mouseReleased(MouseEvent e)
                        {
                           if(downloadSlider.getValue() < downloadSlider.getMaximum() && downloadSlider.getValue() > 0)
                           {
                              Long down = (long) (downloadSlider.getValue() * 1024);
                              Long up   = (long) (uploadSlider.getValue() * 1024);

                              try
                              {
                                 AppleJuiceClient.getAjFassade().setMaxUpAndDown(up, down);
                              }
                              catch(IllegalArgumentException e1)
                              {
                                 logger.error(e1);
                              }
                           }
                           else
                           {
                              downloadSlider.setValue((int) ajSettings.getMaxDownloadInKB());
                           }
                        }
                     });
                  SwingUtilities.invokeLater(() -> {
                     popup.add(uploadMenu);
                     popup.add(downloadMenu);
                  });
               }
            }
         }.start();
      return popup;
   }

   public static void showInformation(String information)
   {
      JOptionPane.showMessageDialog(theApp, information, "appleJuice Client", JOptionPane.OK_OPTION);
   }

   private String getVerbindungsStatusAsString(Information information)
   {
      switch(information.getVerbindungsStatus())
      {

         case Information.VERBUNDEN:
            return verbunden;

         case Information.NICHT_VERBUNDEN:
            return nichtVerbunden;

         case Information.VERSUCHE_ZU_VERBINDEN:
            return verbinden;

         default:
            return "";
      }
   }

   private void checkAndDisplayUpdate()
   {
      VersionChecker.check();
   }

   public void informWrongPassword()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String           nachricht        = languageSelector.getFirstAttrbuteByTagName("mainform.msgdlgtext3");

      SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
      closeWithErrormessage(nachricht, true);
   }

   private class TxtFileFilter extends FileFilter
   {
      public boolean accept(File file)
      {
         if(!file.isFile())
         {
            return true;
         }
         else
         {
            String name = file.getName();

            return (name.toLowerCase().endsWith(".ajl"));
         }
      }

      public String getDescription()
      {
         return "AJL-Dateien";
      }
   }


   private class JCheckBoxLookAndFeelMenuItem extends JCheckBoxMenuItem
   {
      private final LookAFeel lookAFeel;

      public JCheckBoxLookAndFeelMenuItem(LookAFeel lookAFeelToUse)
      {
         super(lookAFeelToUse.getName());
         this.lookAFeel = lookAFeelToUse;
         addItemListener(ae -> {
            if(isSelected())
            {
               try
               {
                  UIManager.setLookAndFeel(lookAFeel.getClassName());
                  SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
               }
               catch(Exception ex)
               {
                  logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
               }
            }
         });
      }
   }
}
