package symbol;

import lexer.*;

import java.awt.geom.FlatteningPathIterator;

public class Type extends Word {
    public int width = 0; // size of token
    public Type(String str, int tag, int wid) {
        super(str, tag);
        width = wid;
    }

    public static final Type
        Int = new Type("int", Tag.BASIC, 4),
        Float = new Type("float", Tag.BASIC, 8),
        Char = new Type("char", Tag.BASIC, 1),
        Bool = new Type("bool", Tag.BASIC, 1);

    public static boolean numeric(Type peek) {
        if (peek == Type.Char || peek == Type.Int || peek == Type.Float) return true;
        else return false;
    }

    public static Type max(Type p1, Type p2) {
        if(!numeric(p1) || !numeric(p2)) return null;
        else if(p1 == Type.Float || p2 == Type.Float) return Type.Float;
        else if(p1 == Type.Int || p2 == Type.Int) return Type.Int;
        else return Type.Char;
    }
}


