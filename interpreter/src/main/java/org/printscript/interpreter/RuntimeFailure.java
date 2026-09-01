package org.printscript.interpreter;

import org.printscript.diagnostics.Diagnostic;

public final class RuntimeFailure extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final transient Diagnostic diagnostic;

  public RuntimeFailure(Diagnostic diagnostic) {
    super(diagnostic.message());
    this.diagnostic = diagnostic;
  }

  public Diagnostic diagnostic() {
    return diagnostic;
  }
}
