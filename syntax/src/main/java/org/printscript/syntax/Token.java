package org.printscript.syntax;

import org.printscript.common.SourceSpan;

record Token(
        TokenType type,
        String semanticLexeme,
        String text,
        String leadingTrivia,
        SourceSpan span) {}
