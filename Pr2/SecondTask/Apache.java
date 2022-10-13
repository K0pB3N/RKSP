package Pr2.SecondTask;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Apache {
    private static void copyFile(File src, File dest) throws IOException {
        FileUtils.copyFile(src, dest);
    }

    public static void main(String[] args) {
        File from = new File("Pr2\\SecondTask\\secTaskOrig.txt");
        File to = new File("Pr2\\SecondTask\\secTaskOrigCOPY3.txt");

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