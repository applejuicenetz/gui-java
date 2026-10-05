package de.applejuicenet.client.gui.tray;

import org.junit.Test;
import static org.junit.Assert.*;

public class TrayBackendTest {
    @Test public void skipsNativeBackendOn32BitJvm() {
        assertFalse(TrayLoader.useNativeTray("Linux", "32"));
    }

    @Test public void keepsNativeBackendOn64BitLinux() {
        assertTrue(TrayLoader.useNativeTray("Linux", "64"));
        assertFalse(TrayLoader.useNativeTray("Windows", "64"));
        assertFalse(TrayLoader.useNativeTray("Linux", ""));
    }
}
