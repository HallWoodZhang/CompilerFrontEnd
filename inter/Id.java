package inter;
import lexer.*;
import symbol.*;

public class Id extends Expr {
    public int offset;
    public Id(Word id, Type t, int ofst) {
        super(id, t);
        offset = ofst;
    }
}
