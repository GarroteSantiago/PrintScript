package org.printscript.analyzer;

public record AnalyzerConfig(NamingStyle namingStyle, boolean restrictPrintlnToSimpleArguments) {
  public static AnalyzerConfig defaults() {
    return new AnalyzerConfig(NamingStyle.SNAKE_CASE, true);
  }
}
