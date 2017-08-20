package inter;
import lexer.*;
import symbol.*;

public class Rel extends Logical {
    public Rel(Token tk, Expr x1, Expr x2) {
        super(tk, x1, x2);
    }

    public Type check(Type lhs, Type rhs) {
        if (lhs instanceof Array || rhs instanceof Array) return null;
        else if (lhs == rhs) return Type.Bool;
        else return null;
    }

    public void jumping(int t, int f) {
        Expr a = expr1.reduce();
        Expr b = expr2.reduce();

        String test = a.toString() + " " + operator.toString() + " " + b.toString();
        emitjumps(test, t, f);
    }
}
