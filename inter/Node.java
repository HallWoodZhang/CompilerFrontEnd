package inter;
import lexer.*;


public class Node {
    int lexline = 0;
    Node() {
        lexline = Lexer.line;
    }

    void error(String msg) {
        throw new Error("near line " + lexline + ": " + msg);
    }

    static int labels = 0;
    public int newLabel() {
        return ++labels;
    }

    public void emitLabel(int i) {
        System.out.print("L" + i + ":");
    }

    public void emit(String str) {
        System.out.println("\t" + str);
    }
}
