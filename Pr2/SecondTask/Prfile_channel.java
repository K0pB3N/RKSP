package Pr2.SecondTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Prfile_channel {
    public static void copyFile(File src, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(src).getChannel();
                FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        }
    }

    public static void main(String[] args) {
        File from = new File("Pr2\\SecondTask\\secTaskOrig.txt");
        File to = new File("Pr2\\SecondTask\\secTaskOrigCOPY2.txt");
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
