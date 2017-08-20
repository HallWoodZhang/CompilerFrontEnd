package lexer;

public class Num extends Token {
    public final int value;
    public Num(int val) {
        super(Tag.NUM);
        value = val;
    }

    public String toString() {
        return "" + value;
    }
}
