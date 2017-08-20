package inter;
import lexer.*;
import symbol.*;

public class Op extends Expr {
    public Op(Token tok, Type t) {
        super(tok, t);
    }

    public Expr reduce() {
        Expr x = gen();
        Temp tmp = new Temp(type);
        emit(tmp.toString() + " = " + x.toString());
        return tmp;
    }
}
