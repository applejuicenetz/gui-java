package de.applejuicenet.client.gui;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.applejuicenet.client.facade.shared.WebsiteContentLoader;
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
        if (currentVersion == null || currentVersion.isBlank()) {
            logger.debug("Skipping update check: no packaged version metadata (development start).");
            if (!silent) {
                ui.failed();
            }
            return;
        }
        logger.debug("VersionWorkerThread started.");

        try {
            String downloadData = loader.load();

            if (downloadData == null || downloadData.isEmpty()) {
                throw new IllegalStateException("empty response");
            }

            JsonObject jsonObject = Json.parse(downloadData).asObject();
            String latestVersion = jsonObject.get("tag_name").asString();

            logger.info("current version " + currentVersion + " | latest released version: " + latestVersion);

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

        logger.debug("VersionWorkerThread finished.");
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

    private static final java.util.regex.Pattern VERSION = java.util.regex.Pattern.compile(
            "^[vV]?(\\d+(?:\\.\\d+)*)(?:[-+].*)?$");

    /**
     * Compares release versions such as {@code 0.86.4}. A leading {@code v} and a suffix after
     * {@code -} or {@code +} (e.g. {@code -beta}, {@code +build7}) are ignored.
     *
     * @throws IllegalArgumentException if a value is not a version number
     */
    public static int compareVersion(String a, String b) {
        List<Long> first = parseVersion(a);
        List<Long> second = parseVersion(b);
        int length = Math.max(first.size(), second.size());

        for (int i = 0; i < length; i++) {
            long left = i < first.size() ? first.get(i) : 0;
            long right = i < second.size() ? second.get(i) : 0;

            if (left != right) {
                return left > right ? 1 : -1;
            }
        }
        return 0;
    }

    private static List<Long> parseVersion(String version) {
        java.util.regex.Matcher matcher = VERSION.matcher(version == null ? "" : version.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Not a version number: '" + version + "'");
        }
        try {
            return Arrays.stream(matcher.group(1).split("\\.")).map(Long::valueOf).collect(Collectors.toList());
        } catch (NumberFormatException tooLarge) {
            throw new IllegalArgumentException("Not a version number: '" + version + "'", tooLarge);
        }
    }
}
