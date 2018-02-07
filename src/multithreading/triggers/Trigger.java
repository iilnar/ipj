package multithreading.triggers;

import multithreading.exceptions.DuplicateException;

import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface Trigger {
    Object holder = new Object();

    void put(ConcurrentHashMap<String, Object> map, String s) throws DuplicateException;
}
