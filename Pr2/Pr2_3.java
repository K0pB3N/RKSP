package Pr2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Pr2_3 {
    private static int sum(ByteBuffer bb) {
        int sum = 0;
        while (bb.hasRemaining()) {
            if ((sum & 1) != 0)
                sum = (sum >> 1) + 0x8000;
            else
                sum >>= 1;
            sum += bb.get() & 0xff;
            sum &= 0xffff;
        }
        return sum;
    }

    private static void sum(File f) throws IOException {
        try (
                FileInputStream fis = new FileInputStream(f);
                FileChannel fc = fis.getChannel()) {
            int sz = (int) fc.size();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
            int sum = sum(bb);
            int kb = (sz + 1023) / 1024;
            System.out.println(sum + "\t" + kb + "\t" + f);
        }
    }

    public static void main(String[] args) {
        File f = new File("Pr2\\Onetext.txt");
        try {
            sum(f);
        } catch (IOException e) {
            System.err.println(f + ": " + e);
        }
    }
}
