package de.applejuicenet.client.shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/WebsiteContentLoader.java,v 1.7 2004/03/09 16:25:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: WebsiteContentLoader.java,v $
 * Revision 1.7  2004/03/09 16:25:17  maj0r
 * PropertiesManager besser gekapselt.
 *
 * Revision 1.6  2004/03/05 15:49:39  maj0r
 * PMD-Optimierung
 *
 * Revision 1.5  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.4  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.3  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.2  2003/09/12 13:31:55  maj0r
 * Bugs behoben.
 *
 * Revision 1.1  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 *
 */

public abstract class WebsiteContentLoader {

    public static String getWebsiteContent(String website, int port,
                                           String pfadAndparameters) {
        Logger logger = Logger.getLogger(WebsiteContentLoader.class);
        StringBuffer htmlContent = new StringBuffer();
        try {
            String tmpUrl = website + ":" + port + pfadAndparameters;
            ProxySettings proxySettings = ProxyManagerImpl.getInstance().
                getProxySettings();
            if (proxySettings.isUse()) {
                System.getProperties().put("proxyHost", proxySettings.getHost());
                System.getProperties().put("proxyPort",
                                           Integer.toString(proxySettings.
                    getPort()));
            }
            URL url = new URL(tmpUrl);
            URLConnection uc = url.openConnection();
            if (proxySettings.isUse()) {
                uc.setRequestProperty("Proxy-Authorization",
                                      "Basic " + proxySettings.getUserpass());
            }
            InputStream content = uc.getInputStream();
            BufferedReader in =
                new BufferedReader(new InputStreamReader(content));
            String line;
            while ( (line = in.readLine()) != null) {
                htmlContent.append(line);
            }
            if (proxySettings.isUse()) {
                System.getProperties().remove("proxyHost");
                System.getProperties().remove("proxyPort");
            }
        }
        catch (IOException e) {
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info(
                    "Zugang zu einer Webseite verweigert. Falsche Proxyeinstellungen?");
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
        return htmlContent.toString();
    }
}
