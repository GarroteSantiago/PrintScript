package linter;

import java.util.ArrayList;
import java.util.List;

import common.Node;
import linter.api.AnalyzerConfig;
import linter.api.Rule;
import linter.api.Violation;

public final class AnalyzerRunner {
  private final List<Rule> rules;

  public AnalyzerRunner(List<Rule> rules) {
    this.rules = rules;
  }

  public List<Violation> analyze(Node root, AnalyzerConfig cfg) {
    List<Violation> out = new ArrayList<>();
    for (Rule r : rules) {
      if (r.enabled(cfg)) out.addAll(r.check(root, cfg));
    }
    return out;
  }
}
