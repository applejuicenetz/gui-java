package de.applejuicenet.client.shared;

import java.net.*;

import de.applejuicenet.client.gui.plugins.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/PluginJarClassLoader.java,v 1.6 2003/08/16 17:58:58 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PluginJarClassLoader.java,v $
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
  private String className;

  public PluginJarClassLoader(URL url, String className) {
    super(new URL[] {url});
    this.url = url;
    this.className = className;
  }

  public PluginConnector getPlugin() throws Exception{
    Class aClass = loadClass("de.applejuicenet.client.gui.plugins." + className);
    Object aPlugin = aClass.newInstance();
    if (! (aPlugin instanceof PluginConnector)) {
      return null;
    }
    return (PluginConnector) aPlugin;
  }
}