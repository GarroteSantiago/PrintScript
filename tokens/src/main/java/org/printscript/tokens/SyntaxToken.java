package org.printscript.tokens;

import org.printscript.source.SourceSpan;

public record SyntaxToken(
    TokenType type, String semanticLexeme, String text, String leadingTrivia, SourceSpan span) {
  public static SyntaxToken from(Token token) {
    return new SyntaxToken(
        token.type(), token.semanticLexeme(), token.text(), token.leadingTrivia(), token.span());
  }
}
