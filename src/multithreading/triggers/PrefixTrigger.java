package multithreading.triggers;

import multithreading.exceptions.DuplicateException;

import java.util.concurrent.ConcurrentHashMap;

public class PrefixTrigger implements Trigger {
    private static final int PREFIX_LEN = 10;

    @Override
    public void put(ConcurrentHashMap<String, Object> map, String s) throws DuplicateException {
        s = s.substring(0, Math.min(s.length(), PREFIX_LEN));
        if (map.putIfAbsent(s, holder) != null) {
            throw new DuplicateException(s);
        }
    }
}
