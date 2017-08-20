package inter;
import lexer.*;
import symbol.*;

public class Logical extends Expr {
    public Expr expr1, expr2;
    public Logical(Token tok, Expr x1, Expr x2) {
        super(tok, null);
        expr1 = x1;
        expr2 = x2;
        type = check(expr1.type, expr2.type);
        if(type == null) emit("type error");
    }

    public Type check(Type t1, Type t2) {
        if(t1 == Type.Bool && t2 == Type.Bool) return Type.Bool;
        else return null;
    }

    public Expr gen() {
        int flag = newLabel();
        int a = newLabel();
        Temp tmp = new Temp(type);
        this.jumping(0, flag);
        emit(tmp.toString() + " = true");
        emit("goto L" + a);
        emitLabel(flag);
        emit(tmp.toString() + " = false");
        emitLabel(a);
        return tmp;
    }
}
