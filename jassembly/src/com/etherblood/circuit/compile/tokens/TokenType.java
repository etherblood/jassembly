package com.etherblood.circuit.compile.tokens;

/**
 *
 * @author Philipp
 */
public enum TokenType {
    WHITESPACE("[ \t\f\r\n]+"),
    OPEN_PAREN("\\("),
    CLOSE_PAREN("\\)"),
    OPEN_BRACE("\\{"),
    CLOSE_BRACE("\\}"),
    SEMICOLON(";"),
    OP_COMPLEMENT("~"),
    OP_MINUS("-"),
    OP_PLUS("\\+"),
    OP_MULTIPLY("\\*"),
    OP_DIVIDE("/"),
    OP_REMAINDER("%"),
    OP_ALL("&&"),
    OP_ANY("\\|\\|"),
    OP_AND("&"),
    OP_OR("\\|"),
    OP_XOR("\\^"),
    KEYWORD_RETURN("return"),
    KEYWORD_INT("int"),
    LITERAL_INT("[0-9]+"),
    IDENTIFIER("[a-zA-Z]\\w*");
    
    private final String pattern;

    private TokenType(String pattern) {
        this.pattern = pattern;
    }

    public String pattern() {
        return pattern;
    }
}
