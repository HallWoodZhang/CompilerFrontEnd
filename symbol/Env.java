package symbol;
import java.util.*;
import lexer.*;
import inter.*;


public class Env {
    private Hashtable table;
    protected Env prev;
    public Env(Env pre) {
        table = new Hashtable();
        prev = pre;
    }

    public void put(Token word, Id id) {
        table.put(word, id);
    }

    public Id get(Token word) {
        for(Env env = this; env != null; env = env.prev) {
            Id found = (Id)(env.table.get(word));
            if (found != null) return found;
        }
        return null;
    }
}
