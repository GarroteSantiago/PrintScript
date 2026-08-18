package org.printscript.syntax.tokens;

import org.printscript.common.SourceSpan;
import org.printscript.syntax.TokenType;

public record Token(
        TokenType type,
        String semanticLexeme,
        String text,
        String leadingTrivia,
        SourceSpan span) {
}
