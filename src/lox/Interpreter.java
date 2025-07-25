package lox;

public class Interpreter implements Expr.Visitor<Object> {


    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.op.type) {
            case MINUS -> {
                checkNumberOperands(expr.op, left, right);
                return (double)left - (double)right;
            }
            case SLASH -> {
                checkNumberOperands(expr.op, left, right);
                return (double)left / (double)right;
            }
            case STAR -> {
                checkNumberOperands(expr.op, left, right);
                return (double)left * (double)right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(expr.op, "Oprands must be two integers or strings");
            }
            case LESS -> {
                checkNumberOperands(expr.op, left, right);
                return (double)left < (double)right;
            }
            case GREATER -> {
                checkNumberOperands(expr.op, left, right);
                return (double)left > (double)right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.op, left, right);
                return (double)left <= (double)right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.op, left, right);
                return (double)left >= (double)right;
            }
            case EQUAL_EQUAL -> {
                return isEqual(left,right);
            }
            case BANG_EQUAL -> {
                return !isEqual(left,right);
            }
        }
        //Unreachable
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.op.type) {
            case MINUS:
                checkNumberOperand(expr.op, right);
                return -(double)right;
            case BANG:
                return !isTruthLike(right);
        }

        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTruthLike(Object object) {
        if (object==null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b){
        if(a==null && b==null) return true;
        if (a==null) return false;
        return a.equals(b);
    }

    private void checkNumberOperand(Token op, Object operand) {
        if (operand instanceof Double) return;
        throw(new RuntimeError(op, "Operand must be a number."));

    }

    private void checkNumberOperands(Token op, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw(new RuntimeError(op, "Operands must be a number."));
    }

    void interpret(Expr expr) {
        try {
            Object value = evaluate(expr);
            System.out.println(stringify(value));
        }
        catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String s = object.toString();
            if (s.endsWith(".0")) s = s.substring(0, s.length()-2);
            return s;
        }

        return object.toString();
    }
}
