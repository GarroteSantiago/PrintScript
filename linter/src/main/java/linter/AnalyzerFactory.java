package linter;

import java.util.List;
import linter.api.Rule;
import linter.rules.NamingRule;
import linter.rules.PrintlnSimpleArgRule;

public final class AnalyzerFactory {
  private AnalyzerFactory() {}

  public static AnalyzerRunner defaultRunner() {
    List<Rule> rules = List.of(new NamingRule(), new PrintlnSimpleArgRule());
    return new AnalyzerRunner(rules);
  }
}
