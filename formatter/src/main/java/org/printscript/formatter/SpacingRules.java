package org.printscript.formatter;

import java.util.Optional;
import org.printscript.tokens.SyntaxToken;
import org.printscript.tokens.TokenType;

@FunctionalInterface
public interface SpacingRules {
  Optional<String> leadingTriviaFor(
      SyntaxToken token, SyntaxToken previous, FormatterConfigProvider config);

  static SpacingRules v1() {
    return (token, previous, config) ->
        switch (token.type()) {
          case SEMICOLON -> spaces(config.spacesBeforeSemicolon());
          case EQUAL -> spacesOrFallback(config.spacesAroundAssignment(), config);
          case PLUS, MINUS, STAR, SLASH -> spacesOrFallback(config.spacesAroundOperators(), config);
          case COLON -> spacesOrFallback(config.spacesBeforeColon(), config);
          case RIGHT_PAREN, LEFT_PAREN -> blanketOnly(config);
          default ->
              switch (previous.type()) {
                case SEMICOLON -> lineBreakAfterSemicolon(config);
                case EQUAL -> spacesOrFallback(config.spacesAroundAssignment(), config);
                case PLUS, MINUS, STAR, SLASH ->
                    spacesOrFallback(config.spacesAroundOperators(), config);
                case COLON -> spacesOrFallback(config.spacesAfterColon(), config);
                case LEFT_PAREN -> blanketOnly(config);
                default ->
                    Optional.of(hasLineBreak(token.leadingTrivia()) ? token.leadingTrivia() : " ");
              };
        };
  }

  static SpacingRules v1_1() {
    return (token, previous, config) ->
        switch (token.type()) {
          case SEMICOLON -> spaces(config.spacesBeforeSemicolon());
          case EQUAL -> spacesOrFallback(config.spacesAroundAssignment(), config);
          case PLUS, MINUS, STAR, SLASH -> spacesOrFallback(config.spacesAroundOperators(), config);
          case COLON -> spacesOrFallback(config.spacesBeforeColon(), config);
          case RIGHT_PAREN -> blanketOnly(config);
          case LEFT_PAREN ->
              previous.type() == TokenType.IF ? Optional.of(" ") : blanketOnly(config);
          case LEFT_BRACE -> braceOwnTrivia(config);
          case RIGHT_BRACE -> Optional.of("\n");
          default ->
              switch (previous.type()) {
                case SEMICOLON -> lineBreakAfterSemicolon(config);
                case EQUAL -> spacesOrFallback(config.spacesAroundAssignment(), config);
                case PLUS, MINUS, STAR, SLASH ->
                    spacesOrFallback(config.spacesAroundOperators(), config);
                case COLON -> spacesOrFallback(config.spacesAfterColon(), config);
                case LEFT_PAREN -> blanketOnly(config);
                case LEFT_BRACE, RIGHT_BRACE -> braceContentTrivia(config);
                default ->
                    Optional.of(hasLineBreak(token.leadingTrivia()) ? token.leadingTrivia() : " ");
              };
        };
  }

  private static Optional<String> spaces(Optional<Integer> count) {
    return count.map(n -> " ".repeat(n));
  }

  /**
   * Uses the specific rule when configured; otherwise falls back to a single space if the blanket
   * {@code mandatorySingleSpaceSeparation} rule is on, else leaves the trivia untouched.
   */
  private static Optional<String> spacesOrFallback(
      Optional<Integer> specific, FormatterConfigProvider config) {
    return specific.isPresent() ? spaces(specific) : blanketOnly(config);
  }

  private static Optional<String> blanketOnly(FormatterConfigProvider config) {
    return Boolean.TRUE.equals(config.mandatorySingleSpaceSeparation().orElse(false))
        ? Optional.of(" ")
        : Optional.empty();
  }

  private static Optional<String> lineBreakAfterSemicolon(FormatterConfigProvider config) {
    if (config.spacesAfterSemicolon().isPresent()) {
      return spaces(config.spacesAfterSemicolon()).map(spaces -> "\n" + spaces);
    }
    return Boolean.TRUE.equals(config.mandatoryLineBreakAfterStatement().orElse(false))
        ? Optional.of("\n")
        : Optional.empty();
  }

  private static Optional<String> braceOwnTrivia(FormatterConfigProvider config) {
    return config.ifBraceOnSameLine().map(sameLine -> sameLine ? " " : "\n");
  }

  private static Optional<String> braceContentTrivia(FormatterConfigProvider config) {
    return config.blockIndentSpaces().isPresent() ? Optional.of("\n") : Optional.empty();
  }

  private static boolean hasLineBreak(String trivia) {
    return trivia.indexOf('\n') >= 0;
  }
}
