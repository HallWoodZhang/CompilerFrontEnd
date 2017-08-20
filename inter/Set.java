package inter;
import lexer.*;
import symbol.*;

public class Set extends Stmt {
    public Id id;
    public Expr expr;
    public Set(Id i, Expr x) {
        id = i;
        expr = x;
        if (check(id.type, expr.type) == null) error("type error");
    }

    public Type check(Type t1, Type t2) {
        if(Type.numeric(t1) && Type.numeric(t2)) return t2;
        else if(t1 == Type.Bool && t2 == Type.Bool) return t2;
        else return null;
    }

    public void gen(int b, int a) {
        emit(id.toString() + " = " + expr.gen().toString());
    }
}
