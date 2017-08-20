package lexer;

import java.io.IOException;
import java.util.Hashtable;
import symbol.*;

public class Lexer {
    public static int line = 1;
    char peek = ' ';
    Hashtable words = new Hashtable();

    void reserve(Word w) {
        words.put(w.lexeme, w);
    }

    public Lexer() {
        reserve(new Word("if", Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("break", Tag.BREAK));
        reserve(Word.True);
        reserve(Word.False);
        reserve(Type.Int);
        reserve(Type.Char);
        reserve(Type.Bool);
        reserve(Type.Float);
    }

    void readCh() throws IOException {
        peek = (char)System.in.read();
    }

    boolean readCh(char ch) throws IOException {
        readCh();
        if (peek != ch) return false;
        peek = ' ';
        return true;
    }

    public Token scan() throws IOException {
        for(;; readCh()) {
            if(peek == ' ' || peek == '\t') continue;
            else if(peek == '\n') line += 1;
            else break;
        }
        switch(peek) {
            case '&':
                if(readCh('&')) return Word.and;
                else return new Token('&');
            case '|':
                if(readCh('|')) return Word.or;
                else return new Token('|');
            case '=':
                if(readCh('=')) return Word.eq;
                else return new Token('=');
            case '!':
                if(readCh('=')) return Word.ne;
                else return new Token('!');
            case '<':
                if(readCh('=')) return Word.le;
                else return new Token('<');
            case '>':
                if(readCh('=')) return Word.ge;
                else return new Token('>');
        }

        if(Character.isDigit(peek)) {
            int val = 0;
            do {
                val = 10*val + Character.digit(peek, 10);
                readCh();
            } while(Character.isDigit(peek));
            if(peek != '.') return new Num(val);
            float real = val, div = 10;
            for(;;) {
                readCh();
                if(!Character.isDigit(peek)) break;
                real += Character.digit(peek, 10)/div;
                div *= 10;
            }
            return new Real(real);
        }

        if(Character.isLetter(peek)) {
            StringBuffer buff = new StringBuffer();
            do {
                buff.append(peek);
                readCh();
            } while(Character.isLetterOrDigit(peek));
            String str = buff.toString();
            Word w = (Word)words.get(str);
            if(w != null) return w;
            w = new Word(str, Tag.ID);
            words.put(str, w);
            return w;
        }

        Token token = new Token(peek);
        peek = ' ';
        return token;
    }
}
