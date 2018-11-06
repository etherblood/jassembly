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
    NEGATION("-"),
    COMPLEMENT("~"),
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
    
    public String group() {
        return name().replaceAll("_", "");
    }
}
