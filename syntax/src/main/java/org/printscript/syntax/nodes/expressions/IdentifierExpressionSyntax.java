package org.printscript.syntax.nodes.expressions;

import org.printscript.common.SourceSpan;
import org.printscript.syntax.tokens.SyntaxToken;

public record IdentifierExpressionSyntax(SyntaxToken identifier) implements ExpressionSyntax {
    @Override
    public SourceSpan span() {
        return identifier.span();
    }
}
