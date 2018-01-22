package multithreading.generator;

import multithreading.exceptions.WrongCharException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileStream implements StringStream {
    private Scanner sc;
    private TokenizerStream ts;

    public FileStream(String filename) throws FileNotFoundException {
        this.sc = new Scanner(new File(filename));
        this.ts = new TokenizerStream();
    }

    @Override
    public String next() throws WrongCharException {
        while (!ts.hasNext()) {
            ts = new TokenizerStream(sc.next());
        }
        return ts.next();
    }

    @Override
    public boolean hasNext() {
        while (!ts.hasNext()) {
            if (!sc.hasNext()) {
                return false;
            }
            ts = new TokenizerStream(sc.next());
        }
        return ts.hasNext();
    }

    @Override
    public void close() {
        sc.close();
    }
}
