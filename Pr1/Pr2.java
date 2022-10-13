import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Pr2 {
    public static class Calculator {
        private ExecutorService executor = Executors.newSingleThreadExecutor();

        public Future<Integer> calculate(Integer input) {
            return executor.submit(() -> {
                int timer = (int) Math.round(1 + (Math.random() * 4));
                System.out.println("Task will be solved in " + timer + " seconds");
                Thread.sleep(timer * 1000);
                return input * input;
            });
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        Calculator calculator = new Calculator();
        while (true) {
            int num = sc.nextInt();
            Future<Integer> future = calculator.calculate(num);
            while (!future.isDone()) {
                Thread.sleep(1000);
            }
            Integer result = future.get();
            System.out.println(result);
            if (result == 0)
                break;
        }
        calculator.executor.shutdown();
    }
}
