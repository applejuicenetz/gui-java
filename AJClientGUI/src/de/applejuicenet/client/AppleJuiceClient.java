package de.applejuicenet.client;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.UpdateInformationDialog;
import de.applejuicenet.client.gui.components.listener.KeyStates;
import de.applejuicenet.client.gui.connect.ConnectFrame;
import de.applejuicenet.client.gui.connect.QuickConnectionSettingsDialog;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.LinkListener;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.wizard.WizardDialog;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.Splash;
import de.applejuicenet.client.shared.WebsiteContentLoader;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.84 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class AppleJuiceClient {
    private static Logger logger;
    private static String fileAppenderPath;
    private static HTMLLayout layout;
    public static Splash splash = null;

    public static HTMLLayout getLoggerHtmlLayout() {
        return layout;
    }

    public static String getLoggerFileAppenderPath() {
        return fileAppenderPath;
    }

    public static void main(String[] args) {
        AppleJuiceClientTG tg = new AppleJuiceClientTG();
        final String[] myargs = args;
        Runnable runnable = new Runnable() {
            public void run() {
                AppleJuiceClient.runmain(myargs);
            }
        };
        Thread t = new Thread(tg, runnable, "appleJuiceCoreGUI");
        t.start();
    }

    public static void runmain(String[] args) {
        String javaVersion = System.getProperty("java.version");
        StringBuffer version = new StringBuffer(javaVersion);
        for (int i = version.length() - 1; i >= 0; i--) {
            if (version.charAt(i) == '.') {
                version.deleteCharAt(i);
            }
        }
        if (javaVersion.length() > 2) {
            javaVersion = javaVersion.substring(0, 2);
        }
        boolean gueltig = false;
        try {
            int versionsNr = Integer.parseInt(version.toString().substring(0, 2));
            if (versionsNr >= 14) {
                gueltig = true;
            }
        }
        catch (Exception e) {
            ;
            //nix zu tun
        }
        if (!gueltig) {
            JOptionPane.showMessageDialog(new Frame(),
                "Es wird mindestens JRE 1.4 benoetigt!", "appleJuice Client",
                                          JOptionPane.ERROR_MESSAGE);
        }

        boolean processLink = false;
        String link = "";
        boolean doubleInstance = false;
        try {
            new LinkListener();
        }
        catch (IOException ex) {
            //bereits ein GUI vorhanden, also GUI schliessen
            doubleInstance = true;
        }
        if (args != null && args.length > 0) {
            try {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].indexOf("-path=") != -1) {
                        System.setProperty("user.dir", args[i].substring(6));
                        break;
                    }
                }
                boolean hilfeAusgegeben = false;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].compareTo("-help") == 0) {
                        if (hilfeAusgegeben) {
                            continue;
                        }
                        System.out.println();
                        System.out.println(
                            " -help                       Diese Uebersicht.");
                        System.out.println(" -path=<pfad>                Ausfuehrpfad setzen. Alles im GUI ist relativ zu diesem.");
                        System.out.println(
                            " -link=<md5Passwort|link>    ajfsp-Link ans GUI uebergeben. " +
                            " Das GUI wird ggf gestartet.");
                        System.out.println();
                        hilfeAusgegeben = true;
                    }
                    else if (args[i].indexOf("-command=") != -1) {
                        if (doubleInstance){
                            int PORT = OptionsManagerImpl.getInstance().
                                getLinkListenerPort();
                            String passwort = OptionsManagerImpl.getInstance().
                                getRemoteSettings().getOldPassword();
                            Socket socket = new Socket("localhost", PORT);
                            PrintStream out = new PrintStream(socket.
                                getOutputStream());
                            DataInputStream in = new DataInputStream(socket.
                                getInputStream());
                            out.println(passwort + "|" + args[i]);
                            BufferedReader reader = new BufferedReader(new
                                InputStreamReader(in));
                            String line = reader.readLine();
                            System.out.println(line);
                            socket.close();
                            System.exit(1);
                        }
                        else{
                            System.out.println("appleJuice-JavaGUI nicht gestartet");
                            System.exit(1);
                        }
                    }
                    else if (args[i].indexOf("-link=") != -1) {
                        if (doubleInstance){
                            int PORT = OptionsManagerImpl.getInstance().
                                getLinkListenerPort();
                            String passwort = OptionsManagerImpl.getInstance().
                                getRemoteSettings().getOldPassword();
                            Socket socket = new Socket("localhost", PORT);
                            PrintStream out = new PrintStream(socket.
                                getOutputStream());
                            out.println(passwort + "|" + args[i]);
                            socket.close();
                            //war nur Linkprocessing, also GUI schliessen
                            System.exit(1);
                        }
                        else{
                            ApplejuiceFassade.getInstance().processLink(link);
                        }
                    }
                }
            }
            catch (IOException ioE) {
                //Keine bisherige GUI-Instanz vorhanden, also GUI oeffnen
                processLink = true;
            }
            catch (Exception e) {
                System.exit(1);
            }
        }
        if (doubleInstance) {
            //bereits ein GUI vorhanden, also GUI schliessen
            JOptionPane.showMessageDialog(new Frame(),
                "Eine Instanz des GUIs ist bereits in Verwendung.",
                "appleJuice Client",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        Logger rootLogger = Logger.getRootLogger();
        logger = Logger.getLogger(AppleJuiceClient.class.getName());

        String datum = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date(
            System.currentTimeMillis()));
        String dateiName;
        dateiName = datum + ".html";
        layout = new HTMLLayout();
        layout.setTitle("appleJuice-Core-GUI-Log " + datum);
        layout.setLocationInfo(true);
        Level logLevel = OptionsManagerImpl.getInstance().getLogLevel();
        try {
            rootLogger.addAppender(new ConsoleAppender());
            String path;
            if (System.getProperty("os.name").toLowerCase().indexOf("windows")==-1) {
                path = System.getProperty("user.home") + File.separator +
                    "appleJuice" + File.separator +
                    "gui" + File.separator + "logs";
            }
            else {
                path = System.getProperty("user.dir") + File.separator +
                    "logs";
            }
            File aFile = new File(path);
            if (!aFile.exists()) {
                aFile.mkdir();
            }
            fileAppenderPath = path + File.separator + dateiName;
            if (logLevel != Level.OFF) {
                FileAppender fileAppender = new FileAppender(layout,
                    fileAppenderPath);
                rootLogger.removeAllAppenders();
                rootLogger.addAppender(fileAppender);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        rootLogger.setLevel(logLevel);

        try {
            String nachricht = "appleJuice-Core-GUI Version " +
                ApplejuiceFassade.GUI_VERSION + " wird gestartet...";
            ConnectFrame connectFrame = new ConnectFrame();
            splash = new Splash(connectFrame,
                                IconManager.getInstance().getIcon(
                "splashscreen").getImage(), 0, 100);
            KeyStates ks = new KeyStates();
            splash.addKeyListener(ks);
            splash.setVisible(true);
            try {
                if (OptionsManagerImpl.getInstance().isThemesSupported()) {
                    java.lang.reflect.Method method = JFrame.class.
                        getMethod("setDefaultLookAndFeelDecorated",
                                  new Class[] {boolean.class});
                    method.invoke(null, new Object[] {Boolean.TRUE});

                    method = JDialog.class.
                        getMethod("setDefaultLookAndFeelDecorated",
                                  new Class[] {boolean.class});
                    method.invoke(null, new Object[] {Boolean.TRUE});
                }
            }
            catch (Exception e) {
                if (logger.isEnabledFor(Level.FATAL)) {
                    logger.fatal("Programmabbruch", e);
                }
            }
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info(nachricht);
            }
            System.out.println(nachricht);
            if (logger.isEnabledFor(Level.INFO)) {
                nachricht = "erkanntes GUI-OS: " + System.getProperty("os.name");
                logger.info(nachricht);
                nachricht = "erkannte Java-Version: " + System.getProperty("java.version");
                logger.info(nachricht);
            }
            String titel = null;
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            QuickConnectionSettingsDialog remoteDialog = null;
            splash.setProgress(5, "Lade Themes...");
            AppleJuiceDialog.initThemes();
            splash.setProgress(10, "Teste Verbindung...");
            boolean showDialog = OptionsManagerImpl.getInstance().
                shouldShowConnectionDialogOnStartup();
            boolean keyDown = ks.isKeyDown(KeyEvent.VK_SHIFT);
            if (!showDialog) {
                showDialog = keyDown;
            }
            ApplejuiceFassade applejuiceFassade = ApplejuiceFassade.getInstance();
            boolean firstTry = keyDown ? false : true;
            while (showDialog || !applejuiceFassade.istCoreErreichbar()) {
                splash.setVisible(false);
                if (!showDialog) {
                    titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                        getFirstAttrbuteByTagName(".root.mainform.caption"));
                    nachricht = ZeichenErsetzer.korrigiereUmlaute(
                        languageSelector.
                        getFirstAttrbuteByTagName(".root.javagui.startup.fehlversuch"));
                    SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
                    JOptionPane.showMessageDialog(connectFrame, nachricht,
                                                  titel,
                                                  JOptionPane.ERROR_MESSAGE);
                }
                showDialog = false;
                remoteDialog = new QuickConnectionSettingsDialog(connectFrame);
                if (firstTry && OptionsManagerImpl.getInstance().isErsterStart()){
                   	firstTry = false;
                   	remoteDialog.setNieWiederAnzeigen();
                   	remoteDialog.pressOK();
                }
                else{
	                remoteDialog.setVisible(true);
	                if (remoteDialog.getResult() ==
	                    QuickConnectionSettingsDialog.ABGEBROCHEN) {
	                    nachricht = ZeichenErsetzer.korrigiereUmlaute(
	                        languageSelector.
	                        getFirstAttrbuteByTagName(".root.javagui.startup.verbindungsfehler"));
	                    nachricht = nachricht.replaceFirst("%s",
	                        OptionsManagerImpl.getInstance().
	                        getRemoteSettings().
	                        getHost());
	                    JOptionPane.showMessageDialog(connectFrame, nachricht,
	                                                  titel,
	                                                  JOptionPane.OK_OPTION);
	                    logger.fatal(nachricht);
	                    System.out.println("Fehler: " + nachricht);
	                    System.exit( -1 );
	                }
                }
                splash.setVisible(true);
            }
            SoundPlayer.getInstance().playSound(SoundPlayer.ZUGANG_GEWAEHRT);
            PositionManager lm = PositionManagerImpl.getInstance();
            splash.setProgress(20, "Lade Hauptdialog...");
            final AppleJuiceDialog theApp = new AppleJuiceDialog();
            splash.setProgress(100, "GUI geladen...");
            if (lm.isLegal()) {
                theApp.setLocation(lm.getMainXY());
                theApp.setSize(lm.getMainDimension());
            }
            else {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension screenSize = tk.getScreenSize();
                Dimension appScreenSize = new Dimension(screenSize.width,
                    screenSize.height);
                Insets insets = tk.getScreenInsets(theApp.
                    getGraphicsConfiguration());
                appScreenSize.width -= (insets.left + insets.right);
                appScreenSize.width = appScreenSize.width / 5 * 4;
                appScreenSize.height -= (insets.top + insets.bottom);
                appScreenSize.height = appScreenSize.height / 5 * 4;
                Point location = new Point( (screenSize.width -
                                             appScreenSize.width) / 2,
                                           (screenSize.height -
                                            appScreenSize.height) / 2);
                lm.setMainXY(location);
                lm.setMainDimension(appScreenSize);
                theApp.setSize(appScreenSize);
                theApp.setLocation(location);
            }
            theApp.setVisible(true);
            nachricht = "appleJuice-Core-GUI laeuft...";
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info(nachricht);
            }
            System.out.println(nachricht);
            splash.dispose();
            if (processLink) {
                ApplejuiceFassade.getInstance().processLink(link);
            }
            if (OptionsManagerImpl.getInstance().isErsterStart()) {
                showConnectionWizard(theApp);
            }
            Thread versionWorker = new Thread() {
                public void run() {
                    if (logger.isEnabledFor(Level.DEBUG)) {
                        logger.debug("VersionWorkerThread gestartet. " + this);
                    }
                    try {
                        String downloadData = WebsiteContentLoader.
                            getWebsiteContent("http://www.tkl-soft.de", 80,
                                              "/applejuice/version.txt");

                        if (downloadData.length() > 0) {
                            int pos1 = downloadData.indexOf("|");
                            String aktuellsteVersion = downloadData.substring(0,
                                pos1);
                            StringTokenizer token1 = new StringTokenizer(
                                aktuellsteVersion, ".");
    						String guiVersion = ApplejuiceFassade.GUI_VERSION;
    						if (guiVersion.indexOf('-') != -1){
    							guiVersion = guiVersion.substring(0, guiVersion.indexOf('-'));
    						}
                            StringTokenizer token2 = new StringTokenizer(guiVersion, ".");
                            if (token1.countTokens() != 3 ||
                                token2.countTokens() != 3) {
                                return;
                            }
                            String[] versionInternet = new String[3];
                            String[] aktuelleVersion = new String[3];
                            for (int i = 0; i < 3; i++) {
                                versionInternet[i] = token1.nextToken();
                                aktuelleVersion[i] = token2.nextToken();
                            }
                            int versionsInfoModus = OptionsManagerImpl.getInstance().
                                getVersionsinfoModus();
                            boolean showInfo = false;
                            boolean versionUpdate = false;
                            boolean importantUpdate = false;
                            boolean cosmeticUpdate = false;
                            if (Integer.parseInt(versionInternet[0]) >
                                Integer.parseInt(aktuelleVersion[0])) {
                                versionUpdate = true;
                            }
                            else if (Integer.parseInt(versionInternet[1]) >
                                     Integer.parseInt(aktuelleVersion[1])) {
                                importantUpdate = true;
                            }
                            else if (Integer.parseInt(versionInternet[2]) >
                                     Integer.parseInt(aktuelleVersion[2])) {
                                cosmeticUpdate = true;
                            }
                            if (versionsInfoModus == 2
                                &&
                                (cosmeticUpdate || importantUpdate || versionUpdate)) {
                                showInfo = true;
                            }
                            else if (versionsInfoModus == 1
                                     && (importantUpdate || versionUpdate)) {
                                showInfo = true;
                            }
                            else if (versionsInfoModus == 0
                                     && versionUpdate) {
                                showInfo = true;
                            }
                            if (showInfo) {
                                int pos2 = downloadData.lastIndexOf("|");
                                String winLink = downloadData.substring(pos1 +
                                    1, pos2);
                                String sonstigeLink = downloadData.substring(
                                    pos2 + 1);
                                UpdateInformationDialog updateInformationDialog =
                                    new UpdateInformationDialog(theApp,
                                    aktuellsteVersion, winLink, sonstigeLink);
                                updateInformationDialog.setVisible(true);
                            }
                        }
                    }
                    catch (Exception e) {
                        if (logger.isEnabledFor(Level.INFO)) {
                            logger.info(
                                "Aktualisierungsinformationen konnten nicht geladen werden. Server down?");
                        }
                    }
                    if (logger.isEnabledFor(Level.DEBUG)) {
                        logger.debug("VersionWorkerThread beendet. " + this);
                    }
                }
            };
            versionWorker.start();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.FATAL)) {
                logger.fatal("Programmabbruch", e);
            }
            System.exit( -1);
        }
    }

    public static void showConnectionWizard(JFrame frame) throws
        HeadlessException {
        WizardDialog wizardDialog = new WizardDialog(frame, true);
        Dimension appDimension = wizardDialog.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        wizardDialog.setLocation( (screenSize.width -
                                   appDimension.width) / 2,
                                 (screenSize.height -
                                  appDimension.height) / 2);
        wizardDialog.setVisible(true);
    }
    
    public static boolean showConnectionWizard(JDialog dialog, AJSettings ajSettings) throws
        HeadlessException {
        WizardDialog wizardDialog = new WizardDialog(dialog, true, ajSettings);
        Dimension appDimension = wizardDialog.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        wizardDialog.setLocation( (screenSize.width -
                                   appDimension.width) / 2,
                                 (screenSize.height -
                                  appDimension.height) / 2);
        wizardDialog.setVisible(true);
        return wizardDialog.isRegularClosed();
    }
}
