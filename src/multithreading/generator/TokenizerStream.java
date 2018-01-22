package multithreading.generator;

import multithreading.exceptions.WrongCharException;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class TokenizerStream implements StringStream {
    private char[] str;
    private int pos = 0;

    private static char[] punctuation = ".,;:-?!()\"".toCharArray();
    private static String numbers = "0123456789";
    private static String letters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static char[] allowed = (numbers + letters).toCharArray();

    public TokenizerStream() {
        this("");
    }

    public TokenizerStream(String s) {
        this.str = s.toCharArray();
    }

    private boolean checkChar(char c) {
        for (char symbol : allowed) {
            if (c == symbol) {
                return true;
            }
        }
        return false;
    }

    private boolean isDelimiter(char c) {
        if (Character.isWhitespace(c)) {
            return true;
        }
        for (char symbol : punctuation) {
            if (c == symbol) {
                return true;
            }
        }
        return false;
    }

    private void skipDelimiters() {
        while (pos < str.length && isDelimiter(str[pos])) {
            pos++;
        }
    }

    @Override
    public String next() throws WrongCharException {
        skipDelimiters();
        int beg = pos;
        while (pos < str.length) {
            if (isDelimiter(str[pos])) {
                break;
            }
            if (!checkChar(str[pos])) {
                throw new WrongCharException(str[pos]);
            }
            pos++;
        }
        return new String(str, beg, pos - beg);
    }

    @Override
    public boolean hasNext() {
        skipDelimiters();
        return pos != str.length;
    }

    @Override
    public void close() {
    }
}
