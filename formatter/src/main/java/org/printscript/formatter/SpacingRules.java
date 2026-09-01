package org.printscript.formatter;

import org.printscript.tokens.SyntaxToken;

@FunctionalInterface
public interface SpacingRules {
  String leadingTriviaFor(SyntaxToken token, SyntaxToken previous, FormatterConfig config);

  static SpacingRules v1() {
    return (token, previous, config) ->
        switch (token.type()) {
          case SEMICOLON -> " ".repeat(config.spacesBeforeSemicolon());
          case EQUAL -> " ".repeat(config.spacesAroundAssignment());
          case PLUS, MINUS, STAR, SLASH -> " ".repeat(config.spacesAroundOperators());
          case COLON, RIGHT_PAREN, LEFT_PAREN -> "";
          default ->
              switch (previous.type()) {
                case SEMICOLON -> "\n" + " ".repeat(config.spacesAfterSemicolon());
                case EQUAL -> " ".repeat(config.spacesAroundAssignment());
                case PLUS, MINUS, STAR, SLASH -> " ".repeat(config.spacesAroundOperators());
                case COLON -> " ";
                case LEFT_PAREN -> "";
                default -> hasLineBreak(token.leadingTrivia()) ? token.leadingTrivia() : " ";
              };
        };
  }

  private static boolean hasLineBreak(String trivia) {
    return trivia.indexOf('\n') >= 0;
  }
}
