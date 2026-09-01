package org.printscript.syntax;

import org.printscript.syntax.nodes.statements.StatementSyntax;
import org.printscript.tokens.SyntaxToken;

public interface StatementSource {
  boolean hasNext();

  StatementSyntax next();

  SyntaxToken eof();
}
