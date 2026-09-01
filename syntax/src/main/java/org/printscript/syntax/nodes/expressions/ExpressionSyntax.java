package org.printscript.syntax.nodes.expressions;

import org.printscript.syntax.nodes.SyntaxNode;

public sealed interface ExpressionSyntax extends SyntaxNode
    permits LiteralExpressionSyntax,
        IdentifierExpressionSyntax,
        BinaryExpressionSyntax,
        CallExpressionSyntax {}
