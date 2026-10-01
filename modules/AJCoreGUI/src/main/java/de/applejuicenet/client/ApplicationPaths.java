package de.applejuicenet.client;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Resource files live beside the JAR, regardless of the OS launch directory. */
final class ApplicationPaths {
    private ApplicationPaths() { }

    static void configure(String[] args) {
        try {
            Path location = Path.of(AppleJuiceClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path workingDirectory = Path.of(System.getProperty("user.dir"));
            System.setProperty("user.dir", resourceDirectory(location, workingDirectory, args).toString());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("GUI-Ressourcenpfad kann nicht bestimmt werden", e);
        }
    }

    static Path resourceDirectory(Path location, Path workingDirectory, String[] args) {
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith("-path=")) {
                    return workingDirectory.resolve(arg.substring(6)).normalize();
                }
            }
        }
        Path directory = location.getParent();
        if (Files.isRegularFile(location) && directory != null && Files.isRegularFile(directory.resolve("wizard.xml"))) {
            return directory;
        }
        return workingDirectory;
    }
}
