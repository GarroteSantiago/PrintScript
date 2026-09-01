package org.printscript.syntax.nodes;

import org.printscript.source.SourceSpan;
import org.printscript.syntax.nodes.expressions.ExpressionSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;

public sealed interface SyntaxNode permits ProgramSyntax, StatementSyntax, ExpressionSyntax {
  SourceSpan span();
}
