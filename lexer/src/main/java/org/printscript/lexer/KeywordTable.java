package org.printscript.lexer;

import org.printscript.tokens.TokenType;

@FunctionalInterface
public interface KeywordTable {
  TokenType classify(String lexeme);

  static KeywordTable v1() {
    return lexeme ->
        switch (lexeme) {
          case "let" -> TokenType.LET;
          case "number", "string" -> TokenType.TYPE;
          default -> TokenType.IDENTIFIER;
        };
  }

  static KeywordTable v1_1() {
    return lexeme ->
        switch (lexeme) {
          case "let" -> TokenType.LET;
          case "const" -> TokenType.CONST;
          case "if" -> TokenType.IF;
          case "else" -> TokenType.ELSE;
          case "number", "string", "boolean" -> TokenType.TYPE;
          case "true", "false" -> TokenType.BOOLEAN;
          default -> TokenType.IDENTIFIER;
        };
  }
}
