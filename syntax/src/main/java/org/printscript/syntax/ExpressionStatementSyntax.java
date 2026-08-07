package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public record ExpressionStatementSyntax(ExpressionSyntax expression, SyntaxToken semicolon) implements StatementSyntax {
    @Override
    public SourceSpan span() {
        return new SourceSpan(expression.span().start(), semicolon.span().end());
    }
}
