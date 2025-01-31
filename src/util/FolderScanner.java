package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FolderScanner {
    public static List<Path> getFiles(String path) {
        Path folderPath = Paths.get(path);

        try {
            return Files.list(folderPath)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return List.of();
        }
    }
}