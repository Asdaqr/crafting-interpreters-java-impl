package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lox is a language specified in "Crafting Interpreters" by Robert Nystrom.
 * This is my implementation of it.
 */
public class Lox {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);//UNIX sysexists code (EX_USAGE)
        } else if (args.length == 1) {
            runFile(args[0]); //TODO: Implement runFile
        } else {
            runPrompt(); //TODO: Implement runPrompt
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));//TODO: implement run
    }

    private static void runPrompt() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        for(;;) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null) break;
            run(line);
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = new ArrayList<>();
//        List<Token> tokens = scanner.scanTokens();


        for (Token token: tokens) {
            System.out.println(token); //TODO: Replace when interpreter gets implemented

        }
    }


}

