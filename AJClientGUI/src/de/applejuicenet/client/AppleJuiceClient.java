package de.applejuicenet.client;

import java.io.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.*;
import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import com.l2fprod.util.OS;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import java.net.Socket;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.37 2003/11/19 11:54:07 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AppleJuiceClient.java,v $
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
 * Konsolenausgaben hinzugefügt.
 *
 * Revision 1.13  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefügt.
 *
 * Revision 1.12  2003/06/22 19:02:58  maj0r
 * Fehlernachricht bei nicht erreichbarem Core geändert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class AppleJuiceClient {
    private static Logger logger;

    public static void main(String[] args) {
        boolean processLink = false;
        if (args != null && (args.length == 1 || args.length==2)) {
            try {
                if (args.length==2){
                    System.setProperty("user.dir", args[1]);
                }
                int PORT = PropertiesManager.getOptionsManager().getLinkListenerPort();
                String passwort = PropertiesManager.getOptionsManager().
                    getRemoteSettings().getOldPassword();
                Socket socket = new Socket("localhost", PORT);
                PrintStream out = new PrintStream(socket.getOutputStream());
                out.println(passwort + "|" + args[0]);
                socket.close();
                System.exit(0);
            }
            catch (IOException ioE) {
                //Keine bisherige GUI-Instanz vorhanden, also GUI oeffnen
                processLink = true;
            }
            catch(Exception e){
                System.exit(1);
            }
        }
        Logger rootLogger = Logger.getRootLogger();
        logger = Logger.getLogger(AppleJuiceClient.class.getName());

        try{
            if (PropertiesManager.getOptionsManager().isThemesSupported()){
                if (OS.isOneDotFour()) {
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
        }
        catch(Exception e){
            if (logger.isEnabledFor(Level.FATAL))
                logger.fatal("Programmabbruch", e);
        }
        String datum = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date(
                System.currentTimeMillis()));
        String dateiName;
        dateiName = datum + ".html";
        HTMLLayout layout = new HTMLLayout();
        layout.setTitle("appleJuice-Core-GUI-Log " + datum);
        layout.setLocationInfo(true);

        try
        {
            String path = System.getProperty("user.dir") + File.separator +
                    "logs";
            File aFile = new File(path);
            if (!aFile.exists())
            {
                aFile.mkdir();
            }
            FileAppender fileAppender = new FileAppender(layout,
                                                         path + File.separator + dateiName);
            rootLogger.addAppender(fileAppender);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        rootLogger.setLevel(PropertiesManager.getOptionsManager().getLogLevel());

        try
        {
            String nachricht = "appleJuice-Core-GUI Version " + ApplejuiceFassade.GUI_VERSION + " wird gestartet...";
            if (logger.isEnabledFor(Level.INFO))
                logger.info(nachricht);
            System.out.println(nachricht);
            nachricht = "erkanntes GUI-OS: " + System.getProperty("os.name");
            if (logger.isEnabledFor(Level.INFO))
                logger.info(nachricht);

            Frame dummyFrame = new Frame();
            Image img = IconManager.getInstance().getIcon("applejuice").getImage();
            dummyFrame.setIconImage(img);
            String titel = null;
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            QuickConnectionSettingsDialog remoteDialog = null;
            int versuche = 0;
            while (!ApplejuiceFassade.istCoreErreichbar())
            {

                versuche++;
                titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                          getFirstAttrbuteByTagName(new String[]{"mainform", "caption"}));
                nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "startup",
                                                                                                     "fehlversuch"}));
                SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
                JOptionPane.showMessageDialog(dummyFrame, nachricht, titel,
                                              JOptionPane.ERROR_MESSAGE);
                remoteDialog = new QuickConnectionSettingsDialog(dummyFrame);
                remoteDialog.show();
                if (remoteDialog.getResult() == QuickConnectionSettingsDialog.ABGEBROCHEN)
                {
                    nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                  getFirstAttrbuteByTagName(new String[]{"javagui", "startup",
                                                                                                         "verbindungsfehler"}));
                    nachricht = nachricht.replaceFirst("%s",
                                                       PropertiesManager.getOptionsManager().
                                                       getRemoteSettings().
                                                       getHost());
                    JOptionPane.showMessageDialog(dummyFrame, nachricht, titel,
                                                  JOptionPane.OK_OPTION);
                    logger.fatal(nachricht);
                    System.out.println("Fehler: " + nachricht);
                    System.exit(-1);
                }
            }
            if (versuche>0){
                SoundPlayer.getInstance().playSound(SoundPlayer.ZUGANG_GEWAEHRT);
            }
            Splash splash = new Splash(IconManager.getInstance().getIcon("splashscreen").getImage());
            splash.show();
            PositionManager lm = PropertiesManager.getPositionManager();
            final AppleJuiceDialog theApp = new AppleJuiceDialog();
            if (lm.isLegal())
            {
                theApp.setLocation(lm.getMainXY());
                theApp.setSize(lm.getMainDimension());
            }
            else
            {
                theApp.setLocation(20, 20);
            }
            theApp.show();
            nachricht = "appleJuice-Core-GUI läuft...";
            if (logger.isEnabledFor(Level.INFO))
                logger.info(nachricht);
            System.out.println(nachricht);
            splash.dispose();
            LinkListener linkListener = new LinkListener();
            if (processLink){
                ApplejuiceFassade.getInstance().processLink(args[0]);
            }
            if (PropertiesManager.getOptionsManager().isErsterStart())
            {
                WizardDialog wizardDialog = new WizardDialog(theApp, true);
                Dimension appDimension = wizardDialog.getSize();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                wizardDialog.setLocation((screenSize.width - appDimension.width) / 2,
                                         (screenSize.height - appDimension.height) / 2);
                wizardDialog.show();
            }
            Thread versionWorker = new Thread() {
                public void run() {
                    if (logger.isEnabledFor(Level.DEBUG))
                        logger.debug("VersionWorkerThread gestartet. " + this);
                    try
                    {
                        //http://download.berlios.de/applejuicejava/version.txt
                        String strAktuellsteVersion = WebsiteContentLoader.getWebsiteContent("http://download.berlios.de", 80,
                                                                                             "/applejuicejava/version.txt");
                        if (strAktuellsteVersion.length() > 0)
                        {
                            int pos = ApplejuiceFassade.GUI_VERSION.indexOf(' ');
                            double aktuelleVersion;
                            if (pos != -1)
                            {
                                aktuelleVersion = Double.parseDouble(ApplejuiceFassade.GUI_VERSION.substring(0, pos));
                            }
                            else
                            {
                                aktuelleVersion = Double.parseDouble(ApplejuiceFassade.GUI_VERSION);
                            }
                            double aktuellsteVersion = Double.parseDouble(strAktuellsteVersion);
                            if (aktuellsteVersion > aktuelleVersion)
                            {
                                LanguageSelector ls = LanguageSelector.getInstance();
                                String titel = ls.getFirstAttrbuteByTagName(new String[]{"javagui", "startup", "newversiontitel"});
                                String nachricht = ls.getFirstAttrbuteByTagName(new String[]{"javagui", "startup", "newversionnachricht"});
                                nachricht = nachricht.replaceFirst("%s", strAktuellsteVersion);
                                JOptionPane.showMessageDialog(theApp, nachricht, titel,
                                                              JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        if (logger.isEnabledFor(Level.INFO))
                            logger.info("Aktualisierungsinformationen konnten nicht geladen werden. Server down?");
                    }
                    if (logger.isEnabledFor(Level.DEBUG))
                        logger.debug("VersionWorkerThread beendet. " + this);
                }
            };
            versionWorker.start();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.FATAL))
                logger.fatal("Programmabbruch", e);
            System.exit(-1);
        }
    }
}