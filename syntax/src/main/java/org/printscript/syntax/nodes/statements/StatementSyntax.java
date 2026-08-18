package org.printscript.syntax.nodes.statements;

import org.printscript.syntax.nodes.SyntaxNode;

public sealed interface StatementSyntax extends SyntaxNode
        permits VariableDeclarationSyntax, AssignmentSyntax, ExpressionStatementSyntax {
}
