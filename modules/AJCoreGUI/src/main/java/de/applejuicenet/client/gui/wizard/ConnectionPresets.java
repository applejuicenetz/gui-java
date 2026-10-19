package de.applejuicenet.client.gui.wizard;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the connection presets of the wizard from <code>presets.json</code> (format of the
 * config-wizard repository, <code>presets.json</code>).
 */
public class ConnectionPresets {
    public static final String FILE_NAME = "presets.json";
    private static final int MAX_NEW_CONNECTIONS_PER_10_SEC = 50;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPresets.class);

    private ConnectionPresets() {
    }

    public static ConnectionKind[] getConnections() {
        Path path = Paths.get(System.getProperty("user.dir"), FILE_NAME);
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return parse(reader);
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            return null;
        }
    }

    static ConnectionKind[] parse(Reader reader) throws IOException {
        List<ConnectionKind> kinds = new ArrayList<>();
        JsonArray groups = Json.parse(reader).asArray();
        for (JsonValue group : groups) {
            for (JsonValue item : group.asObject().get("items").asArray()) {
                JsonObject object = item.asObject();
                kinds.add(new ConnectionKind(object.getString("name", ""),
                        uploadKiBPerSecond(object.getInt("upload", 0)), 0,
                        MAX_NEW_CONNECTIONS_PER_10_SEC));
            }
        }
        return kinds.toArray(new ConnectionKind[0]);
    }

    /** Upload in kbit/s -> KiB/s, as before in wizard.xml. */
    static int uploadKiBPerSecond(int kbit) {
        return (int) Math.round(kbit * 1000.0 / 8 / 1024);
    }
}
