package lox;

import java.util.List;

public abstract class Expr {

    public interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }
    public static class Assign extends Expr {
        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
        final Token name;
        final Expr value;
    }
    public static class Binary extends Expr {
        public Binary(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        final Expr left;
        final Token op;
        final Expr right;
    }
    public static class Call extends Expr {
        public Call(Expr callee, Token paren, List<Expr> args) {
            this.callee = callee;
            this.paren = paren;
            this.args = args;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
        final Expr callee;
        final Token paren;
        final List<Expr> args;
    }
    public static class Grouping extends Expr {
        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
        final Expr expression;
    }
    public static class Literal extends Expr {
        public Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
        final Object value;
    }
    public static class Logical extends Expr {
        public Logical(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
        final Expr left;
        final Token op;
        final Expr right;
    }
    public static class Unary extends Expr {
        public Unary(Token op, Expr right) {
            this.op = op;
            this.right = right;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
        final Token op;
        final Expr right;
    }
    public static class Variable extends Expr {
        public Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept (Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
        final Token name;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
