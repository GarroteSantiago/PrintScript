package org.printscript.syntax;

public sealed interface ExpressionSyntax extends SyntaxNode
        permits LiteralExpressionSyntax, IdentifierExpressionSyntax, BinaryExpressionSyntax, CallExpressionSyntax {}
