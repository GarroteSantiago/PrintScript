package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public record BinaryExpressionSyntax(ExpressionSyntax left, SyntaxToken operator, ExpressionSyntax right)
        implements ExpressionSyntax {
    @Override
    public SourceSpan span() {
        return new SourceSpan(left.span().start(), right.span().end());
    }
}
