package lox;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private Environment env = new Environment();
    private boolean isBreakable = false;
    private boolean breakFlag = false;

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

    /**
     * Evaluates left-hand side and checks if it's an OR statement and true.
     * If so, then short-circuit and return true.
     * If it was false and an AND, shirt-circuit false.
     * Otherwise, evaluate the right-hand side
     * @param expr Logical AND or OR expression
     * @return truth value
     */
    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.op.type == TokenType.OR) {
            if(isTruthLike(expr.left)) return left;
        } else if(!isTruthLike(expr.left)) return left;

        return evaluate(expr.right);
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

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        env.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return env.get(expr.name);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    /**
     * Note that the statements live inside the block.
     * Therefore, this code executes a block within a lexical scope
     * @param stmt block statement
     * @return null
     */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(env));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if(isTruthLike(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        }
        else if(stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }

        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.print(stringify(value) + System.lineSeparator());
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        env.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        isBreakable = true;
        while(isTruthLike(evaluate(stmt.condition)) && !breakFlag) {
            execute(stmt.body);
        }

        isBreakable = false;
        breakFlag = false;
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        if(isBreakable) {
             breakFlag = true;
        } else {
            throw (new RuntimeError(stmt.self, "Break statement not enclosed"));
        }
        return null;
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

    protected void interpret(List<Stmt> stmts) {
        try {
            for (Stmt stmt : stmts) {
                execute(stmt);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    /**
     * Execute code in a given environment.
     * If a stmt is another block, mutual recursion occurs, and this code is called to
     * deal with the inner loop.
     * Exits the enviorment in the end.
     * @param stmts stmts to be executed
     * @param env enviorment that statments are located in
     */
    private void executeBlock(List<Stmt> stmts, Environment env) {
        Environment prev = this.env;

        try {
            this.env = env;

            for (Stmt stmt : stmts) {
                if (breakFlag) break;
                execute(stmt);
            }
        } finally {
            this.env = prev;
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
