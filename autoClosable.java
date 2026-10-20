class FileResource implements AutoCloseable {

    public FileResource() {
        System.out.println("Resource opened.");
    }

    public void readFile() {
        System.out.println("Reading file...");
        throw new RuntimeException("Original error: File reading failed!");
    }

    @Override
    public void close() {
        System.out.println("Resource closed.");
    }
}

public class autoClosable {

    public static void main(String[] args) {

        try (FileResource resource = new FileResource()) {
            resource.readFile();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}