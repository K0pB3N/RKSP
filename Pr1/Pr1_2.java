import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pr1_2 {
    public static class FindMin {
        int[] array = Pr1.fillArray();
        int min = array[0];
        public void findMin() {
            for (int i = 0; i < array.length; i++) {
                if (array[i] < min) {
                    min = array[i];
                }
            }
        }
    }

    public static void main(String[] args) {
        Object lock = new Object();
        Pr1_1.FindMin Found = new Pr1_1.FindMin();
        ExecutorService executor = Executors.newWorkStealingPool();
        long startTime = System.nanoTime();
        Runnable task = () -> {
            synchronized (lock) {
                Found.findMin();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        for (int el : Found.array) {
            executor.submit(task);
        }

        executor.shutdown();

        while (true) {
            if (executor.isTerminated())
                break;
        }
        System.out.println(Found.min);
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution in millis: " + elapsedTime / 1000000);
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Bytes used: " + usedBytes);
    }
}
