package org.printscript.syntax.nodes.expressions;

import java.util.List;
import org.printscript.common.SourceSpan;
import org.printscript.syntax.tokens.SyntaxToken;

public record CallExpressionSyntax(
        SyntaxToken callee, SyntaxToken leftParen, List<ExpressionSyntax> arguments, SyntaxToken rightParen)
        implements ExpressionSyntax {
    public CallExpressionSyntax {
        arguments = List.copyOf(arguments);
    }

    @Override
    public SourceSpan span() {
        return new SourceSpan(callee.span().start(), rightParen.span().end());
    }
}
