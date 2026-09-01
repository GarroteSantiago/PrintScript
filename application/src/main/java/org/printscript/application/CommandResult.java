package org.printscript.application;

import java.util.List;
import org.printscript.diagnostics.Diagnostic;
import org.printscript.diagnostics.Severity;

public record CommandResult<T>(T value, List<Diagnostic> diagnostics) {
  public CommandResult {
    diagnostics = List.copyOf(diagnostics);
  }

  public static <T> CommandResult<T> success(T value) {
    return new CommandResult<>(value, List.of());
  }

  public static <T> CommandResult<T> failure(List<Diagnostic> diagnostics) {
    return new CommandResult<>(null, diagnostics);
  }

  public boolean isSuccess() {
    return diagnostics.stream().noneMatch(d -> d.severity() == Severity.ERROR);
  }
}
