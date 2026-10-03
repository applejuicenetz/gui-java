package de.applejuicenet.client.fassade.shared;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class XMLDecoderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void writesUtf8AttributesWithEscapedCharacters() throws Exception {
        File file = temporaryFolder.newFile("settings.xml");
        Files.writeString(file.toPath(), "<root><settings name='old'/></root>", StandardCharsets.UTF_8);
        WritableDecoder decoder = new WritableDecoder(file);
        String value = "Größe & <Spaß> \"名\"";
        decoder.setAttributeByTagName(new String[]{"settings", "name"}, value);
        WritableDecoder reloaded = new WritableDecoder(file);
        assertEquals(value, reloaded.getFirstAttrbuteByTagName(new String[]{"settings", "name"}));
    }

    @Test
    public void writesIntegerAttributes() throws Exception {
        File file = temporaryFolder.newFile("settings.xml");
        Files.writeString(file.toPath(), "<root><settings port='1'/></root>", StandardCharsets.UTF_8);
        WritableDecoder decoder = new WritableDecoder(file);
        decoder.setAttributeByTagName(new String[]{"settings", "port"}, 9851);
        WritableDecoder reloaded = new WritableDecoder(file);
        assertEquals("9851", reloaded.getFirstAttrbuteByTagName(new String[]{"settings", "port"}));
    }

    private static class WritableDecoder extends XMLDecoder {
        WritableDecoder(File file) {
            super(file);
        }
    }
}
