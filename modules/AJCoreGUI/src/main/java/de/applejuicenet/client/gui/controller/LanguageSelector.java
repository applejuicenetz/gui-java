/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.CharArrayWriter;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/LanguageSelector.java,v 1.30 2009/01/12 09:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f\uFFFDr den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class LanguageSelector extends XMLValueHolder {
    private static LanguageSelector instance = null;
    private static final Logger logger = LoggerFactory.getLogger(LanguageSelector.class);
    private Set<LanguageListener> languageListener = new HashSet<LanguageListener>();
    private CharArrayWriter contents = new CharArrayWriter();
    private StringBuffer key = new StringBuffer();
    @SuppressWarnings("unchecked")
    private Set pluginsToWatch = null;

    private LanguageSelector(String path) {
        super();
        try {
            parseProperties(new File(path));
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    public static LanguageSelector getInstance() {
        if (instance == null) {
            String path = System.getProperty("user.dir") + File.separator + "language" + File.separator;
            OptionsManager om = OptionsManagerImpl.getInstance();
            String datei = om.getSprache();

            if (null == datei || datei.length() == 0) {
                datei = "deutsch";
            }

            path += datei + ".properties";

            //zZ werden die Header der TableModel nicht aktualisiert, deshalb hier schon
            return new LanguageSelector(path);
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public void addPluginsToWatch(Set plugins) {
        pluginsToWatch = plugins;
    }

    private void init(File languageFile) {
        try {
            if (key.length() > 0) {
                key.delete(0, key.length() - 1);
            }

            parseProperties(languageFile);
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public static LanguageSelector getInstance(String path) {
        if (instance == null) {
            instance = new LanguageSelector(path);
        } else {
            File sprachDatei = new File(path);

            if (!sprachDatei.isFile()) {
                logger.info("Die in der ajgui.properties hinterlegte Sprachdatei wurde nicht gefunden.");
                AppleJuiceDialog.closeWithErrormessage("Die in der ajgui.properties hinterlegte Sprachdatei wurde nicht gefunden. appleJuice wird beendet.", false);
            }

            instance.init(sprachDatei);
            instance.informLanguageListener();
        }

        return instance;
    }

    public void fireLanguageChanged() {
        informLanguageListener();
    }

    public void addLanguageListener(LanguageListener listener) {
        if (!(languageListener.contains(listener))) {
            languageListener.add(listener);
        }
    }

    public void removeLanguageListener(LanguageListener listener) {
        if (languageListener.contains(listener)) {
            languageListener.remove(listener);
        }
    }

    public String getFirstAttrbuteByTagName(String[] pathToValue) {
        StringBuffer path = new StringBuffer();

        path.append(".");
        path.append("root");
        for (int i = 0; i < pathToValue.length; i++) {
            path.append(".");
            path.append(pathToValue[i]);
        }

        return getFirstAttrbuteByTagName(path.toString());
    }

    public String getFirstAttrbuteByTagName(String identifier) {
        if (values.containsKey(identifier)) {
            return values.getProperty(identifier);
        } else {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private void informLanguageListener() {
        Iterator it = languageListener.iterator();

        while (it.hasNext()) {
            ((LanguageListener) it.next()).fireLanguageChanged();
        }

        if (pluginsToWatch != null) {
            it = pluginsToWatch.iterator();
            String language = getFirstAttrbuteByTagName("Languageinfo.name").toLowerCase();

            while (it.hasNext()) {
                ((PluginConnector) it.next()).setLanguage(language);
            }
        }
    }
}
