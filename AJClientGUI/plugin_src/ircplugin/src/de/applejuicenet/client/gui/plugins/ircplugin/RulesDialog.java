package de.applejuicenet.client.gui.plugins.ircplugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.ZeichenErsetzer;

public class RulesDialog extends JDialog{
	private Logger logger;

    public RulesDialog(Frame parent, boolean modal){
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        try{
            setTitle("#applejuice - Chanrules");
            getContentPane().setLayout(new BorderLayout());
            JTextPane rules = new JTextPane();
            rules.setContentType("text/html");
            rules.setEditable(false);
            rules.setText(channelrules);
            rules.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() ==
                        HyperlinkEvent.EventType.ACTIVATED) {
                        if (e.getURL() != null) {
                            String url = e.getURL().toString();
                            if (url.length() != 0) {
                                executeLink(url);
                            }
                        }
                    }
                }
            });
            JScrollPane sp = new JScrollPane(rules);
            sp.setHorizontalScrollBarPolicy(JScrollPane.
                                            HORIZONTAL_SCROLLBAR_NEVER);
            getContentPane().add(sp, BorderLayout.CENTER);
            JButton ok = new JButton("OK");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    dispose();
                }
            });
            getContentPane().add(ok, BorderLayout.SOUTH);
            setSize(650, 500);
            Dimension size = getSize();
            Dimension screenSize = Toolkit.getDefaultToolkit().
                getScreenSize();
            setBounds( (screenSize.width - size.width) / 2,
                      (screenSize.height - size.height) / 2, size.width, size.height);
        }
        catch(Exception e){
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void executeLink(String link) {
        try {
            String browser = OptionsManagerImpl.getInstance().
                getStandardBrowser();
            try {
                Runtime.getRuntime().exec(new String[] {browser, link});
            }
            catch (Exception ex) {
                LanguageSelector ls = LanguageSelector.
                    getInstance();
                String nachricht = ZeichenErsetzer.korrigiereUmlaute(ls.
                    getFirstAttrbuteByTagName(".root.javagui.startup.updatefehlernachricht"));
                String titel = ZeichenErsetzer.korrigiereUmlaute(ls.
                    getFirstAttrbuteByTagName(".root.mainform.caption"));
                JOptionPane.showMessageDialog(this, nachricht,
                                              titel, JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private String channelrules =
        "<html><p>In diesem Channel gibt es Regeln, die zu beachten sind.</p></a>" +
        "<h1>Regeln</h1><ol><li><u>Keine Verbreitung/Unterst&uuml;tzung oder Andeutungen zur Verbreitung zu " +
        "illegalen Daten, Inhalten und Links jeglicher Art.</u><br>Dies betrifft ausdr&uuml;cklich jede " +
        "erdenkliche Form, so zB die Nutzung von Nicks wie aj-user^splintercell vor dem offiziellen " +
        "Releasetermin, VHosts, Quit-Messages, Chan-Notices und was es noch so alles gibt.<br><br>" +
        "<li><u>Keine Frage \"ob man fragen darf\". Keine Metafragen. Keine andauernden " +
        "Wiederholungen. (=Spamm)</u><br>Dieser Channel ist zu einem guten Teil dazu da" +
        ", um den Nutzern Unterst&uuml;tzung anzubieten, und um dem Entwickler R&uuml;" +
        "ckmeldungen zu geben, d.h. das Fragen bez&uuml;glich Applejuice oder Fehlerreports " +
        "ausdr&uuml;cklich erw&uuml;nscht sind. Frage dennoch freundlich, denn der Channel " +
        "garantiert nat&uuml;rlich keine Support. Fragen der Art \"Ist XYZ besser als " +
        "Applejuice?\" oder \"Hast du von XYZ Ahnung?\" sind letzlich immer subjektiv zu " +
        "beantworten. Versuche deshalb deine Fragen so exakt und zielgerichtet wie m&ouml;" +
        "glich zu stellen. Auch das st&auml;ndige Wechseln des Nickname ist nicht erw&uuml;nscht. Wozu gibt es /away. <br><br>" +
        "<li><u>Kein Spamm, keine Werbung, keine Floods, und schon gar kein \"Cracking\" !</u><br>" +
        "Jeglicher Spamm ist nicht erw&uuml;nscht: Mp3-Scripte, Onjoin-Spamm, " +
        "Ontext-Spamm usw. Es sind keine Channel-CTCPs erw&uuml;nscht. Werbung in jeglicher " +
        "Form ist nicht erw&uuml;nscht. &Uuml;berm&auml;ssige und sinnlose Nutzung von Farbcodes wird " +
        "als Spamm betrachtet. Das Verteilen von viralem/lethalen Code jeglicher Art " +
        "ist verboten und wird verfolgt!<br><br>" +
        "<li><u>Keine Beleidigungen. Verhalte und unterhalte dich wie von Angesicht zu " +
        "Angesicht.</u><br>Es gelten die gleichen sozialen Verhaltensgrundregeln wie " +
        "im normalem Leben. Das schliesst die demokratische Hierarchie des IRC ein " +
        "gelegentlicher Streit kann im Querry ausgetragen werden. Beleidigung und " +
        "auschliessliche Nutzung von F&auml;kalsprache werden nicht toleriert.<br><br>" +
        "<li><u>Keine Admin/Op/halfOp Betteleien, keine Querries an die Admins/OPs/" +
        "halfOPs ohne Einverst&auml;ndnis. Keine Bots ohne Einverst&auml;ndnis.</u><br></li></ol><p>&nbsp;</p>" +
        "<p>Die Anweisungen der Admins, Ops und HalfOPs sind zu " +
        "befolgen. Normalerweise macht dich ein netter Mensch auf deinen Regelverstoss " +
        "aufmerksam, da du offensichtlich nicht nach Betreten des Channels die " +
        "Chanrules gelesen hast. Solltest du dennoch nicht auf die Hinweise achten, " +
        "hast du dein Anwesenheitsrecht verwirkt. Auf eingeschaltetes Auto-Rejoin nach " +
        "Kick wird mit Ban reagiert, da Auto-Rejoin den Sinn eines Kick untergr&auml;bt.</p>" +
        "<p>Sollte ein \"kl&auml;rendes Gespr&auml;ch\" per Query auch nur zu " +
        "einem Ban gef&uuml;hrt haben, und das bereitet dir schlaflose N&auml;chte, dann " +
        "solltest du deinen Umgangston &uuml;berdenken. Wende dich in diesem Fall ans <a " +
        "HREF=\"http://community.applejuicenet.de/\">Forum</a>. <b>Und nun " +
        "viel Spass im IRC</b>:)</p>"+
        "<p>Diesen Dialog kannst Du &uuml;ber Optionen->Plugins->IrcPlugin deaktivieren.</p></html>";
}