package de.applejuicenet.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.QuickConnectionSettingsDialog;
import de.applejuicenet.client.gui.WizardDialog;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.LinkListener;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.Splash;
import de.applejuicenet.client.shared.WebsiteContentLoader;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import de.applejuicenet.client.gui.ConnectFrame;
import de.applejuicenet.client.gui.UpdateInformationDialog;
import java.awt.Insets;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.55 2004/01/09 11:40:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AppleJuiceClient.java,v $
 * Revision 1.55  2004/01/09 11:40:03  maj0r
 * Bei Vollbild werden nun fixe Leisten wie eine Taskleiste beachtet.
 *
 * Revision 1.54  2004/01/09 11:38:53  maj0r
 * Bei Vollbild werden nun fixe Leisten wie eine Taskleiste beachtet.
 *
 * Revision 1.53  2004/01/05 13:22:43  maj0r
 * Wenn eine neue Version gefunden wird, kann diese nun direkt mit dem Standardbrowser herunter geladen werden.
 *
 * Revision 1.52  2003/12/30 21:09:54  maj0r
 * Versionskontrolle geaendert.
 *
 * Revision 1.51  2003/12/30 15:31:18  maj0r
 * Das GUI wird jetzt beim Start beendet, wenn bereits eine Instanz vorhanden ist.
 *
 * Revision 1.50  2003/12/30 13:42:24  maj0r
 * Tippfehler berichtigt.
 *
 * Revision 1.49  2003/12/30 13:40:06  maj0r
 * Ueberpruefung auf gueltige Javaversion eingebaut.
 *
 * Revision 1.48  2003/12/29 16:35:28  maj0r
 * Taskbareinbau verbessert.
 *
 * Revision 1.47  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.46  2003/12/29 15:20:05  maj0r
 * Neue Versionupdatebenachrichtigung fertiggestellt.
 *
 * Revision 1.45  2003/12/29 11:00:58  maj0r
 * Taskbareintrag auch fuer den Splashscreen eingebaut.
 *
 * Revision 1.44  2003/12/29 10:31:58  maj0r
 * Bug #2 gefixt (Danke an muhviestarr).
 * Wenn das Gui nicht zur Core verbinden kann, hat das GUI nun einen Taskbareintrag.
 *
 * Revision 1.43  2003/12/29 09:49:35  maj0r
 * Bug #1 gefixt (Danke an muhviestarr).
 * Look and Feel beim Verbindungsdialog korrigiert.
 *
 * Revision 1.42  2003/12/29 07:23:18  maj0r
 * Begonnen, auf neues Versionupdateinformationssystem umzubauen.
 *
 * Revision 1.41  2003/12/27 21:14:24  maj0r
 * Logging kann nun komplett deaktiviert werden (Danke an muhviestarr).
 *
 * Revision 1.40  2003/12/27 09:54:28  maj0r
 * Splashscreen wird nun frueher angezeigt (Danke an muhviestarr).
 *
 * Revision 1.39  2003/11/25 14:32:46  maj0r
 * -help Parameter eingebaut.
 *
 * Revision 1.38  2003/11/25 14:22:37  maj0r
 * Parameteruebergabe an das GUI geaendert.
 *
 * Revision 1.37  2003/11/19 11:54:07  maj0r
 * LinkListener fertiggestellt.
 *
 * Revision 1.36  2003/11/18 17:01:12  maj0r
 * Erste Version des LinkListener eingebaut.
 *
 * Revision 1.35  2003/11/18 16:41:50  maj0r
 * Erste Version des LinkListener eingebaut.
 * Themes koennen nun ueber die properties.xml komplett deaktiviert werden.
 *
 * Revision 1.34  2003/11/16 12:34:23  maj0r
 * Themes einngebaut (Danke an LinuxDoc)
 *
 * Revision 1.33  2003/10/31 19:04:58  maj0r
 * Sounds eingebaut.
 *
 * Revision 1.32  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.31  2003/10/02 13:42:17  maj0r
 * Fensterpositionierung fuer ersten Start korrigiert.
 *
 * Revision 1.30  2003/10/01 20:10:44  maj0r
 * Bischen Logging hinzu gefuegt.
 *
 * Revision 1.29  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 * Revision 1.28  2003/09/11 08:39:29  maj0r
 * Start durch Einbau von Threads beschleunigt.
 *
 * Revision 1.27  2003/09/09 12:28:14  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.26  2003/09/09 06:37:36  maj0r
 * Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.25  2003/09/07 09:29:55  maj0r
     * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.24  2003/09/06 14:57:55  maj0r
 * Splashscreenausblendung verlagert.
 *
 * Revision 1.23  2003/09/05 12:07:28  maj0r
 * Logdateinamen auf 24 Stunden korrigiert.
 *
 * Revision 1.22  2003/08/29 19:34:03  maj0r
 * Einige Aenderungen.
 * Version 0.17 Beta
 *
 * Revision 1.21  2003/08/25 08:01:21  maj0r
 * SplashScreen-Bild geaendert.
 *
 * Revision 1.20  2003/08/24 19:43:23  maj0r
 * Splashscreen eingefuegt.
 *
 * Revision 1.19  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
 * Revision 1.18  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.17  2003/07/01 14:52:45  maj0r
 * Wenn kein Core gefunden wird, können nun die entsprechenden Einstellungen beim Start der GUI angepasst werden.
 *
 * Revision 1.16  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.15  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.14  2003/06/22 20:03:54  maj0r
 * Konsolenausgaben hinzugefuegt.
 *
 * Revision 1.13  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefuegt.
 *
 * Revision 1.12  2003/06/22 19:02:58  maj0r
 * Fehlernachricht bei nicht erreichbarem Core geaendert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class AppleJuiceClient {
    private static Logger logger;
    private static String fileAppenderPath;
    private static HTMLLayout layout;

    public static HTMLLayout getLoggerHtmlLayout() {
        return layout;
    }

    public static String getLoggerFileAppenderPath() {
        return fileAppenderPath;
    }

    public static void main(String[] args) {
        String javaVersion = System.getProperty("java.version");
        StringBuffer version = new StringBuffer(javaVersion);
        for (int i=version.length()-1; i>=0; i--){
            if (version.charAt(i)=='.'){
                version.deleteCharAt(i);
            }
        }
        if (javaVersion.length()>2){
            javaVersion = javaVersion.substring(0, 2);
        }
        boolean gueltig = false;
        try{
            int versionsNr = Integer.parseInt(version.toString().substring(0,2));
            if (versionsNr >= 14){
                gueltig = true;
            }
        }
        catch(Exception e){
            //nix zu tun
        }
        if (!gueltig){
            JOptionPane.showMessageDialog(new Frame(), "Es wird mindestens JRE 1.4 benötigt!", "appleJuice Client",
                                          JOptionPane.ERROR_MESSAGE);
        }

        boolean processLink = false;
        String link = "";
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
                    if (args[i].indexOf("-link=") != -1) {
                        link = args[i].substring(6);
                        int PORT = PropertiesManager.getOptionsManager().
                            getLinkListenerPort();
                        String passwort = PropertiesManager.getOptionsManager().
                            getRemoteSettings().getOldPassword();
                        Socket socket = new Socket("localhost", PORT);
                        PrintStream out = new PrintStream(socket.
                            getOutputStream());
                        out.println(passwort + "|" + link);
                        socket.close();
                        System.exit(0);
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
        else{
            int PORT = PropertiesManager.getOptionsManager().
                getLinkListenerPort();
            String passwort = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            try {
                Socket socket = new Socket("localhost", PORT);
                socket.close();
                //bereits ein GUI vorhanden, also GUI schliessen
                JOptionPane.showMessageDialog(new Frame(), "Eine Instanz des GUIs ist bereits in Verwendung.",
                                              "appleJuice Client", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            catch (IOException ex) {
                //alles bestens
            }
        }
        Logger rootLogger = Logger.getRootLogger();
        logger = Logger.getLogger(AppleJuiceClient.class.getName());

        try {
            if (PropertiesManager.getOptionsManager().isThemesSupported()) {
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
        String datum = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date(
            System.currentTimeMillis()));
        String dateiName;
        dateiName = datum + ".html";
        layout = new HTMLLayout();
        layout.setTitle("appleJuice-Core-GUI-Log " + datum);
        layout.setLocationInfo(true);
        Level logLevel = PropertiesManager.getOptionsManager().getLogLevel();
        try {
            rootLogger.addAppender(new ConsoleAppender());
            String path = System.getProperty("user.dir") + File.separator +
                "logs";
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
            connectFrame.show();
            Splash splash = new Splash(connectFrame, IconManager.getInstance().getIcon(
                "splashscreen").getImage());
            splash.show();
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info(nachricht);
            }
            System.out.println(nachricht);
            nachricht = "erkanntes GUI-OS: " + System.getProperty("os.name");
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info(nachricht);

            }
            String titel = null;
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            QuickConnectionSettingsDialog remoteDialog = null;
            int versuche = 0;
            AppleJuiceDialog.initThemes();
            while (!ApplejuiceFassade.istCoreErreichbar()) {
                versuche++;
                titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[] {"mainform",
                                              "caption"}));
                nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[] {"javagui",
                                              "startup",
                                              "fehlversuch"}));
                SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
                splash.setVisible(false);
                JOptionPane.showMessageDialog(connectFrame, nachricht, titel,
                                              JOptionPane.ERROR_MESSAGE);
                remoteDialog = new QuickConnectionSettingsDialog(connectFrame);
                remoteDialog.show();
                if (remoteDialog.getResult() ==
                    QuickConnectionSettingsDialog.ABGEBROCHEN) {
                    nachricht = ZeichenErsetzer.korrigiereUmlaute(
                        languageSelector.
                        getFirstAttrbuteByTagName(new String[] {"javagui",
                                                  "startup",
                                                  "verbindungsfehler"}));
                    nachricht = nachricht.replaceFirst("%s",
                        PropertiesManager.getOptionsManager().
                        getRemoteSettings().
                        getHost());
                    JOptionPane.showMessageDialog(connectFrame, nachricht, titel,
                                                  JOptionPane.OK_OPTION);
                    logger.fatal(nachricht);
                    System.out.println("Fehler: " + nachricht);
                    connectFrame.dispose();
                    System.exit( -1);
                }
                splash.setVisible(true);
            }
            if (versuche > 0) {
                SoundPlayer.getInstance().playSound(SoundPlayer.ZUGANG_GEWAEHRT);
            }
            PositionManager lm = PropertiesManager.getPositionManager();
            final AppleJuiceDialog theApp = new AppleJuiceDialog();
            if (lm.isLegal()) {
                theApp.setLocation(lm.getMainXY());
                theApp.setSize(lm.getMainDimension());
            }
            else {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension screenSize = tk.getScreenSize();
                Insets insets = tk.getScreenInsets(theApp.getGraphicsConfiguration());
                screenSize.width -= (insets.left + insets.right);
                screenSize.height -= (insets.top + insets.bottom);
                theApp.setSize(screenSize);
                theApp.setLocation(insets.left, insets.top);
            }

            connectFrame.dispose();
            theApp.show();
            nachricht = "appleJuice-Core-GUI läuft...";
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info(nachricht);
            }
            System.out.println(nachricht);
            splash.dispose();
            LinkListener linkListener = new LinkListener();
            if (processLink) {
                ApplejuiceFassade.getInstance().processLink(link);
            }
            if (PropertiesManager.getOptionsManager().isErsterStart()) {
                WizardDialog wizardDialog = new WizardDialog(theApp, true);
                Dimension appDimension = wizardDialog.getSize();
                Dimension screenSize = Toolkit.getDefaultToolkit().
                    getScreenSize();
                wizardDialog.setLocation( (screenSize.width -
                                           appDimension.width) / 2,
                                         (screenSize.height -
                                          appDimension.height) / 2);
                wizardDialog.show();
            }
            Thread versionWorker = new Thread() {
                public void run() {
                    if (logger.isEnabledFor(Level.DEBUG)) {
                        logger.debug("VersionWorkerThread gestartet. " + this);
                    }
                    try {
                        String downloadData = WebsiteContentLoader.
                            getWebsiteContent("http://download.berlios.de", 80,
                            "/applejuicejava/version.txt");

                        if (downloadData.length() > 0) {
                            int pos1 = downloadData.indexOf("|");
                            String aktuellsteVersion = downloadData.substring(0, pos1);
                            StringTokenizer token1 = new StringTokenizer(
                                aktuellsteVersion, ".");
                            StringTokenizer token2 = new StringTokenizer(
                                ApplejuiceFassade.GUI_VERSION, ".");
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
                            int versionsInfoModus = PropertiesManager.
                                getOptionsManager().getVersionsinfoModus();
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
                            if (versionsInfoModus==2
                                && (cosmeticUpdate || importantUpdate || versionUpdate )){
                                showInfo = true;
                            }
                            else if(versionsInfoModus==1
                                    && (importantUpdate || versionUpdate)){
                                showInfo = true;
                            }
                            else if(versionsInfoModus==0
                                    && versionUpdate){
                                showInfo = true;
                            }
                            if (showInfo) {
                                int pos2 = downloadData.lastIndexOf("|");
                                String winLink = downloadData.substring(pos1 + 1, pos2);
                                String sonstigeLink = downloadData.substring(pos2 + 1);
                                UpdateInformationDialog updateInformationDialog =
                                    new UpdateInformationDialog(theApp, aktuellsteVersion, winLink, sonstigeLink);
                                updateInformationDialog.show();
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
}