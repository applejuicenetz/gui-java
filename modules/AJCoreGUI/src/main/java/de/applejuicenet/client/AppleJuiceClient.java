/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.gui.DialogLocation;

import ch.qos.logback.classic.Level;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.UpdateChecker;
import de.applejuicenet.client.gui.components.listener.KeyStates;
import de.applejuicenet.client.gui.connect.ConnectFrame;
import de.applejuicenet.client.gui.connect.QuickConnectionSettingsDialog;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.handler.ajfspURIHandler;
import de.applejuicenet.client.gui.handler.ajlFileHandler;
import de.applejuicenet.client.gui.wizard.WizardDialog;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.Splash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.109 2009/02/12 13:11:40 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class AppleJuiceClient {
    public static Splash splash = null;
    private static Logger logger;
    private static ApplejuiceFacade ajFacade = null;
    private static CoreConnectionSettingsHolder conn = null;
    private static String rootDirectory = null;

    public static LinkListener linkListener = null;

    /**
     * @return Returns the path of the Client-GUI.
     */
    public static String getPath() {
        return getRootDirectory();
    }

    public static synchronized ApplejuiceFacade getAjFacade() {
        if (ajFacade == null) {
            ConnectionSettings rm = OptionsManagerImpl.getInstance().getRemoteSettings();

            try {
                conn = new CoreConnectionSettingsHolder(rm.getHost(), rm.getXmlPort(), rm.getOldPassword(), false);
                ajFacade = new ApplejuiceFacade(conn);
            } catch (IllegalArgumentException e) {
                logger.error("connection failed", e);
            }
        }

        return ajFacade;
    }

    public static CoreConnectionSettingsHolder getCoreConnectionSettingsHolder() {
        return conn;
    }

    public static void main(String[] args) {
        waitForRestartParent(args);
        useMacScreenMenuBar();
        ApplicationPaths.configure(args);
        logger = LoggerFactory.getLogger(AppleJuiceClient.class);

        try {
            Level logLevel = OptionsManagerImpl.getInstance().getLogLevel();
            AppleJuiceClient.setLogLevel(logLevel);
        } catch (IllegalStateException e) {
            logger.error("Startup aborted because the settings were not loaded");
            return;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Headless system detected, exiting...");
            System.exit(1);
        }

        AppleJuiceThreadGroup tg = new AppleJuiceThreadGroup();
        // Virtual threads do not belong to a ThreadGroup and would otherwise end up at the default handler
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> AppleJuiceThreadGroup.handle(error));
        final String[] myargs = args;
        Runnable runnable = () -> AppleJuiceClient.runmain(myargs);

        Thread t = new Thread(tg, runnable, "appleJuiceCoreGUI");

        t.start();
    }

    static java.util.List<String> restartCommand(String executable, String[] arguments, long parentPid) {
        java.util.List<String> command = new java.util.ArrayList<>();
        command.add(executable);
        for (String argument : arguments) {
            if (!argument.startsWith("-restart-parent=") && !argument.startsWith("ajfsp://")
                    && !argument.startsWith("web+ajfsp://") && !argument.startsWith("-link=")
                    && !argument.startsWith("-command=") && !argument.toLowerCase(Locale.ROOT).endsWith(".ajl")) {
                command.add(argument);
            }
        }
        command.add("-restart-parent=" + parentPid);
        return command;
    }

    public static void restartGui() throws IOException {
        ProcessHandle.Info process = ProcessHandle.current().info();
        String executable = process.command().orElseThrow(() -> new IOException("GUI launcher unavailable"));
        String[] arguments = process.arguments().orElseThrow(() -> new IOException("GUI launcher arguments unavailable"));
        java.util.List<String> command = restartCommand(executable, arguments, ProcessHandle.current().pid());
        command.removeIf(argument -> argument.startsWith("-path="));
        command.add("-path=" + System.getProperty("user.dir"));
        new ProcessBuilder(command).inheritIO().start();
        AppleJuiceDialog.getApp().shutdownForRestart();
    }

    private static void waitForRestartParent(String[] args) {
        for (String argument : args) {
            if (argument.startsWith("-restart-parent=")) {
                ProcessHandle.of(Long.parseLong(argument.substring("-restart-parent=".length())))
                        .ifPresent(parent -> parent.onExit().join());
            }
        }
    }

    static void useMacScreenMenuBar() {
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }

    public static void runmain(String[] args) {
        boolean processLink = false;
        String link = "";
        boolean doubleInstance = false;
        boolean ajlForwarded = false;
        boolean linkForwarded = false;

        IOException listenerError = null;

        try {
            linkListener = new LinkListener();
        } catch (InstanceLock.AlreadyRunningException ex) {
            logger.info("Another GUI instance is already running");
            doubleInstance = true;
        } catch (IOException ex) {
            logger.error("The link listener could not be started", ex);
            listenerError = ex;
        }

        if (Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_FILE)) {
            Desktop.getDesktop().setOpenFileHandler(new ajlFileHandler());
        }

        if (Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_URI)) {
            Desktop.getDesktop().setOpenURIHandler(new ajfspURIHandler());
        }

        if (args != null && args.length > 0) {
            try {
                boolean helpPrinted = false;

                for (String curArg : args) {
                    if (curArg.compareTo("-help") == 0) {
                        if (helpPrinted) {
                            continue;
                        }

                        System.out.println();
                        System.out.println(" -help                       Show this overview.");
                        System.out.println(" -path=<path>                Set the working path. Everything in the GUI is relative to it.");
                        System.out.println(" -link=<md5Password|link>    Pass an ajfsp link to the GUI. The GUI is started if necessary.");
                        System.out.println();
                        helpPrinted = true;
                    } else if (curArg.startsWith("ajfsp://") || curArg.startsWith("web+ajfsp://") || (curArg.contains("-link=") && curArg.length() > "-link=".length() + 1)) {
                        link = curArg.startsWith("ajfsp://") || curArg.startsWith("web+ajfsp://") ? curArg : curArg.substring(curArg.indexOf("-link=") + "-link=".length());

                        if (doubleInstance) {
                            LinkListener.forwardLink(link);
                            linkForwarded = true;
                        } else {
                            linkListener.processLink(link, "");
                        }
                    } else if (curArg.toLowerCase(Locale.ROOT).endsWith(".ajl")) {
                        logger.info(".ajl file argument found: " + curArg);

                        File inputFile = new File(curArg);

                        if (inputFile.exists() && !inputFile.isDirectory()) {
                            if (doubleInstance) {
                                LinkListener.forwardAjl(inputFile);
                                ajlForwarded = true;
                            } else {
                                linkListener.processAjl(inputFile, "");
                            }
                        } else {
                            logger.info("cannot open .ajl file: " + curArg);
                        }
                    }
                }
            } catch (IOException ioE) {
                //no previous GUI instance exists, so open the GUI
                processLink = true;
            } catch (Exception e) {
                logger.error("Handing over the link or .ajl file failed", e);
                System.exit(1);
            }
        }

        if (listenerError != null) {
            JOptionPane.showMessageDialog(new Frame(),
                    GuiText.text("javagui.instance.failed", listenerError.getMessage()), "appleJuice Client", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        if (doubleInstance && (ajlForwarded || linkForwarded)) {
            System.exit(0);
            return;
        }

        if (doubleInstance) {
            //a GUI already exists, so close this GUI
            JOptionPane.showMessageDialog(new Frame(), GuiText.text("javagui.instance.running"), "appleJuice Client", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        if (processLink) {
            linkListener.processLink(link, "");
        }

        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            try {
                Toolkit xToolkit = Toolkit.getDefaultToolkit();
                Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
                awtAppClassNameField.setAccessible(true);
                awtAppClassNameField.set(xToolkit, "AJCoreGUI");
            } catch (Exception ignored) {
            }
        }

        try {
            String message = "appleJuice-GUI " + AppleJuiceDialog.getVersion() + " is starting...";
            ConnectFrame connectFrame = new ConnectFrame();

            KeyStates ks = new KeyStates();

            SwingUtilities.invokeAndWait(() -> {
                splash = new Splash(connectFrame, IconManager.getInstance().getIcon("splashscreen"));
                splash.addKeyListener(ks);
                splash.setVisible(true);
            });

            if (logger.isInfoEnabled()) {
                logger.info(message);
            }

            if (logger.isInfoEnabled()) {
                message = "detected GUI OS: " + System.getProperty("os.name");
                logger.info(message);
                message = "detected Java version: " + System.getProperty("java.version");
                logger.info(message);
            }

            String title = null;
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            QuickConnectionSettingsDialog remoteDialog;

            AppleJuiceDialog.initLookAndFeel();

            boolean restarting = java.util.Arrays.stream(args).anyMatch(arg -> arg.startsWith("-restart-parent="));
            boolean showDialog = !restarting && OptionsManagerImpl.getInstance().shouldShowConnectionDialogOnStartup();
            boolean keyDown = ks.isKeyDown(KeyEvent.VK_SHIFT);

            if (!showDialog) {
                showDialog = keyDown;
            }

            boolean firstTry = keyDown ? false : true;
            int reachability = 2;

            if (ajFacade == null) {
                getAjFacade();
            }
            while (showDialog || (reachability = ajFacade.isCoreAvailable()) != 0) {
                SwingUtilities.invokeAndWait(() -> splash.setVisible(false));
                if (!showDialog) {
                    if (reachability == 2) {
                        title = languageSelector.getFirstAttributeByTagName("mainform.caption");
                        message = languageSelector.getFirstAttributeByTagName("javagui.startup.connectionfailed");
                        SoundPlayer.getInstance().playSound(SoundPlayer.DENIED);
                        JOptionPane.showMessageDialog(connectFrame, message, title, JOptionPane.ERROR_MESSAGE);
                    } else {
                        title = languageSelector.getFirstAttributeByTagName("mainform.caption");
                        message = languageSelector.getFirstAttributeByTagName("mainform.msgdlgtext3");
                        SoundPlayer.getInstance().playSound(SoundPlayer.DENIED);
                        JOptionPane.showMessageDialog(connectFrame, message, title, JOptionPane.ERROR_MESSAGE);
                    }
                }

                showDialog = false;
                remoteDialog = new QuickConnectionSettingsDialog(connectFrame);
                if (firstTry && OptionsManagerImpl.getInstance().isFirstStart()) {
                    firstTry = false;
                    remoteDialog.setNeverShowAgain();
                    remoteDialog.pressOK();
                } else {
                    remoteDialog.setVisible(true);
                    if (remoteDialog.getResult() == QuickConnectionSettingsDialog.CANCELLED) {
                        message = languageSelector.getFirstAttributeByTagName("javagui.startup.connectionerror");
                        message = message.replaceFirst("%s", OptionsManagerImpl.getInstance().getRemoteSettings().getHost());
                        JOptionPane.showMessageDialog(connectFrame, message, title, JOptionPane.OK_OPTION);
                        logger.error(message);
                        System.exit(-1);
                    }
                }

                SwingUtilities.invokeAndWait(() -> splash.setVisible(true));
            }

            SoundPlayer.getInstance().playSound(SoundPlayer.ACCESS_GRANTED);

            SwingUtilities.invokeLater(() -> {
                final AppleJuiceDialog theApp = new AppleJuiceDialog();

                PositionManager lm = PositionManagerImpl.getInstance();

                Rectangle savedBounds = lm.isLegal() && lm.getMainXY() != null && lm.getMainDimension() != null
                        ? new Rectangle(lm.getMainXY(), lm.getMainDimension()) : null;
                Rectangle restoredBounds = WindowPlacement.normalize(savedBounds);
                if (savedBounds != null && !savedBounds.equals(restoredBounds)) {
                    logger.warn("Repairing saved main window bounds: {} to {}", savedBounds, restoredBounds);
                }
                lm.setMainXY(restoredBounds.getLocation());
                lm.setMainDimension(restoredBounds.getSize());
                theApp.setBounds(restoredBounds);
                theApp.showMainWindow();
                String message1 = "appleJuice GUI started...";

                logger.info(message1);
                splash.dispose();
                if (OptionsManagerImpl.getInstance().isFirstStart()) {
                    showConnectionWizard(theApp);
                }

                boolean UpdateInfo = OptionsManagerImpl.getInstance().getUpdateInfo();

                if (UpdateInfo) {
                    UpdateChecker.check();
                }
            });

        } catch (Exception e) {
            logger.error("Startup aborted", e);

            System.exit(-1);
        }
    }

    private static String getRootDirectory() {
        if (rootDirectory == null) {
            rootDirectory = System.getProperty("user.home") + File.separator + "appleJuice" + File.separator + "gui";
        }

        return rootDirectory;
    }

    public static void showConnectionWizard(JFrame frame) throws HeadlessException {
        WizardDialog wizardDialog = new WizardDialog(frame, true);
        DialogLocation.center(wizardDialog);
        wizardDialog.setVisible(true);
    }

    public static boolean showConnectionWizard(JDialog dialog, AJSettings ajSettings)
            throws HeadlessException {
        WizardDialog wizardDialog = new WizardDialog(dialog, true, ajSettings);
        DialogLocation.center(wizardDialog);
        wizardDialog.setVisible(true);
        return wizardDialog.isRegularClosed();
    }

    public static String getPropertiesPath() {
        String dir = System.getProperty("user.home") + File.separator + "appleJuice";
        File directory = new File(dir);

        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        dir += File.separator + "gui";
        directory = new File(dir);
        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        dir += File.separator + "ajgui.properties";
        return dir;
    }

    public static void setLogLevel(Level logLevel) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(logLevel);
    }
}
