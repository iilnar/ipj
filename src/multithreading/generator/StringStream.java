package multithreading.generator;

import multithreading.exceptions.WrongCharException;

/**
 * Created by Ilnar on 17/01/2018.
 */
public interface StringStream {
    String next() throws WrongCharException;
    boolean hasNext();
}
