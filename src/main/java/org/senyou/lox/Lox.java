package org.senyou.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * The main class for the Lox programming language.
 */
public class Lox {
    static boolean hadError = false;

    /**
     * Generate an error message.
     * @param line
     * @param where
     * @param message
     */
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    /**
     * Report an error at a given line.
     * @param line
     * @param message
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Read and execute the source code from the given path.
     * @param path
     * @throws IOException
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
    }

    /**
     * Run the source code in an interactive prompt(Read-Eval-Print Loop or REPL).
     * @throws IOException
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while(true) {
            System.out.print("> ");
            String line = reader.readLine();
            if(line == null) break;
            run(line);
            // If the user made an error, we don't want to kill the entire REPL session.
            hadError = false;
        }
    }

    /**
     * Scan the source code and transform it into tokens.
     * @param source
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for(Token token : tokens) {
            System.out.println(token);
        }
    }

    public static void main(String[] args) {
        if(args.length > 1) {
            System.out.println("Usage: jlox [script]");
            // https://man.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE&format=html#:~:text=EX_USAGE%20(64)%09%20%20%20The%20command%20was%20used%20incorrectly%2C%20e.g.%2C%20with%20the%0A%09%09%09%20%20%20wrong%20number%20of%20arguments%2C%20a%20bad%20flag%2C%20a%20bad%20syntax%0A%09%09%09%20%20%20in%20a%20parameter%2C%20or%20whatever.
            System.exit(64);
        } else if(args.length == 1) {
            //runFile(args[0]);
        } else {
            //runPrompt();
        }
    }
}
