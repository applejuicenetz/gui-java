package de.applejuicenet.client.gui.wizard;

import org.junit.Test;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConnectionPresetsTest {
    @Test
    public void parsesGroupsIntoFlatListWithConvertedUpload() throws Exception {
        String json = "[{\"group\":\"A\",\"items\":[{\"name\":\"Fiber 1.000\",\"download\":1000000,\"upload\":50000,\"ram\":4096}]},"
                + "{\"group\":\"B\",\"items\":[{\"name\":\"DSL 384\",\"download\":384,\"upload\":128,\"ram\":128}]}]";
        ConnectionKind[] kinds = ConnectionPresets.parse(new StringReader(json));
        assertEquals(2, kinds.length);
        assertEquals("Fiber 1.000", kinds[0].getBezeichnung());
        assertEquals(6104, kinds[0].getMaxUpload());
        assertEquals(0, kinds[0].getMaxDownload());
        assertEquals(50, kinds[0].getMaxNewConnectionsPro10Sek());
        assertEquals(16, kinds[1].getMaxUpload());
    }

    @Test
    public void shippedWizardJsonIsReadable() throws Exception {
        for (String dir : new String[]{"resources", "../../resources"}) {
            if (Files.isRegularFile(Paths.get(dir, "presets.json"))) {
                ConnectionKind[] kinds = ConnectionPresets.parse(
                        Files.newBufferedReader(Paths.get(dir, "presets.json"), StandardCharsets.UTF_8));
                assertTrue(kinds.length > 0);
                return;
            }
        }
        throw new AssertionError("resources/presets.json nicht gefunden");
    }
}
