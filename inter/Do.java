package inter;
import lexer.*;
import symbol.*;

public class Do extends Stmt {
    Expr expr;
    Stmt stmt;
    public Do() {
        expr = null;
        stmt = null;
    }

    public void init(Expr x, Stmt s) {
        expr = x;
        stmt = s;
        if(expr.type != Type.Bool) error("boolean required in do");
    }

    public void gen(int b, int a) {
        after = a;
        int label = newLabel();
        stmt.gen(b, label);
        emitLabel(label);
        expr.jumping(b, 0);
    }
}
