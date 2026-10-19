/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.shared.RefreshIntervals;
import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.Information;
import de.applejuicenet.client.facade.entity.Server;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.listener.DataUpdateListener;
import de.applejuicenet.client.facade.shared.NetworkInfo;
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
import de.applejuicenet.client.shared.LookAndFeelLoader;
import de.applejuicenet.client.shared.SoundPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
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
 * Title: AppleJuice Client-GUI
 * </p>
 * <p>
 * Description: Official GUI for the appleJuice core developed by muhviehstarr
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class AppleJuiceDialog extends JFrame implements LanguageListener, DataUpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(AppleJuiceDialog.class);
    public static boolean rewriteProperties = false;
    private static AppleJuiceDialog theApp;
    private static boolean lookAndFeelInitialized = false;
    private static boolean useTrayIcon = false;
    private static TrayLoader trayLoader = null;
    private Information information = null;
    private RegisterPanel registerPane;
    private JLabel[] statusbar = new JLabel[9];
    private JPanel statusPanel;
    private JMenu languageMenu;
    private JMenu optionsMenu;
    private JMenu themesMenu = null;
    private JMenu iconsetMenu = null;
    private JMenu soundsetMenu = null;
    private JMenu coreMenu;
    private JMenuItem menuItemOptions = new JMenuItem();
    private JMenuItem menuItemFileList = new JMenuItem();
    private JMenuItem menuItemCheckUpdate = new JMenuItem();
    private JMenuItem menuItemCoreExit = new JMenuItem();
    private JMenuItem menuItemAbout = new JMenuItem();
    private JMenuItem menuItemExit = new JMenuItem();
    private JMenuItem popupOptionsMenuItem = new JMenuItem();
    private JMenuItem popupAboutMenuItem = new JMenuItem();
    private JMenuItem popupExitMenuItem = new JMenuItem();
    private JMenuItem popupShowHideMenuItem = new JMenuItem();
    private JMenuItem popupCheckUpdateMenuItem = new JMenuItem();
    private JButton coreHost = new JButton();
    private JButton sound = new JButton();
    private JButton memory = new JButton();
    private String noServer;
    private boolean firstChange = true;
    private MemoryMonitorDialog memoryMonitorDialog;
    private String restartTitle;
    private String restartMessage;
    private boolean automaticPowerdownloadEnabled = false;
    private String title;
    private String confirmation;
    private int desktopHeight;
    private int desktopWidth;
    private boolean maximized = false;
    private Dimension lastFrameSize;
    private Point lastFrameLocation;
    private Icon hideIcon = null;
    private Icon showIcon = null;
    private boolean firewalled = false;
    private String firewallWarning;
    private String alreadyLoaded;
    private String invalidLink;
    private String linkFailure;
    private String dialogTitle;
    private String connectedText;
    private String openConnectionsText;
    private String connectingText;
    private String notConnected;
    private ImageIcon firewallIcon;
    private ImageIcon connectedIcon;
    private ImageIcon notConnectedIcon;
    private JPopupMenu popup;
    private DownloadlinkPanel linkPane = new DownloadlinkPanel();

    public AppleJuiceDialog() {
        super();
        try {
            theApp = this;
            init();
            pack();
            LanguageSelector.getInstance().addLanguageListener(this);
            initKeyStrokes();

        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
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
        LookAFeel preferred = null;
        try {
            preferred = OptionsManagerImpl.getInstance().getDefaultLookAndFeel();
        } catch (Exception | LinkageError exception) {
            logger.warn("Cannot read selected theme; using the default", exception);
        }
        LookAFeel active = LookAndFeelLoader.install(preferred);
        if (preferred == null || !preferred.getClassName().equals(active.getClassName())) {
            try {
                OptionsManagerImpl.getInstance().setDefaultLookAndFeel(active);
            } catch (Exception | LinkageError exception) {
                logger.warn("Cannot save fallback theme", exception);
            }
        }
        installClipboardKeys();
        lookAndFeelInitialized = true;
    }

    public static AppleJuiceDialog getApp() {
        return theApp;
    }

    private void init() throws Exception {
        title = "appleJuice GUI (" + AppleJuiceDialog.getVersion() + ")";
        IconManager im = IconManager.getInstance();

        firewallIcon = im.getIcon("firewall");
        connectedIcon = im.getIcon("serververbunden");
        notConnectedIcon = im.getIcon("serverversuche");

        Image image = im.getIcon("applejuice").getImage();

        setTitle(title);

        setIconImage(image);
        menuItemOptions.setIcon(im.getIcon("optionen"));
        menuItemAbout.setIcon(im.getIcon("info"));
        menuItemCoreExit.setIcon(im.getIcon("skull"));
        menuItemFileList.setIcon(im.getIcon("speichern"));
        menuItemCheckUpdate.setIcon(im.getIcon("update"));
        menuItemExit.setIcon(im.getIcon("abbrechen"));

        setJMenuBar(createMenuBar());

        LanguageSelector languageSelector = LanguageSelector.getInstance();

        registerPane = new RegisterPanel(this);
        languageSelector.fireLanguageChanged();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                if (shouldHideToTray()) {
                    setVisible(false);
                    return;
                }
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

            useTrayIcon = trayLoader.makeTray(title, this, popupShowHideMenuItem, showIcon, hideIcon, popup);
            AppleJuiceClient.getAjFacade().addDataUpdateListener(new DataUpdateListener() {
                private Map<Integer, Integer> states = new HashMap<Integer, Integer>();
                private List<Integer> alreadyNotified = new ArrayList<Integer>();

                public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
                    Map<Integer, Download> downloads = (Map<Integer, Download>) content;

                    for (Download curDownload : downloads.values()) {
                        Integer oldDownloadStatus = states.get(curDownload.getId());

                        if (null == oldDownloadStatus) {
                            // new
                            states.put(curDownload.getId(), curDownload.getStatus());
                        } else if (curDownload.getStatus() == Download.FINISHED && oldDownloadStatus != Download.FINISHED &&
                                !alreadyNotified.contains(curDownload.getId())) {
                            // completed and not yet notified
                            alreadyNotified.add(curDownload.getId());
                            showMessage(GuiText.text("javagui.download.finishedtitle"), GuiText.text("javagui.download.finishedmessage", curDownload.getFilename()));
                        }
                    }

                    ArrayList<Integer> toRemove = new ArrayList<Integer>();

                    for (Integer curKey : states.keySet()) {
                        if (!downloads.containsKey(curKey)) {
                            // download was removed
                            toRemove.add(curKey);
                        }
                    }

                    for (Integer curKey : toRemove) {
                        states.remove(curKey);
                    }
                }
            }, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
        } catch (Throwable e) {
            useTrayIcon = false;
        }

        getContentPane().setLayout(new BorderLayout());
        linkPane.getBtnStartDownload().addActionListener(e -> applyLink());

        getContentPane().add(linkPane, BorderLayout.NORTH);
        getContentPane().add(registerPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());
        statusPanel = panel;

        for (int i = 0; i < statusbar.length; i++) {
            int minChars = i == 2 || i == 7 ? 12 : i == 3 || i == 8 ? 11 : i == 6 ? 5 : 0;
            statusbar[i] = new StatusLabel(minChars);
            statusbar[i].setIconTextGap(4);
            if (i == 2 || i == 3 || i == 7 || i == 8) {
                statusbar[i].setHorizontalTextPosition(SwingConstants.LEFT);
            }
            statusbar[i].setHorizontalAlignment(JLabel.RIGHT);
            java.awt.Color separator = UIManager.getColor("Separator.foreground");
            statusbar[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 1, 0, 0, separator != null ? separator : java.awt.Color.GRAY),
                    BorderFactory.createEmptyBorder(1, 5, 1, 5)));
            statusbar[i].setFont(new java.awt.Font(java.awt.Font.MONOSPACED, 0, 11));
        }

        coreHost.setFont(statusbar[0].getFont());
        coreHost.setMargin(new Insets(2, 6, 2, 6));
        coreHost.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateCoreHost();
        coreHost.addActionListener(e -> {
            var dialog = new de.applejuicenet.client.gui.connect.QuickConnectionSettingsDialog(this, true);
            dialog.setVisible(true);
            dialog.dispose();
            updateCoreHost();
        });
        AppleJuiceClient.getCoreConnectionSettingsHolder().addListener((item, oldValue, newValue) ->
                SwingUtilities.invokeLater(this::updateCoreHost));

        memory.setIcon(IconManager.getInstance().getIcon("mmonitor"));
        memory.addActionListener(ae -> {
            if (memoryMonitorDialog == null) {
                memoryMonitorDialog = new MemoryMonitorDialog(AppleJuiceDialog.this);
            }

            if (!memoryMonitorDialog.isVisible()) {
                DialogLocation.center(memoryMonitorDialog);
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
        panel.add(coreHost, constraints);
        constraints.gridx = 1;
        panel.add(statusbar[0], constraints);
        constraints.gridx = 2;
        statusbar[6].setIcon(StatusIcons.connections());
        panel.add(statusbar[6], constraints);
        constraints.gridx = 3;
        constraints.weightx = 1;
        statusbar[1].setHorizontalAlignment(JLabel.LEFT);
        statusbar[1].setMinimumSize(new java.awt.Dimension(0, 0));
        statusbar[1].setPreferredSize(new java.awt.Dimension(150, 1));
        statusbar[1].setIcon(StatusIcons.server());
        panel.add(statusbar[1], constraints);
        constraints.weightx = 0;
        constraints.gridx = 4;
        statusbar[2].setIcon(StatusIcons.speedDown());
        panel.add(statusbar[2], constraints);
        constraints.gridx = 5;
        statusbar[7].setIcon(StatusIcons.speedUp());
        panel.add(statusbar[7], constraints);
        constraints.gridx = 6;
        statusbar[3].setIcon(StatusIcons.totalDown());
        panel.add(statusbar[3], constraints);
        constraints.gridx = 7;
        statusbar[8].setIcon(StatusIcons.totalUp());
        panel.add(statusbar[8], constraints);
        constraints.gridx = 8;
        statusbar[4].setIcon(StatusIcons.globe());
        panel.add(statusbar[4], constraints);
        constraints.gridx = 9;
        statusbar[5].setIcon(StatusIcons.coin());
        statusbar[5].setToolTipText(GuiText.text("javagui.mainform.credits.hint"));
        panel.add(statusbar[5], constraints);
        constraints.gridx = 10;
        panel.add(memory, constraints);
        constraints.gridx = 11;
        panel.add(sound, constraints);
        getContentPane().add(panel, BorderLayout.SOUTH);
        AjfspTransferHandler.install(getRootPane(), this::applyLink);

        //set tooltips
        ToolTipManager.sharedInstance().setInitialDelay(1);
        ToolTipManager.sharedInstance().setDismissDelay(50000);
        LanguageSelector.getInstance().addLanguageListener(linkPane);
        fireLanguageChanged();
        ApplejuiceFacade dm = AppleJuiceClient.getAjFacade();

        dm.addDataUpdateListener(this, DATALISTENER_TYPE.INFORMATION_CHANGED);
        dm.addDataUpdateListener(this, DATALISTENER_TYPE.NETINFO_CHANGED);
        dm.addDataUpdateListener(this, DATALISTENER_TYPE.SERVER_CHANGED);

        // saved refresh intervals apply from the first poll on
        dm.setUpdateInterval(OptionsManagerImpl.getInstance().getCorePollIntervalMs());
        RefreshIntervals.setPartlistRefreshMs(OptionsManagerImpl.getInstance().getPartlistRefreshIntervalMs());

        try {
            dm.startXMLCheck();

        }catch (RuntimeException e) {
            String msg = String.format("Core Version %s required!", ApplejuiceFacade.MIN_NEEDED_CORE_VERSION);
            AppleJuiceDialog.closeWithErrormessage(msg, false);
            System.exit(1);
        }
    }

    public static synchronized void showMessage(String caption, String message) {
        if (null != trayLoader) {
            trayLoader.showBalloon(caption, message);
        }
    }

    protected void applyLink() {
        if (linkPane.isLinkInvalid()) {
            return;
        }

        final java.util.List<String> links = DownloadlinkPanel.splitLinks(
                linkPane.getTxtDownloadLink().getText().replace("%7C", "|"));
        if (!links.isEmpty()) {
            linkPane.getTxtDownloadLink().setText("");
            links.forEach(this::applyLink);
        }
    }

    private void applyLink(String link) {
        Object sel = linkPane.getCmbTargetDir().getSelectedItem();
        String tmp;

        if (sel != null) {
            tmp = (String) sel;
        } else {
            tmp = "";
        }

        final String targetDir = tmp;

        if (link.length() != 0) {
            Thread.ofVirtual().name("ProcessLink").start(() -> {
                try {
                    final String result = AppleJuiceClient.getAjFacade().processLink(link, targetDir);

                    SoundPlayer.getInstance().playSound(SoundPlayer.LOADING);
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
                                JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message, dialogTitle, JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    }
                } catch (IllegalArgumentException e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                }
            });

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

    private static void saveSettingsAction() {
        try {
            String languageText = LanguageSelector.getInstance().getFirstAttributeByTagName("Languageinfo.name");

            OptionsManagerImpl.getInstance().setLanguage(languageText);
            Rectangle bounds = AppleJuiceDialog.getApp().getNormalWindowBounds();
            PositionManager pm = PositionManagerImpl.getInstance();

            pm.setMainXY(bounds.getLocation());
            pm.setMainDimension(bounds.getSize());
            pm.save();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public void informAutomaticPowerdownloadEnabled(boolean enabled) {
        if (enabled != automaticPowerdownloadEnabled) {
            automaticPowerdownloadEnabled = enabled;
            setTitle(title);
            repaint();
        }
    }

    public void shutdownForRestart() {
        saveSettingsAction();
        AppleJuiceClient.getAjFacade().stopXMLCheck();
        setVisible(false);
        if (trayLoader != null) trayLoader.close();
        System.exit(0);
    }

    private boolean shouldHideToTray() {
        return useTrayIcon && trayLoader != null && OptionsManagerImpl.getInstance().shouldCloseToTray();
    }

    private void closeDialog(WindowEvent evt) {
        AppleJuiceClient.getAjFacade().stopXMLCheck();
        if (rewriteProperties) {
            PropertiesManager.restoreProperties();
        }

        String message = "appleJuice GUI is shutting down...";

        logger.info(message);
        if (!rewriteProperties) {
            saveSettingsAction();
        }

        setVisible(false);
        if (trayLoader != null) trayLoader.close();

        System.exit(0);
    }

    public static void closeWithErrormessage(String error, boolean saveSettings) {
        JOptionPane.showMessageDialog(theApp, error, "appleJuice Client", JOptionPane.OK_OPTION);
        if (rewriteProperties) {
            PropertiesManager.restoreProperties();
        } else {
            AppleJuiceClient.getAjFacade().stopXMLCheck();
        }

        String message = "appleJuice GUI is shutting down...";

        logger.info(message);
        if (saveSettings && !rewriteProperties) {
            saveSettingsAction();
        }

        logger.error("Fatal error: {}", error);

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
                logger.info("The folder " + path + " for the language selection properties files does not exist." + "\r\nappleJuice will exit.");

                closeWithErrormessage("The folder " + path + " for the language selection properties files does not exist." +
                        "\r\nappleJuice will exit.", false);
            }

            String[] tempList = languagePath.list();
            HashSet<String> languageFiles = new HashSet<String>();

            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].contains(".properties")) {
                    languageFiles.add(tempList[i]);
                }
            }

            if (languageFiles.size() == 0) {
                logger.info("There are no properties files for the language selection in the folder " + path + "." +
                        "\r\nappleJuice will exit.");

                closeWithErrormessage("There are no properties files for the language selection in the folder " + path + "." +
                        "\r\nappleJuice will exit.", false);
            }

            JMenuBar menuBar = new JMenuBar();

            optionsMenu = new JMenu();
            menuItemOptions.addActionListener(e -> showOptionsDialog());
            menuItemOptions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            menuItemFileList.addActionListener(e -> importFileList());
            menuItemCheckUpdate.addActionListener(e -> checkAndDisplayUpdate());
            menuItemCoreExit.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.getApp(), confirmation, "appleJuice Client",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    Thread.ofVirtual().name("ShutdownCore").start(() -> AppleJuiceClient.getAjFacade().shutdownCore());

                }
            });
            menuItemAbout.addActionListener(e -> showAboutDialog());
            menuItemExit.addActionListener(e -> closeDialog(null));
            optionsMenu.add(menuItemOptions);
            optionsMenu.add(menuItemFileList);
            optionsMenu.add(menuItemCheckUpdate);
            optionsMenu.add(menuItemAbout);
            optionsMenu.add(menuItemExit);
            menuBar.add(optionsMenu);

            languageMenu = new JMenu();
            menuBar.add(languageMenu);
            ButtonGroup lafGroup = new ButtonGroup();

            for (String curLanguageFile : languageFiles) {
                String languageText;
                try {
                    languageText = LanguageSelector.readLanguageName(new File(path, curLanguageFile));
                } catch (IOException | java.lang.IllegalArgumentException e) {
                    logger.warn("Language file {} is not readable", curLanguageFile, e);
                    continue;
                }
                if (languageText.isBlank()) {
                    continue;
                }
                JCheckBoxMenuItem rb = new JCheckBoxMenuItem(languageText);

                if (OptionsManagerImpl.getInstance().getLanguage().equalsIgnoreCase(languageText)) {
                    rb.setSelected(true);
                }

                String flagFile = curLanguageFile.substring(0, curLanguageFile.length() - ".properties".length()) + ".gif";
                rb.setIcon(new ImageIcon(new File(languagePath, flagFile).getAbsolutePath()));

                languageMenu.add(rb);
                rb.addItemListener(ae -> {
                    JCheckBoxMenuItem rb2 = (JCheckBoxMenuItem) ae.getSource();

                    if (rb2.isSelected()) {
                        String path1 = System.getProperty("user.dir") + File.separator + "language" + File.separator;
                        String fileName = path1 + rb2.getText().toLowerCase() + ".properties";

                        LanguageSelector.getInstance(fileName);
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
            coreMenu.add(menuItemCoreExit);
            menuBar.add(coreMenu);
            coreMenu.setText(GuiText.text("javagui.mainform.core"));
            return menuBar;
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            return null;
        }
    }

    public void updateExternalIp() {
        if (information == null) {
            return;
        }
        String ip = information.getExternalIP();

        if (ip != null && OptionsManagerImpl.getInstance().shouldMaskIpInStatusbar()) {
            ip = ip.replaceFirst("^(\\d{1,3}\\.\\d{1,3})\\.\\d{1,3}\\.\\d{1,3}$", "$1.xxx.xxx");
        }
        statusbar[4].setText(ip);
        statusbar[4].setToolTipText(ip != null && !ip.equals(information.getExternalIP()) ? information.getExternalIP() : null);
    }

    private void showOptionsDialog() {
        OptionsDialog od = new OptionsDialog(getApp());
        DialogLocation.center(od);
        od.setVisible(true);
    }

    private void showAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(getApp(), true);
        DialogLocation.center(aboutDialog);
        aboutDialog.setVisible(true);
    }

    private void changeIconOrSoundSet() {
        int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.this, restartMessage, "appleJuice Client", JOptionPane.YES_NO_OPTION);

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
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public void setTitle(String newTitle) {
        if (!automaticPowerdownloadEnabled) {
            super.setTitle(newTitle);
        } else {
            super.setTitle(newTitle + " - Autopilot");
        }

        title = newTitle;
    }

    private void importFileList() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new TxtFileFilter());
        fileChooser.setDialogTitle(menuItemFileList.getText());
        fileChooser.setMultiSelectionEnabled(false);
        int i = fileChooser.showOpenDialog(this);

        if (i == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();

            if (file.isFile()) {
                String[] dirs = AppleJuiceClient.getAjFacade().getCurrentIncomingDirs();
                IncomingDirSelectionDialog incomingDirSelectionDialog = new IncomingDirSelectionDialog(AppleJuiceDialog.getApp(), dirs,
                        null);

                incomingDirSelectionDialog.setVisible(true);
                String directory = incomingDirSelectionDialog.getSelectedIncomingDir();

                if (directory != null) {
                    directory = directory.trim();
                    if (directory.indexOf(File.separator) == 0 || directory.indexOf(ApplejuiceFacade.separator) == 0) {
                        directory = directory.substring(1);
                    }
                } else {
                    directory = "";
                }

                final String targetDir = directory;

                Thread.ofVirtual().name("AJL-Import").start(() -> importAjl(file, targetDir));
            }
        }
    }

    static String getCoreHostText(String host) {
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host)
                ? "localhost" : host;
    }

    private void updateCoreHost() {
        coreHost.setText(getCoreHostText(AppleJuiceClient.getCoreConnectionSettingsHolder().getCoreHost()));
        coreHost.setToolTipText(GuiText.text("javagui.mainform.switchcore.hint"));
    }

    public void fireLanguageChanged() {
        try {
            updateCoreHost();
            statusbar[2].setToolTipText(GuiText.text("javagui.mainform.speeddown.hint"));
            statusbar[7].setToolTipText(GuiText.text("javagui.mainform.speedup.hint"));
            statusbar[3].setToolTipText(GuiText.text("javagui.mainform.totaldown.hint"));
            statusbar[8].setToolTipText(GuiText.text("javagui.mainform.totalup.hint"));
            LanguageSelector languageSelector = LanguageSelector.getInstance();

            connectedText = languageSelector.getFirstAttributeByTagName("javagui.mainform.connected");
            connectingText = languageSelector.getFirstAttributeByTagName("javagui.mainform.connecting");
            notConnected = languageSelector.getFirstAttributeByTagName("javagui.mainform.notconnected");
            openConnectionsText = languageSelector.getFirstAttributeByTagName("mainform.status.status0");
            updateConnectionStatus();
            noServer = languageSelector.getFirstAttributeByTagName("javagui.mainform.noserver");
            restartTitle = languageSelector.getFirstAttributeByTagName("mainform.caption");
            restartMessage = languageSelector.getFirstAttributeByTagName("javagui.mainform.restartmessage");
            languageMenu.setText(languageSelector.getFirstAttributeByTagName("einstform.languagesheet.caption"));
            menuItemOptions.setText(languageSelector.getFirstAttributeByTagName("mainform.optbtn.caption"));
            menuItemOptions.setToolTipText(null);
            menuItemCoreExit.setText(languageSelector.getFirstAttributeByTagName("javagui.menu.exitcore"));
            menuItemCoreExit.setToolTipText(null);
            menuItemAbout.setText(languageSelector.getFirstAttributeByTagName("mainform.aboutbtn.caption"));
            menuItemExit.setText(languageSelector.getFirstAttributeByTagName("javagui.menu.exit"));
            menuItemCheckUpdate.setText(languageSelector.getFirstAttributeByTagName("mainform.checkupdate.caption"));
            menuItemCheckUpdate.setToolTipText(null);
            optionsMenu.setText(languageSelector.getFirstAttributeByTagName("javagui.menu.extras"));
            menuItemFileList.setText(languageSelector.getFirstAttributeByTagName("javagui.menu.filelist"));
            menuItemFileList.setToolTipText(null);
            themesMenu.setText(languageSelector.getFirstAttributeByTagName("javagui.menu.themes"));
            iconsetMenu.setText(languageSelector.getFirstAttributeByTagName("javagui.menu.icons"));
            soundsetMenu.setText(languageSelector.getFirstAttributeByTagName("javagui.menu.sound"));
            confirmation = languageSelector.getFirstAttributeByTagName("javagui.menu.confirmation");

            firewallWarning = languageSelector.getFirstAttributeByTagName("mainform.firewallwarning.caption");
            alreadyLoaded = languageSelector.getFirstAttributeByTagName("javagui.downloadform.alreadyloaded");
            invalidLink = languageSelector.getFirstAttributeByTagName("javagui.downloadform.invalidlink");
            linkFailure = languageSelector.getFirstAttributeByTagName("javagui.downloadform.otherlinkerrorshort");
            dialogTitle = languageSelector.getFirstAttributeByTagName("mainform.caption");
            updateConnectionTooltip();
            statusbar[6].setToolTipText(GuiText.text("javagui.mainform.connections.hint"));
            if (useTrayIcon && null != trayLoader) {
                trayLoader.setHideText(languageSelector.getFirstAttributeByTagName("javagui.menu.hide"));
                trayLoader.setShowText(languageSelector.getFirstAttributeByTagName("javagui.menu.show"));
                popupAboutMenuItem.setText(menuItemAbout.getText());
                popupAboutMenuItem.setToolTipText(menuItemAbout.getToolTipText());
                popupOptionsMenuItem.setText(menuItemOptions.getText());
                popupOptionsMenuItem.setToolTipText(menuItemOptions.getToolTipText());
                popupExitMenuItem.setText(menuItemExit.getText());
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
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
                        updateConnectionTooltip();

                        updateFirewall();
                    }
                } catch (Exception e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                }
            });
        } else if (type == DATALISTENER_TYPE.SERVER_CHANGED) {
            SwingUtilities.invokeLater(this::updateServerStatus);
        } else if (type == DATALISTENER_TYPE.INFORMATION_CHANGED) {
            SwingUtilities.invokeLater(() -> {
                try {
                    information = (Information) content;
                    updateConnectionStatus();
                    updateServerStatus();

                    statusbar[2].setText(information.getDownAsString().toString());
                    statusbar[7].setText(information.getUpAsString().toString());
                    statusbar[3].setText(information.getSessionDownAsString().toString());
                    statusbar[8].setText(information.getSessionUpAsString().toString());
                    updateExternalIp();
                    statusbar[5].setText(information.getCreditsAsString().strip().replaceFirst("^[^:]*:\\s*", ""));
                    updateFirewall();
                    ensureStatusbarFits();
                } catch (Exception e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                }
            });
        }
    }

    private void ensureStatusbarFits() {
        if (statusPanel == null || maximized || getExtendedState() != Frame.NORMAL) {
            return;
        }
        Insets insets = getInsets();
        int needed = statusPanel.getPreferredSize().width + insets.left + insets.right;
        if (getWidth() < needed) {
            setSize(needed, getHeight());
        }
    }

    private void updateConnectionTooltip() {
        statusbar[0].setToolTipText(firewalled ? firewallWarning
                : GuiText.text("javagui.mainform.connectionstatus.hint"));
    }

    private void updateConnectionStatus() {
        if (information != null) {
            statusbar[0].setText(getConnectionStatusAsString(information));
            statusbar[6].setText(getConnectionCountText(openConnectionsText, information.getOpenConnections()));
        }
    }

    static String getConnectionCountText(String connectionsCaption, long openConnections) {
        return connectionsCaption.replace("%d", Long.toString(openConnections));
    }

    private void updateServerStatus() {
        if (information != null) {
            boolean connected = information.getConnectionStatus() != Information.NOT_CONNECTED;
            statusbar[1].setText(connected ? getServerDisplayName(information) : noServer);
            statusbar[1].setToolTipText(connected ? getServerAddress(information) : null);
        }
    }

    static String getServerAddress(Information information) {
        String address = information.getServerName();
        Server server = information.getServer();
        if (address == null || address.isBlank()) {
            address = server != null ? server.getHost() + ":" + server.getPort() : "";
        }
        return address;
    }

    static String getServerDisplayName(Information information) {
        Server server = information.getServer();
        String name = server != null ? server.getName() : null;
        return name == null || name.isBlank() ? getServerAddress(information) : name.strip();
    }

    protected void updateFirewall() {
        if (information != null && information.getConnectionStatus() != Information.CONNECTED) {
            statusbar[0].setIcon(notConnectedIcon);
        } else {
            if (firewalled) {
                statusbar[0].setIcon(firewallIcon);
            } else {
                statusbar[0].setIcon(connectedIcon);
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
        popupOptionsMenuItem.addActionListener(ae -> showOptionsDialog());

        popupOptionsMenuItem.setText(menuItemOptions.getText());
        popup.add(popupOptionsMenuItem);
        IconManager im = IconManager.getInstance();

        hideIcon = im.getIcon("hide");
        showIcon = im.getIcon("applejuice");
        Icon aboutIcon = im.getIcon("about");

        popupOptionsMenuItem.setIcon(im.getIcon("optionen"));
        popupAboutMenuItem.setIcon(aboutIcon);
        popupAboutMenuItem.addActionListener(ae -> showAboutDialog());
        popupAboutMenuItem.setText(menuItemAbout.getText());
        popup.add(popupAboutMenuItem);
        popup.addSeparator();
        popupExitMenuItem.setText(menuItemExit.getText());
        popupExitMenuItem.addActionListener(ae -> closeDialog(null));
        popup.add(popupExitMenuItem);
        return popup;
    }

    public void importAjl(File file, String targetDir) {
        try {
            ApplejuiceFacade af = AppleJuiceClient.getAjFacade();
            final StringBuilder returnValues = new StringBuilder();
            boolean somethingAdded = false;

            for (String link : AjlFile.readLinks(file)) {
                String result;

                try {
                    result = af.processLink(link, targetDir);
                } catch (IllegalArgumentException e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
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
                    textArea.setBackground(new JLabel().getBackground());
                    textArea.setText(returnValues.toString());
                    JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), new JScrollPane(textArea),
                            dialogTitle,
                            JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                });
            }
        } catch (IOException ex) {
            logger.warn("Could not import AJL file: " + file, ex);
        }
    }

    public static void showInformation(String information) {
        JOptionPane.showMessageDialog(theApp, information, "appleJuice Client", JOptionPane.OK_OPTION);
    }

    private String getConnectionStatusAsString(Information information) {
        switch (information.getConnectionStatus()) {

            case Information.CONNECTED:
                return connectedText;

            case Information.NOT_CONNECTED:
                return notConnected;

            case Information.TRYING_TO_CONNECT:
                return connectingText;

            default:
                return "";
        }
    }

    private void checkAndDisplayUpdate() {
        UpdateChecker.check(false);
    }

    public void informWrongPassword() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String message = languageSelector.getFirstAttributeByTagName("mainform.msgdlgtext3");

        SoundPlayer.getInstance().playSound(SoundPlayer.DENIED);
        closeWithErrormessage(message, true);
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
            return GuiText.text("javagui.filefilter.ajl");
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
                        logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
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
                        logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
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
                        logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
                    }
                }
            });
        }
    }
}
