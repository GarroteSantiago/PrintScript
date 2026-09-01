package org.printscript.syntax;

import java.util.ArrayList;
import java.util.List;
import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;

public final class SyntaxTreeBuilder {
  private final StatementSource statements;

  public SyntaxTreeBuilder(StatementSource statements) {
    this.statements = statements;
  }

  public ProgramSyntax buildProgram() {
    List<StatementSyntax> result = new ArrayList<>();
    while (statements.hasNext()) result.add(statements.next());
    return new ProgramSyntax(result, statements.eof());
  }
}
