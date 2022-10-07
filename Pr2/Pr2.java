package Pr2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Pr2 {

    public static void readAsBytes(Path filePath) {
        if (Files.exists(filePath)) {
            try {
                byte[] bytes = Files.readAllBytes(filePath);
                for (byte b : bytes) {
                    System.out.print((char) b);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found");
        }
    }

    public static void main(String[] args) {
        Path path = Paths.get("Pr2\\text.txt");
        readAsLines(path);

    }

    private static void readAsLines(Path path) {
        try {
            Files.lines(path).forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}