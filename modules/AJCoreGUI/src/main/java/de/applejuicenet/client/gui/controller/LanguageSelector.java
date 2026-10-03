/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;

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
    private final Properties englishValues = new Properties();
    @SuppressWarnings("unchecked")
    private Set pluginsToWatch = null;

    LanguageSelector(String path) {
        super();
        init(new File(path));
    }

    public static LanguageSelector getInstance() {
        if (instance == null) {
            String path = System.getProperty("user.dir") + File.separator + "language" + File.separator;
            OptionsManager om = OptionsManagerImpl.getInstance();
            String datei = om.getSprache();

            if (null == datei || datei.length() == 0) {
                datei = "english";
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
        englishValues.clear();
        values.clear();
        try {
            if (key.length() > 0) {
                key.delete(0, key.length() - 1);
            }

            File englishFile = new File(languageFile.getAbsoluteFile().getParentFile(), "english.properties");
            try (Reader reader = Files.newBufferedReader(englishFile.toPath(), StandardCharsets.UTF_8)) {
                englishValues.load(reader);
            }
            values.putAll(englishValues);
            if (languageFile.isFile()) {
                Properties selectedValues = new Properties();
                try (Reader reader = Files.newBufferedReader(languageFile.toPath(), StandardCharsets.UTF_8)) {
                    selectedValues.load(reader);
                }
                values.putAll(selectedValues);
            } else {
                logger.warn("Sprachdatei {} fehlt; Englisch wird verwendet.", languageFile);
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public static LanguageSelector getInstance(String path) {
        if (instance == null) {
            instance = new LanguageSelector(path);
        } else {
            File sprachDatei = new File(path);

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
        String value = values.getProperty(identifier);
        return value == null || value.isBlank() ? englishValues.getProperty(identifier, "") : value;
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
