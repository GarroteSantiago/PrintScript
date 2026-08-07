package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public record SyntaxToken(
        TokenType type,
        String semanticLexeme,
        String text,
        String leadingTrivia,
        SourceSpan span) {
    static SyntaxToken from(Token token) {
        return new SyntaxToken(
                token.type(), token.semanticLexeme(), token.text(), token.leadingTrivia(), token.span());
    }
}
