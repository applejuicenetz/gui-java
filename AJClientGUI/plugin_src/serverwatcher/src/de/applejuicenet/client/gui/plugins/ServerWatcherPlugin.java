package de.applejuicenet.client.gui.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import de.applejuicenet.client.gui.plugins.serverwatcher.NewServerDialog;
import de.applejuicenet.client.gui.plugins.serverwatcher.ServerConfig;
import de.applejuicenet.client.gui.plugins.serverwatcher.ServerXML;
import de.applejuicenet.client.shared.ProxySettings;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/serverwatcher/src/de/applejuicenet/client/gui/plugins/Attic/ServerWatcherPlugin.java,v 1.8 2004/04/14 14:02:05 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ServerWatcherPlugin.java,v $
 * Revision 1.8  2004/04/14 14:02:05  maj0r
 * An neue Schnittstelle und an neuen Speicherort fuer Settingsdateien angepasst.
 * Version 1.2
 *
 * Revision 1.7  2004/03/03 13:13:58  maj0r
 * Pfad zur xml-Datei angepasst und Proxysupport eingebaut.
 *
 * Revision 1.6  2004/03/03 12:49:26  maj0r
 * Sprachunterstuetzung eungebaut.
 *
 * Revision 1.5  2004/03/02 21:10:57  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.4  2003/09/13 18:35:19  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.3  2003/09/12 11:15:49  maj0r
 * Server lassen sich nun speichern und entfernen.
 * Version 1.1
 *
 * Revision 1.2  2003/09/12 06:29:03  maj0r
 * ServerWatcher v1.0
 *
 * Revision 1.1  2003/09/11 15:03:00  maj0r
 * ServerWatcherPlugin begonnen.
 *
 *
 */

public class ServerWatcherPlugin extends PluginConnector {
    private JPanel topPanel = new JPanel(new GridBagLayout());
    private JEditorPane editorPane = new JEditorPane();
    private JComboBox ip = new JComboBox();
    private JButton status = new JButton("Status");
    private JButton serverliste = new JButton("Serverliste");
    private JButton neu = new JButton("Neu");
    private JButton entfernen = new JButton("Entfernen");
    private JLabel statusText = new JLabel();
    private static Logger logger;

    public ServerWatcherPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon) {
        super(pluginsPropertiesXMLHolder, languageFiles, icon);
        logger = Logger.getLogger(getClass());
        try{
            setLayout(new BorderLayout());
            initServerList();
            int height = ip.getPreferredSize().height;
            status.setPreferredSize(new Dimension(status.getPreferredSize().width, height));
            serverliste.setPreferredSize(new Dimension(serverliste.getPreferredSize().width, height));
            statusText.setPreferredSize(new Dimension(statusText.getPreferredSize().width, height));
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel1.add(new JLabel("Server "));
            panel1.add(ip);
            panel1.add(new JLabel("             "));
            panel1.add(neu);
            panel1.add(entfernen);
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel2.add(status);
            panel2.add(serverliste);
            topPanel.add(panel1, constraints);
            constraints.gridy = 1;
            topPanel.add(panel2, constraints);
            constraints.gridx = 1;
            constraints.weightx = 1;
            topPanel.add(new JLabel(), constraints);

            status.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    if (ip.getSelectedIndex()!=-1){
                        getHtmlContent(false);
                    }
                }
            });
            serverliste.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    if (ip.getSelectedIndex()!=-1){
                        getHtmlContent(true);
                    }
                }
            });
            neu.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    NewServerDialog newServerDialog = new NewServerDialog(AppleJuiceDialog.getApp(), true);
                    Dimension appDimension = newServerDialog.getSize();
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    newServerDialog.setLocation((screenSize.width - appDimension.width)/2,
                                   (screenSize.height - appDimension.height)/2);
                    newServerDialog.show();
                    if (newServerDialog.isSave()){
                        ServerXML.addServer(newServerDialog.getServerConfig());
                    }
                    initServerList();
                }
            });
            entfernen.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    if (ip.getSelectedIndex()!=-1){
                        ServerXML.removeServer((ServerConfig)ip.getSelectedItem());
                        initServerList();
                    }
                }
            });
            add(topPanel, BorderLayout.NORTH);
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");
            add(new JScrollPane(editorPane), BorderLayout.CENTER);
            add(statusText, BorderLayout.SOUTH);
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void initServerList(){
        ip.removeAllItems();
        ServerConfig[] server = ServerXML.getServer();
        for (int i=0; i<server.length; i++){
            ip.addItem(server[i]);
        }
    }

    private void getHtmlContent(final boolean serverList){
        Thread serverWatcherWorker = new Thread(){
            public void run(){
                if (logger.isEnabledFor(Level.DEBUG))
                    logger.debug("ServerWatcherWorkerthread gestartet. " + this);
                serverliste.setEnabled(false);
                status.setEnabled(false);
                StringBuffer htmlContent = new StringBuffer();
                try {
                    ServerConfig server = (ServerConfig) ip.getSelectedItem();
                    String tmpUrl = "http://" + server.getDyn() + ":" + server.getPort();
                    if (serverList){
                        tmpUrl += "/serverlist.htm";
                    }
                    else{
                        tmpUrl += "/status.htm";
                    }
                    URL url = new URL(tmpUrl);
                    ProxySettings proxySettings = ProxyManagerImpl.getInstance().getProxySettings();
                    if (proxySettings.isUse()) {
                        System.getProperties().put("proxyHost", proxySettings.getHost());
                        System.getProperties().put("proxyPort",
                                                   Integer.toString(proxySettings.
                            getPort()));
                    }
                    URLConnection uc = url.openConnection();
                    if (proxySettings.isUse()) {
                        uc.setRequestProperty("Proxy-Authorization",
                                              "Basic " + proxySettings.getUserpass());
                    }
                    uc.setRequestProperty("Authorization", "Basic " + server.getUserPass());
                    InputStream content = uc.getInputStream();
                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = in.readLine()) != null) {
                        htmlContent.append(line);
                    }
                    if (proxySettings.isUse()) {
                        System.getProperties().remove("proxyHost");
                        System.getProperties().remove("proxyPort");
                    }
                    statusText.setText("");
                }
                catch (MalformedURLException e) {
                    editorPane.setText("");
                    statusText.setText("Ung�ltige URL");
                }
                catch (IOException e) {
                    editorPane.setText("");
                    statusText.setText("Zugang verweigert");
                }
                catch (Exception e){
                    if (logger.isEnabledFor(Level.ERROR))
                        logger.error("Unbehandelte Exception", e);
                }
                editorPane.setText(htmlContent.toString());
                serverliste.setEnabled(true);
                status.setEnabled(true);
                if (logger.isEnabledFor(Level.DEBUG))
                    logger.debug("ServerWatcherWorkerthread beendet. " + this);
            }
        };
        serverWatcherWorker.start();
    }

    public void fireLanguageChanged() {
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      �ber den DataManger k�nnen diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
    }

    public void registerSelected() {
    }
}
