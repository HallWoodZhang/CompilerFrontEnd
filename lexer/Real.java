package lexer;

public class Real extends Token{
    public final float value;
    public Real(float val) {
        super(Tag.REAL);
        value = val;
    }

    public String toString() {
        return "" + value;
    }
}
