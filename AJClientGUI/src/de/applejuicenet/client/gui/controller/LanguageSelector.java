package de.applejuicenet.client.gui.controller;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import java.util.Map;
import java.util.Set;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/LanguageSelector.java,v 1.20 2004/03/05 15:49:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f\uFFFDr den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: LanguageSelector.java,v $
 * Revision 1.20  2004/03/05 15:49:39  maj0r
 * PMD-Optimierung
 *
 * Revision 1.19  2004/03/04 19:18:41  maj0r
 * Ausgabe korrigiert.
 *
 * Revision 1.18  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.17  2004/03/03 11:56:53  maj0r
 * Sprachunterstuetzung fuer Plugins eingebaut.
 *
 * Revision 1.16  2004/02/27 13:17:14  maj0r
 * Logging verbessert und Muell entfernt.
 *
 * Revision 1.15  2004/02/26 13:58:19  maj0r
 * Unicodefehler behoben.
 *
 * Revision 1.14  2004/02/24 14:26:13  maj0r
 * Eingelesene Woerter werden nicht mehr entfernt, sondern nur noch ueberschrieben.
 *
 * Revision 1.13  2004/02/20 16:13:33  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.12  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.11  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.10  2003/09/11 06:54:15  maj0r
 * Auf neues Sessions-Prinzip umgebaut.
 * Sprachenwechsel korrigert, geht nun wieder flott.
 *
 * Revision 1.9  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.8  2003/09/09 06:37:36  maj0r
 * Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.7  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugef\uFFFDgt.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class LanguageSelector
    extends DefaultHandler {

    private static LanguageSelector instance = null;

    private Set languageListener = new HashSet();
    private Map words = new HashMap();
    private XMLReader xr = null;
    private CharArrayWriter contents = new CharArrayWriter();
    private static Logger logger = Logger.getLogger(LanguageSelector.class);
    private StringBuffer key = new StringBuffer();
    private Set pluginsToWatch = new HashSet();

    private LanguageSelector(String path) {
        try {
            Class parser = SAXParser.class;
            xr = XMLReaderFactory.createXMLReader(parser.getName());
            xr.setContentHandler(this);
            init(new File(path));
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public static LanguageSelector getInstance() {
        if (instance == null) {
            String path = System.getProperty("user.dir") + File.separator +
                "language" +
                File.separator;
            OptionsManager om = PropertiesManager.getOptionsManager();
            String datei = om.getSprache();
            path += datei + ".xml";
            //zZ werden die Header der TableModel nicht aktualisiert, deshalb hier schon
            return new LanguageSelector(path);
        }
        return instance;
    }

    public void addPluginsToWatch(Set plugins){
        pluginsToWatch = plugins;
    }

    private void init(File languageFile){
        try {
            if (key.length()>0){
                key.delete(0, key.length() - 1);
            }
            xr.parse( new InputSource(
                new FileInputStream( languageFile )) );
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public static LanguageSelector getInstance(String path) {
        if (instance == null) {
            instance = new LanguageSelector(path);
        }
        else {
            File sprachDatei = new File(path);
            if (!sprachDatei.isFile()) {
                if (logger.isEnabledFor(Level.INFO)) {
                    logger.info(
                        "Die in der properties.xml hinterlegte Sprachdatei wurde nicht gefunden." +
                        "\r\nappleJuice wird beendet.");
                }
                AppleJuiceDialog.closeWithErrormessage
                    ("Die in der properties.xml hinterlegte Sprachdatei wurde nicht gefunden." +
                     "\r\nappleJuice wird beendet.", false);
            }
            instance.init(sprachDatei);
            instance.informLanguageListener();
        }
        return instance;
    }

    public void addLanguageListener(LanguageListener listener) {
        if (! (languageListener.contains(listener))) {
            languageListener.add(listener);
        }
    }

    public void removeLanguageListener(LanguageListener listener) {
        if (languageListener.contains(listener)) {
            languageListener.remove(listener);
        }
    }

    public String getFirstAttrbuteByTagName(String[] pathToValue){
        StringBuffer path = new StringBuffer();
        path.append(".");
        path.append("root");
        for (int i=0; i<pathToValue.length; i++){
            path.append(".");
            path.append(pathToValue[i]);
        }
        return getFirstAttrbuteByTagName(path.toString());
    }

    public String getFirstAttrbuteByTagName(String identifier){
        if (words.containsKey(identifier)){
            return (String)words.get(identifier);
        }
        else{
            return "";
        }
    }

    private void informLanguageListener() {
        Iterator it = languageListener.iterator();
        while (it.hasNext()) {
            ( (LanguageListener) it.next()).fireLanguageChanged();
        }
        it = pluginsToWatch.iterator();
        String language = getFirstAttrbuteByTagName(".root.Languageinfo.name").toLowerCase();
        while (it.hasNext()) {
            ( (PluginConnector) it.next()).setLanguage(language);
        }
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        contents.reset();
        key.append(".");
        key.append(localName);
        for (int i = 0; i < attr.getLength(); i++) {
            words.put(key.toString() + "." + attr.getLocalName(i), attr.getValue(i));
        }
    }

    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        key.delete(key.length() - localName.length() - 1, key.length());
    }

    public void characters(char[] ch, int start, int length) throws
        SAXException {
        words.put(key.toString(), ch);
    }
}
