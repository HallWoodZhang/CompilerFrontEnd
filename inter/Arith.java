package inter;
import lexer.*;
import symbol.*;

public class Arith extends Op {
    public Expr expr1, expr2;
    public Arith(Token tok, Expr lhs, Expr rhs) {
        super(tok, null);
        expr1 = lhs;
        expr2 = rhs;
        type = Type.max(expr1.type, expr2.type);
        if(type == null) error("Type Error");
    }

    public Expr gen() {
        return new Arith(operator, expr1.reduce(), expr2.reduce());
    }

    public String toString() {
        return expr1.toString() + " " + operator.toString() + " " + expr2.toString();
    }
}
