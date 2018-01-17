package multithreading;

import multithreading.generator.RandomStringStream;
import multithreading.generator.TokenizerStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

    private static String filename = "input.txt";

    public Master(int threads) {
        slaves = new Slave[threads];
        pool = new Thread[threads];
        storage = new ConcurrentHashMap<>();

        Scanner sc = null;
        try {
            sc = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.err.println("File " + filename + " not found.");
        }

        for (int i = 0; i < threads; ++i) {
//            slaves[i] = new Slave(this, new RandomStringStream(), storage);
            slaves[i] = new Slave(this, new TokenizerStream(sc.nextLine()), storage);
        }

        if (sc != null) {
            sc.close();
        }

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threads; ++i) {
            pool[i] = new Thread(slaves[i]);
            pool[i].start();
        }
        for (int i = 0; i < threads; ++i) {
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

    public static void main(String[] args) throws FileNotFoundException {
        int n = 4;
//        RandomStringStream rs = new RandomStringStream();
//        PrintWriter pw = new PrintWriter(filename);
//        for (int j = 0; j < n; j++) {
//            for (int i = 0; i < 1000000; i++) {
//                pw.print(rs.next() + " ");
//            }
//            pw.println(rs.next());
//        }
//        pw.close();

        new Master(n);
    }
}
