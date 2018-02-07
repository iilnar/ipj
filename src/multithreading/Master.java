package multithreading;

import multithreading.generator.FileStream;
import multithreading.generator.RandomStringStream;
import multithreading.triggers.Trigger;
import multithreading.triggers.TriggerLoader;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class Master {
    private Slave[] slaves;
    private Thread[] pool;
    private ConcurrentHashMap<String, Object> storage;
    private volatile boolean workFlag = true;

    public Master(String[] filenames, Trigger trigger) {
        int threads = filenames.length;

        slaves = new Slave[threads];
        pool = new Thread[threads];
        storage = new ConcurrentHashMap<>();

        for (int i = 0; i < threads; ++i) {
            try {
                slaves[i] = new Slave(this, new FileStream(filenames[i]), storage, trigger);
            } catch (FileNotFoundException e) {
                for (int j = 0; j < i; ++j) {
                    slaves[i].stop();
                }
                throw new RuntimeException("Can't create master", e);
            }
        }
    }

    public Master(int threads, Trigger trigger) {
        slaves = new Slave[threads];
        pool = new Thread[threads];
        storage = new ConcurrentHashMap<>();

        for (int i = 0; i < threads; ++i) {
            slaves[i] = new Slave(this, new RandomStringStream(), storage, trigger);
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < pool.length; ++i) {
            pool[i] = new Thread(slaves[i]);
            pool[i].start();
        }
        for (Thread aPool : pool) {
            try {
                aPool.join();
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
        TriggerLoader loader = new TriggerLoader();
        Trigger trigger;
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Enter trigger name or quit");
            String triggerName = in.next();
            if ("quit".equals(triggerName)) {
                break;
            }

            try {
                trigger = loader.loadTrigger(triggerName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Can't run process", e);
            }
            new Master(3, trigger).run();
        }
    }
}
