package inter;
import lexer.*;
import symbol.*;

public class Not extends Logical {
    public Not(Token tk, Expr x) {
        super(tk , x, x);
    }

    public void jumping(int t, int f) {
        expr2.jumping(f, t);
    }

    public String toString() {
        return operator.toString() + " " + expr2.toString();
    }
}
