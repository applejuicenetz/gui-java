package de.applejuicenet.client.shared;

import java.net.*;
import java.io.File;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import de.applejuicenet.client.gui.plugins.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/PluginJarClassLoader.java,v 1.9 2003/10/21 14:08:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r aj@tkl-soft.de>
 *
 * $Log: PluginJarClassLoader.java,v $
 * Revision 1.9  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.8  2003/08/23 11:16:35  maj0r
 * Plattformunabhaengigkeit wieder hergestellt.
 *
 * Revision 1.7  2003/08/20 10:52:51  maj0r
 * JarClassloader korrigiert.
 *
 * Revision 1.6  2003/08/16 17:58:58  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.5  2003/07/01 14:51:28  maj0r
 * Unnützen Krimskram entfernt.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class PluginJarClassLoader
        extends URLClassLoader {
    private URL url;

    public PluginJarClassLoader(URL url) {
        super(new URL[]{url});
        this.url = url;
    }

    public PluginConnector getPlugin(String jar) throws Exception {
        File aJar = new File(jar);
        String theClassName = jar.substring(jar.lastIndexOf(File.separatorChar) + 1, jar.lastIndexOf(".jar"));
        loadClassBytesFromJar(aJar);
        Class cl = loadClass("de.applejuicenet.client.gui.plugins." + theClassName);
        Object aPlugin = cl.newInstance();
        return (PluginConnector) aPlugin;
    }

    private void loadClassBytesFromJar(File jar) throws Exception {
        if (!jar.isFile())
            return;

        JarFile jf = new JarFile(jar);
        String entryName;

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
        }
    }
}