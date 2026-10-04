/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
import de.applejuicenet.client.gui.about.AboutDialog;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.download.DownloadController;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.memorymonitor.MemoryMonitorDialog;
import de.applejuicenet.client.gui.options.IncomingDirSelectionDialog;
import de.applejuicenet.client.gui.options.OptionsDialog;
import de.applejuicenet.client.gui.server.ServerPanel;
import de.applejuicenet.client.gui.share.ShareController;
import de.applejuicenet.client.gui.tray.TrayLoader;
import de.applejuicenet.client.gui.upload.UploadController;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.SoundPlayer;
import de.tklsoft.gui.controls.TKLButton;
import de.tklsoft.gui.controls.TKLFrame;
import de.tklsoft.gui.controls.TKLLabel;
import de.tklsoft.gui.controls.TKLPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import de.applejuicenet.client.shared.AjlFile;

import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

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
 * @author Maj0r [aj@tkl-soft.de]
 */
public class AppleJuiceDialog extends TKLFrame implements LanguageListener, DataUpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(AppleJuiceDialog.class);
    public static boolean rewriteProperties = false;
    private static AppleJuiceDialog theApp;
    private static boolean lookAndFeelInitialized = false;
    private static boolean useTrayIcon = false;
    private static TrayLoader trayLoader = null;
    private Information information = null;
    private RegisterPanel registerPane;
    private TKLLabel[] statusbar = new TKLLabel[6];
    private JMenu sprachMenu;
    private JMenu optionenMenu;
    private JMenu themesMenu = null;
    private JMenu iconsetMenu = null;
    private JMenu soundsetMenu = null;
    private JMenu coreMenu;
    private JMenuItem menuItemOptionen = new JMenuItem();
    private JMenuItem menuItemDateiliste = new JMenuItem();
    private JMenuItem menuItemCheckUpdate = new JMenuItem();
    private JMenuItem menuItemCoreBeenden = new JMenuItem();
    private JMenuItem menuItemUeber = new JMenuItem();
    private JMenuItem menuItemBeenden = new JMenuItem();
    private JMenuItem popupOptionenMenuItem = new JMenuItem();
    private JMenuItem popupAboutMenuItem = new JMenuItem();
    private JMenuItem popupBeendenMenuItem = new JMenuItem();
    private JMenuItem popupShowHideMenuItem = new JMenuItem();
    private JMenuItem popupCheckUpdateMenuItem = new JMenuItem();
    private TKLButton sound = new TKLButton();
    private TKLButton memory = new TKLButton();
    private String keinServer;
    private boolean firstChange = true;
    private MemoryMonitorDialog memoryMonitorDialog;
    private String neustartTitel;
    private String neustartNachricht;
    private boolean automaticPwdlEnabled = false;
    private String titel;
    private String bestaetigung;
    private int desktopHeight;
    private int desktopWidth;
    private boolean maximized = false;
    private Dimension lastFrameSize;
    private Point lastFrameLocation;
    private Icon versteckenIcon = null;
    private Icon zeigenIcon = null;
    private boolean firewalled = false;
    private String firewallWarning;
    private String alreadyLoaded;
    private String invalidLink;
    private String linkFailure;
    private String dialogTitel;
    private String verbunden;
    private String verbinden;
    private String nichtVerbunden;
    private ImageIcon firewallIcon;
    private ImageIcon verbundenIcon;
    private ImageIcon nichtVerbundenIcon;
    private JPopupMenu popup;
    private DownloadlinkPanel linkPane = new DownloadlinkPanel();

    public AppleJuiceDialog() {
        super();
        try {
            enableCloseWindowListener(false);
            theApp = this;
            init();
            pack();
            LanguageSelector.getInstance().addLanguageListener(this);
            initKeyStrokes();

        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public static String getVersion() {
        return AppleJuiceDialog.class.getPackage().getImplementationVersion();
    }

    private void initKeyStrokes() {
        int tabCount = registerPane.getTabCount();

        for (int i = 0; i < tabCount; i++) {
            int event = i < 9 ? KeyEvent.VK_1 + i : KeyEvent.VK_A + i - 9;
            KeyStroke stroke = KeyStroke.getKeyStroke(event, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            final int index = i;
            AbstractAction action = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    registerPane.setSelectedIndex(index);
                }
            };

            String commandName = "ctrl_" + ((char) event);

            ((JComponent) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, commandName);
            ((JComponent) getContentPane()).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, commandName);
            ((JComponent) getContentPane()).getActionMap().put(commandName, action);
        }
    }

    private static void installClipboardKeys() {
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        String[] inputMaps = {"TextField", "PasswordField", "TextArea", "TextPane", "EditorPane", "FormattedTextField"};

        for (String name : inputMaps) {
            InputMap im = (InputMap) UIManager.get(name + ".focusInputMap");

            if (im == null) {
                continue;
            }

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcutMask), DefaultEditorKit.selectAllAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutMask), DefaultEditorKit.pasteAction);
            if (!"PasswordField".equals(name)) {
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutMask), DefaultEditorKit.copyAction);
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutMask), DefaultEditorKit.cutAction);
            }
        }
    }

    public static void initLookAndFeel() {
        try {
            lookAndFeelInitialized = true;
            LookAFeel defaultlookandfeel = OptionsManagerImpl.getInstance().getDefaultLookAndFeel();

            if (defaultlookandfeel != null) {
                UIManager.setLookAndFeel(defaultlookandfeel.getClassName());
                installClipboardKeys();
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public static AppleJuiceDialog getApp() {
        return theApp;
    }

    private void init() throws Exception {
        titel = "appleJuice GUI (" + AppleJuiceDialog.getVersion() + ")";
        IconManager im = IconManager.getInstance();

        firewallIcon = im.getIcon("firewall");
        verbundenIcon = im.getIcon("serververbunden");
        nichtVerbundenIcon = im.getIcon("serverversuche");

        Image image = im.getIcon("applejuice").getImage();

        setTitle(titel);

        setIconImage(image);
        menuItemOptionen.setIcon(im.getIcon("optionen"));
        menuItemUeber.setIcon(im.getIcon("info"));
        menuItemCoreBeenden.setIcon(im.getIcon("skull"));
        menuItemDateiliste.setIcon(im.getIcon("speichern"));
        menuItemCheckUpdate.setIcon(im.getIcon("update"));
        menuItemBeenden.setIcon(im.getIcon("abbrechen"));

        setJMenuBar(createMenuBar());

        LanguageSelector languageSelector = LanguageSelector.getInstance();

        registerPane = new RegisterPanel(this);
        languageSelector.fireLanguageChanged();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        desktopWidth = screenSize.width;
        desktopHeight = screenSize.height;
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if ((getExtendedState() & Frame.ICONIFIED) != 0) {
                    return;
                }
                int x = getWidth();
                int y = getHeight();

                if (x == desktopWidth && y == desktopHeight) {
                    if (maximized) {
                        demaximize();
                    } else {
                        maximize();
                    }
                } else {
                    if (canRememberWindowBounds()) {
                        lastFrameSize = getSize();
                        lastFrameLocation = getLocation();
                    }

                    super.componentResized(e);
                }
            }

            public void componentMoved(ComponentEvent e) {
                if (canRememberWindowBounds()) {
                    lastFrameLocation = getLocation();
                }

                super.componentMoved(e);
            }
        });
        useTrayIcon = true;
        popup = makeSwingPopup();
        try {
            trayLoader = new TrayLoader();

            useTrayIcon = trayLoader.makeTray(titel, this, popupShowHideMenuItem, zeigenIcon, versteckenIcon, popup);
            AppleJuiceClient.getAjFassade().addDataUpdateListener(new DataUpdateListener() {
                private Map<Integer, Integer> stati = new HashMap<Integer, Integer>();
                private List<Integer> alreadyNotified = new ArrayList<Integer>();

                public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
                    Map<Integer, Download> downloads = (Map<Integer, Download>) content;

                    for (Download curDownload : downloads.values()) {
                        Integer oldDownloadStatus = stati.get(curDownload.getId());

                        if (null == oldDownloadStatus) {
                            // neu
                            stati.put(curDownload.getId(), curDownload.getStatus());
                        } else if (curDownload.getStatus() == Download.FERTIG && oldDownloadStatus != Download.FERTIG &&
                                !alreadyNotified.contains(curDownload.getId())) {
                            // fertiggestellt und noch nicht benachrichtigt
                            alreadyNotified.add(curDownload.getId());
                            showMessage("Download fertig", curDownload.getFilename() + " abgeschlossen!");
                        }
                    }

                    ArrayList<Integer> toRemove = new ArrayList<Integer>();

                    for (Integer curKey : stati.keySet()) {
                        if (!downloads.containsKey(curKey)) {
                            // download wurde entfernt
                            toRemove.add(curKey);
                        }
                    }

                    for (Integer curKey : toRemove) {
                        stati.remove(curKey);
                    }
                }
            }, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
        } catch (Throwable e) {
            useTrayIcon = false;
        }

        getContentPane().setLayout(new BorderLayout());
        linkPane.getBtnStartDownload().addActionListener(e -> uebernehmeLink());

        getContentPane().add(linkPane, BorderLayout.NORTH);
        getContentPane().add(registerPane, BorderLayout.CENTER);

        TKLPanel panel = new TKLPanel(new GridBagLayout());

        for (int i = 0; i < statusbar.length; i++) {
            statusbar[i] = new TKLLabel("            ");
            statusbar[i].setHorizontalAlignment(TKLLabel.RIGHT);
            statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
            statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
        }

        memory.setIcon(IconManager.getInstance().getIcon("mmonitor"));
        memory.addActionListener(ae -> {
            if (memoryMonitorDialog == null) {
                memoryMonitorDialog = new MemoryMonitorDialog(AppleJuiceDialog.this);
                Point loc = memory.getLocationOnScreen();

                loc.setLocation(loc.getX() - memoryMonitorDialog.getWidth(), loc.getY() - memoryMonitorDialog.getHeight());
                memoryMonitorDialog.setLocation(loc);
            }

            if (!memoryMonitorDialog.isVisible()) {
                memoryMonitorDialog.setVisible(true);
            }
        });

        sound.addActionListener(new ActionListener() {

            {
                if (OptionsManagerImpl.getInstance().isSoundEnabled()) {
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                } else {
                    sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
                }
            }

            public void actionPerformed(ActionEvent ae) {
                OptionsManager om = OptionsManagerImpl.getInstance();

                om.enableSound(!om.isSoundEnabled());
                if (om.isSoundEnabled()) {
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                } else {
                    sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
                }
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(statusbar[0], constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(statusbar[1], constraints);
        constraints.weightx = 0;
        constraints.gridx = 2;
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

        try {
            dm.startXMLCheck();

        }catch (RuntimeException e) {
            String msg = String.format("Core Version %s required!", ApplejuiceFassade.MIN_NEEDED_CORE_VERSION);
            AppleJuiceDialog.closeWithErrormessage(msg, false);
            System.exit(1);
        }
    }

    public static synchronized void showMessage(String caption, String message) {
        if (null != trayLoader) {
            trayLoader.showBallon(caption, message);
        }
    }

    protected void uebernehmeLink() {
        if (linkPane.getTxtDownloadLink().isInvalid()) {
            return;
        }

        final String link = linkPane.getTxtDownloadLink().getText().trim().replace("%7C", "|");
        Object sel = linkPane.getCmbTargetDir().getSelectedItem();
        String tmp;

        if (sel != null) {
            tmp = (String) sel;
        } else {
            tmp = "";
        }

        final String targetDir = tmp;

        if (link.length() != 0) {
            linkPane.getTxtDownloadLink().setText("");
            Thread linkThread = new Thread() {
                public void run() {
                    try {
                        final String result = AppleJuiceClient.getAjFassade().processLink(link, targetDir);

                        SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
                        if (result.indexOf("ok") != 0) {
                            SwingUtilities.invokeLater(() -> {
                                String message = null;

                                if (result.contains("already downloaded")) {
                                    message = alreadyLoaded.replaceAll("%s", link);
                                } else if (result.contains("incorrect link")) {
                                    message = invalidLink.replaceAll("%s", link);
                                } else if (result.contains("failure")) {
                                    message = linkFailure;
                                }

                                if (message != null) {
                                    JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message, dialogTitel, JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        }
                    } catch (IllegalArgumentException e) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    }
                }
            };

            linkThread.start();
        }
    }

    private void demaximize() {
        setSize(lastFrameSize);
        setLocation(lastFrameLocation);
        maximized = false;
    }

    private void maximize() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        Insets insets = tk.getScreenInsets(getGraphicsConfiguration());

        screenSize.width -= (insets.left + insets.right);
        screenSize.height -= (insets.top + insets.bottom);
        setSize(screenSize);
        setLocation(insets.left, insets.top);
        maximized = true;
    }

    private static void einstellungenSpeichern() {
        try {
            String sprachText = LanguageSelector.getInstance().getFirstAttrbuteByTagName("Languageinfo.name");

            OptionsManagerImpl.getInstance().setSprache(sprachText);
            Rectangle bounds = AppleJuiceDialog.getApp().getNormalWindowBounds();
            PositionManager pm = PositionManagerImpl.getInstance();

            pm.setMainXY(bounds.getLocation());
            pm.setMainDimension(bounds.getSize());
            pm.save();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public void informAutomaticPwdlEnabled(boolean enabled) {
        if (enabled != automaticPwdlEnabled) {
            automaticPwdlEnabled = enabled;
            setTitle(titel);
            repaint();
        }
    }

    private void closeDialog(WindowEvent evt) {
        AppleJuiceClient.getAjFassade().stopXMLCheck();
        if (rewriteProperties) {
            PropertiesManager.restoreProperties();
        }

        String nachricht = "appleJuice-GUI wird beendet...";

        logger.info(nachricht);

        System.out.println(nachricht);
        if (!rewriteProperties) {
            einstellungenSpeichern();
        }

        setVisible(false);
        if (trayLoader != null) trayLoader.close();

        System.exit(0);
    }

    public static void closeWithErrormessage(String error, boolean speichereEinstellungen) {
        JOptionPane.showMessageDialog(theApp, error, "appleJuice Client", JOptionPane.OK_OPTION);
        if (rewriteProperties) {
            PropertiesManager.restoreProperties();
        } else {
            AppleJuiceClient.getAjFassade().stopXMLCheck();
        }

        String nachricht = "appleJuice-GUI wird beendet...";

        logger.info(nachricht);

        System.out.println(nachricht);
        if (speichereEinstellungen && !rewriteProperties) {
            einstellungenSpeichern();
        }

        System.out.println("Fehler: " + error);

        if (trayLoader != null) trayLoader.close();
        System.exit(-1);
    }

    protected JMenuBar createMenuBar() {
        try {
            if (!lookAndFeelInitialized) {
                AppleJuiceDialog.initLookAndFeel();
            }

            String path = System.getProperty("user.dir") + File.separator + "language" + File.separator;
            File languagePath = new File(path);

            if (!languagePath.isDirectory()) {
                logger.info("Der Ordner " + path + " für die Sprachauswahl properties-Dateien ist nicht vorhanden." + "\r\nappleJuice wird beendet.");

                closeWithErrormessage("Der Ordner " + path + " fuer die Sprachauswahl properties-Dateien ist nicht vorhanden." +
                        "\r\nappleJuice wird beendet.", false);
            }

            String[] tempListe = languagePath.list();
            HashSet<String> sprachDateien = new HashSet<String>();

            for (int i = 0; i < tempListe.length; i++) {
                if (tempListe[i].contains(".properties")) {
                    sprachDateien.add(tempListe[i]);
                }
            }

            if (sprachDateien.size() == 0) {
                logger.info("Es sind keine properties-Dateien fuer die Sprachauswahl im Ordner " + path + " vorhanden." +
                        "\r\nappleJuice wird beendet.");

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

                if (result == JOptionPane.YES_OPTION) {
                    AppleJuiceClient.getAjFassade().shutdownCore();
                }
            });
            menuItemUeber.addActionListener(e -> showAboutDialog());
            menuItemBeenden.addActionListener(e -> closeDialog(null));
            optionenMenu.add(menuItemOptionen);
            optionenMenu.add(menuItemDateiliste);
            optionenMenu.add(menuItemCheckUpdate);
            optionenMenu.add(menuItemUeber);
            optionenMenu.add(menuItemBeenden);
            menuBar.add(optionenMenu);

            sprachMenu = new JMenu();
            menuBar.add(sprachMenu);
            ButtonGroup lafGroup = new ButtonGroup();

            for (String curSprachDatei : sprachDateien) {
                String sprachText;
                try {
                    sprachText = LanguageSelector.readLanguageName(new File(path, curSprachDatei));
                } catch (IOException | java.lang.IllegalArgumentException e) {
                    logger.warn("Sprachdatei {} nicht lesbar", curSprachDatei, e);
                    continue;
                }
                if (sprachText.isBlank()) {
                    continue;
                }
                JCheckBoxMenuItem rb = new JCheckBoxMenuItem(sprachText);

                if (OptionsManagerImpl.getInstance().getSprache().equalsIgnoreCase(sprachText)) {
                    rb.setSelected(true);
                }

                String flagFile = curSprachDatei.substring(0, curSprachDatei.length() - ".properties".length()) + ".gif";
                rb.setIcon(new ImageIcon(new File(languagePath, flagFile).getAbsolutePath()));

                sprachMenu.add(rb);
                rb.addItemListener(ae -> {
                    JCheckBoxMenuItem rb2 = (JCheckBoxMenuItem) ae.getSource();

                    if (rb2.isSelected()) {
                        String path1 = System.getProperty("user.dir") + File.separator + "language" + File.separator;
                        String dateiName = path1 + rb2.getText().toLowerCase() + ".properties";

                        LanguageSelector.getInstance(dateiName);
                    }
                });
                lafGroup.add(rb);
            }

            themesMenu = new JMenu();
            final LookAFeel[] feels = OptionsManagerImpl.getInstance().getLookAndFeels();
            LookAFeel defaultlookandfeel = OptionsManagerImpl.getInstance().getDefaultLookAndFeel();
            ButtonGroup lafGroup2 = new ButtonGroup();

            for (LookAFeel feel : feels) {
                final JCheckBoxLookAndFeelMenuItem lookAndFeelMenuItem = new JCheckBoxLookAndFeelMenuItem(feel);

                lafGroup2.add(lookAndFeelMenuItem);
                themesMenu.add(lookAndFeelMenuItem);
                lookAndFeelMenuItem.setSelected(defaultlookandfeel != null && feel.getName().equals(defaultlookandfeel.getName()));

                lookAndFeelMenuItem.addItemListener(ae -> {
                    if (lookAndFeelMenuItem.isSelected()) {
                        activateLaF(lookAndFeelMenuItem.getText());
                    }
                });
            }
            menuBar.add(themesMenu);

            iconsetMenu = new JMenu();
            ButtonGroup iconsetGroup = new ButtonGroup();
            String iconsetPath = System.getProperty("user.dir") + File.separator + "icons" + File.separator;
            String iconsetDefault = OptionsManagerImpl.getInstance().getIconSetName();

            File iconsetPathFile = new File(iconsetPath);
            File[] iconsetDirectoryListing = iconsetPathFile.listFiles();
            if (iconsetDirectoryListing != null) {
                for (File iconSet : iconsetDirectoryListing) {
                    if (iconSet.isDirectory()) {
                        final iconsetFeelMenuItem iconsetMenuItem = new iconsetFeelMenuItem(iconSet.getName());
                        iconsetMenuItem.setSelected(iconSet.getName().equals(iconsetDefault));
                        iconsetMenuItem.addActionListener(ce -> changeIconOrSoundSet());
                        iconsetMenu.add(iconsetMenuItem);
                        iconsetGroup.add(iconsetMenuItem);
                    }
                }
            }

            soundsetMenu = new JMenu();
            ButtonGroup soundsetGroup = new ButtonGroup();

            String soundsetPath = System.getProperty("user.dir") + File.separator + "sounds" + File.separator;
            String soundsetDefault = OptionsManagerImpl.getInstance().getSoundSetName();

            File soundsetPathFile = new File(soundsetPath);
            File[] soundsetDirectoryListing = soundsetPathFile.listFiles();
            if (soundsetDirectoryListing != null) {
                for (File soundSet : soundsetDirectoryListing) {
                    if (soundSet.isDirectory()) {
                        final soundsetFeelMenuItem soundsetMenuItem = new soundsetFeelMenuItem(soundSet.getName());
                        soundsetMenuItem.setSelected(soundSet.getName().equals(soundsetDefault));
                        soundsetMenu.add(soundsetMenuItem);
                        soundsetGroup.add(soundsetMenuItem);
                    }
                }
            }

            menuBar.add(iconsetMenu);
            menuBar.add(soundsetMenu);

            coreMenu = new JMenu();
            coreMenu.add(menuItemCoreBeenden);
            menuBar.add(coreMenu);
            coreMenu.setText("Core");
            return menuBar;
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            return null;
        }
    }

    private void showOptionsDialog() {
        OptionsDialog od = new OptionsDialog(getApp());
        Dimension optDimension = od.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        od.setLocation((screenSize.width - optDimension.width) / 2, (screenSize.height - optDimension.height) / 2);
        od.setVisible(true);
    }

    private void showAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(getApp(), true);
        Dimension appDimension = aboutDialog.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        aboutDialog.setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
        aboutDialog.setVisible(true);
    }

    private void changeIconOrSoundSet() {
        int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.this, neustartNachricht, "appleJuice Client", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            closeDialog(null);
        }
    }

    private void activateLaF(String laf) {
        try {

            final LookAFeel[] feels = OptionsManagerImpl.getInstance().getLookAndFeels();

            if (feels != null && laf != null) {
                for (LookAFeel feel : feels) {
                    if (laf.equals(feel.getName())) {
                        OptionsManagerImpl.getInstance().setDefaultLookAndFeel(feel);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public void setTitle(String newTitle) {
        if (!automaticPwdlEnabled) {
            super.setTitle(newTitle);
        } else {
            super.setTitle(newTitle + " - Autopilot");
        }

        titel = newTitle;
    }

    private void dateiListeImportieren() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new TxtFileFilter());
        fileChooser.setDialogTitle(menuItemDateiliste.getText());
        fileChooser.setMultiSelectionEnabled(false);
        int i = fileChooser.showOpenDialog(this);

        if (i == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();

            if (file.isFile()) {
                String[] dirs = AppleJuiceClient.getAjFassade().getCurrentIncomingDirs();
                IncomingDirSelectionDialog incomingDirSelectionDialog = new IncomingDirSelectionDialog(AppleJuiceDialog.getApp(), dirs,
                        null);

                incomingDirSelectionDialog.setVisible(true);
                String directory = incomingDirSelectionDialog.getSelectedIncomingDir();

                if (directory != null) {
                    directory = directory.trim();
                    if (directory.indexOf(File.separator) == 0 || directory.indexOf(ApplejuiceFassade.separator) == 0) {
                        directory = directory.substring(1);
                    }
                } else {
                    directory = "";
                }

                final String targetDir = directory;

                new Thread(() -> importAjl(file, targetDir)).start();
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();

            verbunden = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.verbunden");
            verbinden = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.verbinden");
            nichtVerbunden = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.nichtverbunden");
            keinServer = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.keinserver");
            neustartTitel = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
            neustartNachricht = languageSelector.getFirstAttrbuteByTagName("javagui.mainform.neustartnachricht");
            sprachMenu.setText(languageSelector.getFirstAttrbuteByTagName("einstform.languagesheet.caption"));
            menuItemOptionen.setText(languageSelector.getFirstAttrbuteByTagName("mainform.optbtn.caption"));
            menuItemOptionen.setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.optbtn.hint"));
            menuItemCoreBeenden.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.corebeenden"));
            menuItemCoreBeenden.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.corebeendenhint"));
            menuItemUeber.setText(languageSelector.getFirstAttrbuteByTagName("mainform.aboutbtn.caption"));
            menuItemBeenden.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.beenden"));
            menuItemCheckUpdate.setText(languageSelector.getFirstAttrbuteByTagName("mainform.checkupdate.caption"));
            menuItemCheckUpdate.setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.checkupdate.hint"));
            optionenMenu.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.extras"));
            menuItemDateiliste.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.dateiliste"));
            menuItemDateiliste.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.dateilistehint"));
            themesMenu.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.themes"));
            iconsetMenu.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.icons"));
            soundsetMenu.setText(languageSelector.getFirstAttrbuteByTagName("javagui.menu.sound"));
            bestaetigung = languageSelector.getFirstAttrbuteByTagName("javagui.menu.bestaetigung");

            firewallWarning = languageSelector.getFirstAttrbuteByTagName("mainform.firewallwarning.caption");
            alreadyLoaded = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.bereitsgeladen");
            invalidLink = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.falscherlink");
            linkFailure = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.sonstigerlinkfehlerkurz");
            dialogTitel = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
            if (firewalled) {
                statusbar[0].setToolTipText(firewallWarning);
            }

            if (useTrayIcon && null != trayLoader) {
                trayLoader.setTextVerstecken(languageSelector.getFirstAttrbuteByTagName("javagui.menu.verstecken"));
                trayLoader.setTextZeigen(languageSelector.getFirstAttrbuteByTagName("javagui.menu.zeigen"));
                popupAboutMenuItem.setText(menuItemUeber.getText());
                popupAboutMenuItem.setToolTipText(menuItemUeber.getToolTipText());
                popupOptionenMenuItem.setText(menuItemOptionen.getText());
                popupOptionenMenuItem.setToolTipText(menuItemOptionen.getToolTipText());
                popupBeendenMenuItem.setText(menuItemBeenden.getText());
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public void fireContentChanged(DATALISTENER_TYPE type, final Object content) {
        if (type == DATALISTENER_TYPE.NETINFO_CHANGED) {
            SwingUtilities.invokeLater(() -> {
                try {
                    NetworkInfo netInfo = (NetworkInfo) content;

                    if (netInfo.isFirewalled() != firewalled) {
                        firewalled = !firewalled;
                        updateFirewall();
                        if (firewalled) {
                            statusbar[0].setToolTipText(firewallWarning);
                        } else {
                            statusbar[0].setToolTipText(null);
                        }

                        updateFirewall();
                    }
                } catch (Exception e) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                }
            });
        } else if (type == DATALISTENER_TYPE.INFORMATION_CHANGED) {
            SwingUtilities.invokeLater(() -> {
                try {
                    information = (Information) content;
                    statusbar[0].setText(getVerbindungsStatusAsString(information));
                    if (information.getVerbindungsStatus() == Information.NICHT_VERBUNDEN) {
                        statusbar[1].setText(keinServer);
                    } else {
                        String tmp = information.getServerName();

                        if (tmp == null || tmp.length() == 0) {
                            Server server = information.getServer();

                            if (server != null) {
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
                } catch (Exception e) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                }
            });
        }
    }

    protected void updateFirewall() {
        if (information != null && information.getVerbindungsStatus() != Information.VERBUNDEN) {
            statusbar[0].setIcon(nichtVerbundenIcon);
        } else {
            if (firewalled) {
                statusbar[0].setIcon(firewallIcon);
            } else {
                statusbar[0].setIcon(verbundenIcon);
            }
        }
    }

    private boolean canRememberWindowBounds() {
        return isVisible() && !maximized && getExtendedState() == Frame.NORMAL
                && getWidth() >= 320 && getHeight() >= 240
                && getBounds().equals(WindowPlacement.normalize(getBounds()));
    }

    private Rectangle getNormalWindowBounds() {
        Rectangle bounds = !canRememberWindowBounds() && lastFrameSize != null && lastFrameLocation != null
                ? new Rectangle(lastFrameLocation, lastFrameSize) : getBounds();
        return WindowPlacement.normalize(bounds);
    }

    public void showMainWindow() {
        if ((getExtendedState() & Frame.ICONIFIED) != 0) {
            setBounds(getNormalWindowBounds());
        }
        WindowPlacement.restore(this);
        if (canRememberWindowBounds()) {
            lastFrameSize = getSize();
            lastFrameLocation = getLocation();
        }
    }

    public JPopupMenu makeSwingPopup() {
        final JPopupMenu popup = new JPopupMenu();

        popupShowHideMenuItem.addActionListener(ae -> {
            if (!isVisible() || (getExtendedState() & Frame.ICONIFIED) != 0) {
                showMainWindow();
            } else {
                if (popup.isVisible()) {
                    popup.setVisible(false);
                }

                setVisible(false);
            }
        });
        popup.add(popupShowHideMenuItem);
        popupOptionenMenuItem.addActionListener(ae -> showOptionsDialog());

        popupOptionenMenuItem.setText(menuItemOptionen.getText());
        popup.add(popupOptionenMenuItem);
        IconManager im = IconManager.getInstance();

        versteckenIcon = im.getIcon("hide");
        zeigenIcon = im.getIcon("applejuice");
        Icon aboutIcon = im.getIcon("about");

        popupOptionenMenuItem.setIcon(im.getIcon("optionen"));
        popupAboutMenuItem.setIcon(aboutIcon);
        popupAboutMenuItem.addActionListener(ae -> showAboutDialog());
        popupAboutMenuItem.setText(menuItemUeber.getText());
        popup.add(popupAboutMenuItem);
        popup.addSeparator();
        popupBeendenMenuItem.setText(menuItemBeenden.getText());
        popupBeendenMenuItem.addActionListener(ae -> closeDialog(null));
        popup.add(popupBeendenMenuItem);
        return popup;
    }

    public void importAjl(File file, String targetDir) {
        try {
            ApplejuiceFassade af = AppleJuiceClient.getAjFassade();
            final StringBuffer returnValues = new StringBuffer();
            boolean somethingAdded = false;

            for (String link : AjlFile.readLinks(file)) {
                String result;

                try {
                    result = af.processLink(link, targetDir);
                } catch (IllegalArgumentException e) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    return;
                }

                if (result.indexOf("ok") == 0) {
                    returnValues.append("'" + link + "' OK\n");
                    somethingAdded = true;
                } else if (result.contains("already downloaded")) {
                    returnValues.append(alreadyLoaded.replaceAll("%s", link) + "\n");
                    somethingAdded = true;
                } else if (result.contains("incorrect link")) {
                    returnValues.append(invalidLink.replaceAll("%s", link) + "\n");
                    somethingAdded = true;
                } else if (result.contains("failure")) {
                    returnValues.append(linkFailure + "\n");
                    somethingAdded = true;
                }
            }

            if (somethingAdded) {
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
        } catch (IOException ex) {
            logger.warn("AJL-Datei konnte nicht importiert werden: " + file, ex);
        }
    }

    public static void showInformation(String information) {
        JOptionPane.showMessageDialog(theApp, information, "appleJuice Client", JOptionPane.OK_OPTION);
    }

    private String getVerbindungsStatusAsString(Information information) {
        switch (information.getVerbindungsStatus()) {

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

    private void checkAndDisplayUpdate() {
        VersionChecker.check();
    }

    public void informWrongPassword() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String nachricht = languageSelector.getFirstAttrbuteByTagName("mainform.msgdlgtext3");

        SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
        closeWithErrormessage(nachricht, true);
    }

    private class TxtFileFilter extends FileFilter {
        public boolean accept(File file) {
            if (!file.isFile()) {
                return true;
            } else {
                String name = file.getName();

                return (name.toLowerCase().endsWith(".ajl"));
            }
        }

        public String getDescription() {
            return "AJL-Dateien";
        }
    }


    private class JCheckBoxLookAndFeelMenuItem extends JCheckBoxMenuItem {
        private final LookAFeel lookAFeel;

        public JCheckBoxLookAndFeelMenuItem(LookAFeel lookAFeelToUse) {
            super(lookAFeelToUse.getName());
            this.lookAFeel = lookAFeelToUse;
            addItemListener(ae -> {
                if (isSelected()) {
                    try {
                        UIManager.setLookAndFeel(lookAFeel.getClassName());
                        SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
                        installClipboardKeys();
                    } catch (Exception ex) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                    }
                }
            });
        }
    }

    private static class iconsetFeelMenuItem extends JCheckBoxMenuItem {
        public iconsetFeelMenuItem(String name) {
            super(name);
            addItemListener(ae -> {
                if (isSelected()) {
                    try {
                        OptionsManagerImpl.getInstance().setIconSetName(name);
                    } catch (Exception ex) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                    }
                }
            });
        }
    }

    private static class soundsetFeelMenuItem extends JCheckBoxMenuItem {
        public soundsetFeelMenuItem(String name) {
            super(name);
            addActionListener(ae -> {
                if (isSelected()) {
                    try {
                        OptionsManagerImpl.getInstance().setSoundSetName(name);
                        SoundPlayer.getInstance().playPreview();
                    } catch (Exception ex) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                    }
                }
            });
        }
    }
}
