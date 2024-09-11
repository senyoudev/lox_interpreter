package org.senyou.lox;

/**
 * The type of lexemes in the Lox programming language(Token types).
 */
public enum TokenType {
    /**
     * Single-character tokens.
     * Example : (, ), {, }, comma, dot, minus, plus, semicolon, slash, star
     */
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    /**
     * One or two character tokens.
     * Example : !, !=, =, ==, >, >=, <, <=
     */
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    /**
     * Literals.
     * Example : identifier, string, number
     */
    IDENTIFIER, STRING, NUMBER,

    /**
     * Keywords.
     * Example : and, class, else, false, fun, for, if, nil, or, print, return, super, this, true, var, while
     */
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    /**
     * End of file.
     */
    EOF

}
