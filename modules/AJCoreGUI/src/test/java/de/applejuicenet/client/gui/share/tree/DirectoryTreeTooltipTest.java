package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.gui.controller.GuiText;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
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
