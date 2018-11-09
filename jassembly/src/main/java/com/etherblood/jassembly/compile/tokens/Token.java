package com.etherblood.jassembly.compile.tokens;

/**
 *
 * @author Philipp
 */
public class Token {

    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", type.name(), value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + type.hashCode();
        hash = 23 * hash + value.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Token)) {
            return false;
        }
        Token other = (Token) obj;
        return type == other.type && value.equals(other.value);
    }
}
