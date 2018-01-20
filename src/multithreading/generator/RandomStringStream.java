package multithreading.generator;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class RandomStringStream implements StringStream {
    private final Random rnd = new Random();
    private static final String numbers = "0123456789";
    private static final String letters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String alphabet = numbers + letters;

    @Override
    public String next() {
        int length = rnd.nextInt(10) + 6;
        return rnd.ints(length, 0, alphabet.length()).mapToObj(alphabet::charAt).map(Object::toString).collect(Collectors.joining());
    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
