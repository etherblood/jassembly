package com.etherblood.jassembly.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.etherblood.jassembly.compile.tokens.Token;
import com.etherblood.jassembly.compile.tokens.TokenType;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 *
 * @author Philipp
 */
public class Lexer {

    private static final Pattern REGEX = Pattern.compile(Arrays.stream(TokenType.values()).map(x -> String.format("(%s)", x.pattern())).collect(Collectors.joining("|")));

    public List<Token> tokenify(String code) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = REGEX.matcher(code);
        while (matcher.find()) {
            Token token = extractToken(matcher);
            if (token.getType() != TokenType.WHITESPACE) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private Token extractToken(Matcher matcher) {
        for (TokenType type : TokenType.values()) {
            String value = matcher.group(type.ordinal() + 1);
            if (value != null) {
                return new Token(type, value);
            }
        }
        throw new IllegalStateException(matcher.group());
    }
}
