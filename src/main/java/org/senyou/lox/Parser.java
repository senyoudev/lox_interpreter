package org.senyou.lox;

import java.util.List;


/**
 * A parser for the Lox programming language.
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    /** A ParseError class. */
    private static class ParseError extends RuntimeException {}

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parse the tokens into an expression.
     * @return the expression.
     */
    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    /**
     * Parse the tokens into an expression.
     * @return
     */
    private Expr expression() {
        return equality();
    }

    /**
     * Parse the tokens into an equality expression.
     * @return
     */
    private Expr equality() {
        Expr expr = comparison();

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parse the tokens into a comparison expression.
     * @return
     */
    private Expr comparison() {
        Expr expr = term();
        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Parse the tokens into a term expression.
     * @return
     */
    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Parse the tokens into a factor expression.
     * @return
     */
    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Parse the tokens into a unary expression.
     * @return
     */
    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    /**
     * Parse the tokens into a primary expression.
     * @return
     */
    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }



    /**
     * Below are the helper methods for the parser.
     */

    /**
     * Check if the current token is any of the given types.
     * @param types
     * @return true if the current token is any of the given types, false otherwise.
     */
    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the current token is of the given type.
     * @param type
     * @return true if the current token is of the given type, false otherwise.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    /**
     * Advance to the next token.
     * @return the current token.
     */
    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    /**
     * Check if we have reached the end of the tokens.
     * @return true if we have reached the end of the tokens, false otherwise.
     */
    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    /**
     * Get the current token.
     * @return the current token.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Get the previous token.
     * @return the previous token.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Consume the current token if it is of the given type, otherwise throw an error.
     * @param type
     * @param message
     * @return the current token.
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }


    /**
     * Report an error at the given token.
     * @param token
     * @param message
     * @return a ParseError.
     */
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    /**
     * Synchronize the parser after an error.
     */
    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type() == TokenType.SEMICOLON) return;
            switch (peek().type()) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }
            advance();
        }
    }


}
