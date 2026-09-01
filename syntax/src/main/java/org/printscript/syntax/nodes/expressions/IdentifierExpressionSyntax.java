package org.printscript.syntax.nodes.expressions;

import org.printscript.source.SourceSpan;
import org.printscript.tokens.SyntaxToken;

public record IdentifierExpressionSyntax(SyntaxToken identifier) implements ExpressionSyntax {
    @Override
    public SourceSpan span() {
        return identifier.span();
    }
}
