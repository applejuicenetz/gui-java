package de.applejuicenet.client.gui;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.applejuicenet.client.facade.shared.WebsiteContentLoader;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateChecker {

    private static final Logger logger = LoggerFactory.getLogger(UpdateChecker.class);

    static final String REPOSITORY_URL = "https://github.com/applejuicenetz/gui-java";

    /** Results of the check; shown by the caller on the EDT. */
    interface Ui {
        void newVersion(String version, String releaseLink);

        void noNewVersion();

        void failed();
    }

    /** Check at startup: only reports a new version, nothing else. */
    public static void check() {
        check(true);
    }

    /**
     * @param silent {@code true}: only report a new version (startup). {@code false}: also report
     * when there is no new version or the check fails (menu "Check for update").
     */
    public static void check(boolean silent) {
        Thread.ofVirtual().name("UpdateChecker").start(() -> run(silent,
                () -> WebsiteContentLoader.getWebsiteContent(OptionsManagerImpl.getInstance().getUpdateServerURL()),
                AppleJuiceDialog.getVersion(), new SwingUi()));
    }

    interface Loader {
        String load() throws Exception;
    }

    static void run(boolean silent, Loader loader, String currentVersion, Ui ui) {
        logger.debug("VersionWorkerThread started.");

        try {
            String downloadData = loader.load();

            if (downloadData == null || downloadData.isEmpty()) {
                throw new IllegalStateException("leere Antwort");
            }

            JsonObject jsonObject = Json.parse(downloadData).asObject();
            String latestVersion = jsonObject.get("tag_name").asString();

            logger.info("current version " + currentVersion + " | latest released version: " + latestVersion);

            if (currentVersion == null) {
                throw new IllegalStateException("own version unknown");
            }

            if (compareVersion(latestVersion, currentVersion) == 1) {
                ui.newVersion(latestVersion, jsonObject.get("html_url").asString());
            } else if (!silent) {
                ui.noNewVersion();
            }
        } catch (Exception e) {
            logger.info("Update information could not be loaded.", e);
            if (!silent) {
                ui.failed();
            }
        }

        logger.debug("VersionWorkerThread beendet.");
    }

    private static final class SwingUi implements Ui {
        public void newVersion(String version, String releaseLink) {
            SwingUtilities.invokeLater(() -> new UpdateInformationDialog(AppleJuiceDialog.getApp(), version, releaseLink)
                    .setVisible(true));
        }

        public void noNewVersion() {
            SwingUtilities.invokeLater(() -> UpdateInformationDialog.noNewVersion(AppleJuiceDialog.getApp()).setVisible(true));
        }

        public void failed() {
            SwingUtilities.invokeLater(() -> UpdateInformationDialog.checkFailed(AppleJuiceDialog.getApp()).setVisible(true));
        }
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
