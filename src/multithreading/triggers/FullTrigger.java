package multithreading.triggers;

import multithreading.exceptions.DuplicateException;

import java.util.concurrent.ConcurrentHashMap;

public class FullTrigger implements Trigger {
    @Override
    public void put(ConcurrentHashMap<String, Object> map, String s) throws DuplicateException {
        if (map.putIfAbsent(s, holder) != null) {
            throw new DuplicateException(s);
        }
    }
}
