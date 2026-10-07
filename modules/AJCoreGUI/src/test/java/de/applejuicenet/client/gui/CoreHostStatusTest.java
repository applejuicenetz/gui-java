package de.applejuicenet.client.gui;

import org.junit.Test;
import static org.junit.Assert.*;

public class CoreHostStatusTest {
    @Test
    public void displaysLocalhostOrRemoteAddress() throws Exception {
        var method = AppleJuiceDialog.class.getDeclaredMethod("getCoreHostText", String.class);
        method.setAccessible(true);
        assertEquals("localhost", method.invoke(null, "127.0.0.1"));
        assertEquals("localhost", method.invoke(null, "::1"));
        assertEquals("192.0.2.10", method.invoke(null, "192.0.2.10"));
    }
}
