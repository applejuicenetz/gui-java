package de.applejuicenet.client.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VersionChecker {

    private static final Logger logger = Logger.getLogger(AppleJuiceDialog.class);

    public static void check() {
        Thread versionWorker = new Thread() {
            public void run() {
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("VersionWorkerThread gestartet. " + this);
                }

                try {
                    ProxySettings proxy = ProxyManagerImpl.getInstance().getProxySettings();
                    String updateServer = OptionsManagerImpl.getInstance().getUpdateServerURL();
                    String downloadData = WebsiteContentLoader.getWebsiteContent(updateServer, proxy);

                    if (downloadData.length() > 0) {
                        JsonObject jsonObject = new JsonParser().parse(downloadData).getAsJsonObject();
                        String aktuellsteVersion = jsonObject.get("tag_name").getAsString();

                        if (logger.isEnabledFor(Level.INFO)) {
                            logger.info("letzte veröffentlichte Version: " + aktuellsteVersion);
                        }

                        if (compareVersion(aktuellsteVersion, AppleJuiceDialog.getVersion()) == 1) {
                            String releaseLink = jsonObject.get("html_url").getAsString();
                            SwingUtilities.invokeLater(() -> {
                                UpdateInformationDialog updateInformationDialog = new UpdateInformationDialog(AppleJuiceDialog.getApp(), aktuellsteVersion, releaseLink);
                                updateInformationDialog.setVisible(true);
                            });
                        } else {
                            if (logger.isEnabledFor(Level.INFO)) {
                                logger.info("aktuelle Version " + AppleJuiceDialog.getVersion() + " | letzte veröffentlichte Version: " + aktuellsteVersion);
                            }
                        }

                    }
                } catch (Exception e) {
                    if (logger.isEnabledFor(Level.INFO)) {
                        logger.info("Aktualisierungsinformationen konnten nicht geladen werden.");
                    }
                }

                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("VersionWorkerThread beendet. " + this);
                }
            }
        };

        versionWorker.start();
    }

    public static int compareVersion(String A, String B) {
        List<String> strList1 = Arrays.stream(A.split("\\."))
                .map(s -> s.replaceAll("^0+(?!$)", ""))
                .collect(Collectors.toList());
        List<String> strList2 = Arrays.stream(B.split("\\."))
                .map(s -> s.replaceAll("^0+(?!$)", ""))
                .collect(Collectors.toList());
        int len1 = strList1.size();
        int len2 = strList2.size();
        int i = 0;
        while (i < len1 && i < len2) {
            if (strList1.get(i).length() > strList2.get(i).length()) return 1;
            if (strList1.get(i).length() < strList2.get(i).length()) return -1;
            int result = new Long(strList1.get(i)).compareTo(new Long(strList2.get(i)));
            if (result != 0) return result;
            i++;
        }
        while (i < len1) {
            if (!strList1.get(i++).equals("0")) return 1;
        }
        while (i < len2) {
            if (!strList2.get(i++).equals("0")) return -1;
        }
        return 0;
    }
}
