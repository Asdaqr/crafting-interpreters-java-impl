package lox;

public class RuntimeError extends RuntimeException {
    final protected Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}

