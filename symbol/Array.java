package symbol;
import lexer.*;

public class Array extends Type {
    public Type of;
    public int size = 1;
    public Array(int sz, Type t) {
        super("[]", Tag.INDEX, sz*t.width);
        size = sz;
        of = t;
    }

    public String toString() {
        return "[" + size + "]" + of.toString();
    }
}
