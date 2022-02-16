/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client;

import ch.qos.logback.classic.Level;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.VersionChecker;
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
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.109 2009/02/12 13:11:40 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class AppleJuiceClient {
    public static Splash splash = null;
    private static Logger logger;
    private static ApplejuiceFassade ajFassade = null;
    private static CoreConnectionSettingsHolder conn = null;
    private static String rootDirectory = null;

    public static LinkListener linkListener = null;

    /**
     * @return Returns the path of the Client-GUI.
     */
    public static String getPath() {
        return getRootDirectory();
    }

    public static synchronized ApplejuiceFassade getAjFassade() {
        if (ajFassade == null) {
            ConnectionSettings rm = OptionsManagerImpl.getInstance().getRemoteSettings();

            try {
                conn = new CoreConnectionSettingsHolder(rm.getHost(), rm.getXmlPort(), rm.getOldPassword(), false);
                ajFassade = new ApplejuiceFassade(conn);
            } catch (IllegalArgumentException e) {
                logger.error("connection failed", e);
            }
        }

        return ajFassade;
    }

    public static CoreConnectionSettingsHolder getCoreConnectionSettingsHolder() {
        return conn;
    }

    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(AppleJuiceClient.class);

        try {
            Level logLevel = OptionsManagerImpl.getInstance().getLogLevel();
            AppleJuiceClient.setLogLevel(logLevel);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        AppleJuiceClientTG tg = new AppleJuiceClientTG();
        final String[] myargs = args;
        Runnable runnable = () -> AppleJuiceClient.runmain(myargs);

        Thread t = new Thread(tg, runnable, "appleJuiceCoreGUI");

        t.start();
    }

    public static void runmain(String[] args) {
        boolean processLink = false;
        String link = "";
        boolean doubleInstance = false;

        try {
            linkListener = new LinkListener();
        } catch (IOException ex) {
            //bereits ein GUI vorhanden, also GUI schliessen
            doubleInstance = true;
        }

        if (Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_FILE)) {
            Desktop.getDesktop().setOpenFileHandler(new ajlFileHandler());
        }

        if (Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_URI)) {
            Desktop.getDesktop().setOpenURIHandler(new ajfspURIHandler());
        }

        if (args != null && args.length > 0) {
            try {
                for (String curArg : args) {
                    if (curArg.contains("-path=")) {
                        System.setProperty("user.dir", curArg.substring(6));
                        break;
                    }
                }

                boolean hilfeAusgegeben = false;

                for (String curArg : args) {
                    if (curArg.compareTo("-help") == 0) {
                        if (hilfeAusgegeben) {
                            continue;
                        }

                        System.out.println();
                        System.out.println(" -help                       Diese Übersicht.");
                        System.out.println(" -path=<pfad>                Ausführpfad setzen. Alles im GUI ist relativ zu diesem.");
                        System.out.println(" -link=<md5Passwort|link>    ajfsp-Link ans GUI übergeben. Das GUI wird ggf gestartet.");
                        System.out.println();
                        hilfeAusgegeben = true;
                    } else if (curArg.contains("-command=")) {
                        if (linkListener == null) {
                            try {
                                linkListener = new LinkListener();
                            } catch (IOException ex) {
                                //bereits ein GUI vorhanden, also GUI schliessen
                                doubleInstance = true;
                            }
                        }

                        if (doubleInstance) {
                            int PORT = OptionsManagerImpl.getInstance().getLinkListenerPort();
                            String passwort = OptionsManagerImpl.getInstance().getRemoteSettings().getOldPassword();
                            Socket socket = new Socket("localhost", PORT);
                            PrintStream out = new PrintStream(socket.getOutputStream());
                            DataInputStream in = new DataInputStream(socket.getInputStream());

                            out.println(passwort + "|" + curArg);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String line = reader.readLine();

                            System.out.println(line);
                            socket.close();
                            System.exit(1);
                        } else {
                            System.out.println("appleJuice-JavaGUI nicht gestartet");
                            System.exit(1);
                        }
                    } else if (curArg.startsWith("ajfsp://") || (curArg.contains("-link=") && curArg.length() > "-link=".length() + 1)) {
                        link = curArg.startsWith("ajfsp://") ? curArg : curArg.substring(curArg.indexOf("-link=") + "-link=".length());

                        if (doubleInstance) {
                            int PORT = OptionsManagerImpl.getInstance().getLinkListenerPort();
                            String passwort = OptionsManagerImpl.getInstance().getRemoteSettings().getOldPassword();
                            Socket socket = new Socket("localhost", PORT);
                            PrintStream out = new PrintStream(socket.getOutputStream());

                            out.println(passwort + "|" + curArg);
                            socket.close();
                            //war nur Linkprocessing, also GUI schliessen
                            System.exit(1);
                        } else {
                            linkListener.processLink(link, "");
                        }
                    } else if (curArg.endsWith(".ajl")) {
                        logger.info(".ajl Datei argument gefunden: " + curArg);

                        File inputFile = new File(curArg);

                        if (inputFile.exists() && !inputFile.isDirectory()) {
                            new AppleJuiceDialog().importAjl(inputFile, "");
                        } else {
                            logger.info("kann .ajl Datei nicht öffnen: " + curArg);
                        }
                    }
                }
            } catch (IOException ioE) {
                //Keine bisherige GUI-Instanz vorhanden, also GUI oeffnen
                processLink = true;
            } catch (Exception e) {
                System.exit(1);
            }
        }

        if (linkListener == null) {
            try {
                linkListener = new LinkListener();
            } catch (IOException ex) {
                //bereits ein GUI vorhanden, also GUI schliessen
                doubleInstance = true;
            }
        }

        if (doubleInstance) {
            //bereits ein GUI vorhanden, also GUI schliessen
            JOptionPane.showMessageDialog(new Frame(), "Eine Instanz des GUIs ist bereits in Verwendung.", "appleJuice Client", JOptionPane.ERROR_MESSAGE);
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
            String nachricht = "appleJuice-GUI " + AppleJuiceDialog.getVersion() + " wird gestartet...";
            ConnectFrame connectFrame = new ConnectFrame();

            splash = new Splash(connectFrame, IconManager.getInstance().getIcon("splashscreen").getImage(), 0, 100);
            KeyStates ks = new KeyStates();

            splash.addKeyListener(ks);
            splash.setVisible(true);

            splash.setProgress(10, "check system...");

            try {
                if (OptionsManagerImpl.getInstance().isThemesSupported()) {
                    Method method = JFrame.class.getMethod("setDefaultLookAndFeelDecorated",
                            new Class[]{boolean.class});

                    method.invoke(null, new Object[]{Boolean.TRUE});

                    method = JDialog.class.getMethod("setDefaultLookAndFeelDecorated", new Class[]{boolean.class});
                    method.invoke(null, new Object[]{Boolean.TRUE});
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Programmabbruch", e);
                }
            }

            if (logger.isInfoEnabled()) {
                logger.info(nachricht);
            }

            System.out.println(nachricht);

            if (logger.isInfoEnabled()) {
                nachricht = "erkanntes GUI-OS: " + System.getProperty("os.name");
                logger.info(nachricht);
                nachricht = "erkannte Java-Version: " + System.getProperty("java.version");
                logger.info(nachricht);
            }

            String titel = null;
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            QuickConnectionSettingsDialog remoteDialog;

            splash.setProgress(25, "load themes...");
            AppleJuiceDialog.initThemes();

            splash.setProgress(50, "test connection...");
            boolean showDialog = OptionsManagerImpl.getInstance().shouldShowConnectionDialogOnStartup();
            boolean keyDown = ks.isKeyDown(KeyEvent.VK_SHIFT);

            if (!showDialog) {
                showDialog = keyDown;
            }

            boolean firstTry = keyDown ? false : true;
            int erreichbarkeit = 2;

            if (ajFassade == null) {
                getAjFassade();
            }
            while (showDialog || (erreichbarkeit = ajFassade.isCoreAvailable()) != 0) {
                splash.setVisible(false);
                if (!showDialog) {
                    if (erreichbarkeit == 2) {
                        titel = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
                        nachricht = languageSelector.getFirstAttrbuteByTagName("javagui.startup.fehlversuch");
                        SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
                        JOptionPane.showMessageDialog(connectFrame, nachricht, titel, JOptionPane.ERROR_MESSAGE);
                    } else {
                        titel = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
                        nachricht = languageSelector.getFirstAttrbuteByTagName("mainform.msgdlgtext3");
                        SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
                        JOptionPane.showMessageDialog(connectFrame, nachricht, titel, JOptionPane.ERROR_MESSAGE);
                    }
                }

                showDialog = false;
                remoteDialog = new QuickConnectionSettingsDialog(connectFrame);
                if (firstTry && OptionsManagerImpl.getInstance().isErsterStart()) {
                    firstTry = false;
                    remoteDialog.setNieWiederAnzeigen();
                    remoteDialog.pressOK();
                } else {
                    remoteDialog.setVisible(true);
                    if (remoteDialog.getResult() == QuickConnectionSettingsDialog.ABGEBROCHEN) {
                        nachricht = languageSelector.getFirstAttrbuteByTagName("javagui.startup.verbindungsfehler");
                        nachricht = nachricht.replaceFirst("%s", OptionsManagerImpl.getInstance().getRemoteSettings().getHost());
                        JOptionPane.showMessageDialog(connectFrame, nachricht, titel, JOptionPane.OK_OPTION);
                        logger.error(nachricht);
                        System.out.println("Fehler: " + nachricht);
                        System.exit(-1);
                    }
                }

                splash.setVisible(true);
            }

            SoundPlayer.getInstance().playSound(SoundPlayer.ZUGANG_GEWAEHRT);

            splash.setProgress(75, "load GUI...");
            SwingUtilities.invokeLater(() -> {
                final AppleJuiceDialog theApp = new AppleJuiceDialog();

                splash.setProgress(100, "GUI geladen...");
                PositionManager lm = PositionManagerImpl.getInstance();

                if (lm.isLegal()) {
                    theApp.setLocation(lm.getMainXY());
                    theApp.setSize(lm.getMainDimension());
                } else {
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    Dimension screenSize = tk.getScreenSize();
                    Dimension appScreenSize = new Dimension(screenSize.width, screenSize.height);
                    Insets insets = tk.getScreenInsets(theApp.getGraphicsConfiguration());

                    appScreenSize.width -= (insets.left + insets.right);
                    appScreenSize.width = appScreenSize.width / 5 * 4;
                    appScreenSize.height -= (insets.top + insets.bottom);
                    appScreenSize.height = appScreenSize.height / 5 * 4;
                    Point location = new Point((screenSize.width - appScreenSize.width) / 2,
                            (screenSize.height - appScreenSize.height) / 2);

                    lm.setMainXY(location);
                    lm.setMainDimension(appScreenSize);
                    theApp.setSize(appScreenSize);
                    theApp.setLocation(location);
                }

                theApp.setVisible(true);
                String nachricht1 = "appleJuice-GUI gestartet...";

                logger.info(nachricht1);

                System.out.println(nachricht1);
                splash.dispose();
                if (OptionsManagerImpl.getInstance().isErsterStart()) {
                    showConnectionWizard(theApp);
                }

                boolean UpdateInfo = OptionsManagerImpl.getInstance().getUpdateInfo();

                if (UpdateInfo) {
                    VersionChecker.check();
                }
            });

        } catch (Exception e) {
            logger.error("Programmabbruch", e);

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
        Dimension appDimension = wizardDialog.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        wizardDialog.setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
        wizardDialog.setVisible(true);
    }

    public static boolean showConnectionWizard(JDialog dialog, AJSettings ajSettings)
            throws HeadlessException {
        WizardDialog wizardDialog = new WizardDialog(dialog, true, ajSettings);
        Dimension appDimension = wizardDialog.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        wizardDialog.setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
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
