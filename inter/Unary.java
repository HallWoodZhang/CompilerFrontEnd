package inter;
import lexer.*;
import symbol.*;

public class Unary extends Op {
    public Expr expr;
    public Unary(Token tok, Expr x) {
        super(tok, null);
        expr = x;
        type = Type.max(Type.Int, expr.type);
        if(type == null) error("type error");
    }

    public Expr gen() {
        return new Unary(operator, expr.reduce());
    }

    public String toString() {
        return operator.toString() + " " + expr.toString();
    }
}
