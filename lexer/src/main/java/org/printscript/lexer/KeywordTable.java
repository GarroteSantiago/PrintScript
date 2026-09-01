package org.printscript.lexer;

import org.printscript.tokens.TokenType;

@FunctionalInterface
public interface KeywordTable {
    TokenType classify(String lexeme);

    static KeywordTable v1() {
        return lexeme -> switch (lexeme) {
            case "let" -> TokenType.LET;
            case "number", "string" -> TokenType.TYPE;
            default -> TokenType.IDENTIFIER;
        };
    }
}
