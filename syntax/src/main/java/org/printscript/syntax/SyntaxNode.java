package org.printscript.syntax;

import org.printscript.common.SourceSpan;

public sealed interface SyntaxNode permits ProgramSyntax, StatementSyntax, ExpressionSyntax {
    SourceSpan span();
}
