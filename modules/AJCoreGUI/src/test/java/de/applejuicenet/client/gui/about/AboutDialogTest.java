package de.applejuicenet.client.gui.about;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AboutDialogTest {
    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
    }

    @Test
    public void creditsKeepNamesWithoutMailAddresses() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try (DialogFixture fixture = new DialogFixture("modern")) {
                @SuppressWarnings("unchecked")
                List<AboutDialog.CreditsEntry> entries = (List<AboutDialog.CreditsEntry>) field(fixture.panel, "credits");
                List<String> names = entries.stream().map(AboutDialog.CreditsEntry::getOutputText)
                        .collect(Collectors.toList());
                assertTrue(names.contains("Maj0r"));
                assertTrue(names.contains("loevenwong"));
                assertTrue(names.contains("red171"));
                assertTrue(names.contains("muhviehstarr"));
                assertTrue(names.stream().noneMatch(name -> name.contains("@")));
                assertEquals(6, names.size());
                assertFalse(names.contains("Banner & Bilder"));
                assertFalse(names.contains("Übersetzung"));
                assertFalse(names.contains("Kontakt"));
            } catch (Exception error) {
                throw new AssertionError(error);
            }
        });
    }

    private static Object field(Object object, String name) throws Exception {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    private static class DialogFixture implements AutoCloseable {
        private final Map<String, ImageIcon> icons;
        private final ImageIcon previousBanner;
        private final ImageIcon previousFlag;
        private final ImageIcon banner;
        private final ImageIcon flag;
        private final AboutDialog dialog;
        private final AboutDialog.BackPanel panel;

        @SuppressWarnings("unchecked")
        DialogFixture(String iconSet) throws Exception {
            icons = (Map<String, ImageIcon>) field(IconManager.getInstance(), "icons");
            previousBanner = icons.get("applejuiceinfobanner");
            previousFlag = icons.get("deutsch");
            banner = new ImageIcon(ImageIO.read(Paths.get("../../resources/icons/" + iconSet + "/applejuiceinfobanner.png").toFile()));
            flag = new ImageIcon(ImageIO.read(Paths.get("../../resources/icons/" + iconSet + "/deutsch.png").toFile()));
            icons.put("applejuiceinfobanner", banner);
            icons.put("deutsch", flag);
            dialog = new AboutDialog(null, false);
            panel = (AboutDialog.BackPanel) dialog.getContentPane().getComponent(0);
        }

        public void close() {
            dialog.dispose();
            restore("applejuiceinfobanner", previousBanner);
            restore("deutsch", previousFlag);
        }

        private void restore(String key, ImageIcon icon) {
            if (icon == null) {
                icons.remove(key);
            } else {
                icons.put(key, icon);
            }
        }
    }
}
