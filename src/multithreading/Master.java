package multithreading;

import multithreading.generator.FileStream;
import multithreading.generator.RandomStringStream;

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class Master {
    private List<Slave> slaves;
    private List<Thread> pool;
    private ConcurrentHashMap<String, Object> storage;
    private volatile boolean workFlag = true;

    public Master(String[] filenames) {
        storage = new ConcurrentHashMap<>();

        try {
            slaves = Arrays.stream(filenames).map(filename -> {
                try {
                    return new Slave(Master.this, new FileStream(filename), storage);
                } catch (FileNotFoundException e) {
                    throw new UncheckedIOException(e);
                }
            }).collect(Collectors.toList());
        } catch (UncheckedIOException e) {
            slaves.stream().filter(Objects::nonNull).forEach(Slave::stop);
            throw new RuntimeException("Can't create master", e.getCause());
        }
    }

    public Master(int threads) {
        storage = new ConcurrentHashMap<>();
        slaves = IntStream.range(0, threads).mapToObj(x -> new Slave(this, new RandomStringStream(), storage)).collect(Collectors.toList());
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        pool = slaves.stream().map(Thread::new).collect(Collectors.toList());
        pool.forEach(Thread::start);

        pool.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long endTime = System.currentTimeMillis();

        System.out.println("Total words in storage: " + storage.size());
        System.out.println("Time: " + (endTime - startTime));
    }

    public boolean shouldWork() {
        return workFlag;
    }

    public synchronized void stop() {
        workFlag = false;
    }

    public static void main(String[] args) {
        new Master(4).run();
    }
}
