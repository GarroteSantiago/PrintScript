package org.printscript.tokens;

import org.printscript.source.SourceSpan;

public record Token(
    TokenType type, String semanticLexeme, String text, String leadingTrivia, SourceSpan span) {}
