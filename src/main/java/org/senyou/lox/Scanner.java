package org.senyou.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A scanner for the Lox programming language.
 * It scans the source code and generates a list of tokens.
 */
public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String,TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    /**
     * Scan the source code and generate a list of tokens.
     * @return
     */
    List<Token> scanTokens() {
        while(!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    /**
     * Check if we are at the end of the source code.
     * @return true if we are at the end of the source code, false otherwise.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Scan the next token.
     */
    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case('!') : addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case('=') : addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case('<') : addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case('>') : addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case('/') :
                if(match('/')) {
                    // A comment goes until the end of the line.
                    while(peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case(' ') :
            case('\r') :
            case('\t') :
                // Ignore whitespace.
                break;
            case('\n') :
                line++;
                break;
            case('"') :
                string();
                break;
            default :
                if(isDigit(c)) {
                    number();
                } else if(isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    /**
     * Advance to the next character in the source code.
     * @return the character at the current position.
     */
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    /**
     * Add a token to the list of tokens.
     * @param type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Add a token to the list of tokens.
     * @param type
     * @param literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    /**
     * Check if the current character matches the expected character.
     * If it does, we advance to the next character.
     * @param expected
     * @return true if the current character matches the expected character, false otherwise.
     */
    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    /**
     * Look at the next character without advancing.
     * @return the next character.
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Handle string literals.
     */
    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }

        // Unterminated string.
        if(isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance(); // Move one more character to consume the closing quote.

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    /**
     * Check if a character is a digit.
     */
    private boolean isDigit(char c) {
        return c>='0' && c <= '9';
    }

    /**
     *  Handle number literals.
     */
    private void number() {
        while(isDigit(peek())) advance();

        // When we encounter a '.' => The fractional part
        if(peek() == '.' && isDigit(peekNext())) {
            advance();
            // The literal remaining part
            while(isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start,current)));
    }

    /**
     * Look at the next character without advancing.
     * @return the next character.
     */
    private char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Check if a character is an alpha
     * It means that it is a letter or an underscore.
     * @param c
     * @return true if the character is an alpha, false otherwise.
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    /**
     * Check if a character is an alpha-numeric
     * It means that it is a letter, a digit or an underscore.
     * @param c
     * @return true if the character is an alpha-numeric, false otherwise.
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Handle identifiers.
     */
    private void identifier() {
        while(isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

}
