package lox;

import java.util.List;

public abstract class Expr {

    public interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
    }
    public static class Binary extends Expr {
        public Binary(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }
        final Expr left;
        final Token op;
        final Expr right;
    }
    public static class Grouping extends Expr {
        public Grouping(Expr expression) {
            this.expression = expression;
        }
        final Expr expression;
    }
    public static class Literal extends Expr {
        public Literal(Object value) {
            this.value = value;
        }
        final Object value;
    }
    public static class Unary extends Expr {
        public Unary(Token op, Expr right) {
            this.op = op;
            this.right = right;
        }
        final Token op;
        final Expr right;
    }
}
