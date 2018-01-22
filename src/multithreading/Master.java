package multithreading;

import multithreading.generator.FileStream;
import multithreading.generator.RandomStringStream;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class Master {
    private Slave[] slaves;
    private Thread[] pool;
    private ConcurrentHashMap<String, Object> storage;
    private volatile boolean workFlag = true;

    public Master(String[] filenames) {
        int threads = filenames.length;

        slaves = new Slave[threads];
        pool = new Thread[threads];
        storage = new ConcurrentHashMap<>();

        for (int i = 0; i < threads; ++i) {
            try {
                slaves[i] = new Slave(this, new FileStream(filenames[i]), storage);
            } catch (FileNotFoundException e) {
                for (int j = 0; j < i; ++j) {
                    slaves[i].stop();
                }
                throw new RuntimeException("Can't create master", e);
            }
        }
    }

    public Master(int threads) {
        slaves = new Slave[threads];
        pool = new Thread[threads];
        storage = new ConcurrentHashMap<>();

        for (int i = 0; i < threads; ++i) {
            slaves[i] = new Slave(this, new RandomStringStream(), storage);
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < pool.length; ++i) {
            pool[i] = new Thread(slaves[i]);
            pool[i].start();
        }
        for (int i = 0; i < pool.length; ++i) {
            try {
                pool[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
        new Master(args).run();
    }
}
