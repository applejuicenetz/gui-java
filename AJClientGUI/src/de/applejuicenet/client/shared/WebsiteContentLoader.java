package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.PropertiesManager;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/WebsiteContentLoader.java,v 1.1 2003/09/12 13:19:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: WebsiteContentLoader.java,v $
 * Revision 1.1  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 *
 */

public abstract class WebsiteContentLoader {

    public static String getWebsiteContent(String website, int port, String pfadAndparameters){
        Logger logger = Logger.getLogger(WebsiteContentLoader.class);
        StringBuffer htmlContent = new StringBuffer();
        try {
            String tmpUrl = website + ":" + port + pfadAndparameters;
            ProxySettings proxySettings = PropertiesManager.getProxyManager().getProxySettings();
            PropertiesManager.getProxyManager().saveProxySettings(proxySettings);
            if (proxySettings.isUse()){
                System.getProperties().put("proxyHost", proxySettings.getHost());
                System.getProperties().put("proxyPort", Integer.toString(proxySettings.getPort()));
            }
            URL url = new URL(tmpUrl);
            URLConnection uc = url.openConnection();
            if (proxySettings.isUse()){
                uc.setRequestProperty("Proxy-Authorization","Basic " + proxySettings.getUserpass());
            }
            InputStream content = uc.getInputStream();
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                htmlContent.append(line);
            }
            if (proxySettings.isUse()){
                System.getProperties().remove("proxyHost");
                System.getProperties().remove("proxyPort");
            }
        }
        catch (IOException e) {
            if (logger.isEnabledFor(Level.INFO))
                logger.info("Zugang zu einer Webseite verweigert. Falsche Proxyeinstellungen?");
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
        return htmlContent.toString();
    }
}
