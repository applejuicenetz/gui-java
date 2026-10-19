package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.gui.controller.GuiText;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DirectoryTreeTooltipTest {
    @BeforeClass
    public static void loadLanguage() {
        de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
    }

    @Test
    public void coreFoldersGetTheirOwnTooltip() {
        assertEquals("mainform.sharetree.incoming.hint", DirectoryTree.tooltipKey(node(true, false)));
        assertEquals("mainform.sharetree.temp.hint", DirectoryTree.tooltipKey(node(false, true)));
        assertNull(DirectoryTree.tooltipKey(node(false, false)));
    }

    @Test
    public void criticalFoldersGetTheirOwnTooltip() {
        DirectoryNode critical = new DirectoryNode(null, null) {
            @Override
            public boolean isCriticalDirectory() {
                return true;
            }
        };

        assertEquals("mainform.sharetree.critical.hint", DirectoryTree.tooltipKey(critical));
        assertTrue(GuiText.textOr("mainform.sharetree.critical.hint", null) != null);
    }

    @Test
    public void homeDirectoryIsCritical() {
        String home = System.getProperty("user.home");
        de.applejuicenet.client.facade.entity.Directory dir = folder(home);
        de.applejuicenet.client.facade.entity.Directory below = folder(home + "/projects");

        assertFalse(new DirectoryNode(null, below).isCriticalDirectory());
        assertTrue(new DirectoryNode(null, dir).isCriticalDirectory());
    }

    @Test
    public void tooltipTextsExistInLanguageFiles() {
        String incoming = GuiText.textOr("mainform.sharetree.incoming.hint", null);
        String temp = GuiText.textOr("mainform.sharetree.temp.hint", null);

        assertTrue(incoming != null && incoming.contains("Incoming"));
        assertTrue(temp != null && temp.contains("Temp"));
    }

    @Test
    public void tooltipForCoreFoldersResolvesLanguageText() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DirectoryTree tree = new DirectoryTree();

            assertEquals(GuiText.textOr("mainform.sharetree.incoming.hint", null), tree.tooltipFor(node(true, false)));
            assertEquals(GuiText.textOr("mainform.sharetree.temp.hint", null), tree.tooltipFor(node(false, true)));
            assertNull(tree.tooltipFor(node(false, false)));
        });
    }

    private static de.applejuicenet.client.facade.entity.Directory folder(String path) {
        return new de.applejuicenet.client.facade.entity.Directory() {
            public String getName() { return path; }
            public int getType() { return TYPE_FOLDER; }
            public boolean isFileSystem() { return true; }
            public String getPath() { return path; }
            public de.applejuicenet.client.facade.entity.Directory[] getChildren() { return new de.applejuicenet.client.facade.entity.Directory[0]; }
        };
    }

    private static DirectoryNode node(boolean incoming, boolean temp) {
        return new DirectoryNode(null, null) {
            @Override
            public boolean isIncomingDirectory() {
                return incoming;
            }

            @Override
            public boolean isTempDirectory() {
                return temp;
            }
        };
    }
}
