package de.applejuicenet.client.fassade.shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/shared/WebsiteContentLoader.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class WebsiteContentLoader {

    private static Logger logger = Logger.getLogger(WebsiteContentLoader.class);

    public static String getWebsiteContent(ProxySettings proxySettings, 
            String website, int port,
            String pfadAndparameters) {
        StringBuffer htmlContent = new StringBuffer();
        try {
            String tmpUrl = website + ":" + port + pfadAndparameters;
            if (proxySettings != null) {
                System.getProperties().put("proxyHost", proxySettings.getHost());
                System.getProperties().put("proxyPort",
                                           Integer.toString(proxySettings.
                    getPort()));
            }
            URL url = new URL(tmpUrl);
            URLConnection uc = url.openConnection();
            if (proxySettings != null) {
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
            if (proxySettings != null) {
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
    
    public static String getWebsiteContent(String website, int port,
                                           String pfadAndparameters) {
        return getWebsiteContent(null, website, port, pfadAndparameters);
    }
}
