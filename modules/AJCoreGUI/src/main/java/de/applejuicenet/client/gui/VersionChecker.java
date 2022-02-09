package de.applejuicenet.client.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VersionChecker {

    private static final Logger logger = LoggerFactory.getLogger(VersionChecker.class);

    public static void check() {
        Thread versionWorker = new Thread("VersionChecker") {
            public void run() {
                logger.debug("VersionWorkerThread gestartet. " + this);

                try {
                    String updateServer = OptionsManagerImpl.getInstance().getUpdateServerURL();
                    String downloadData = WebsiteContentLoader.getWebsiteContent(updateServer);

                    if (downloadData.length() > 0) {
                        JsonObject jsonObject = JsonParser.parseString(downloadData).getAsJsonObject();
                        String aktuellsteVersion = jsonObject.get("tag_name").getAsString();

                        logger.info("aktuelle Version " + AppleJuiceDialog.getVersion() + " | letzte veröffentlichte Version: " + aktuellsteVersion);

                        if (compareVersion(aktuellsteVersion, AppleJuiceDialog.getVersion()) == 1) {
                            String releaseLink = jsonObject.get("html_url").getAsString();
                            SwingUtilities.invokeLater(() -> {
                                UpdateInformationDialog updateInformationDialog = new UpdateInformationDialog(AppleJuiceDialog.getApp(), aktuellsteVersion, releaseLink);
                                updateInformationDialog.setVisible(true);
                            });
                        }

                    }
                } catch (Exception e) {
                    logger.info("Aktualisierungsinformationen konnten nicht geladen werden.", e);
                }

                logger.debug("VersionWorkerThread beendet. " + this);
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
            int result = Long.valueOf(strList1.get(i)).compareTo(Long.valueOf(strList2.get(i)));
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
