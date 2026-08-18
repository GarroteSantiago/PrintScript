package org.printscript.syntax.nodes.statements;

import org.printscript.common.SourceSpan;
import org.printscript.syntax.nodes.expressions.ExpressionSyntax;
import org.printscript.syntax.tokens.SyntaxToken;

public record ExpressionStatementSyntax(ExpressionSyntax expression, SyntaxToken semicolon) implements StatementSyntax {
    @Override
    public SourceSpan span() {
        return new SourceSpan(expression.span().start(), semicolon.span().end());
    }
}
