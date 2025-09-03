package linter.api;

public record AnalyzerConfig(Naming naming, Println println) {
  public record Naming(boolean enabled, CaseStyle style) {}

  public enum CaseStyle {
    CAMEL,
    SNAKE
  }

  public record Println(boolean enabled, boolean onlyIdentifierOrLiteral) {}

  public static AnalyzerConfig defaults() {
    return new AnalyzerConfig(new Naming(true, CaseStyle.CAMEL), new Println(true, true));
  }
}
