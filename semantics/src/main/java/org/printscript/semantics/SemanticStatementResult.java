package org.printscript.semantics;

import java.util.List;
import org.printscript.diagnostics.Diagnostic;

public record SemanticStatementResult(
    SemanticContext nextContext, SemanticModel semanticModel, List<Diagnostic> diagnostics) {
  public SemanticStatementResult {
    diagnostics = List.copyOf(diagnostics);
  }

  public boolean isSuccess() {
    return diagnostics.isEmpty();
  }
}
