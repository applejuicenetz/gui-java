package de.applejuicenet.client.shared;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.PluginConnector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/PluginJarClassLoader.java,v 1.25 2005/02/28 14:58:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r aj@tkl-soft.de>
 *
 */

public class PluginJarClassLoader
    extends URLClassLoader {
    private URL url;
    private Logger logger;
    private XMLValueHolder pluginsPropertiesXMLHolder = null;
    private ImageIcon pluginIcon = null;
    private Map<String, XMLValueHolder> languageXMLs = new HashMap<String, XMLValueHolder>();

    public PluginJarClassLoader(URL url) {
        super(new URL[] {url});
        this.url = url;
        logger = Logger.getLogger(getClass());
    }

    public PluginConnector getPlugin(String jar) throws Exception {
        pluginsPropertiesXMLHolder = null;
        pluginIcon = null;
        languageXMLs.clear();
        File aJar = new File(jar);
        try {
            loadClassBytesFromJar(aJar);
            String theClassName = pluginsPropertiesXMLHolder.getXMLAttributeByTagName(".root.general.classname.value");
            Class cl = loadClass(theClassName);
            Class[] constructorHelper = {XMLValueHolder.class, Map.class, ImageIcon.class };
            Constructor con = cl.getConstructor(constructorHelper);
            Object aPlugin = con.newInstance(new Object[]{pluginsPropertiesXMLHolder, languageXMLs, pluginIcon});
            return (PluginConnector) aPlugin;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Plugin " + jar +
                    " entspricht nicht dem Standard und wurde nicht geladen.", e);
            }
            return null;
        }
    }

    private void loadClassBytesFromJar(File jar) throws Exception {
        if (!jar.isFile()) {
            return;
        }

        JarFile jf = new JarFile(jar);
        String entryName;
        Vector<String> classes = new Vector<String>();

        for (Enumeration e = jf.entries(); e.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            entryName = entry.getName();
            if (entryName.indexOf(".class") == -1 && !entryName.equals("plugin_properties.xml")
                && entryName.indexOf("icon.gif") == -1 && entryName.indexOf("language_xml_") == -1) {
                continue;
            }
            InputStream is = jf.getInputStream(entry);
            int l = (int) entry.getSize();
            byte[] buf = new byte[l];
            int read = 0;

            while (read < l) {
                int incr = is.read(buf, read, l - read);
                read += incr;
            }
            if (entryName.equals("plugin_properties.xml")){
                String xmlString = new String(buf, 0, buf.length);
                pluginsPropertiesXMLHolder = new XMLValueHolder();
                pluginsPropertiesXMLHolder.parse(xmlString);
            }
            else if (entryName.indexOf("icon.gif") != -1){
                pluginIcon = new ImageIcon(buf);
            }
            else if (entryName.indexOf("language_xml_") != -1){
                String xmlString = new String(buf, 0, buf.length);
                XMLValueHolder languageFile = new XMLValueHolder();
                languageFile.parse(xmlString);
                String sprache = languageFile.getXMLAttributeByTagName(".root.language.value");
                languageXMLs.put(sprache, languageFile);
            }
            else{
                String name = entryName.replace('/', '.');
                name = name.replaceAll(".class", "");
                try{
                    defineClass(name, buf, 0, buf.length);
                    classes.add(name);
                }
                catch(LinkageError lE){
                    //Klasse wurde aus irgendeinem Grund bereits geladen
                }
            }
        }
    }
}
