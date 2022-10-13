package Pr3;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;

public class Main {

    public static void main(String[] args) {
        // Program1();
        // Program2();
        // Program3();
        Program4();
    }

    public static void Program1() {
        final Program1.SensorTemp[] SensorTemp = new Program1.SensorTemp[1];
        new Thread(() -> SensorTemp[0] = new Program1.SensorTemp()).start();
        final Program1.SensorCO2[] SensorCO2 = new Program1.SensorCO2[1];
        new Thread(() -> SensorCO2[0] = new Program1.SensorCO2()).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final boolean[] Alarm = { false, false };
        SensorTemp[0].Temperature.subscribeOn(new NewThreadScheduler()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(Integer integer) {
                if (integer > 25) {
                    Alarm[0] = true;
                    if (Alarm[1])
                        System.out.println("ALARM!!!");
                    else
                        System.out.println("Температура превысила норму");
                } else {
                    Alarm[0] = false;
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        SensorCO2[0].CO2.subscribeOn(new NewThreadScheduler()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(Integer integer) {
                if (integer > 70) {
                    Alarm[1] = true;
                    if (Alarm[0])
                        System.out.println("ALARM!!!");
                    else
                        System.out.println("CO2 превысил норму");
                } else {
                    Alarm[1] = false;
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
            }
        });
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Program1 {
        public static class SensorTemp {
            public Observable<Integer> Temperature;

            public SensorTemp() {
                Observable<Integer> source = Observable.create(emitter -> {
                    Random rnd = new Random();
                    while (true) {
                        emitter.onNext(rnd.nextInt(15) + 15);
                        Thread.sleep(1000);
                    }
                });
                Temperature = source;
            }
        }

        public static class SensorCO2 {
            public Observable<Integer> CO2;

            public SensorCO2() {
                Observable<Integer> source = Observable.create(emitter -> {
                    Random rnd = new Random();
                    while (true) {
                        emitter.onNext(rnd.nextInt(70) + 30);
                        Thread.sleep(1000);
                    }
                });
                CO2 = source;
            }
        }
    }

    public static void Program2() {
        Random rnd = new Random();
        // 2.1.3
        System.out.println("2.1.3");
        var sourceStream = java.util.stream.IntStream.range(0, rnd.nextInt(1000))
                .map(x -> rnd.nextInt());
        var finalStream = sourceStream.limit(10);
        finalStream.forEach(s -> System.out.println(s));
        // 2.2.3
        System.out.println("2.2.3");
        sourceStream = java.util.stream.IntStream.range(0, 1000)
                .map(x -> rnd.nextInt());
        var sourceStream2 = java.util.stream.IntStream.range(0, 1000)
                .map(x -> rnd.nextInt());
        var sourceArray2 = sourceStream2.toArray();
        AtomicInteger i = new AtomicInteger();
        finalStream = sourceStream.flatMap(x -> java.util.stream.IntStream.of(x, sourceArray2[i.getAndIncrement()]));
        finalStream.forEach(s -> System.out.println(s));
        // 2.3.3
        System.out.println("2.3.3");
        sourceStream = java.util.stream.IntStream.range(0, rnd.nextInt(1000))
                .map(x -> rnd.nextInt());
        System.out.println(sourceStream.reduce((x, y) -> y).getAsInt());
    }

    public static void Program3() {
        Random rnd = new Random();
        for (int i = 0; i < Program3.userFriends.length; i++) {
            Program3.userFriends[i] = new Program3.UserFriend(rnd.nextInt(), rnd.nextInt());
        }
        // int !?
        Integer[] userIds = new Integer[10];
        for (int i = 0; i < userIds.length; i++) {
            userIds[i] = Program3.userFriends[rnd.nextInt(Program3.userFriends.length)].userId;
        }
        var observable = Observable.fromArray(userIds).flatMap(x -> Program3.getFriends(x));
        observable.forEach(s -> System.out.println(s));
    }

    public static class Program3 {
        public static Program3.UserFriend[] userFriends = new UserFriend[100];

        public static class UserFriend {
            public int userId = 0;
            public int friendId = 0;

            public UserFriend(int userId, int friendId) {
                this.userId = userId;
                this.friendId = friendId;
            }

            @Override
            public String toString() {
                return "UserFriend{" +
                        "userId=" + userId +
                        ", friendId=" + friendId +
                        '}';
            }
        }

        public static Observable<UserFriend> getFriends(int userId) {
            return Observable.fromArray(userFriends).filter(x -> x.userId == userId);
        }
    }

    public static void Program4() {
        Program4.Generator gn = new Program4.Generator();
        Program4.Handler XMLHandler = new Program4.Handler(gn.Files, "XML");
        Program4.Handler JSONHandler = new Program4.Handler(gn.Files, "JSON");
        Program4.Handler XLSHandler = new Program4.Handler(gn.Files, "XLS");

        // new Thread(() -> gn.Start()).start();
        new Thread(() -> XMLHandler.Start()).start();
        new Thread(() -> JSONHandler.Start()).start();
        new Thread(() -> XLSHandler.Start()).start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Program4 {
        public static class File {

            public String FileType;
            public int FileSize;

            public File(String fileType, int fileSize) {
                FileType = fileType;
                FileSize = fileSize;
            }
        }

        public static class Generator {
            public Observable<File> Files;

            public Generator() {
                Files = Observable.create(emitter -> {
                    Random rnd = new Random();
                    while (true) {
                        emitter.onNext(new File(FileTypes[rnd.nextInt(FileTypes.length)], rnd.nextInt(90) + 10));
                        Thread.sleep(rnd.nextInt(900) + 100);
                    }
                });
            }

            public void Start() {
                Files = Observable.create(emitter -> {
                    Random rnd = new Random();
                    while (true) {
                        if (Files.count().blockingGet() < 5) {
                            emitter.onNext(new File(FileTypes[rnd.nextInt(FileTypes.length)], rnd.nextInt(90) + 10));
                        }
                        Thread.sleep(rnd.nextInt(900) + 100);
                    }
                });
            }

            private static final String[] FileTypes = new String[] { "XML", "JSON", "XLS" };
            // private static final int[] FileSizes = new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
        }

        public static class Handler {
            Observable<File> FileQueue;
            String FileType;

            public Handler(Observable<File> fileQueue, String fileType) {
                FileQueue = fileQueue;
                FileType = fileType;
                // FileSize = fileSizes;
            }

            public void Start() {
                FileQueue.subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onNext(File file) {
                        if (file.FileType.equals(FileType)) {
                            try {
                                Thread.sleep(file.FileSize * 7);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(file.FileType + " " + "create with size" + " " + file.FileSize + " " + "generated");
                        }
                        
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            }
        }

    }
}
