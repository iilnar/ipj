package multithreading.exceptions;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class WrongCharException extends CommonException {
    private char cause;

    public WrongCharException(char c) {
        cause = c;
    }

    @Override
    public String getMessage() {
        return "Unsupported char: " + cause;
    }
}
