import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Pr3 {
    public static class File {
        String type;
        int size;

        File(String type, int size) {
            this.type = type;
            this.size = size;
        }
    }

    public static class FileGenerator {
        private ExecutorService executor = Executors.newSingleThreadExecutor();
        String[] types = new String[] { "XML", "JSON", "XLS" };

        public Future<Boolean> generate(ArrayDeque<File> queue, int amount) {
            return executor.submit(() -> {
                for (int i = 0; i < amount; i++) {
                    int timer = (int) Math.round(1 + (Math.random() * 9));
                    int size = (int) Math.round(10 + (Math.random() * 90));
                    int rnd = new Random().nextInt(types.length);
                    try {
                        Thread.sleep(100 * timer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (queue.size() >= 5) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    queue.add(new File(types[rnd], size));
                    System.out.println(types[rnd] + "file with size " + size + " generated");
                }
                System.out.println("All files generated");
                return true;
            });
        }
    }

    public static class FileProcessor {
        int counter = 0;
        String type;
        private ExecutorService handler = Executors.newSingleThreadExecutor();

        public void handle(File file) {
            handler.submit(() -> {
                try {
                    Thread.sleep(file.size * 7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(file.type + "file with size " + file.size + " handled");
                counter++;
            });
        }

        FileProcessor(String type) {
            this.type = type;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int amount = 30;
        ArrayDeque<File> queue = new ArrayDeque<>();
        FileGenerator fileGenerator = new FileGenerator();
        FileProcessor fileProcessorXML = new FileProcessor("XML");
        FileProcessor fileProcessorJSON = new FileProcessor("JSON");
        FileProcessor fileProcessorXLS = new FileProcessor("XLS");
        FileProcessor[] fileProcessors = new FileProcessor[] { fileProcessorXML, fileProcessorJSON, fileProcessorXLS };
        Future<Boolean> future = fileGenerator.generate(queue, amount);
        executor.submit(() -> {
            while (!future.isDone() || !queue.isEmpty()) {
                if (!queue.isEmpty()) {
                    File file = queue.poll();
                    for (FileProcessor processor : fileProcessors) {
                        if (Objects.equals(processor.type, file.type)) {
                            processor.handle(file);
                        }
                    }
                }
            }
        });
        fileGenerator.executor.shutdown();
        executor.shutdown();
        while ((fileProcessorXML.counter + fileProcessorJSON.counter + fileProcessorXLS.counter) != amount) {
            Thread.sleep(100);
        }
        for (FileProcessor processor : fileProcessors) {
            processor.handler.shutdown();
        }
    }
}
