package de.applejuicenet.client.gui.plugins;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/serverwatcher/src/de/applejuicenet/client/gui/plugins/Attic/ServerWatcherPlugin.java,v 1.2 2003/09/12 06:29:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ServerWatcherPlugin.java,v $
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
    private JTextField ip = new JTextField();
    private JTextField port = new JTextField("8001");
    private JTextField user = new JTextField();
    private JTextField pass = new JTextField();
    private JButton status = new JButton("Status");
    private JButton serverliste = new JButton("Serverliste");
    private JLabel statusText = new JLabel();
    private static Logger logger;

    public ServerWatcherPlugin() {
        logger = Logger.getLogger(getClass());
        try{
            setLayout(new BorderLayout());
            ip.setColumns(15);
            port.setColumns(6);
            int height = ip.getPreferredSize().height;
            user.setColumns(15);
            user.setPreferredSize(new Dimension(user.getPreferredSize().width, height));
            pass.setColumns(15);
            pass.setPreferredSize(new Dimension(pass.getPreferredSize().width, height));
            status.setPreferredSize(new Dimension(status.getPreferredSize().width, height));
            serverliste.setPreferredSize(new Dimension(serverliste.getPreferredSize().width, height));
            statusText.setPreferredSize(new Dimension(statusText.getPreferredSize().width, height));
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            JPanel panel1 = new JPanel(new FlowLayout());
            panel1.add(new JLabel("IP "));
            panel1.add(ip);
            panel1.add(new JLabel("Port "));
            panel1.add(port);
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panel2.add(status);
            panel2.add(serverliste);
            topPanel.add(panel1, constraints);
            constraints.gridy = 1;
            topPanel.add(panel2, constraints);

            constraints.gridx = 1;
            constraints.gridy = 0;
            JLabel label1 = new JLabel("User ");
            label1.setPreferredSize(new Dimension(label1.getPreferredSize().width, height));
            topPanel.add(label1, constraints);
            constraints.gridy = 1;
            JLabel label2 = new JLabel("Passwort ");
            label2.setPreferredSize(new Dimension(label2.getPreferredSize().width, height));
            topPanel.add(label2, constraints);
            constraints.gridx = 2;
            constraints.gridy = 0;
            topPanel.add(user, constraints);
            constraints.gridy = 1;
            topPanel.add(pass, constraints);
            constraints.gridx = 3;
            constraints.weightx = 1;
            topPanel.add(new JLabel(), constraints);

            String classname = getClass().toString();
            String path = System.getProperty("user.dir") + File.separator + "plugins" +
                    File.separator + classname.substring(classname.lastIndexOf('.') + 1) + ".jar";
            initIcon(path);

            status.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    if (ip.getText().length()>0 && port.getText().length()>0
                        && user.getText().length()>0 && pass.getText().length()>0){
                        getHtmlContent(false);
                    }
                }
            });
            serverliste.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    if (ip.getText().length()>0 && port.getText().length()>0
                        && user.getText().length()>0 && pass.getText().length()>0){
                        getHtmlContent(true);
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

    private void getHtmlContent(final boolean serverList){
        Thread serverWatcherWorker = new Thread(){
            public void run(){
                if (logger.isEnabledFor(Level.DEBUG))
                    logger.debug("ServerWatcherWorkerthread gestartet. " + this);
                serverliste.setEnabled(false);
                status.setEnabled(false);
                StringBuffer htmlContent = new StringBuffer();
                try {
                    String tmpUrl = "http://" + ip.getText() + ":" + port.getText();
                    if (serverList){
                        tmpUrl += "/serverlist.htm";
                    }
                    else{
                        tmpUrl += "/status.htm";
                    }
                    URL url = new URL(tmpUrl);
                    String encoding = new sun.misc.BASE64Encoder().encode((user.getText() + ":" +
                            pass.getText()).getBytes());
                    URLConnection uc = url.openConnection();
                    uc.setRequestProperty("Authorization", "Basic " + encoding);
                    InputStream content = uc.getInputStream();
                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = in.readLine()) != null) {
                        htmlContent.append(line);
                    }
                    statusText.setText("");
                }
                catch (MalformedURLException e) {
                    editorPane.setText("");
                    statusText.setText("Ungültige URL");
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
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
    }

    public void registerSelected() {
    }

    public String getTitle() {
        return "ServerWatcher";
    }

    public String getAutor() {
        return "Maj0r";
    }

    public String getBeschreibung() {
        return "Mit dem Serverwatcher kann der Status von Server angezeigt werden.";
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean istReiter() {
        return true;
    }
}