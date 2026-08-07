package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public record VariableDeclarationSyntax(
        SyntaxToken letKeyword,
        SyntaxToken identifier,
        SyntaxToken colon,
        SyntaxToken type,
        SyntaxToken equals,
        ExpressionSyntax initializer,
        SyntaxToken semicolon)
        implements StatementSyntax {
    @Override
    public SourceSpan span() {
        return new SourceSpan(letKeyword.span().start(), semicolon.span().end());
    }
}
