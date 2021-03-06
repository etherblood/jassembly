package com.etherblood.jassembly.compile.jassembly.language.parsing.tokens;

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
    COMMA(","),
    LITERAL_UINT("[0-9]+"),
    LITERAL_SINT("[+-][0-9]+"),
    LITERAL_BOOL("true|false"),
    OP_COMPLEMENT("~"),
    OP_MINUS("-"),
    OP_PLUS("\\+"),
    OP_MULTIPLY("\\*"),
    OP_DIVIDE("/"),
    OP_REMAINDER("%"),
    OP_RSHIFT(">>"),
    OP_LSHIFT("<<"),
    OP_LAZY_AND("&&"),
    OP_LAZY_OR("\\|\\|"),
    OP_AND("&"),
    OP_OR("\\|"),
    OP_XOR("\\^"),
    OP_EQUAL("=="),
    OP_LESSOREQUAL("<="),
    OP_GREATEROREQUAL(">="),
    OP_NOTEQUAL("!="),
    OP_LESS("<"),
    OP_GREATER(">"),
    OP_ASSIGN("="),
    OP_NOT("!"),
    KEYWORD_RETURN("return"),
    KEYWORD_TYPE("uint|sint|bool"),
    KEYWORD_IF("if"),
    KEYWORD_ELSE("else"),
    KEYWORD_WHILE("while"),
    KEYWORD_BREAK("break"),
    KEYWORD_CONTINUE("continue"),
    IDENTIFIER("[a-zA-Z]\\w*");

    private final String pattern;

    private TokenType(String pattern) {
        this.pattern = pattern;
    }

    public String pattern() {
        return pattern;
    }
}
