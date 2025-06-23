package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Lox is a language specified in "Crafting Interpreters" by Robert Nystrom.
 * This is my implementation of it.
 */
public class Lox {
    static boolean hadError;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);//UNIX sysexists code (EX_USAGE)
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65); //EX_DATAERR
    }

    private static void runPrompt() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        for(;;) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    /**
     * Runs on string loaded in buffer.
     * Scans for tokens, and is the base of interpreter. Not yet implemented.
     * @param source String or piece of code to be interpreted.
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();


        for (Token token: tokens) {
            System.out.println(token); //TODO: Replace when interpreter gets implemented

        }
    }

    /**
     * Error "interface" for reporting an error.
     * Wrapper around report with shorter syntax. <br>
     * Where is ignored as there may not be a specific char
     * @param line Line of error.
     * @param message Message to print.
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Prints and formats an error message to stderr.
     * @param line Line where error happened.
     * @param where Char where error occurred.
     * @param message Error message.
     */
    private static void report(int line, String where, String message) {
        System.err.println("[" + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}

