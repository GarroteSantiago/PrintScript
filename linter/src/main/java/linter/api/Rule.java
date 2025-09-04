package linter.api;

import common.Node;

import java.util.List;

public interface Rule {
  String id();

  String description();

  boolean enabled(AnalyzerConfig cfg);

  List<Violation> check(Node root, AnalyzerConfig cfg);
}