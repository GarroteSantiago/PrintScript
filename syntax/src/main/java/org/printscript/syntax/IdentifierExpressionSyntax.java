package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public record IdentifierExpressionSyntax(SyntaxToken identifier) implements ExpressionSyntax {
    @Override
    public SourceSpan span() {
        return identifier.span();
    }
}
