package de.applejuicenet.client;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class GuiRestartTest {
    @Test
    public void restartKeepsLauncherButDoesNotReplayLinks() throws Exception {
        var method = AppleJuiceClient.class.getDeclaredMethod("restartCommand", String.class, String[].class, long.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> command = (List<String>) method.invoke(null, "/jdk/bin/java",
                new String[]{"-cp", "/gui/lib/*", "de.applejuicenet.client.AppleJuiceClient",
                        "-path=/gui", "ajfsp://old", "-restart-parent=12"}, 42L);
        assertEquals(List.of("/jdk/bin/java", "-cp", "/gui/lib/*",
                "de.applejuicenet.client.AppleJuiceClient", "-path=/gui", "-restart-parent=42"), command);
    }
}
