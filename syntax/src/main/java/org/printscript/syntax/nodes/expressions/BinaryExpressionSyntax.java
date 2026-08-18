package org.printscript.syntax.nodes.expressions;

import org.printscript.common.SourceSpan;
import org.printscript.syntax.tokens.SyntaxToken;

public record BinaryExpressionSyntax(ExpressionSyntax left, SyntaxToken operator, ExpressionSyntax right)
        implements ExpressionSyntax {
    @Override
    public SourceSpan span() {
        return new SourceSpan(left.span().start(), right.span().end());
    }
}
