package de.applejuicenet.client.gui;

import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.apache.log4j.*;
import com.l2fprod.gui.plaf.skin.*;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.plugins.*;
import de.applejuicenet.client.gui.tools.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/AppleJuiceDialog.java,v 1.72 2004/01/02 17:57:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AppleJuiceDialog.java,v $
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
 * Historie eingefügt.
 *
 *
 */

public class AppleJuiceDialog
    extends JFrame
    implements LanguageListener, DataUpdateListener {

    private RegisterPanel registerPane;
    private JLabel[] statusbar = new JLabel[6];
    private JMenu sprachMenu;
    private JMenu optionenMenu;
    private JMenu themesMenu = null;
    private JMenu coreMenu;
    private HashSet plugins;
    private JMenuItem menuItemOptionen = new JMenuItem();
    private JMenuItem menuItemCoreBeenden = new JMenuItem();
    private JMenuItem menuItemUeber = new JMenuItem();
    private JFrame _this;
    private JButton sound = new JButton();
    private JButton memory = new JButton();
    private String keinServer = "";
    private static Logger logger;
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
                        themesDateien.add(themeFiles[i].toURL());
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
        logger = Logger.getLogger(getClass());
        try {
            init();
            pack();
            _this = this;
            theApp = this;
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
        setTitle("AppleJuice Client");
        plugins = new HashSet();
        IconManager im = IconManager.getInstance();
        setIconImage(im.getIcon("applejuice").getImage());
        menuItemOptionen.setIcon(im.getIcon("optionen"));
        menuItemUeber.setIcon(im.getIcon("info"));

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

    private static void einstellungenSpeichern() {
        try {
            String sprachText = LanguageSelector.getInstance().
                getFirstAttrbuteByTagName(new String[] {"Languageinfo", "name"});
            PropertiesManager.getOptionsManager().setSprache(sprachText);
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
        int[] downloadWidths = DownloadPanel._this.getColumnWidths();
        int[] uploadWidths = UploadPanel._this.getColumnWidths();
        int[] serverWidths = ServerPanel._this.getColumnWidths();
        int[] shareWidths = SharePanel._this.getColumnWidths();
        Dimension dim = getSize();
        Point p = getLocationOnScreen();
        setVisible(false);
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
            PositionManager pm = PropertiesManager.getPositionManager();
            pm.setMainXY(p);
            pm.setMainDimension(dim);
            pm.setDownloadWidths(downloadWidths);
            pm.setUploadWidths(uploadWidths);
            pm.setServerWidths(serverWidths);
            pm.setShareWidths(shareWidths);
            pm.save();
        }
        System.exit(0);
    }

    public static void closeWithErrormessage(String error,
                                             boolean speichereEinstellungen) {
        JOptionPane.showMessageDialog(theApp, error, "Fehler!",
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
                    OptionsDialog od = new OptionsDialog(_this);
                    Dimension appDimension = od.getSize();
                    Dimension screenSize = _this.getSize();
                    od.setLocation( (screenSize.width - appDimension.width) / 4,
                                   (screenSize.height - appDimension.height) /
                                   4);
                    od.show();
                }
            });
            menuItemCoreBeenden.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.getApp(), bestaetigung,
                                                  "appleJuice Client", JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.WARNING_MESSAGE);
                    if (result==JOptionPane.YES_OPTION){
                        ApplejuiceFassade.getInstance().exitCore();
                    }
                }
            });
            menuItemUeber.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AboutDialog aboutDialog = new AboutDialog(_this, true);
                    Dimension appDimension = aboutDialog.getSize();
                    Dimension screenSize = Toolkit.getDefaultToolkit().
                        getScreenSize();
                    aboutDialog.setLocation( (screenSize.width -
                                              appDimension.width) / 2,
                                            (screenSize.height -
                                             appDimension.height) / 2);
                    aboutDialog.show();
                }
            });
            optionenMenu.add(menuItemOptionen);
            optionenMenu.add(menuItemUeber);
            menuBar.add(optionenMenu);

            sprachMenu = new JMenu("Sprache");
            menuBar.add(sprachMenu);
            ButtonGroup lafGroup = new ButtonGroup();

            Iterator it = sprachDateien.iterator();
            while (it.hasNext()) {
                String sprachText = LanguageSelector.getInstance(path +
                    (String) it.next()).
                    getFirstAttrbuteByTagName(new String[] {"Languageinfo",
                                              "name"});
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
                        themesDateien.add(themeFiles[i].toURL());
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
                JMenuItem menuItem = new JMenuItem();
                menuItem.setText("deaktivieren");
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ce) {
                        activateThemeSupport(false);
                    }
                });
                themesMenu.add(menuItem);
            }
            else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setText("aktivieren");
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ce) {
                        activateThemeSupport(true);
                    }
                });
                themesMenu.add(menuItem);
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

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            String versionsNr = ApplejuiceFassade.getInstance().getCoreVersion().
                getVersion();
            titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new
                                          String[] {"mainform", "caption"})) +
                " (Core " + versionsNr +
                " - GUI " + ApplejuiceFassade.GUI_VERSION + ")";
            setTitle(titel);
            keinServer = languageSelector.getFirstAttrbuteByTagName(new String[] {
                "javagui", "mainform", "keinserver"});
            themeSupportTitel = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "caption"}));
            themeSupportNachricht = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "mainform",
                                          "themesupportnachricht"}));
            sprachMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"einstform",
                                          "languagesheet",
                                          "caption"})));
            menuItemOptionen.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "optbtn",
                                          "caption"})));
            menuItemOptionen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "optbtn",
                                          "hint"})));
            menuItemCoreBeenden.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "menu",
                                          "corebeenden"})));
            menuItemCoreBeenden.setToolTipText(ZeichenErsetzer.
                                               korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "menu",
                                          "corebeendenhint"})));
            menuItemUeber.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "aboutbtn",
                                          "caption"})));
            menuItemUeber.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "aboutbtn",
                                          "hint"})));
            optionenMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "menu",
                                          "extras"})));
            themesMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "menu",
                                          "themes"})));
            bestaetigung = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "menu",
                                          "bestaetigung"}));
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

    private static void restorePropertiesXml() {
        String dateiname = System.getProperty("user.dir") + File.separator +
            "properties.xml";
        StringBuffer xmlData = new StringBuffer();

        xmlData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlData.append("<root>");
        xmlData.append("    <options firststart=\"true\" sound=\"true\" sprache=\"deutsch\" themes=\"true\" defaulttheme=\"aquathemepack\"");
        xmlData.append(
            "             linklistenerport=\"8768\" versionsinfo=\"1\" >");
        xmlData.append(
            "        <remote host=\"localhost\" passwort=\"\"  port=\"9851\"/>");
        xmlData.append("        <logging level=\"INFO\"/>");
        xmlData.append("        <download uebersicht=\"true\"/>");
        xmlData.append("        <farben aktiv=\"true\">");
        xmlData.append(
            "            <hintergrund downloadFertig=\"-13382656\" quelle=\"-205\"/>");
        xmlData.append("        </farben>");
        xmlData.append("        <location>");
        xmlData.append(
            "            <main height=\"\" width=\"\" x=\"\" y=\"\"/>");
        xmlData.append(
            "            <download column0=\"80\" column1=\"80\" column2=\"80\"");
        xmlData.append(
            "                column3=\"80\" column4=\"81\" column5=\"80\" column6=\"81\"");
        xmlData.append(
            "                column7=\"80\" column8=\"81\" column9=\"80\"/>");
        xmlData.append(
            "            <upload column0=\"136\" column1=\"111\" column2=\"111\"");
        xmlData.append("                column3=\"111\" column4=\"111\" column5=\"112\" column6=\"111\"/>");
        xmlData.append("            <server column0=\"201\" column1=\"201\" column2=\"201\" column3=\"200\"/>");
        xmlData.append(
            "            <search column0=\"103\" column1=\"103\" column2=\"103\"");
        xmlData.append(
            "                column3=\"103\" column4=\"103\" column5=\"103\"/>");
        xmlData.append(
            "            <share column0=\"194\" column1=\"195\" column2=\"194\"/>");
        xmlData.append("        </location>");
        xmlData.append(
            "        <proxy host=\"\" port=\"\" use=\"false\" userpass=\"=\"/>");
        xmlData.append("    </options>");
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
}
