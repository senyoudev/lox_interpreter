package org.senyou.lox;

/**
 * A token in the Lox programming language.
 */
public record Token(TokenType type, String lexeme, Object literal, int line) {
    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
