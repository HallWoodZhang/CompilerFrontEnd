package inter;
import lexer.*;
import symbol.*;

public class SetElem extends Stmt {
    public Id array;
    public Expr index;
    public Expr expr;
    public SetElem(Access ax, Expr x) {
        array = ax.array;
        index = ax.index;
        expr = x;
        if(check(ax.type, expr.type) == null) error("type error");
    }

    public Type check(Type t1, Type t2)  {
        if (t1 instanceof Array || t2 instanceof Array) return null;
        else if (t1 == t2) return t2;
        else if (Type.numeric(t1) && Type.numeric(t2)) return t2;
        else return null;
    }


}
