package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public record LiteralExpressionSyntax(SyntaxToken literal, TypeName literalType) implements ExpressionSyntax {
    @Override
    public SourceSpan span() {
        return literal.span();
    }
}
