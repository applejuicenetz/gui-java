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
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/AppleJuiceDialog.java,v 1.54 2003/10/31 11:31:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AppleJuiceDialog.java,v $
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
    private HashSet plugins;
    private JMenuItem menuItemOptionen = new JMenuItem();
    private JMenuItem menuItemUeber = new JMenuItem();
    private JFrame _this;
    private JButton sound = new JButton();
    private String keinServer = "";
    private static Logger logger;
    public static boolean rewriteProperties = false;

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
                RegisterI register = (RegisterI) registerPane.getSelectedComponent();
                register.registerSelected();
            }
        });
        getContentPane().add(registerPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());

        for (int i = 0; i < statusbar.length; i++)
        {
            statusbar[i] = new JLabel("            ");
            statusbar[i].setHorizontalAlignment(JLabel.RIGHT);
            statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
            statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
        }
//        pause.setFont(new java.awt.Font("SansSerif", 0, 11));
        sound.addActionListener(new ActionListener(){
            {
                if (PropertiesManager.getOptionsManager().isSoundEnabled()){
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                }
                else{
                    sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
                }
            }

            public void actionPerformed(ActionEvent ae){
                OptionsManager om = PropertiesManager.getOptionsManager();
                om.enableSound(!om.isSoundEnabled());
                if (om.isSoundEnabled()){
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                }
                else{
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
        int[] shareWidths = SharePanel._this.getColumnWidths();
        Dimension dim = getSize();
        Point p = getLocationOnScreen();
        setVisible(false);
        ApplejuiceFassade.getInstance().stopXMLCheck();
        if (rewriteProperties){
            restorePropertiesXml();
        }
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        if (logger.isEnabledFor(Level.INFO))
            logger.info(nachricht);
        System.out.println(nachricht);
        if (!rewriteProperties){
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

    public static void closeWithErrormessage(String error, boolean speichereEinstellungen) {
        JOptionPane.showMessageDialog(theApp, error, "Fehler!",
                                      JOptionPane.OK_OPTION);
        if (rewriteProperties){
            restorePropertiesXml();
        }
        else{
            ApplejuiceFassade.getInstance().stopXMLCheck();
        }
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        Logger aLogger = Logger.getLogger(AppleJuiceDialog.class.getName());
        if (aLogger.isEnabledFor(Level.INFO))
            aLogger.info(nachricht);
        System.out.println(nachricht);
        if (speichereEinstellungen && !rewriteProperties)
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
                closeWithErrormessage("Der Ordner " + path + " für die Sprachauswahl xml-Dateien ist nicht vorhanden." +
                                      "\r\nappleJuice wird beendet.", false);
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
                closeWithErrormessage("Es sind keine xml-Dateien für die Sprachauswahl im Ordner " + path + " vorhanden." +
                                      "\r\nappleJuice wird beendet.", false);
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
            keinServer = languageSelector.getFirstAttrbuteByTagName(new String[]{
                "javagui", "mainform", "keinserver"});
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
/*            if (paused)
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
            }*/
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void fireContentChanged(int type, Object content) {
        try{
            if (type == DataUpdateListener.INFORMATION_CHANGED)
            {
                Information information = (Information) content;
                statusbar[0].setText(information.getVerbindungsStatusAsString());
                if (information.getVerbindungsStatus()==Information.NICHT_VERBUNDEN){
                    statusbar[1].setText(keinServer);
                }
                else{
                    statusbar[1].setText(information.getServerName());
                }
                statusbar[2].setText(information.getUpDownAsString());
                statusbar[3].setText(information.getUpDownSessionAsString());
                statusbar[4].setText(information.getExterneIP());
                statusbar[5].setText(information.getCreditsAsString());
            }
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private static void restorePropertiesXml(){
        String dateiname = System.getProperty("user.dir") + File.separator +
                "properties.xml";
        StringBuffer xmlData = new StringBuffer();

        xmlData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlData.append("<root>");
        xmlData.append("    <options firststart=\"true\" sprache=\"deutsch\">");
        xmlData.append("        <remote host=\"localhost\" passwort=\"\"  port=\"9851\"/>");
        xmlData.append("        <logging level=\"INFO\"/>");
        xmlData.append("        <download uebersicht=\"true\"/>");
        xmlData.append("        <farben aktiv=\"true\">");
        xmlData.append("            <hintergrund downloadFertig=\"-13382656\" quelle=\"-205\"/>");
        xmlData.append("        </farben>");
        xmlData.append("        <location>");
        xmlData.append("            <main height=\"\" width=\"\" x=\"\" y=\"\"/>");
        xmlData.append("            <download column0=\"80\" column1=\"80\" column2=\"80\"");
        xmlData.append("                column3=\"80\" column4=\"81\" column5=\"80\" column6=\"81\"");
        xmlData.append("                column7=\"80\" column8=\"81\" column9=\"80\"/>");
        xmlData.append("            <upload column0=\"136\" column1=\"111\" column2=\"111\"");
        xmlData.append("                column3=\"111\" column4=\"111\" column5=\"112\" column6=\"111\"/>");
        xmlData.append("            <server column0=\"201\" column1=\"201\" column2=\"201\" column3=\"200\"/>");
        xmlData.append("            <search column0=\"103\" column1=\"103\" column2=\"103\"");
        xmlData.append("                column3=\"103\" column4=\"103\" column5=\"103\"/>");
        xmlData.append("            <share column0=\"194\" column1=\"195\" column2=\"194\"/>");
        xmlData.append("        </location>");
        xmlData.append("        <proxy host=\"\" port=\"\" use=\"false\" userpass=\"=\"/>");
        xmlData.append("        <server pfad=\"/?t=1592\" url=\"http://www.applejuicenet.org\"/>");
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
