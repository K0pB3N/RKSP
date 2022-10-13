package Pr2.SecondTask;
import java.io.*;

public class FileINOUT {
    public static void copyFile(File src, File dest) throws IOException {
        try (InputStream is = new FileInputStream(src);
        OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
    }

    public static void main(String[] args) {
        File from = new File("Pr2\\SecondTask\\secTaskOrig.txt");
        File to = new File("Pr2\\SecondTask\\secTaskOrigCOPY1.txt");

        try {
            long startTime = System.nanoTime();
            copyFile(from, to);
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("File copied successfully.");
            System.out.println("Total execution in millis: " + elapsedTime / 1000000);
            long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println("Bytes used: " + usedBytes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
