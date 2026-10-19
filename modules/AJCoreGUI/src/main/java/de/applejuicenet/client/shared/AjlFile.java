package de.applejuicenet.client.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class AjlFile {
    private AjlFile() { }

    public static List<String> readLinks(File file) throws IOException {
        List<String> links = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null && !line.equals("100")) { }
            if (line == null) {
                throw new IOException("AJL file does not contain format marker 100");
            }
            while ((line = reader.readLine()) != null) {
                String checksum = reader.readLine();
                String size = reader.readLine();
                if (checksum == null || size == null) {
                    throw new IOException("AJL file contains an incomplete entry");
                }
                links.add("ajfsp://file|" + line + "|" + checksum + "|" + size + "/");
            }
        }
        return links;
    }
}
