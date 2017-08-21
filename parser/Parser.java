package parser;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import lexer.*;
import symbol.*;
import inter.*;

import java.io.IOException;
import java.util.Calendar;

public class Parser {

    private Lexer lex;
    private Token look;
    Env top = null;
    int used = 0;

    public Parser(Lexer l) throws IOException {
        lex = l;
        move();
    }

    void move() throws IOException {
        look = lex.scan();
    }

    void error(String msg) {
        throw new Error("near line " + lex.line + ": " + msg);
    }

    void match(int tag) throws IOException {
        if(look.tag == tag) move();
        else error("syntax error");
    }

    public void program() throws IOException { // program -> block
        Stmt s = block();
        int begin = s.newLabel();
        int after = s.newLabel();
        s.emitLabel(begin);
        s.gen(begin, after);
        s.emitLabel(after);
    }

    Stmt block() throws IOException { // block -> { decls stmts }
        match('{');
        Env saved = top;
        top = new Env(top);
        decls();
        Stmt s = stmts();
        match('}');
        top = saved;
        return s;
    }

    void decls() throws IOException {
        while( look.tag == Tag.BASIC ) { // D -> typr ID ;
            Type p = type();
            Token tok = look;
            match(Tag.ID);
            match(';');
            Id id = new Id((Word) tok, p, used);
            top.put(tok, id);
            used = used + p.width;
        }
    }

    Type type() throws IOException {
        Type t = (Type) look;
        match(Tag.BASIC);
        if( look.tag != '[') return t; // T -> basic
        else return dims(t);
    }

    Type dims(Type t) throws IOException {
        match('[');
        Token tok = look;
        match(Tag.NUM);
        match(']');
        if(look.tag == '[') t = dims(t);
        return new Array(((Num)tok).value, t);
    }

    Stmt stmts() throws IOException {
        if (look.tag == '}')  return Stmt.Null;
        else return new Seq(stmt(), stmts());
    }

    Stmt stmt() throws IOException {
        Expr x;
        Stmt s, s1, s2;
        Stmt saved;
        switch (look.tag) {
            case ';':
                move();
                return Stmt.Null;
            case Tag.IF:
                match(Tag.IF);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                if (look.tag != Tag.ELSE) return new If(x, s1);
                match(Tag.ELSE);
                s2 = stmt();
                return new Else(x, s1, s2);
            case Tag.WHILE:
                While node = new While();
                saved = Stmt.Enclosing;
                Stmt.Enclosing = node;
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                node.init(x, s1);
                Stmt.Enclosing = saved;
                return node;
            case Tag.DO:
                Do donode = new Do();
                saved = Stmt.Enclosing;
                Stmt.Enclosing = donode;
                match(Tag.DO);
                s1 = stmt();
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                match(';');
                donode.init(x, s1);
                Stmt.Enclosing = saved;
                return donode;
            case Tag.BREAK:
                match(Tag.BREAK);
                match(';');
                return new Break();
            case '{':
                return block();
            default:
                return assign();
        }
    }

    Stmt assign() throws IOException {
        Stmt stmt;
        Token t = look;
        match(Tag.ID);
        Id  id = top.get(t);
        if(id == null) error(t.toString() + " undeclared");
        if(look.tag == '=') { // S -> id = E
            move();
            stmt = new Set(id, bool());
        }

        else {
            Access x = offset(id);
            match('=');
            stmt = new SetElem(x, bool());
        }

        match(';');
        return stmt;
    }

    Expr bool() throws IOException {
        Expr x = join();
        while( look.tag == Tag.OR) {
            Token tk = look;
            move();
            x= new Or(tk, x, join());
        }
        return x;
    }

    Expr join() throws IOException {
        Expr x = equality();
        while( look.tag == Tag.AND ) {
            Token tk = look;
            move();
            x = new And(tk, x, equality());
        }
        return x;
    }

    Expr equality() throws IOException {
        Expr x = rel();
        while(look.tag == Tag.EQ || look.tag == Tag.NE) {
            Token tk = look;
            move();
            x = new Rel(tk, x, rel());
        }

        return x;
    }

    Expr rel() throws IOException {
        Expr x = expr();
        switch(look.tag) {
            case '<':
            case Tag.LE:
            case '>':
            case Tag.GE:
                Token tk = look;
                move();
                x = new Rel(tk, x, rel());
            default:
                return x;
        }
    }

    Expr expr() throws IOException {
        Expr x = term();
        while(look.tag == '+' || look.tag == '-') {
            Token tk = look;
            move();
            x = new Arith(tk, x, term());
        }
        return x;
    }

    Expr term() throws IOException {
        Expr x = unary();
        while(look.tag == '*' || look.tag == '/') {
            Token tk = look;
            move();
            x = new Arith(tk, x, term());
        }
        return x;
    }

    Expr unary() throws IOException {
        if( look.tag == '-') {
            move();
            return new Unary(Word.minus, unary());
        }
        else if( look.tag == '!') {
            Token tk = look;
            move();
            return new Not(tk, unary());
        }
        else return factor();
    }

    Expr factor() throws IOException {
        Expr x = null;
        switch(look.tag) {
            case '(':
                move();
                x = bool();
                match(')');
                return x;
            case Tag.NUM:
                x = new Constant(look, Type.Int);
                move();
                return x;
            case Tag.REAL:
                x = new Constant(look, Type.Float);
                move();
                return x;
            case Tag.TRUE:
                x = Constant.True;
                move();
                return x;
            case Tag.FALSE:
                x = Constant.False;
                move();
                return x;
            case Tag.ID:
                String str = look.toString();
                Id id = top.get(look);
                if(id == null) error(look.toString() + " undeclared");
                move();
                if(look.tag != '[') return id;
                else return offset(id);
        }
        return null;
    }

    Access offset(Id a) throws IOException {
        Expr i, w, t1, t2, loc;
        Type type = a.type;
        match('[');
        i = bool();
        match(']');
        type = ((Array)type).of;
        w = new Constant(type.width);
        t1 = new Arith(new Token('*'), i, w);
        loc = t1;
        while(look.tag == '[') {
            match('[');
            i = bool();
            match(']');
            type = ((Array)type).of;
            w = new Constant(type.width);
            t1 = new Arith(new Token('*'), i, w);
            t2 = new Arith(new Token('+'), loc, t1);
            loc = t2;
        }

        return new Access(a, loc, type);

    }

}
