package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public record AssignmentSyntax(SyntaxToken identifier, SyntaxToken equals, ExpressionSyntax value, SyntaxToken semicolon)
        implements StatementSyntax {
    @Override
    public SourceSpan span() {
        return new SourceSpan(identifier.span().start(), semicolon.span().end());
    }
}
