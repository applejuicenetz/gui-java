/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.exception.NoAccessException;
import de.applejuicenet.client.gui.AppleJuiceDialog;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/WebsiteContentLoader.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 */

public abstract class WebsiteContentLoader {
    public static String getWebsiteContent(String website, ProxySettings proxySettings) throws NoAccessException {
        StringBuilder htmlContent = new StringBuilder();

        try {
            if (proxySettings != null) {
                System.getProperties().put("proxyHost", proxySettings.getHost());
                System.getProperties().put("proxyPort", Integer.toString(proxySettings.getPort()));
            }

            URL url = new URL(website);
            URLConnection uc = url.openConnection();

            if (proxySettings != null) {
                uc.setRequestProperty("Proxy-Authorization", "Basic " + proxySettings.getUserpass());
            }

            uc.setRequestProperty("User-Agent",
                    String.format("AJCoreGUI/%s; Java/%s; (%s/%s)", AppleJuiceDialog.getVersion(), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.version")));

            InputStream content = uc.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;

            while ((line = in.readLine()) != null) {
                htmlContent.append(line);
            }

            if (proxySettings != null) {
                System.getProperties().remove("proxyHost");
                System.getProperties().remove("proxyPort");
            }
        } catch (IOException e) {
            throw new NoAccessException("wrong proxysettings?", e);
        }

        return htmlContent.toString();
    }

    public static String getWebsiteContent(ProxySettings proxySettings, String website, int port, String pfadAndParameters) throws NoAccessException {
        String tmpUrl = website + ":" + port + pfadAndParameters;
        return WebsiteContentLoader.getWebsiteContent(tmpUrl, proxySettings);
    }

    public static String getWebsiteContent(String website, int port, String pfadAndparameters) throws NoAccessException {
        return getWebsiteContent(null, website, port, pfadAndparameters);
    }
}
