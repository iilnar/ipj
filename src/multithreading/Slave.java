package multithreading;

import multithreading.exceptions.DuplicateException;
import multithreading.exceptions.WrongCharException;
import multithreading.generator.StringStream;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class Slave implements Runnable {
    private final StringStream ss;
    private final ConcurrentHashMap<String, Object> storage;
    private final Master master;

    private static Object setMaker = new Object();

    public Slave(Master master, StringStream ss, ConcurrentHashMap<String, Object> storage) {
        this.master = master;
        this.ss = ss;
        this.storage = storage;
    }

    @Override
    public void run() {
        while (ss.hasNext() && master.shouldWork()) {
            String token = null;
            try {
                token = ss.next();
                if (storage.putIfAbsent(token, setMaker) != null) {
                    throw new DuplicateException(token);
                }
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
