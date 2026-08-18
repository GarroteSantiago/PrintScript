package org.printscript.syntax.nodes.expressions;

import org.printscript.common.SourceSpan;
import org.printscript.syntax.TypeName;
import org.printscript.syntax.tokens.SyntaxToken;

public record LiteralExpressionSyntax(SyntaxToken literal, TypeName literalType) implements ExpressionSyntax {
    @Override
    public SourceSpan span() {
        return literal.span();
    }
}
