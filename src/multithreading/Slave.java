package multithreading;

import multithreading.exceptions.DuplicateException;
import multithreading.exceptions.WrongCharException;
import multithreading.generator.StringStream;
import multithreading.triggers.Trigger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class Slave implements Runnable {
    private final StringStream ss;
    private final ConcurrentHashMap<String, Object> storage;
    private final Master master;
    private final Trigger trigger;

    private static Object setMaker = new Object();

    public Slave(Master master, StringStream ss, ConcurrentHashMap<String, Object> storage, Trigger trigger) {
        this.master = master;
        this.ss = ss;
        this.storage = storage;
        this.trigger = trigger;
    }

    @Override
    public void run() {
        while (ss.hasNext() && master.shouldWork()) {
            String token = null;
            try {
                trigger.put(storage, ss.next());
            } catch (DuplicateException | WrongCharException e) {
                System.err.println(e.getMessage());
                master.stop();
            }
        }
        stop();
    }

    public void stop() {
        ss.close();
    }
}
