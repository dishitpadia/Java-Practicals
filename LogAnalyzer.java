import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class LogAnalyzer {
    public static void main(String[] args) {
        Path file1 = Paths.get("log1.txt");
        Path file2 = Paths.get("log2.txt");
        Path file3 = Paths.get("log3.txt");

        String[] contents = {
            "INFO: Application started\nERROR: Database connection failed\nINFO: User logged in",
            "WARNING: Low memory\nERROR: File not found\nINFO: Application running",
            "INFO: Server started\nINFO: Request received\nERROR: Connection timeout"
        };

        try {
            Files.write(file1, contents[0].getBytes());
            Files.write(file2, contents[1].getBytes());
            Files.write(file3, contents[2].getBytes());

            Path[] files = {file1, file2, file3};

            String keyword = "ERROR";
            int totalLines = 0;
            int matchingLines = 0;

            for (Path file : files) {
                List<String> lines = Files.readAllLines(file);

                for (String line : lines) {
                    totalLines++;

                    if (line.contains(keyword)) {
                        matchingLines++;
                    }
                }

                BasicFileAttributes attributes = Files.readAttributes(
                    file, BasicFileAttributes.class
                );

                System.out.println("File: " + file.getFileName());
                System.out.println("Size: " + attributes.size() + " bytes");
                System.out.println("---------------------------");
            }

            System.out.println("Total lines across all files: " + totalLines);
            System.out.println("Lines containing \"" + keyword + "\": " + matchingLines);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}