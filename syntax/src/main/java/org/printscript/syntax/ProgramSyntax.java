package org.printscript.syntax;

import java.util.List;
import org.printscript.common.SourceSpan;

public record ProgramSyntax(List<StatementSyntax> statements, SyntaxToken eof) implements SyntaxNode {
    public ProgramSyntax {
        statements = List.copyOf(statements);
    }

    @Override
    public SourceSpan span() {
        if (statements.isEmpty()) return eof.span();
        return new SourceSpan(statements.getFirst().span().start(), eof.span().end());
    }
}
