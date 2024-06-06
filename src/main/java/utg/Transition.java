package utg;

import core.utils.App;
import core.utils.Globals;
import core.utils.Serializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Transition {

    public static void transit(Version from, Version to) {
        if (from.compare(Version.parse("v1.1.5")) == Version.LESS) {
            // Starting from v1.1.5 (from v.1.1.2), Dashboard path changes from ".utgdashboard" to ".utgsd".
            try {
                final String oldPath = Globals.joinPaths(System.getProperty("user.home"), ".dashboard");
                final String newPath = Globals.joinPaths(System.getProperty("user.home"), ".utgsd");
                copyDirectory(oldPath, newPath);
                // Todo: delete oldPath?
            } catch (IOException e) {
                App.silenceException(e);
            }
        }
    }

    public static void copyDirectory(String sourceDir, String destinationDir) throws IOException {
        Files.walk(Paths.get(sourceDir)).forEach(source -> {
            final Path destination = Paths.get(destinationDir, source.toString().substring(sourceDir.length()));
            try {
                Files.copy(source, destination);
            } catch (IOException e) {
                App.silenceException(e.getMessage());
            }
        });
    }

}
