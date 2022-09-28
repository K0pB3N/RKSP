import java.util.Arrays;

public class Pr1 {
    public static int[] fillArray() {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) Math.round((Math.random() * 500001));
        }
        return array;
    }
    public static int FindMin(int[] array) throws InterruptedException {
        int min = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
            Thread.sleep(1);
        }
        return min;
    }

    public static void main(String[] args) throws InterruptedException {
        int[] arr = fillArray();
        long startTime = System.nanoTime();
        System.out.println(Arrays.stream(arr).min());
        System.out.println(Arrays.stream(arr).max());
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution in millis: " + elapsedTime/1000000);
        long usedBytes = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println("Bytes used: " + usedBytes);
    }
}