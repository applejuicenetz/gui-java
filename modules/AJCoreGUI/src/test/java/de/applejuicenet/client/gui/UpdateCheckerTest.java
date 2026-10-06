package de.applejuicenet.client.gui;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UpdateCheckerTest {
    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
    }

    private static class Recorder implements UpdateChecker.Ui {
        final List<String> events = new ArrayList<>();

        public void newVersion(String version, String releaseLink) {
            events.add("new:" + version + "|" + releaseLink);
        }

        public void noNewVersion() {
            events.add("none");
        }

        public void failed() {
            events.add("failed");
        }
    }

    private static String release(String tag) {
        return "{\"tag_name\":\"" + tag + "\",\"html_url\":\"https://github.com/x/y/releases/tag/" + tag + "\"}";
    }

    private static Recorder run(boolean silent, UpdateChecker.Loader loader, String current) {
        Recorder recorder = new Recorder();
        UpdateChecker.run(silent, loader, current, recorder);
        return recorder;
    }

    @Test
    public void newerVersionIsReportedSilentOrNot() {
        for (boolean silent : new boolean[] {true, false}) {
            assertEquals(List.of("new:0.87.0|https://github.com/x/y/releases/tag/0.87.0"),
                    run(silent, () -> release("0.87.0"), "0.86.4").events);
        }
    }

    @Test
    public void sameOrOlderVersionShowsNoticeOnlyWhenNotSilent() {
        assertEquals(List.of(), run(true, () -> release("0.86.4"), "0.86.4").events);
        assertEquals(List.of("none"), run(false, () -> release("0.86.4"), "0.86.4").events);
        assertEquals(List.of(), run(true, () -> release("0.86.3"), "0.86.4").events);
        assertEquals(List.of("none"), run(false, () -> release("0.86.3"), "0.86.4").events);
    }

    @Test
    public void failuresStaySilentAtStartButAreShownOnRequest() {
        UpdateChecker.Loader broken = () -> {
            throw new java.io.IOException("offline");
        };
        assertEquals(List.of(), run(true, broken, "0.86.4").events);
        assertEquals(List.of("failed"), run(false, broken, "0.86.4").events);
        assertEquals(List.of(), run(true, () -> "", "0.86.4").events);
        assertEquals(List.of("failed"), run(false, () -> "", "0.86.4").events);
        assertEquals(List.of("failed"), run(false, () -> "kein json", "0.86.4").events);
        assertEquals(List.of("failed"), run(false, () -> "{\"name\":\"x\"}", "0.86.4").events);
    }

    @Test
    public void unknownOwnVersionIsNotReportedAsUpToDate() {
        assertEquals(List.of(), run(true, () -> release("0.87.0"), null).events);
        assertEquals(List.of("failed"), run(false, () -> release("0.87.0"), null).events);
    }

    @Test
    public void noNewVersionDialogShowsMessageAndRepositoryLink() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            UpdateInformationDialog dialog = UpdateInformationDialog.noNewVersion(null);
            List<String> texts = new ArrayList<>();
            collect(dialog.getContentPane(), texts);
            assertTrue(texts.toString(), texts.stream().anyMatch(t -> t.contains("keine neuere Version")));
            assertTrue(texts.toString(), texts.stream().anyMatch(t -> t.contains(UpdateChecker.REPOSITORY_URL)));
            assertEquals("Update prüfen", dialog.getTitle());
            dialog.dispose();
        });
    }

    @Test
    public void failureDialogShowsMessageAndRepositoryLink() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            UpdateInformationDialog dialog = UpdateInformationDialog.checkFailed(null);
            List<String> texts = new ArrayList<>();
            collect(dialog.getContentPane(), texts);
            assertTrue(texts.toString(), texts.stream().anyMatch(t -> t.contains("fehlgeschlagen")));
            assertTrue(texts.toString(), texts.stream().anyMatch(t -> t.contains(UpdateChecker.REPOSITORY_URL)));
            dialog.dispose();
        });
    }

    private static void collect(Component component, List<String> texts) {
        if (component instanceof JLabel label && label.getText() != null) {
            texts.add(label.getText());
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                collect(child, texts);
            }
        }
    }
}
