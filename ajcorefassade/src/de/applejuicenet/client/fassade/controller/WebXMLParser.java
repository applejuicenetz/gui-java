package de.applejuicenet.client.fassade.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.ConnectionSettings;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.XMLDecoder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/controller/Attic/WebXMLParser.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class WebXMLParser
    extends XMLDecoder
    implements DataUpdateListener {
    private String host;
    private String xmlCommand;
    private long timestamp = 0;
    private boolean useTimestamp = false;
    private String password;
    private Logger logger;
    private String zipMode = "";
    private static DocumentBuilder builder;
    private Integer port;

    static{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            ;
        }
    }

    public WebXMLParser(String host, String password, Integer port, String xmlCommand, String parameters) {
        this(host, password, port, xmlCommand, parameters, false);
    }

    public WebXMLParser(String host, String password, Integer port, String xmlCommand, String parameters,
                        boolean useTimestamp) {
        super();
        init(host, password, port, xmlCommand, useTimestamp);
    }

    private void init(String host, String password, Integer port, String xmlCommand, boolean useTimestamp) {
        logger = Logger.getLogger(getClass());
        this.useTimestamp = useTimestamp;
        this.port = port;
        if (host == null || host.length() == 0) {
            host = "localhost";
        }
        if (host.compareToIgnoreCase("localhost") != 0 &&
            host.compareTo("127.0.0.1") != 0) {
            zipMode = "mode=zip&";
        }
        this.xmlCommand = xmlCommand;
        webXML = true;
    }

    private String getCommand(String parameters){
        String command = xmlCommand + "?";
        if (parameters.indexOf("mode=zip") == -1) {
            command += zipMode;
        }
        if (useTimestamp) {
            command += "password=" + password + "&timestamp=" +
                timestamp + parameters;
        }
        else {
            if (parameters.length() != 0) {
                command += "password=" + password + "&" + parameters;
            }
            else {
                command += "password=" + password;
            }
        }
        return command;
    }

    public void reload(String parameters, boolean throwWebSiteNotFoundException) throws
        Exception {
        String xmlData = null;
        try {
            String command = getCommand(parameters);
            xmlData = HtmlLoader.getHtmlXMLContent(host, port, HtmlLoader.GET, command);
            if (xmlData == null || xmlData.length() == 0 || xmlData.startsWith("error:")) {
                throw new IllegalArgumentException();
            }
        }
        catch (WebSiteNotFoundException ex) {
            if (throwWebSiteNotFoundException) {
                throw ex;
            }
            else {
                return;
            }
        }
        try {
            document = builder.parse(new InputSource(new StringReader(xmlData)));
            if (useTimestamp) {
                timestamp = Long.parseLong(getFirstAttrbuteByTagName(new
                    String[] {
                    "applejuice", "time"}
                    , true));
            }
        }
        catch (Exception e) {
            Exception x = e;
            if (e.getClass() == SAXException.class) {
                if ( ( (SAXException) e).getException() != null) {
                    x = ( (SAXException) e).getException();
                }
            }
            if (logger.isEnabledFor(Level.ERROR)) {
                String zeit = Long.toString(System.currentTimeMillis());
                String path = System.getProperty("user.dir") + File.separator +
                    "logs";
                File aFile = new File(path);
                if (!aFile.exists()) {
                    aFile.mkdir();
                }
                FileWriter fileWriter = null;
                String dateiname = path + File.separator + zeit + ".exc";
                try {
                    fileWriter = new FileWriter(dateiname);
                    fileWriter.write(xmlData);
                    fileWriter.close();
                }
                catch (IOException ioE) {
                    logger.error(ioE);
                }
                logger.error("Unbehandelte SAX-Exception -> Inhalt in " +
                             dateiname, x);
            }
        }
    }

    public abstract void update();

    public void setPassword(String password) {
        this.password = password;
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.CONNECTION_SETTINGS_CHANGED) {
            host = ( (ConnectionSettings) content).getHost();
            password = ( (ConnectionSettings) content).getOldPassword();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
        }
    }
}
