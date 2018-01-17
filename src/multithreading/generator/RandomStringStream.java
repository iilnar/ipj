package multithreading.generator;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class RandomStringStream implements StringStream {
    private Random rnd = new Random();
    private String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ0123456789.,;:-?!()\"";

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
