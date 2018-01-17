package multithreading.generator;

import multithreading.exceptions.WrongCharException;

/**
 * Created by Ilnar on 17/01/2018.
 */
public class TokenizerStream implements StringStream {
    private char[] str;
    private int pos = 0;
    private char[] alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ0123456789.,;:-?!()\"".toCharArray();

    public TokenizerStream(String s) {
        this.str = s.toCharArray();
    }

    private boolean checkChar(char c) {
        for (char symbol : alphabet) {
            if (c == symbol) {
                return true;
            }
        }
        return false;
    }

    private void skipWhitespace() {
        while (pos < str.length && Character.isWhitespace(str[pos])) {
            pos++;
        }
    }

    @Override
    public String next() throws WrongCharException {
        skipWhitespace();
        int beg = pos;
        while (pos < str.length) {
            if (Character.isWhitespace(str[pos])) {
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
        return pos != str.length;
    }
}
