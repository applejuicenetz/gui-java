package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.shared.WebsiteContentLoader;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import javax.swing.JOptionPane;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/StartPanel.java,v 1.39 2004/01/05 14:13:13 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: StartPanel.java,v $
 * Revision 1.39  2004/01/05 14:13:13  maj0r
 * Links im Startbereich sind jetzt anklickbar, sofern ein Standardbrowser ausgewaehlt ist.
 *
 * Revision 1.38  2003/12/31 15:46:48  maj0r
 * Bug #19 gefixt (Danke an dsp2004)
 * Stringverwendung in StringBuffer umgebaut.
 *
 * Revision 1.37  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.36  2003/12/29 09:39:21  maj0r
 * Alte BugIDs entfernt, da auf neuen Bugtracker auf bugs.applejuicenet.de umgestiegen wurde.
 *
 * Revision 1.35  2003/12/28 10:31:31  maj0r
 * Nullpointer behoben der auftrat, wenn der verbundene Server keinen Namen hat (Danke an paderborner).
 *
 * Revision 1.34  2003/12/27 13:33:18  maj0r
 * Text von Netzwerk, Neuigkeiten und Nachrichten ist nun auch schwarz (Danke an muhviestarr).
 * Die Ueberschrift "Warnungen" auf der Startseite wird nun ausgeblendet, wenn es keine Warnungen gibt (Danke an muhviestarr).
 *
 * Revision 1.33  2003/12/17 11:06:30  maj0r
 * RegisterI erweitert, um auf Verlassen eines Tabs reagieren zu koennen.
 *
 * Revision 1.32  2003/10/21 11:36:32  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 * Revision 1.31  2003/10/09 15:42:52  maj0r
 * Bug behoben, dass nicht immer der aktuell verbundene Server angezeigt wurde.
 *
 * Revision 1.30  2003/10/05 11:54:48  maj0r
 * Kleinen Fehler behoben.
 *
 * Revision 1.29  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
 * Revision 1.28  2003/10/04 15:48:04  maj0r
 * Unnoetige Imports entfernt.
 *
 * Revision 1.27  2003/10/04 15:47:13  maj0r
 * Schoenheitskorrektur
 *
 * Revision 1.26  2003/10/01 20:10:44  maj0r
 * Bischen Logging hinzu gefuegt.
 *
 * Revision 1.25  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 * Revision 1.24  2003/09/11 08:39:30  maj0r
 * Start durch Einbau von Threads beschleunigt.
 *
 * Revision 1.23  2003/09/11 06:54:15  maj0r
 * Auf neues Sessions-Prinzip umgebaut.
 * Sprachenwechsel korrigert, geht nun wieder flott.
 *
 * Revision 1.22  2003/09/06 16:25:39  maj0r
 * Newsanfrage an neue Domain angepasst.
 * HtmlLoader korrigiert.
 *
 * Revision 1.21  2003/09/06 14:50:50  maj0r
 * Fehlerbehandlung verbessert.
 *
 * Revision 1.20  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.19  2003/08/28 10:57:04  maj0r
 * Versionierung geaendert und erhoeht.
 * Version 0.16 Beta.
 *
 * Revision 1.18  2003/08/25 13:18:35  maj0r
 * Farbe in ein geiles Rot geaendert,
 *
 * Revision 1.17  2003/08/20 07:49:50  maj0r
 * Programmstart beschleunigt.
 *
 * Revision 1.16  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.15  2003/08/12 11:08:23  maj0r
 * Anzeige korrigiert.
 *
 * Revision 1.14  2003/08/11 16:52:39  maj0r
 * Ausgabefehler korrigiert.
 *
 * Revision 1.13  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 * Revision 1.12  2003/08/09 16:47:42  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.11  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.10  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.9  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.8  2003/06/13 15:07:30  maj0r
 * Versionsanzeige hinzugefügt.
 * Da der Controllerteil refactort werden kann, haben Controller und GUI separate Versionsnummern.
 *
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class StartPanel
    extends JPanel
    implements LanguageListener, RegisterI, DataUpdateListener {
    private static final Color APFEL_ROT = new Color(146, 36, 60);

    private AppleJuiceDialog parent;

    private JLabel warnungen;
    private JLabel deinClient;
    private JLabel label7;
    private JTextPane nachrichten;
    private JLabel label8;
    private JLabel netzwerk;
    private JLabel label6;
    private JLabel label9;
    private JLabel label10;
    private JLabel version;
    private JLabel warnungIcon;

    private String label9Text;
    private String label10Text;
    private String label6Text;
    private String firewallWarning;

    private String keinServer = "";

    private Logger logger;

    private NetworkInfo netInfo;
    private Information information;

    private LanguageSelector languageSelector;

    public StartPanel(AppleJuiceDialog parent) {
        logger = Logger.getLogger(getClass());
        this.parent = parent;
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        JPanel panel3 = new JPanel(new GridBagLayout());
        panel3.setBackground(Color.WHITE);
        JPanel panel4 = new JPanel(new BorderLayout());
        panel4.setBackground(Color.WHITE);

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.setBackground(Color.WHITE);
        panel1.setBackground(Color.WHITE);

        IconManager im = IconManager.getInstance();
        ImageIcon icon1 = im.getIcon("applejuicebanner");
        JLabel label1 = new JLabel(icon1);
        panel1.add(label1);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.left = 5;

        ImageIcon icon2 = im.getIcon("start");
        JLabel label2 = new JLabel(icon2);
        panel3.add(label2, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        deinClient = new JLabel("<html><font><h2>Dein Core</h2></font></html>");
        deinClient.setForeground(APFEL_ROT);
        panel3.add(deinClient, constraints);
        constraints.weightx = 0;

        constraints.gridy = 1;
        constraints.insets.left = 15;
        version = new JLabel();
        panel3.add(version, constraints);

        constraints.gridy = 2;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        ImageIcon icon3 = im.getIcon("warnung");
        warnungIcon = new JLabel(icon3);
        panel3.add(warnungIcon, constraints);

        constraints.gridx = 1;
        warnungen = new JLabel("<html><font><h2>Warnungen</h2></font></html>");
        warnungen.setForeground(APFEL_ROT);
        panel3.add(warnungen, constraints);

        constraints.gridy = 3;
        constraints.insets.left = 15;
        label7 = new JLabel();
        label7.setForeground(Color.RED);

        panel3.add(label7, constraints);

        constraints.gridy = 4;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        ImageIcon icon4 = im.getIcon("netzwerk");
        JLabel label4 = new JLabel(icon4);
        panel3.add(label4, constraints);

        constraints.gridx = 1;
        label8 = new JLabel(
            "<html><font><h2>Netzwerk, Neuigkeiten und Nachrichten</h2></font></html>");
        label8.setForeground(APFEL_ROT);
        panel3.add(label8, constraints);

        constraints.gridy = 5;
        constraints.insets.left = 15;
        nachrichten = new JTextPane();
        panel3.add(nachrichten, constraints);

        constraints.gridy = 6;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        ImageIcon icon5 = im.getIcon("server");
        JLabel label5 = new JLabel(icon5);
        panel3.add(label5, constraints);

        constraints.gridx = 1;
        netzwerk = new JLabel(
            "<html><font><h2>appleJuice Netzwerk</h2></font></html>");
        netzwerk.setForeground(APFEL_ROT);
        panel3.add(netzwerk, constraints);

        constraints.gridy = 7;
        constraints.insets.left = 15;
        label9 = new JLabel("Du bist mit xxxx vielleicht verbunden.");
        panel3.add(label9, constraints);

        label10 = new JLabel("10 Verbindungen.");
        constraints.gridy = 8;
        constraints.insets.top = 5;
        panel3.add(label10, constraints);

        constraints.gridy = 9;
        label6 = new JLabel();
        panel3.add(label6, constraints);

        constraints.insets.top = 0;

        add(panel1, BorderLayout.NORTH);
        panel4.add(panel3, BorderLayout.NORTH);
        add(panel4, BorderLayout.CENTER);
        languageSelector = LanguageSelector.getInstance();
        languageSelector.addLanguageListener(this);
        Thread aktualisierungWorker = new Thread() {
            public void run() {
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("AktualisierungsWorkerThread gestartet. " + this);
                }
                try {
                    String coreVersion = ApplejuiceFassade.getInstance().
                        getCoreVersion().getVersion();
                    version.setText("<html>GUI: " +
                                    ApplejuiceFassade.GUI_VERSION +
                                    "<br>Core: " +
                                    coreVersion + "</html>");
                    String nachricht = "verwendeter Core: " + coreVersion;
                    if (logger.isEnabledFor(Level.INFO)) {
                        logger.info(nachricht);

                    }
                    String htmlText = WebsiteContentLoader.getWebsiteContent(
                        "http://www.applejuicenet.org", 80,
                        "/inprog/news.php?version=" +
                        ApplejuiceFassade.getInstance().getCoreVersion().
                        getVersion());

                    int pos = htmlText.toLowerCase().indexOf("<html>");
                    if (pos != -1) {
                        htmlText = htmlText.substring(pos);
                    }
                    else {
                        htmlText = "<html>" + htmlText + "</html>";
                    }
                    nachrichten.setContentType("text/html");
                    nachrichten.setEditable(false);
                    nachrichten.setText(htmlText);
                    nachrichten.addHyperlinkListener(new HyperlinkListener (){
                        public void hyperlinkUpdate(HyperlinkEvent e) {
                           if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                               String url = e.getURL().toString();
                               if (url.length() != 0){
                                   executeLink(url);
                               }
                           }
                       }
                    });
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.INFO)) {
                        logger.info(
                            "Versionsabhaengige Nachrichten konnten nicht geladen werden. Server down?");
                    }
                }
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("AktualisierungsWorkerThread beendet. " + this);
                }
            }
        };
        aktualisierungWorker.start();
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
            DataUpdateListener.NETINFO_CHANGED);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
            DataUpdateListener.INFORMATION_CHANGED);
    }

    private void executeLink(String link) {
        try {
            String browser = PropertiesManager.getOptionsManager().
                getStandardBrowser();
            try {
                Runtime.getRuntime().exec(new String[] {browser, link});
            }
            catch (Exception ex) {
                LanguageSelector ls = LanguageSelector.
                    getInstance();
                String nachricht = ZeichenErsetzer.korrigiereUmlaute(ls.getFirstAttrbuteByTagName(new
                    String[] {"javagui", "startup",
                    "updatefehlernachricht"}));
                String titel = ZeichenErsetzer.korrigiereUmlaute(ls.getFirstAttrbuteByTagName(new
                    String[] {"mainform", "caption"}));
                JOptionPane.showMessageDialog(this, nachricht,
                                              titel,
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void registerSelected() {
    }

    public void fireLanguageChanged() {
        try {
            keinServer = languageSelector.getFirstAttrbuteByTagName(new String[] {
                "javagui", "mainform", "keinserver"});
            netzwerk.setText("<html><font><h2>" +
                             ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "html7"})) +
                             "</h2></font></html>");
            label8.setText("<html><font><h2>" +
                           ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "html13"})) +
                           "</h2></font></html>");
            deinClient.setText("<html><font><h2>" +
                               ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "html1"})) +
                               "</h2></font></html>");
            firewallWarning = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "firewallwarning",
                                          "caption"}));
            if (netInfo != null && netInfo.isFirewalled()) {
                warnungen.setVisible(true);
                warnungIcon.setVisible(true);
                label7.setText(firewallWarning);
            }
            else {
                warnungen.setVisible(false);
                warnungIcon.setVisible(false);
                label7.setText("");
            }
            label9Text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "html10"}));
            StringBuffer temp = new StringBuffer(label9Text);
            int pos = temp.indexOf("%s");
            if (pos != -1){
                temp.replace(pos, pos + 2, keinServer);
            }
            pos = temp.indexOf("%d");
            if (pos != -1){
                temp.replace(pos, pos + 2 , Integer.toString(ApplejuiceFassade.
                    getInstance().
                    getAllServer().
                    size()));
            }
            label9.setText(temp.toString());
            label10Text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "status",
                                          "status0"}));
            temp = new StringBuffer(label10Text);
            pos = temp.indexOf("%d");
            if (pos != -1){
                if (information != null) {
                    temp.replace(pos, pos + 2,
                                             Long.toString(information.
                        getOpenConnections()));
                }
                else {
                    temp.replace(pos, pos + 2, "0");
                }
            }
            label10.setText(temp.toString());

            label6Text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new
                                          String[] {"mainform", "status",
                                          "status2"}));
            temp = new StringBuffer(label6Text);
            if (netInfo != null) {
                pos = temp.indexOf("%d");
                if (pos != -1){
                    temp.replace(pos, pos + 2,
                                        netInfo.
                                        getAJUserGesamtAsStringWithPoints());
                }
                pos = temp.indexOf("%d");
                if (pos != -1){
                    temp.replace(pos, pos + 2, netInfo.
                        getAJAnzahlDateienAsStringWithPoints());
                }
                pos = temp.indexOf("%s");
                if (pos != -1){
                    temp.replace(pos, pos + 2,
                                             netInfo.getAJGesamtShareWithPoints(
                        0));
                }
            }
            else {
                pos = temp.indexOf("%d");
                if (pos != -1){
                    temp.replace(pos, pos + 2, "0");
                }
                pos = temp.indexOf("%d");
                if (pos != -1){
                    temp.replace(pos, pos + 2, "0");
                }
                pos = temp.indexOf("%s");
                if (pos != -1){
                    temp.replace(pos, pos + 2, "0 MB");
                }

            }
            label6.setText(temp.toString());
            warnungen.setText("<html><font><h2>" +
                              ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "html15"})) +
                              "</h2></font></html>");
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.NETINFO_CHANGED) {
                netInfo = (NetworkInfo) content;
                StringBuffer temp = new StringBuffer(label6Text);
                int pos = temp.indexOf("%d");
                if (pos != -1){
                    temp.replace(pos, pos + 2,
                                 netInfo.getAJUserGesamtAsStringWithPoints());
                }
                pos = temp.indexOf("%d");
                if (pos != -1){
                    temp.replace(pos, pos + 2,
                                 netInfo.getAJAnzahlDateienAsStringWithPoints());
                }
                pos = temp.indexOf("%s");
                if (pos != -1){
                    temp.replace(pos, pos + 2,
                                 netInfo.getAJGesamtShareWithPoints(0));
                }
                label6.setText(temp.toString());
                if (netInfo.isFirewalled()) {
                    warnungen.setVisible(true);
                    warnungIcon.setVisible(true);
                    label7.setText(firewallWarning);
                }
                else {
                    warnungen.setVisible(false);
                    warnungIcon.setVisible(false);
                    label7.setText("");
                }
            }
            else if (type == DataUpdateListener.INFORMATION_CHANGED) {
                information = (Information) content;
                StringBuffer temp = new StringBuffer(label9Text);
                int pos = temp.indexOf("%s");
                if (pos != -1) {
                    if (information.getVerbindungsStatus() ==
                        Information.VERBUNDEN) {
                        if (information.getServerName() == null
                            || information.getServerName().length() == 0) {
                            temp.replace(pos, pos + 2, "?");
                        }
                        else {
                            temp.replace(pos, pos + 2,
                                         information.getServerName());
                        }
                    }
                    else {
                        temp.replace(pos, pos + 2, keinServer);
                    }
                    pos = temp.indexOf("%d");
                    if (pos != -1) {
                        temp.replace(pos, pos + 2, Integer.toString(
                            ApplejuiceFassade.
                            getInstance().
                            getAllServer().
                            size()));
                    }
                }
                label9.setText(temp.toString());
                temp = new StringBuffer(label10Text);
                pos = temp.indexOf("%d");
                if (pos != -1) {
                    temp.replace(pos, pos + 2, Long.toString(information.
                        getOpenConnections()));
                }
                label10.setText(temp.toString());
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void lostSelection() {
    }
}