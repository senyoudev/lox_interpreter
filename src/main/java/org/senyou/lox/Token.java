package org.senyou.lox;

/**
 * A token in the Lox programming language.
 * In this implementation, we only specify the line but in a more sophisticated implementation,
 * we would also specify the column and the length too
 */
public record Token(TokenType type, String lexeme, Object literal, int line) {
    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
