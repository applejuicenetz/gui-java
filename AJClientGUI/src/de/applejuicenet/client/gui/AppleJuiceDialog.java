package de.applejuicenet.client.gui;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.plugins.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/AppleJuiceDialog.java,v 1.46 2003/09/09 12:28:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AppleJuiceDialog.java,v $
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

    RegisterPanel registerPane;
    JLabel[] statusbar = new JLabel[6];
    private JMenu sprachMenu;
    private JMenu optionenMenu;
    private HashSet plugins;
    private JMenuItem menuItemOptionen = new JMenuItem();
    private JMenuItem menuItemUeber = new JMenuItem();
    private JFrame _this;
    private JButton pause = new JButton();
    private boolean paused = false;
    private static Logger logger;
    private JLabel memory = new JLabel();
    private Thread memoryWorker;

    private static AppleJuiceDialog theApp;

    public AppleJuiceDialog() {
        super();
        logger = Logger.getLogger(getClass());
        try
        {
            init();
            pack();
            _this = this;
            theApp = this;
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void addPluginToHashSet(PluginConnector plugin) {
        plugins.add(plugin);
    }

    public PluginConnector[] getPlugins() {
        return (PluginConnector[]) plugins.toArray(new PluginConnector[plugins.size()]);
    }

    public static AppleJuiceDialog getApp() {
        return theApp;
    }

    private void init() throws Exception {
        //todo
        pause.setEnabled(false);

        setTitle("AppleJuice Client");
        plugins = new HashSet();
        IconManager im = IconManager.getInstance();
        setIconImage(im.getIcon("applejuice").getImage());
        menuItemOptionen.setIcon(im.getIcon("optionen"));
        menuItemUeber.setIcon(im.getIcon("info"));

        setJMenuBar(createMenuBar());
        String path = System.getProperty("user.dir") + File.separator + "language" +
                File.separator;
        path += PropertiesManager.getOptionsManager().getSprache() + ".xml";
        registerPane = new RegisterPanel(this);
        LanguageSelector ls = LanguageSelector.getInstance(path);
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent evt) {
                        closeDialog(evt);
                    }
                });
        getContentPane().setLayout(new BorderLayout());
        registerPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RegisterI register = (RegisterI) registerPane.getSelectedComponent();
                register.registerSelected();
            }
        });
        getContentPane().add(registerPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());

        memory.setHorizontalAlignment(JLabel.RIGHT);
        memory.setBorder(new BevelBorder(BevelBorder.LOWERED));
        memory.setFont(new java.awt.Font("SansSerif", 0, 11));
        for (int i = 0; i < statusbar.length; i++)
        {
            statusbar[i] = new JLabel("            ");
            statusbar[i].setHorizontalAlignment(JLabel.RIGHT);
            statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
            statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
        }
        pause.setFont(new java.awt.Font("SansSerif", 0, 11));
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
        panel.add(memory, constraints);
        constraints.gridx = 3;
        panel.add(statusbar[2], constraints);
        constraints.gridx = 4;
        panel.add(statusbar[3], constraints);
        constraints.gridx = 5;
        panel.add(statusbar[4], constraints);
        constraints.gridx = 6;
        panel.add(statusbar[5], constraints);
        constraints.gridx = 7;
        panel.add(pause, constraints);
        getContentPane().add(panel, BorderLayout.SOUTH);

        //Tooltipps einstellen
        ToolTipManager.sharedInstance().setInitialDelay(1);
        ToolTipManager.sharedInstance().setDismissDelay(50000);
        fireLanguageChanged();
        ApplejuiceFassade dm = ApplejuiceFassade.getInstance();
        dm.addDataUpdateListener(this,
                                 DataUpdateListener.STATUSBAR_CHANGED);

        memoryWorker = new Thread() {
            public void run() {
                if (logger.isEnabledFor(Level.DEBUG))
                    logger.debug("MemoryWorkerThread gestartet. " + memoryWorker);
                try{
                    Runtime runtime = Runtime.getRuntime();
                    while (!isInterrupted())
                    {
                            final long free = runtime.freeMemory();
                            final long total = runtime.totalMemory();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    memory.setText("Mem: U/T " + parseGroesse(total-free) + " / " + parseGroesse(total));
                                }
                            });
                        try{
                            sleep(5000);
                        }
                        catch (InterruptedException e){
                            interrupt();
                        }
                    }
                }
                catch (Exception e)
                {
                    if (logger.isEnabledFor(Level.ERROR))
                        logger.error("Unbehandelte Exception", e);
                }
                if (logger.isEnabledFor(Level.DEBUG))
                    logger.debug("MemoryWorkerThread beendet. " + memoryWorker);
            }

            private String parseGroesse(long groesse) {
                double share = groesse;
                share = share / 1e5;
                String result = Double.toString(share);
                if (result.indexOf(".") + 3 < result.length())
                {
                    result = result.substring(0, result.indexOf(".") + 3);
                }
                result = result.replace('.', ',');
                return result + " MB";
            }
        };
        try{
            //http://download.berlios.de/applejuicejava/version.txt
            String strAktuellsteVersion = HtmlLoader.getHtmlContent("download.berlios.de", 80, HtmlLoader.GET,
                                                        "/applejuicejava/version.txt");
            if (strAktuellsteVersion.length()>0){
                int pos = ApplejuiceFassade.GUI_VERSION.indexOf(' ');
                double aktuelleVersion;
                if (pos!=-1){
                    aktuelleVersion = Double.parseDouble(ApplejuiceFassade.GUI_VERSION.substring(0, pos));
                }
                else{
                    aktuelleVersion = Double.parseDouble(ApplejuiceFassade.GUI_VERSION);
                }
                double aktuellsteVersion = Double.parseDouble(strAktuellsteVersion);
                if (aktuellsteVersion>aktuelleVersion){
                    String titel = ls.getFirstAttrbuteByTagName(new String[]{"javagui", "startup", "newversiontitel"});
                    String nachricht = ls.getFirstAttrbuteByTagName(new String[]{"javagui", "startup", "newversionnachricht"});
                    nachricht = nachricht.replaceFirst("%s", strAktuellsteVersion);
                    JOptionPane.showMessageDialog(this, nachricht, titel,
                            JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        catch (WebSiteNotFoundException e){
            if (logger.isEnabledFor(Level.INFO))
                logger.info("Aktualisierungsinformationen konnten nicht geladen werden. Proxy?");
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.INFO))
                logger.info("Aktualisierungsinformationen konnten nicht geladen werden. Server down?");
        }
        dm.startXMLCheck();
        memoryWorker.start();
    }

    private static void einstellungenSpeichern() {
        try{
            String sprachText = LanguageSelector.getInstance().
                    getFirstAttrbuteByTagName(new String[]{"Languageinfo", "name"});
            PropertiesManager.getOptionsManager().setSprache(sprachText);
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void closeDialog(WindowEvent evt) {
        int[] downloadWidths = DownloadPanel._this.getColumnWidths();
        int[] uploadWidths = UploadPanel._this.getColumnWidths();
        int[] serverWidths = ServerPanel._this.getColumnWidths();
        int[] searchWidths = SearchPanel._this.getColumnWidths();
        int[] shareWidths = SharePanel._this.getColumnWidths();
        Dimension dim = getSize();
        Point p = getLocationOnScreen();
        setVisible(false);
        if (memoryWorker!=null){
            memoryWorker.interrupt();
        }
        ApplejuiceFassade.getInstance().stopXMLCheck();
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        if (logger.isEnabledFor(Level.INFO))
            logger.info(nachricht);
        System.out.println(nachricht);
        einstellungenSpeichern();
        PositionManager pm = PropertiesManager.getPositionManager();
        pm.setMainXY(p);
        pm.setMainDimension(dim);
        pm.setDownloadWidths(downloadWidths);
        pm.setUploadWidths(uploadWidths);
        pm.setServerWidths(serverWidths);
        pm.setSearchWidths(searchWidths);
        pm.setShareWidths(shareWidths);
        pm.save();
        System.exit(0);
    }

    public static void closeWithErrormessage(String error, boolean speichereEinstellungen) {
        JOptionPane.showMessageDialog(theApp, error, "Fehler!",
                                      JOptionPane.OK_OPTION);
        ApplejuiceFassade.getInstance().stopXMLCheck();
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        Logger aLogger = Logger.getLogger(AppleJuiceDialog.class.getName());
        if (aLogger.isEnabledFor(Level.INFO))
            aLogger.info(nachricht);
        System.out.println(nachricht);
        if (speichereEinstellungen)
            einstellungenSpeichern();
        System.out.println("Fehler: " + error);
        System.exit(-1);
    }

    protected JMenuBar createMenuBar() {
        try{
            String path = System.getProperty("user.dir") + File.separator + "language" +
                    File.separator;
            File languagePath = new File(path);
            if (!languagePath.isDirectory())
            {
                closeWithErrormessage("Der Ordner 'language' für die Sprachauswahl xml-Dateien ist nicht vorhanden.\r\nappleJuice wird beendet.", false);
            }
            String[] tempListe = languagePath.list();
            HashSet sprachDateien = new HashSet();
            for (int i = 0; i < tempListe.length; i++)
            {
                if (tempListe[i].indexOf(".xml") != -1)
                {
                    sprachDateien.add(tempListe[i]);
                }
            }
            if (sprachDateien.size() == 0)
            {
                closeWithErrormessage("Es sind keine xml-Dateien für die Sprachauswahl im Ordner 'language' vorhanden.\r\nappleJuice wird beendet.", false);
            }
            JMenuBar menuBar = new JMenuBar();
            optionenMenu = new JMenu("Extras");
            menuItemOptionen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    OptionsDialog od = new OptionsDialog(_this);
                    Dimension appDimension = od.getSize();
                    Dimension screenSize = _this.getSize();
                    od.setLocation((screenSize.width - appDimension.width) / 4,
                                   (screenSize.height - appDimension.height) / 4);
                    od.show();
                }
            });
            menuItemUeber.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AboutDialog aboutDialog = new AboutDialog(_this, true);
                    Dimension appDimension = aboutDialog.getSize();
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    aboutDialog.setLocation((screenSize.width - appDimension.width)/2,
                                   (screenSize.height - appDimension.height)/2);
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
            while (it.hasNext())
            {
                String sprachText = LanguageSelector.getInstance(path + (String) it.next()).
                        getFirstAttrbuteByTagName(new String[]{"Languageinfo", "name"});
                JCheckBoxMenuItem rb = new JCheckBoxMenuItem(sprachText);
                if (PropertiesManager.getOptionsManager().getSprache().equalsIgnoreCase(sprachText))
                {
                    rb.setSelected(true);
                }
                Image img = Toolkit.getDefaultToolkit().getImage(path + sprachText.toLowerCase() + ".gif");
                ImageIcon result = new ImageIcon();
                result.setImage(img);
                rb.setIcon(result);

                sprachMenu.add(rb);
                rb.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ae) {
                        JCheckBoxMenuItem rb2 = (JCheckBoxMenuItem) ae.
                                getSource();
                        if (rb2.isSelected())
                        {
                            String path = System.getProperty("user.dir") + File.separator +
                                    "language" + File.separator;
                            String dateiName = path + rb2.getText().toLowerCase() + ".xml";
                            LanguageSelector.getInstance(dateiName);
                        }
                    }
                });
                lafGroup.add(rb);
            }
            return menuBar;
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
            return null;
        }
    }

    public void fireLanguageChanged() {
        try{
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            String versionsNr = ApplejuiceFassade.getInstance().getCoreVersion().getVersion();
            setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                       getFirstAttrbuteByTagName(new
                                                               String[]{"mainform", "caption"})) + " (Core " + versionsNr + ")");
            sprachMenu.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                 getFirstAttrbuteByTagName(new String[]{"einstform", "languagesheet",
                                                                                                        "caption"})));
            menuItemOptionen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"mainform", "optbtn", "caption"})));
            menuItemOptionen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"mainform", "optbtn", "hint"})));
            menuItemUeber.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"mainform", "aboutbtn", "caption"})));
            menuItemUeber.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"mainform", "aboutbtn", "hint"})));
            optionenMenu.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "menu", "extras"})));
            if (paused)
            {
                pause.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                getFirstAttrbuteByTagName(new String[]{"javagui", "mainform",
                                                                                                       "fortsetzen"})));
            }
            else
            {
                pause.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                getFirstAttrbuteByTagName(new String[]{"javagui", "mainform",
                                                                                                       "pause"})));
            }
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void fireContentChanged(int type, Object content) {
        try{
            if (type == DataUpdateListener.STATUSBAR_CHANGED)
            {
                String[] status = (String[]) content;
                statusbar[0].setText(status[0]);
                statusbar[1].setText(status[1]);
                statusbar[2].setText(status[2]);
                statusbar[3].setText(status[3]);
                statusbar[4].setText(status[4]);
                statusbar[5].setText(status[5]);
            }
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }
}
