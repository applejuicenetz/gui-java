package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/StartPanel.java,v 1.18 2003/08/25 13:18:35 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: StartPanel.java,v $
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
    private JLabel nachrichten;
    private JLabel label8;
    private JLabel netzwerk;
    private JLabel label6;
    private JLabel label9;

    private String label9Text;
    private String label6Text;
    private String firewallWarning;

    private Logger logger;

    NetworkInfo netInfo;

    private LanguageSelector languageSelector;

    public StartPanel(AppleJuiceDialog parent) {
        logger = Logger.getLogger(getClass());
        this.parent = parent;
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
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
        String coreVersion = ApplejuiceFassade.getInstance().getCoreVersion().getVersion();
        panel3.add(new JLabel("GUI: " + parent.GUI_VERSION + " Controller: " + ApplejuiceFassade.DATAMANAGER_VERSION + " Core: " +
                              coreVersion),
                   constraints);

        constraints.gridy = 2;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        ImageIcon icon3 = im.getIcon("warnung");
        JLabel label3 = new JLabel(icon3);
        panel3.add(label3, constraints);

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
        nachrichten = new JLabel();
        nachrichten.setForeground(APFEL_ROT);
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

        constraints.gridy = 8;
        constraints.insets.top = 5;
        label6 = new JLabel();
        panel3.add(label6, constraints);

        constraints.insets.top = 0;

        add(panel1, BorderLayout.NORTH);
        panel4.add(panel3, BorderLayout.NORTH);
        add(panel4, BorderLayout.CENTER);
        languageSelector = LanguageSelector.getInstance();
        languageSelector.addLanguageListener(this);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
                                                              DataUpdateListener.NETINFO_CHANGED);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
                                                              DataUpdateListener.STATUSBAR_CHANGED);
    }

    public void registerSelected() {
    }

    public void fireLanguageChanged() {
        netzwerk.setText("<html><font><h2>" +
                         ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"mainform", "html7"})) +
                         "</h2></font></html>");
        label8.setText("<html><font><h2>" +
                       ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"mainform", "html13"})) +
                       "</h2></font></html>");
        deinClient.setText("<html><font><h2>" +
                           ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                             getFirstAttrbuteByTagName(new String[]{"mainform", "html1"})) +
                           "</h2></font></html>");
        firewallWarning = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "firewallwarning",
                                                                                                   "caption"}));
        if (netInfo != null && netInfo.isFirewalled())
        {
            label7.setText(firewallWarning);
        }
        else
        {
            label7.setText("");
        }
        label9Text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                       getFirstAttrbuteByTagName(new String[]{"mainform", "html10"}));
        String temp = label9Text;
        temp = temp.replaceFirst("%s", "<Kein Server>");
        temp = temp.replaceFirst("%d",
                                 Integer.toString(ApplejuiceFassade.getInstance().
                                                  getAllServer().
                                                  size()));
        temp = temp.replaceAll("%s", "-");
        label9.setText(temp);
        label6Text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                       getFirstAttrbuteByTagName(new
                                                               String[]{"mainform", "status", "status2"}));
        temp = label6Text;
        if (netInfo != null)
        {
            temp = temp.replaceFirst("%d", netInfo.getAJUserGesamtAsStringWithPoints());
            temp = temp.replaceFirst("%d", netInfo.getAJAnzahlDateienAsStringWithPoints());
            temp = temp.replaceFirst("%s", netInfo.getAJGesamtShareWithPoints(0));
        }
        else
        {
            temp = temp.replaceFirst("%d", "0");
            temp = temp.replaceFirst("%d", "0");
            temp = temp.replaceFirst("%s", "0 MB");

        }
        label6.setText(temp);
        warnungen.setText("<html><font><h2>" +
                          ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "html15"})) +
                          "</h2></font></html>");
        try
        {
            String htmlText = HtmlLoader.getHtmlContent("www.applejuicenet.de", 80, HtmlLoader.GET,
                                                        "/inprog/news.php?version=" + ApplejuiceFassade.getInstance().getCoreVersion().getVersion());
            htmlText = "<html>" + htmlText + "</html>";
            nachrichten.setText(htmlText);
        }
        catch (WebSiteNotFoundException e)
        {
            if (logger.isEnabledFor(Level.INFO))
                logger.info("Versionsabhaengige Nachrichten konnten nicht geladen werden. Proxy?");
        }
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.NETINFO_CHANGED)
        {
            netInfo = (NetworkInfo) content;
            StringBuffer temp = new StringBuffer(label6Text);
            int pos = temp.indexOf("%d");
            temp.replace(pos, pos + 2, netInfo.getAJUserGesamtAsStringWithPoints());
            pos = temp.indexOf("%d");
            temp.replace(pos, pos + 2, netInfo.getAJAnzahlDateienAsStringWithPoints());
            pos = temp.indexOf("%s");
            temp.replace(pos, pos + 2, netInfo.getAJGesamtShareWithPoints(0));
            label6.setText(temp.toString());
            if (netInfo.isFirewalled())
            {
                label7.setText(firewallWarning);
            }
            else
            {
                label7.setText("");
            }
        }
        else if (type == DataUpdateListener.STATUSBAR_CHANGED)
        {
            String[] status = (String[]) content;
            StringBuffer temp = new StringBuffer(label9Text);
            int pos = temp.indexOf("%s");
            temp.replace(pos, pos + 2, status[1]);
            pos = temp.indexOf("%d");
            temp.replace(pos, pos + 2, Integer.toString(ApplejuiceFassade.getInstance().
                                                        getAllServer().
                                                        size()));
            pos = temp.indexOf("%s");
            temp.replace(pos, pos + 2, "-");
            pos = temp.indexOf("%s");
            temp.replace(pos, pos + 2, "-");
            label9.setText(temp.toString());
        }
    }
}