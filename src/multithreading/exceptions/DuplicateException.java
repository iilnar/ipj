package multithreading.exceptions;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class DuplicateException extends CommonException {
    private String word;

    public DuplicateException(String duplicate) {
        this.word = duplicate;
    }

    @Override
    public String getMessage() {
        return "Duplicate word: " + word;
    }
}
