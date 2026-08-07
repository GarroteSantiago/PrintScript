package org.printscript.syntax;

public sealed interface StatementSyntax extends SyntaxNode
        permits VariableDeclarationSyntax, AssignmentSyntax, ExpressionStatementSyntax {}
