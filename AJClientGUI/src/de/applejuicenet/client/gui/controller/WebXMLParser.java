package de.applejuicenet.client.gui.controller;

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
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.XMLDecoder;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/WebXMLParser.java,v 1.30 2004/03/03 15:33:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: WebXMLParser.java,v $
 * Revision 1.30  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.29  2004/02/18 20:44:37  maj0r
 * Bugs #223 und #224 behoben.
 *
 * Revision 1.28  2004/02/17 14:42:57  maj0r
 * Bug #220 gefixt (Danke an dsp2004)
 * OutOfMemoryError behoben.
 *
 * Revision 1.26  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.25  2004/02/02 19:28:57  maj0r
 * Kompression wird nur beim initialen Laden und bei entferntem Core verwendet.
 *
 * Revision 1.24  2004/02/02 15:13:47  maj0r
 * Kommunikation GUI<->Core erfolgt nun gezipped.
 *
 * Revision 1.23  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.22  2004/01/06 17:32:50  maj0r
 * Es wird nun zweimal versucht den Core erneut zu erreichen, wenn die Verbindung unterbrochen wurde.
 *
 * Revision 1.21  2004/01/01 14:26:53  maj0r
 * Es koennen nun auch Objekte nach Id vom Core abgefragt werden.
 *
 * Revision 1.20  2003/12/31 16:17:52  maj0r
 * Refactoring.
 *
 * Revision 1.19  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.18  2003/11/03 15:45:26  maj0r
 * Optimierungen.
 *
 * Revision 1.17  2003/10/14 15:39:48  maj0r
 * Stacktraces ausgebaut.
 *
 * Revision 1.16  2003/10/13 19:14:04  maj0r
 * Kleinen Bug beim Entfernen von Downloads gefixt.
 *
 * Revision 1.15  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.14  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 * Revision 1.13  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.12  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.11  2003/08/29 19:34:03  maj0r
 * Einige Aenderungen.
 * Version 0.17 Beta
 *
 * Revision 1.10  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
 * Revision 1.9  2003/08/19 12:38:47  maj0r
 * Passworteingabe und md5 korrigiert.
 *
 * Revision 1.8  2003/08/16 17:50:06  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.7  2003/08/15 17:53:54  maj0r
 * Tree fuer Shareauswahl fortgefuehrt, aber noch nicht fertiggestellt.
 *
 * Revision 1.6  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.5  2003/08/09 16:47:42  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.4  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.3  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.2  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefï¿½gt.
 *
 * Revision 1.1  2003/06/22 18:58:53  maj0r
 * Umbenannt und Hostverwendung korrigiert.
 *
 * Revision 1.9  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
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

    static{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            ;
        }
    }

    public WebXMLParser(String xmlCommand, String parameters) {
        super();
        init(xmlCommand);
        PropertiesManager.getOptionsManager().addSettingsListener(this);
    }

    public WebXMLParser(String xmlCommand, String parameters,
                        boolean useTimestamp) {
        super();
        this.useTimestamp = useTimestamp;
        init(xmlCommand);
    }

    private void init(String xmlCommand) {
        logger = Logger.getLogger(getClass());
        ConnectionSettings rc = PropertiesManager.getOptionsManager().
            getRemoteSettings();
        host = rc.getHost();
        password = rc.getOldPassword();
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

    public void reload(String parameters, boolean throwWebSiteNotFoundException) throws
        Exception {
        String xmlData = null;
        try {
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
            xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                command);
            if (xmlData.length() == 0) {
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
