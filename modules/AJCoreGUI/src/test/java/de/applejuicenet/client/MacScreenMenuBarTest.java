package de.applejuicenet.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MacScreenMenuBarTest {
    private String osName;
    private String screenMenu;

    @Before
    public void saveProperties() {
        osName = System.getProperty("os.name");
        screenMenu = System.getProperty("apple.laf.useScreenMenuBar");
        System.clearProperty("apple.laf.useScreenMenuBar");
    }

    @After
    public void restoreProperties() {
        System.setProperty("os.name", osName);
        if (screenMenu == null) {
            System.clearProperty("apple.laf.useScreenMenuBar");
        } else {
            System.setProperty("apple.laf.useScreenMenuBar", screenMenu);
        }
    }

    @Test
    public void enablesScreenMenuBarOnMac() {
        System.setProperty("os.name", "Mac OS X");
        AppleJuiceClient.useMacScreenMenuBar();
        assertEquals("true", System.getProperty("apple.laf.useScreenMenuBar"));
    }

    @Test
    public void keepsWindowMenuBarOnOtherSystems() {
        System.setProperty("os.name", "Linux");
        AppleJuiceClient.useMacScreenMenuBar();
        assertNull(System.getProperty("apple.laf.useScreenMenuBar"));
    }
}
