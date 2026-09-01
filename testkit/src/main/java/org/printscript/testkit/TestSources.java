package org.printscript.testkit;

import org.printscript.lexer.KeywordTable;
import org.printscript.lexer.Lexer;
import org.printscript.syntax.StatementSource;
import org.printscript.syntax.StatementSyntaxReader;
import org.printscript.syntax.SyntaxTreeBuilder;
import org.printscript.syntax.nodes.ProgramSyntax;

public final class TestSources {
  private TestSources() {}

  public static StatementSource statementsOf(String source) {
    return new StatementSyntaxReader(new Lexer(source));
  }

  public static StatementSource statementsOf(String source, KeywordTable keywords) {
    return new StatementSyntaxReader(new Lexer(source, keywords));
  }

  public static ProgramSyntax programOf(String source) {
    return new SyntaxTreeBuilder(statementsOf(source)).buildProgram();
  }

  public static ProgramSyntax programOf(String source, KeywordTable keywords) {
    return new SyntaxTreeBuilder(statementsOf(source, keywords)).buildProgram();
  }
}
