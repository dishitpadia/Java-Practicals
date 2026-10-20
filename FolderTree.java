import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.stream.Stream;

public class FolderTree {
    public static void main(String[] args) {
        Path folder = Paths.get("TestFolder");
        Path report = Paths.get("file_report.txt");

        try {
            Files.createDirectories(folder);
            Files.createDirectories(folder.resolve("Subfolder"));

            Files.writeString(folder.resolve("file1.txt"), "Hello World");
            Files.writeString(folder.resolve("file2.txt"), "Java NIO");
            Files.writeString(folder.resolve("Subfolder").resolve("file3.txt"),
                    "File inside subfolder");

            try (Stream<Path> paths = Files.walk(folder);
                 java.io.BufferedWriter writer = Files.newBufferedWriter(report)) {

                writer.write("FILE REPORT");
                writer.newLine();
                writer.write("==============================================");
                writer.newLine();

                int[] count = {0};
                long[] totalSize = {0};

                for (Path path : (Iterable<Path>) paths::iterator) {
                    if (Files.isRegularFile(path)) {
                        BasicFileAttributes attr = Files.readAttributes(
                                path, BasicFileAttributes.class);

                        String name = path.toString();
                        long size = attr.size();
                        Instant modified = attr.lastModifiedTime().toInstant();

                        writer.write("Name: " + name);
                        writer.newLine();
                        writer.write("Size: " + size + " bytes");
                        writer.newLine();
                        writer.write("Modified: " + modified);
                        writer.newLine();
                        writer.write("----------------------------------------------");
                        writer.newLine();

                        count[0]++;
                        totalSize[0] += size;
                    }
                }

                writer.write("Total files: " + count[0]);
                writer.newLine();
                writer.write("Total size: " + totalSize[0] + " bytes");
                writer.newLine();

                System.out.println("Report generated successfully: "
                        + report.toAbsolutePath());
                System.out.println("Total files: " + count[0]);
                System.out.println("Total size: " + totalSize[0] + " bytes");
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}