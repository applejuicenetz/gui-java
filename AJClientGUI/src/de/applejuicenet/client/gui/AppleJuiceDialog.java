package de.applejuicenet.client.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.jeans.trayicon.SwingTrayPopup;
import com.jeans.trayicon.WindowsTrayIcon;
import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.tools.MemoryMonitorDialog;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/AppleJuiceDialog.java,v 1.102 2004/02/27 09:54:18 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AppleJuiceDialog.java,v $
 * Revision 1.102  2004/02/27 09:54:18  maj0r
 * Standardthemepack geaendert.
 *
 * Revision 1.101  2004/02/27 07:19:13  maj0r
 * Loggerverwendung korrigiert.
 *
 * Revision 1.100  2004/02/26 15:12:47  maj0r
 * Umgang mit ZeichenErsetzer korrigiert.
 *
 * Revision 1.99  2004/02/26 13:59:06  maj0r
 * Aktivieren/Deaktivieren-Menuepunkt im LanguageSelector aufgenommen.
 *
 * Revision 1.98  2004/02/25 13:10:04  maj0r
 * Bug #244 gefixt (Danke an Homer1Simpson)
 * GUI stoert sich nicht mehr an Nicht-Themes-Zips im Themes-Verzeichnis.
 *
 * Revision 1.97  2004/02/20 16:13:33  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.96  2004/02/17 15:31:18  maj0r
 * Moeglichen NullPointer behoben.
 *
 * Revision 1.95  2004/02/13 14:50:56  maj0r
 * Bug #129 gefixt (Danke an dsp2004)
 * WebsiteException durch Ueberlastung des Servers sollte nun weitgehend unterbunden sein.
 *
 * Revision 1.94  2004/02/05 23:11:26  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.93  2004/02/04 16:10:46  maj0r
 * Titel im Fehlerdialog geaendert.
 *
 * Revision 1.92  2004/02/04 14:44:05  maj0r
 * Fehler beim Speichern der Einstellungen korrigiert.
 *
 * Revision 1.91  2004/02/04 14:26:05  maj0r
 * Bug #185 gefixt (Danke an muhviestarr)
 * Einstellungen des GUIs werden beim Schliessen des Core gesichert.
 *
 * Revision 1.90  2004/01/29 15:44:28  maj0r
 * Formataenderung.
 *
 * Revision 1.89  2004/01/28 20:18:00  maj0r
 * Fehlerhafte Anzeige von Menuetexten bei Nicht-Windows-Systemen gefixt.
 *
 * Revision 1.88  2004/01/26 19:26:41  maj0r
 * TrayIcon hat nun einen richtigen Parent erhalten.
 *
 * Revision 1.87  2004/01/26 16:48:07  maj0r
 * Fehler beim TrayIcon-Menue behoben.
 *
 * Revision 1.86  2004/01/26 16:21:05  maj0r
 * Optionendialog besser positioniert und kann nun ueber das TrayIcon aufgerufen werden.
 *
 * Revision 1.85  2004/01/25 08:30:39  maj0r
 * NullPointer behoben.
 *
 * Revision 1.84  2004/01/23 20:22:49  maj0r
 * Formataenderung.
 *
 * Revision 1.83  2004/01/20 11:18:03  maj0r
 * Format der properties.xml geaendert.
 *
 * Revision 1.82  2004/01/20 10:34:07  maj0r
 * Focus bei der Anzeige des Dialogs nach Verstecken per TrayIcon korrigiert.
 *
 * Revision 1.81  2004/01/19 17:44:49  maj0r
 * ajl-Listen koennen nun ueber das Menue importiert werden.
 *
 * Revision 1.80  2004/01/12 16:15:04  maj0r
 * Bug #91 umgesetzt (Danke an hirsch.marcel)
 * Maxupload- und Maxdownloadgeschwindigkeit kann nun über das TrayIcon eingestellt werden (Windowsversion).
 *
 * Revision 1.79  2004/01/12 07:21:54  maj0r
 * Standard-XML erweitert.
 *
 * Revision 1.78  2004/01/09 13:07:19  maj0r
 * Bug #33 gefixt (Danke an oz_2k)
 * Obwohl ich denke, dass es sich um ein Feature der Themes handelt, wurde der Vollbildmodus auf Wunsch vieler Benutzer an Windowsstandard angepasst.
 *
 * Revision 1.77  2004/01/07 17:16:20  maj0r
 * Button zum Themes deaktivieren an Sprachen angepasst.
 *
 * Revision 1.76  2004/01/06 15:05:43  maj0r
 * TrayIcon-Verwendung korrigiert.
 *
 * Revision 1.75  2004/01/06 12:52:04  maj0r
 * TrayIcon für Windowsplattformen eingebaut.
 *
 * Revision 1.74  2004/01/05 19:17:18  maj0r
 * Bug #56 gefixt (Danke an MeineR)
 * Das Laden der Plugins beim Start kann über das Optionenmenue deaktiviert werden.
 *
 * Revision 1.73  2004/01/05 07:28:58  maj0r
 * Begonnen einen Standardwebbrowser einzubauen.
 *
 * Revision 1.72  2004/01/02 17:57:51  maj0r
 * Menuepunkt zum Beenden des Core auf vielfachen Wunsch an separate Stelle verschoben.
 *
 * Revision 1.71  2004/01/02 16:47:18  maj0r
 * Standard-XML-Datei angepasst.
 *
 * Revision 1.70  2003/12/30 15:13:54  maj0r
 * Der Core kann nun uebers GUI beendet werden.
 *
 * Revision 1.69  2003/12/30 13:40:27  maj0r
 * Muell entfernt.
 *
 * Revision 1.68  2003/12/29 16:35:12  maj0r
 * Look and Feel Verwendung korrigiert.
 *
 * Revision 1.67  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.66  2003/12/29 15:20:05  maj0r
 * Neue Versionupdatebenachrichtigung fertiggestellt.
 *
 * Revision 1.65  2003/12/29 09:49:35  maj0r
 * Bug #1 gefixt (Danke an muhviestarr).
 * Look and Feel beim Verbindungsdialog korrigiert.
 *
 * Revision 1.64  2003/12/29 07:23:18  maj0r
 * Begonnen, auf neues Versionupdateinformationssystem umzubauen.
 *
 * Revision 1.63  2003/12/05 11:17:11  maj0r
 * Sobald man den richtigen Listener verwendet, funktionierts auch richtig...
 *
 * Revision 1.62  2003/11/19 17:05:20  maj0r
 * Autom. Pwdl ueberarbeitet.
 *
 * Revision 1.61  2003/11/19 13:43:39  maj0r
 * Themes sind nun ueber das Menue deaktivierbar.
 *
 * Revision 1.60  2003/11/18 16:41:50  maj0r
 * Erste Version des LinkListener eingebaut.
 * Themes koennen nun ueber die properties.xml komplett deaktiviert werden.
 *
 * Revision 1.59  2003/11/16 12:34:23  maj0r
 * Themes einngebaut (Danke an LinuxDoc)
 *
 * Revision 1.58  2003/11/04 13:14:50  maj0r
 * Memory-Monitor eingebaut.
 *
 * Revision 1.57  2003/11/03 14:26:12  maj0r
 * Titelzeile geaendert.
 *
 * Revision 1.56  2003/10/31 19:04:58  maj0r
 * Sounds eingebaut.
 *
 * Revision 1.55  2003/10/31 11:33:59  maj0r
 * StandardXML angepasst.
 *
 * Revision 1.54  2003/10/31 11:31:45  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt. Kommen noch mehr.
 *
 * Revision 1.53  2003/10/27 14:46:08  maj0r
 * Detailliertere Fehlermeldung eingebaut.
 *
 * Revision 1.52  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.51  2003/10/21 11:36:32  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 * Revision 1.50  2003/10/17 13:33:02  maj0r
 * properties.xml wird nun im Fehlerfall automatisch generiert.
 *
 * Revision 1.49  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.48  2003/09/11 08:39:29  maj0r
 * Start durch Einbau von Threads beschleunigt.
 *
 * Revision 1.47  2003/09/09 17:43:24  maj0r
 * Memory-Anzeige entfernt.
 *
 * Revision 1.46  2003/09/09 12:28:14  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.45  2003/09/09 06:37:36  maj0r
 * Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.44  2003/09/08 06:27:11  maj0r
 * Um Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.43  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.42  2003/09/05 09:02:26  maj0r
 * Threadverwendung verbessert.
 *
 * Revision 1.41  2003/09/04 22:12:45  maj0r
 * Logger verfeinert.
 * Threadbeendigung korrigiert.
 *
 * Revision 1.40  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.39  2003/09/03 12:10:17  maj0r
 * Icons fuer die Sprachauswahl eingefuegt.
 *
 * Revision 1.38  2003/08/29 14:24:15  maj0r
 * About-Dialog mit entsprechendem Menuepunkt eingefuehrt.
 *
 * Revision 1.37  2003/08/28 10:38:40  maj0r
 * Versionierung HIER entfernt.
 *
 * Revision 1.36  2003/08/26 19:46:34  maj0r
 * Sharebereich weiter vervollstaendigt.
 *
 * Revision 1.35  2003/08/25 15:54:49  maj0r
 * Memory-Anzeige eingebaut.
 *
 * Revision 1.34  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.33  2003/08/23 11:16:13  maj0r
 * Plattformunabhaengigkeit wieder hergestellt.
 *
 * Revision 1.32  2003/08/22 12:52:19  maj0r
 * Version auf 0.13 Beta erhoeht.
 *
 * Revision 1.31  2003/08/21 15:13:29  maj0r
 * Auf Thread umgebaut.
 *
 * Revision 1.30  2003/08/20 20:08:24  maj0r
 * Version auf 0.11 erhoeht.
 *
 * Revision 1.29  2003/08/19 15:57:21  maj0r
 * Gesamtgeschwindigkeit wird nun angezeigt.
 *
 * Revision 1.28  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.27  2003/08/12 06:12:19  maj0r
 * Version erhöht.
 *
 * Revision 1.26  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.25  2003/07/04 15:25:38  maj0r
 * Version erhöht.
 * DownloadModel erweitert.
 *
 * Revision 1.24  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.23  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefügt.
 *
 * Revision 1.22  2003/06/13 15:07:30  maj0r
 * Versionsanzeige hinzugefügt.
 * Da der Controllerteil refactort werden kann, haben Controller und GUI separate Versionsnummern.
 *
 * Revision 1.21  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class AppleJuiceDialog
    extends JFrame
    implements LanguageListener, DataUpdateListener {

    private static Logger logger = Logger.getLogger(AppleJuiceDialog.class);

    private RegisterPanel registerPane;
    private JLabel[] statusbar = new JLabel[6];
    private JMenu sprachMenu;
    private JMenu optionenMenu;
    private JMenu themesMenu = null;
    private JMenu coreMenu;
    private HashSet plugins;
    private JMenuItem menuItemOptionen = new JMenuItem();
    private JMenuItem menuItemDateiliste = new JMenuItem();
    private JMenuItem menuItemCoreBeenden = new JMenuItem();
    private JMenuItem menuItemUeber = new JMenuItem();
    private JMenuItem menuItemDeaktivieren = new JMenuItem();
    private JMenuItem menuItemAktivieren = new JMenuItem();
    private JMenuItem popupOptionenMenuItem = new JMenuItem("Optionen");
    private JMenuItem popupAboutMenuItem = new JMenuItem("&Info");
    private JMenuItem popupShowHideMenuItem = new JMenuItem("%Show");
    private JButton sound = new JButton();
    private JButton memory = new JButton();
    private String keinServer = "";
    public static boolean rewriteProperties = false;
    private boolean firstChange = true;
    private MemoryMonitorDialog memoryMonitorDialog;
    private static HashMap themes = new HashMap();
    private String themeSupportTitel;
    private String themeSupportNachricht;
    private boolean automaticPwdlEnabled = false;
    private String titel;
    private static boolean themesInitialized = false;
    private String bestaetigung = "";
    private int desktopHeight;
    private int desktopWidth;
    private boolean maximized = false;
    private Dimension lastFrameSize;
    private Point lastFrameLocation;

    private static boolean useTrayIcon = false;
    private String zeigen = "";
    private String verstecken = "";
    private WindowsTrayIcon trayIcon;
    private Icon versteckenIcon = null;
    private Icon zeigenIcon = null;

    private static AppleJuiceDialog theApp;

    public static void initThemes() {
        try {
            themesInitialized = true;
            if (PropertiesManager.getOptionsManager().isThemesSupported()) {
                HashSet themesDateien = new HashSet();
                File themesPath = new File(System.getProperty("user.dir") +
                                           File.separator + "themes");
                if (!themesPath.isDirectory()) {
                    closeWithErrormessage("Der Ordner" +
                                          " für die Themes zip-Dateien ist nicht vorhanden." +
                                          "\r\nappleJuice wird beendet.", false);
                }
                File[] themeFiles = themesPath.listFiles();
                for (int i = 0; i < themeFiles.length; i++) {
                    if (themeFiles[i].isFile() &&
                        themeFiles[i].getName().indexOf(".zip") != -1) {
                        //testen, ob es wirklich ein skinfile ist
                        ZipFile jf = new ZipFile(themeFiles[i]);
                        ZipEntry entry = jf.getEntry("skinlf-themepack.xml");
                        if (entry!=null){
                            themesDateien.add(themeFiles[i].toURL());
                        }
                    }
                }
                Iterator it = themesDateien.iterator();
                Skin standardSkin = null;
                Skin aSkin = null;
                String temp;
                String shortName = "";
                String defaultTheme = PropertiesManager.getOptionsManager().
                    getDefaultTheme();
                while (it.hasNext()) {
                    URL skinUrl = (URL) it.next();
                    temp = skinUrl.getFile();
                    int index1 = temp.lastIndexOf('/');
                    int index2 = temp.lastIndexOf(".zip");
                    if (index1 == -1 || index2 == -1) {
                        continue;
                    }
                    shortName = temp.substring(index1 + 1, index2);
                    aSkin = SkinLookAndFeel.loadThemePack(skinUrl);
                    themes.put(shortName, aSkin);
                    if (shortName.compareToIgnoreCase(defaultTheme) == 0) {
                        standardSkin = aSkin;
                    }
                }
                if (standardSkin == null) {
                    standardSkin = aSkin;
                }
                SkinLookAndFeel.setSkin(standardSkin);
                SkinLookAndFeel.enable();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public AppleJuiceDialog() {
        super();
        try {
            theApp = this;
            init();
            pack();
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void addPluginToHashSet(PluginConnector plugin) {
        plugins.add(plugin);
    }

    public PluginConnector[] getPlugins() {
        return (PluginConnector[]) plugins.toArray(new PluginConnector[plugins.
            size()]);
    }

    public static AppleJuiceDialog getApp() {
        return theApp;
    }

    private void init() throws Exception {
        String titel = "appleJuice Client";
        IconManager im = IconManager.getInstance();
        Image image = im.getIcon("applejuice").getImage();
        setTitle(titel);
        String osName = System.getProperty("os.name");
        plugins = new HashSet();
        setIconImage(image);
        menuItemOptionen.setIcon(im.getIcon("optionen"));
        menuItemUeber.setIcon(im.getIcon("info"));
        menuItemCoreBeenden.setIcon(im.getIcon("skull"));
        menuItemDateiliste.setIcon(im.getIcon("speichern"));

        setJMenuBar(createMenuBar());
        if (PropertiesManager.getOptionsManager().isThemesSupported()) {
            SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
        }

        String path = System.getProperty("user.dir") + File.separator +
            "language" +
            File.separator;
        path += PropertiesManager.getOptionsManager().getSprache() + ".xml";
        registerPane = new RegisterPanel(this);
        LanguageSelector.getInstance(path);
        addWindowListener(
            new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        desktopWidth = screenSize.width;
        desktopHeight = screenSize.height;
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int x = getWidth();
                int y = getHeight();
                if (x == desktopWidth && y == desktopHeight) {
                    if (maximized) {
                        demaximize();
                    }
                    else {
                        maximize();
                    }
                }
                else {
                    if (!maximized) {
                        lastFrameSize = getSize();
                        lastFrameLocation = getLocation();
                    }
                    super.componentResized(e);
                }
            }

            public void componentMoved(ComponentEvent e) {
                if (!maximized) {
                    lastFrameLocation = getLocation();
                }
                super.componentMoved(e);
            }
        });

        if (osName.toLowerCase().indexOf("win") != -1) {
            try {
                if (!WindowsTrayIcon.isRunning(titel)) {
                    useTrayIcon = true;
                    WindowsTrayIcon.initTrayIcon(titel);
                    trayIcon = new WindowsTrayIcon(image, 16, 16);
                    trayIcon.setVisible(true);
                    trayIcon.setToolTipText(titel);
                    trayIcon.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent evt) {
                            if (!isVisible()) {
                                popupShowHideMenuItem.setText(zeigen);
                                popupShowHideMenuItem.setIcon(zeigenIcon);
                            }
                            else {
                                popupShowHideMenuItem.setText(verstecken);
                                popupShowHideMenuItem.setIcon(versteckenIcon);
                            }
                            if ( (evt.getModifiers() & MouseEvent.BUTTON1_MASK) !=
                                0 &&
                                evt.getClickCount() == 2) {
                                AppleJuiceDialog dialog = AppleJuiceDialog.
                                    getApp();
                                if (!isVisible()) {
                                    dialog.setVisible(true);
                                    dialog.requestFocus();
                                }
                                else {
                                    setVisible(false);
                                }
                            }
                        }
                    });
                    SwingTrayPopup popup = makeSwingPopup();
                    popup.setTrayIcon(trayIcon);
                }
            }
            catch (UnsatisfiedLinkError error) {
                LanguageSelector languageSelector = LanguageSelector.
                    getInstance();
                String fehlerTitel = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.getFirstAttrbuteByTagName(".root.mainform.caption"));

                String fehlerNachricht = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.getFirstAttrbuteByTagName(".root.javagui.startup.trayfehler"));

                JOptionPane.showMessageDialog(this, fehlerNachricht,
                                              fehlerTitel,
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
        getContentPane().setLayout(new BorderLayout());
        registerPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RegisterI register = (RegisterI) registerPane.
                    getSelectedComponent();
                register.registerSelected();
            }
        });
        getContentPane().add(registerPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());

        for (int i = 0; i < statusbar.length; i++) {
            statusbar[i] = new JLabel("            ");
            statusbar[i].setHorizontalAlignment(JLabel.RIGHT);
            statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
            statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
        }
        memory.setIcon(IconManager.getInstance().getIcon("mmonitor"));
        memory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (memoryMonitorDialog == null) {
                    memoryMonitorDialog = new MemoryMonitorDialog(
                        AppleJuiceDialog.this);
                    Point loc = memory.getLocationOnScreen();
                    loc.setLocation(loc.getX() - memoryMonitorDialog.getWidth(),
                                    loc.getY() - memoryMonitorDialog.getHeight());
                    memoryMonitorDialog.setLocation(loc);
                }
                if (!memoryMonitorDialog.isVisible()) {
                    memoryMonitorDialog.show();
                }
            }
        });

        sound.addActionListener(new ActionListener() {
            {
                if (PropertiesManager.getOptionsManager().isSoundEnabled()) {
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                }
                else {
                    sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
                }
            }

            public void actionPerformed(ActionEvent ae) {
                OptionsManager om = PropertiesManager.getOptionsManager();
                om.enableSound(!om.isSoundEnabled());
                if (om.isSoundEnabled()) {
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                }
                else {
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
        fireLanguageChanged();
        ApplejuiceFassade dm = ApplejuiceFassade.getInstance();
        dm.addDataUpdateListener(this,
                                 DataUpdateListener.INFORMATION_CHANGED);
        dm.startXMLCheck();
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
            String sprachText = LanguageSelector.getInstance().
                getFirstAttrbuteByTagName(".root.Languageinfo.name");
            PropertiesManager.getOptionsManager().setSprache(sprachText);
            int[] downloadWidths = DownloadPanel.getInstance().getColumnWidths();
            int[] uploadWidths = UploadPanel.getInstance().getColumnWidths();
            int[] serverWidths = ServerPanel.getInstance().getColumnWidths();
            int[] shareWidths = SharePanel.getInstance().getColumnWidths();
            Dimension dim = AppleJuiceDialog.getApp().getSize();
            Point p = AppleJuiceDialog.getApp().getLocationOnScreen();
            PositionManager pm = PropertiesManager.getPositionManager();
            pm.setMainXY(p);
            pm.setMainDimension(dim);
            pm.setDownloadWidths(downloadWidths);
            pm.setUploadWidths(uploadWidths);
            pm.setServerWidths(serverWidths);
            pm.setShareWidths(shareWidths);
            pm.save();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
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
        ApplejuiceFassade.getInstance().stopXMLCheck();
        if (rewriteProperties) {
            restorePropertiesXml();
        }
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        if (logger.isEnabledFor(Level.INFO)) {
            logger.info(nachricht);
        }
        System.out.println(nachricht);
        if (!rewriteProperties) {
            einstellungenSpeichern();
        }
        setVisible(false);
        if (useTrayIcon) {
            WindowsTrayIcon.cleanUp();
        }
        System.exit(0);
    }

    public static void closeWithErrormessage(String error,
                                             boolean speichereEinstellungen) {
        JOptionPane.showMessageDialog(theApp, error, "appleJuice Client",
                                      JOptionPane.OK_OPTION);
        if (rewriteProperties) {
            restorePropertiesXml();
        }
        else {
            ApplejuiceFassade.getInstance().stopXMLCheck();
        }
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        Logger aLogger = Logger.getLogger(AppleJuiceDialog.class.getName());
        if (aLogger.isEnabledFor(Level.INFO)) {
            aLogger.info(nachricht);
        }
        System.out.println(nachricht);
        if (speichereEinstellungen && !rewriteProperties) {
            einstellungenSpeichern();
        }
        System.out.println("Fehler: " + error);
        if (useTrayIcon) {
            WindowsTrayIcon.cleanUp();
        }
        System.exit( -1);
    }

    protected JMenuBar createMenuBar() {
        try {
            if (!themesInitialized) {
                AppleJuiceDialog.initThemes();
            }
            String path = System.getProperty("user.dir") + File.separator +
                "language" +
                File.separator;
            File languagePath = new File(path);
            if (!languagePath.isDirectory()) {
                closeWithErrormessage("Der Ordner " + path +
                                      " für die Sprachauswahl xml-Dateien ist nicht vorhanden." +
                                      "\r\nappleJuice wird beendet.", false);
            }
            String[] tempListe = languagePath.list();
            HashSet sprachDateien = new HashSet();
            for (int i = 0; i < tempListe.length; i++) {
                if (tempListe[i].indexOf(".xml") != -1) {
                    sprachDateien.add(tempListe[i]);
                }
            }
            if (sprachDateien.size() == 0) {
                closeWithErrormessage(
                    "Es sind keine xml-Dateien für die Sprachauswahl im Ordner " +
                    path + " vorhanden." +
                    "\r\nappleJuice wird beendet.", false);
            }

            JMenuBar menuBar = new JMenuBar();
            optionenMenu = new JMenu("Extras");
            menuItemOptionen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showOptionsDialog();
                }
            });
            menuItemDateiliste.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dateiListeImportieren();
                }
            });
            menuItemCoreBeenden.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.
                        getApp(), bestaetigung,
                        "appleJuice Client", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        ApplejuiceFassade.getInstance().exitCore();
                    }
                }
            });
            menuItemUeber.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showAboutDialog();
                }
            });
            optionenMenu.add(menuItemOptionen);
            optionenMenu.add(menuItemDateiliste);
            optionenMenu.add(menuItemUeber);
            menuBar.add(optionenMenu);

            sprachMenu = new JMenu("Sprache");
            menuBar.add(sprachMenu);
            ButtonGroup lafGroup = new ButtonGroup();

            Iterator it = sprachDateien.iterator();
            while (it.hasNext()) {
                String sprachText = LanguageSelector.getInstance(path +
                    (String) it.next()).
                    getFirstAttrbuteByTagName(".root.Languageinfo.name");
                JCheckBoxMenuItem rb = new JCheckBoxMenuItem(sprachText);
                if (PropertiesManager.getOptionsManager().getSprache().
                    equalsIgnoreCase(sprachText)) {
                    rb.setSelected(true);
                }
                Image img = Toolkit.getDefaultToolkit().getImage(path +
                    sprachText.toLowerCase() + ".gif");
                ImageIcon result = new ImageIcon();
                result.setImage(img);
                rb.setIcon(result);

                sprachMenu.add(rb);
                rb.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ae) {
                        JCheckBoxMenuItem rb2 = (JCheckBoxMenuItem) ae.
                            getSource();
                        if (rb2.isSelected()) {
                            String path = System.getProperty("user.dir") +
                                File.separator +
                                "language" + File.separator;
                            String dateiName = path + rb2.getText().toLowerCase() +
                                ".xml";
                            LanguageSelector.getInstance(dateiName);
                        }
                    }
                });
                lafGroup.add(rb);
            }
            themesMenu = new JMenu("Themes");
            if (PropertiesManager.getOptionsManager().isThemesSupported()) {
                HashSet themesDateien = new HashSet();
                File themesPath = new File(System.getProperty("user.dir") +
                                           File.separator + "themes");
                if (!themesPath.isDirectory()) {
                    closeWithErrormessage("Der Ordner " + path +
                                          " für die Themes zip-Dateien ist nicht vorhanden." +
                                          "\r\nappleJuice wird beendet.", false);
                }
                File[] themeFiles = themesPath.listFiles();
                for (int i = 0; i < themeFiles.length; i++) {
                    if (themeFiles[i].isFile() &&
                        themeFiles[i].getName().indexOf(".zip") != -1) {
                        //testen, ob es wirklich ein skinfile ist
                        ZipFile jf = new ZipFile(themeFiles[i]);
                        ZipEntry entry = jf.getEntry("skinlf-themepack.xml");
                        if (entry!=null){
                            themesDateien.add(themeFiles[i].toURL());
                        }
                    }
                }
                it = themesDateien.iterator();
                ButtonGroup lafGroup2 = new ButtonGroup();
                Skin standardSkin = null;
                String temp;
                String shortName = "";
                String defaultTheme = PropertiesManager.getOptionsManager().
                    getDefaultTheme();
                while (it.hasNext()) {
                    URL skinUrl = (URL) it.next();
                    temp = skinUrl.getFile();
                    int index1 = temp.lastIndexOf('/');
                    int index2 = temp.lastIndexOf(".zip");
                    if (index1 == -1 || index2 == -1) {
                        continue;
                    }
                    shortName = temp.substring(index1 + 1, index2);
                    final JCheckBoxMenuItem rb = new JCheckBoxMenuItem(
                        shortName);
                    if (shortName.compareToIgnoreCase(defaultTheme) == 0) {
                        rb.setSelected(true);
                    }
                    rb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent ae) {
                            if (rb.isSelected()) {
                                activateLaF(rb.getText());
                            }
                        }
                    });
                    lafGroup2.add(rb);
                    themesMenu.add(rb);
                }
                themesMenu.add(new JSeparator());
                menuItemDeaktivieren.setText("deaktivieren");
                menuItemDeaktivieren.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ce) {
                        activateThemeSupport(false);
                    }
                });
                themesMenu.add(menuItemDeaktivieren);
            }
            else {
                menuItemAktivieren.setText("aktivieren");
                menuItemAktivieren.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ce) {
                        activateThemeSupport(true);
                    }
                });
                themesMenu.add(menuItemAktivieren);
            }
            menuBar.add(themesMenu);
            coreMenu = new JMenu("Core");
            coreMenu.add(menuItemCoreBeenden);
            menuBar.add(coreMenu);
            return menuBar;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return null;
        }
    }

    private void showOptionsDialog() {
        OptionsDialog od = new OptionsDialog(getApp());
        Dimension optDimension = od.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        od.setLocation( (screenSize.width -
                         optDimension.width) / 2,
                       (screenSize.height -
                        optDimension.height) / 2);
        od.show();
    }

    private void showAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(getApp(), true);
        Dimension appDimension = aboutDialog.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        aboutDialog.setLocation( (screenSize.width -
                                  appDimension.width) / 2,
                                (screenSize.height -
                                 appDimension.height) / 2);
        aboutDialog.show();
    }

    private void activateThemeSupport(boolean enable) {
        int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.this,
            themeSupportNachricht, themeSupportTitel, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            PropertiesManager.getOptionsManager().
                enableThemeSupport(enable);
            closeDialog(null);
        }
    }

    private void activateLaF(String laf) {
        try {
            Skin aSkin = (Skin) themes.get(laf);
            if (aSkin != null) {
                SkinLookAndFeel.setSkin(aSkin);
                SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
                PropertiesManager.getOptionsManager().setDefaultTheme(laf);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void setTitle(String title) {
        if (!automaticPwdlEnabled) {
            super.setTitle(titel);
        }
        else {
            super.setTitle(titel + " - Autopilot");
        }
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
                new Thread(){
                    public void run(){
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new
                                                        FileReader(file));
                            String line = "";
                            while ( (line = reader.readLine()) != null) {
                                if (line.compareTo("100") == 0) {
                                    break;
                                }
                            }
                            String size = "";
                            String filename = "";
                            String checksum = "";
                            String link = "";
                            ApplejuiceFassade af = ApplejuiceFassade.getInstance();
                            while ( (line = reader.readLine()) != null) {
                                filename = line;
                                checksum = reader.readLine();
                                size = reader.readLine();
                                if (size != null && checksum != null) {
                                    link = "ajfsp://file|" + filename + "|" + checksum +
                                        "|" + size + "/";
                                    af.processLink(link);
                                }
                            }
                        }
                        catch (FileNotFoundException ex) {
                            //nix zu tun
                        }
                        catch (IOException ex1) {
                            //nix zu tun
                        }
                    }
                }.start();
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            String versionsNr = ApplejuiceFassade.getInstance().getCoreVersion().
                getVersion();
            titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.caption")) +
                " (Core " + versionsNr +
                " - GUI " + ApplejuiceFassade.GUI_VERSION + ")";
            setTitle(titel);
            keinServer = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.javagui.mainform.keinserver"));
            themeSupportTitel = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.caption"));
            themeSupportNachricht = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.mainform.themesupportnachricht"));
            sprachMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.einstform.languagesheet.caption")));
            menuItemOptionen.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.optbtn.caption")));
            menuItemOptionen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.optbtn.hint")));
            menuItemCoreBeenden.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.corebeenden")));
            menuItemCoreBeenden.setToolTipText(ZeichenErsetzer.
                                               korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.corebeendenhint")));
            menuItemUeber.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.aboutbtn.caption")));
            menuItemUeber.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.aboutbtn.hint")));
            menuItemDeaktivieren.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.deaktivieren")));
            optionenMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.extras")));
            menuItemDateiliste.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.dateiliste")));
            menuItemDateiliste.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.dateilistehint")));
            themesMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.themes")));
            bestaetigung = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.bestaetigung"));
            menuItemAktivieren.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.aktivieren")));
            menuItemDeaktivieren.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.deaktivieren")));

            if (useTrayIcon) {
                trayIcon.setToolTipText(titel);
                popupAboutMenuItem.setText(menuItemUeber.getText());
                popupAboutMenuItem.setToolTipText(menuItemUeber.getToolTipText());
                zeigen = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.
                    getFirstAttrbuteByTagName(".root.javagui.menu.zeigen"));
                verstecken = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.
                    getFirstAttrbuteByTagName(".root.javagui.menu.verstecken"));
                popupOptionenMenuItem.setText(menuItemOptionen.getText());
                popupOptionenMenuItem.setToolTipText(menuItemOptionen.
                    getToolTipText());
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.INFORMATION_CHANGED) {
                if (firstChange) {
                    firstChange = false;
                    SoundPlayer.getInstance().playSound(SoundPlayer.GESTARTET);
                }
                Information information = (Information) content;
                statusbar[0].setText(information.getVerbindungsStatusAsString());
                if (information.getVerbindungsStatus() ==
                    Information.NICHT_VERBUNDEN) {
                    statusbar[1].setText(keinServer);
                }
                else {
                    statusbar[1].setText(information.getServerName());
                }
                statusbar[2].setText(information.getUpDownAsString());
                statusbar[3].setText(information.getUpDownSessionAsString());
                statusbar[4].setText(information.getExterneIP());
                statusbar[5].setText(information.getCreditsAsString());
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public SwingTrayPopup makeSwingPopup() {
        final SwingTrayPopup popup = new SwingTrayPopup();
        popupShowHideMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!isVisible()) {
                    setVisible(true);
                    toFront();
                    requestFocus();
                }
                else {
                    setVisible(false);
                }
            }
        });
        popup.add(popupShowHideMenuItem);
        popupOptionenMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showOptionsDialog();
            }
        });

        popup.add(popupOptionenMenuItem);
        IconManager im = IconManager.getInstance();
        versteckenIcon = im.getIcon("hide");
        zeigenIcon = im.getIcon("applejuice");
        Icon aboutIcon = im.getIcon("about");
        popupOptionenMenuItem.setIcon(im.getIcon("optionen"));
        popupAboutMenuItem.setIcon(aboutIcon);
        popupAboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showAboutDialog();
            }
        });
        popup.add(popupAboutMenuItem);
        new Thread() {
            public void run() {
                final AJSettings ajSettings = ApplejuiceFassade.getInstance().
                    getAJSettings();
                if ( ajSettings != null){
                    final JSlider uploadSlider = new JSlider(JSlider.VERTICAL,
                        0,
                        50, (int) ajSettings.getMaxUploadInKB());
                    final JSlider downloadSlider = new JSlider(JSlider.VERTICAL,
                        0,
                        300, (int) ajSettings.getMaxDownloadInKB());
                    uploadSlider.setPaintLabels(true);
                    uploadSlider.setPaintTicks(true);
                    uploadSlider.setPaintTrack(true);
                    uploadSlider.setSnapToTicks(true);
                    downloadSlider.setPaintLabels(true);
                    downloadSlider.setPaintTicks(true);
                    downloadSlider.setPaintTrack(true);
                    downloadSlider.setSnapToTicks(true);
                    final JMenu uploadMenu = new JMenu("Upload");
                    final JMenu downloadMenu = new JMenu("Download");
                    JPanel uploadPanel = new JPanel(new BorderLayout());
                    JPanel downloadPanel = new JPanel(new BorderLayout());
                    final JLabel label1 = new JLabel("50 kb/s");
                    final JLabel label2 = new JLabel("50 kb/s");
                    label1.setText(Long.toString(ajSettings.getMaxUploadInKB()) +
                                   " kb/s");
                    label2.setText(Long.toString(ajSettings.getMaxDownloadInKB()) +
                                   " kb/s");
                    uploadPanel.add(label1, BorderLayout.NORTH);
                    uploadPanel.add(uploadSlider, BorderLayout.SOUTH);
                    uploadMenu.add(uploadPanel);
                    downloadPanel.add(label2, BorderLayout.NORTH);
                    downloadPanel.add(downloadSlider, BorderLayout.SOUTH);
                    downloadMenu.add(downloadPanel);
                    uploadSlider.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            JSlider slider = (JSlider) e.getSource();
                            label1.setText(Integer.toString(slider.getValue()) +
                                           " kb/s");
                        }
                    });
                    downloadSlider.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            JSlider slider = (JSlider) e.getSource();
                            label2.setText(Integer.toString(slider.getValue()) +
                                           " kb/s");
                        }
                    });
                    uploadSlider.addMouseListener(new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            if (uploadSlider.getValue() <
                                uploadSlider.getMaximum()
                                && uploadSlider.getValue() > 0) {
                                long down = downloadSlider.getValue() * 1024;
                                long up = uploadSlider.getValue() * 1024;
                                ApplejuiceFassade.getInstance().setMaxUpAndDown(
                                    up,
                                    down);
                            }
                            else {
                                uploadSlider.setValue( (int) ajSettings.
                                    getMaxUploadInKB());
                            }
                        }
                    });
                    downloadSlider.addMouseListener(new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            if (downloadSlider.getValue() <
                                downloadSlider.getMaximum()
                                && downloadSlider.getValue() > 0) {
                                long down = downloadSlider.getValue() * 1024;
                                long up = uploadSlider.getValue() * 1024;
                                ApplejuiceFassade.getInstance().setMaxUpAndDown(
                                    up,
                                    down);
                            }
                            else {
                                downloadSlider.setValue( (int) ajSettings.
                                    getMaxDownloadInKB());
                            }
                        }
                    });
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            popup.add(uploadMenu);
                            popup.add(downloadMenu);
                        }
                    });
                }
            }
        }

        .start();
        return popup;
    }

    private static void restorePropertiesXml() {
        String dateiname = System.getProperty("user.dir") + File.separator +
            "properties.xml";
        StringBuffer xmlData = new StringBuffer();

        xmlData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        xmlData.append("<root>\r\n");
        xmlData.append(
            "    <options dialogzeigen=\"true\" firststart=\"true\" sound=\"true\" sprache=\"deutsch\" ");
        xmlData.append(
            "themes=\"true\" defaulttheme=\"toxicthemepack\" loadplugins=\"true\" ");
        xmlData.append(
            "linklistenerport=\"8768\" versionsinfo=\"1\" >\r\n");
        xmlData.append(
            "        <remote host=\"localhost\" passwort=\"\"  port=\"9851\"/>\r\n");
        xmlData.append("        <logging level=\"INFO\"/>\r\n");
        xmlData.append("        <download uebersicht=\"true\"/>\r\n");
        xmlData.append("        <farben aktiv=\"true\">\r\n");
        xmlData.append(
            "            <hintergrund downloadFertig=\"-13382656\" quelle=\"-205\"/>\r\n");
        xmlData.append("        </farben>\r\n");
        xmlData.append(
            "        <location height=\"\" width=\"\" x=\"\" y=\"\"/>\r\n");
        xmlData.append("        <columns>\r\n");
        xmlData.append("                <download>\r\n");
        xmlData.append(
            "                        <column0 width=\"80\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"80\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"80\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"80\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"81\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                        <column5 width=\"80\" visibility=\"true\" index=\"5\" />\r\n");
        xmlData.append("                        <column6 width=\"81\" visibility=\"true\" index=\"6\" />\r\n");
        xmlData.append("                        <column7 width=\"80\" visibility=\"true\" index=\"7\" />\r\n");
        xmlData.append("                        <column8 width=\"81\" visibility=\"true\" index=\"8\" />\r\n");
        xmlData.append("                        <column9 width=\"80\" visibility=\"true\" index=\"9\" />\r\n");
        xmlData.append("                </download>\r\n");
        xmlData.append("                <upload>\r\n");
        xmlData.append(
            "                        <column0 width=\"136\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"111\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"111\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"111\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"111\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                        <column5 width=\"112\" visibility=\"true\" index=\"5\" />\r\n");
        xmlData.append("                        <column6 width=\"111\" visibility=\"true\" index=\"6\" />\r\n");
        xmlData.append("                </upload>\r\n");
        xmlData.append("                <server>\r\n");
        xmlData.append(
            "                        <column0 width=\"175\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"175\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"175\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"175\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"175\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                </server>\r\n");
        xmlData.append("                <search>\r\n");
        xmlData.append(
            "                        <column0 width=\"103\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"103\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"103\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"103\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"103\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                        <column5 width=\"103\" visibility=\"true\" index=\"5\" />\r\n");
        xmlData.append("                </search>\r\n");
        xmlData.append("                <share>\r\n");
        xmlData.append(
            "                        <column0 width=\"194\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"195\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"194\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                </share>\r\n");
        xmlData.append("        </columns>\r\n");
        xmlData.append("        <browser file=\"\"/>\r\n");
        xmlData.append(
            "        <proxy host=\"\" port=\"\" use=\"false\" userpass=\"=\"/>\r\n");
        xmlData.append("    </options>\r\n");
        xmlData.append("</root>");

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(dateiname);
            fileWriter.write(xmlData.toString());
            fileWriter.close();
        }
        catch (IOException ioE) {
            logger.error(ioE);
        }
    }

    private class TxtFileFilter
        extends FileFilter {
        public boolean accept(File file) {
            if (!file.isFile()) {
                return true;
            }
            else {
                String name = file.getName();
                return (name.toLowerCase().endsWith(".ajl"));
            }
        }

        public String getDescription() {
            return "AJL-Dateien";
        }
    }
}
