package org.printscript.syntax.nodes.statements;

import org.printscript.source.SourceSpan;
import org.printscript.syntax.nodes.expressions.ExpressionSyntax;
import org.printscript.tokens.SyntaxToken;

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
