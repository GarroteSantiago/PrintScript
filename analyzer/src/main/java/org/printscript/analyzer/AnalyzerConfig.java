package org.printscript.analyzer;

public record AnalyzerConfig(
    NamingStyle namingStyle,
    boolean checkIdentifierNaming,
    boolean restrictPrintlnToSimpleArguments,
    boolean restrictReadInputToSimpleArguments) {
  public static AnalyzerConfig defaults() {
    return new AnalyzerConfig(NamingStyle.SNAKE_CASE, true, true, true);
  }
}
