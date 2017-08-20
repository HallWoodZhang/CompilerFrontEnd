package inter;
import lexer.*;
import symbol.*;

public class While extends Stmt {
    Expr expr;
    Stmt stmt;
    public While() {
        expr = null;
        stmt = null;
    }

    public void init(Expr x, Stmt s) {
        expr = x;
        stmt = s;
        if(expr.type != Type.Bool) error("type error! Boolean required in While");
    }

    public void gen(int b, int a) {
        after = a;
        expr.jumping(0, a);
        int label = newLabel();
        emitLabel(label);
        stmt.gen(label, b);
        emit("goto L" + b);
    }
}
