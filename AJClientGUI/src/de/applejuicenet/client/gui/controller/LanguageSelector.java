package de.applejuicenet.client.gui.controller;

import java.io.CharArrayWriter;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/LanguageSelector.java,v 1.27 2005/01/24 10:40:58 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f\uFFFDr den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class LanguageSelector
    extends XMLValueHolder {

    private static LanguageSelector instance = null;

    private Set languageListener = new HashSet();
    private CharArrayWriter contents = new CharArrayWriter();
    private static Logger logger = Logger.getLogger(LanguageSelector.class);
    private StringBuffer key = new StringBuffer();
    private Set pluginsToWatch = null;

    private LanguageSelector(String path) {
    	super();
        try {
            parse(new File(path));
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
            OptionsManager om = OptionsManagerImpl.getInstance();
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
            parse(languageFile);
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
                        "Die in der ajgui.properties hinterlegte Sprachdatei wurde nicht gefunden." +
                        "\r\nappleJuice wird beendet.");
                }
                AppleJuiceDialog.closeWithErrormessage
                    ("Die in der ajgui.properties hinterlegte Sprachdatei wurde nicht gefunden." +
                     "\r\nappleJuice wird beendet.", false);
            }
            instance.init(sprachDatei);
            instance.informLanguageListener();
        }
        return instance;
    }

    public void fireLanguageChanged(){
        informLanguageListener();
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
        if (xmlContents.containsKey(identifier)){
            return xmlContents.get(identifier);
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
        if (pluginsToWatch != null){
	        it = pluginsToWatch.iterator();
	        String language = getFirstAttrbuteByTagName(".root.Languageinfo.name").toLowerCase();
	        while (it.hasNext()) {
	            ( (PluginConnector) it.next()).setLanguage(language);
	        }
        }
    }
}
