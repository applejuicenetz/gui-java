package de.applejuicenet.client.shared;

import java.net.URLClassLoader;
import org.apache.log4j.Logger;
import java.net.URL;
import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownloadPolicy;
import java.io.File;
import java.util.Vector;
import org.apache.log4j.Level;
import java.util.jar.JarFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.io.InputStream;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/PolicyJarClassLoader.java,v 1.1 2004/01/31 08:52:41 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: PolicyJarClassLoader.java,v $
 * Revision 1.1  2004/01/31 08:52:41  maj0r
 * PwdlPolicies werden jetzt wie Plugins als jars eingebunden.
 *
 *
 */

public class PolicyJarClassLoader
        extends URLClassLoader {
    private Logger logger;

    public PolicyJarClassLoader(URL url) {
        super(new URL[]{url});
        logger = Logger.getLogger(getClass());
    }

    public AutomaticPowerdownloadPolicy getPolicy(String jar) throws Exception {
        try{
            File aJar = new File(jar);
            Vector classes = loadClassBytesFromJar(aJar);
            if (classes == null){
                return null;
            }
            String className;
            Class cl;
            for (int i=0; i<classes.size(); i++){
                className = (String)classes.get(i);
                cl = loadClass(className);
                if (AutomaticPowerdownloadPolicy.class.isAssignableFrom(cl)){
                    return (AutomaticPowerdownloadPolicy) cl.newInstance();
                }
            }
            return null;
        }
        catch(Exception e){
            if (logger.isEnabledFor(Level.INFO)){
                logger.info("Plugin " + jar +
                    " entspricht nicht dem Standard und wurde nicht geladen.");
            }
            return null;
        }
    }

    private Vector loadClassBytesFromJar(File jar) throws Exception {
        if (!jar.isFile())
            return null;

        JarFile jf = new JarFile(jar);
        String entryName;
        Vector classes = new Vector();

        for (Enumeration e = jf.entries(); e.hasMoreElements();)
        {
            ZipEntry entry = (ZipEntry) e.nextElement();
            entryName = entry.getName();
            if (entryName.indexOf(".class") == -1)
            {
                continue;
            }
            InputStream is = jf.getInputStream(entry);
            int l = (int) entry.getSize();
            byte[] buf = new byte[l];
            int read = 0;

            while (read < l)
            {
                int incr = is.read(buf, read, l - read);
                read += incr;
            }
            String name = entryName.replace('/', '.');
            name = name.replaceAll(".class", "");
            defineClass(name, buf, 0, buf.length);
            classes.add(name);
        }
        return classes;
    }
}
