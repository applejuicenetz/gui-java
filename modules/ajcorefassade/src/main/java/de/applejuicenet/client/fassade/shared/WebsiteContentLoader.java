/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import de.applejuicenet.client.fassade.exception.NoAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
 * @author Maj0r <aj@tkl-soft.de>
 */

public abstract class WebsiteContentLoader {
    public static String getWebsiteContent(String website) throws NoAccessException {
        StringBuilder htmlContent = new StringBuilder();

        try {
            URL url = new URL(website);
            URLConnection uc = url.openConnection();

            uc.setRequestProperty("User-Agent",
                    String.format("ajcorefassade; Java/%s; (%s/%s)", System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.version")));

            InputStream content = uc.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;

            while ((line = in.readLine()) != null) {
                htmlContent.append(line);
            }

        } catch (IOException e) {
            throw new NoAccessException("wrong proxysettings?", e);
        }

        return htmlContent.toString();
    }
}
