package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.start.HyperlinkAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class ReleaseInfo {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseInfo.class);

    private static final String defaultHost = new String(Base64.getDecoder().decode("aHR0cHM6Ly93d3cuYXBwbGUtZGVsdXhlLmNvL2luZGV4LnBocD9jdD00MDMmdmE9JXM="));

    private static String releaseInfoHost = null;

    private static void init() {
        if (null == releaseInfoHost) {
            Properties props = new Properties();
            String path = System.getProperty("user.home") + File.separator + "appleJuice" + File.separator + "gui" + File.separator + "rel.properties";

            File aFile = new File(path);
            if (aFile.exists()) {
                try {
                    FileInputStream fiS = new FileInputStream(aFile);
                    props.load(fiS);
                    fiS.close();

                    releaseInfoHost = props.getProperty("host", defaultHost);
                } catch (Exception e) {
                    logger.warn("rel.properties could not be read; using the default host", e);
                }
            } else {
                props.setProperty("host", defaultHost);
                try (OutputStream outputStream = new FileOutputStream(aFile)) {
                    props.store(outputStream, null);
                } catch (IOException e) {
                    logger.warn("rel.properties could not be written", e);
                }
            }

            releaseInfoHost = (releaseInfoHost == null) ? defaultHost : releaseInfoHost;
        }
    }

    public static void handle(String filename, String hash, Long size) {
        init();

        String ajfsp = String.format("ajfsp://file%%7C%s%%7C%s%%7C%s/", encodeValue(filename), hash, size);

        HyperlinkAdapter.executeLink(String.format(releaseInfoHost, ajfsp));
    }

    private static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
