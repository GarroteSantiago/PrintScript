package linter.rules;

import java.util.List;
import java.util.ArrayList;

import common.Node;
import expression.binary.BinaryExpression;
import expression.identifier.IdentifierNode;
import expression.literal.LiteralNode;
import linter.api.AnalyzerConfig;
import linter.api.Rule;
import linter.api.Violation;
import results.Result;
import statements.LetStatementNode;
import statements.PrintStatementNode;
import visitor.RuleVisitor;


public final class PrintlnSimpleArgRule implements Rule, RuleVisitor {
  private AnalyzerConfig config;
  private List<Violation> violations;

  @Override
  public String id() {
    return "PS-PRINTLN-ARG";
  }

  @Override
  public String description() {
    return "println must receive only an identifier or a literal when enabled.";
  }

  @Override
  public boolean enabled(AnalyzerConfig cfg) {
    return cfg.println().enabled() && cfg.println().onlyIdentifierOrLiteral();
  }

  @Override
  public List<Violation> check(Node root, AnalyzerConfig cfg) {
    this.config = cfg;
    this.violations = new ArrayList<>();
    return violations;
  }

  @Override
  public Result<String> check(PrintStatementNode node) {
    var exprResult = node.expression();
    if (exprResult.isSuccessful()) {
      var expr = exprResult.result();
      if (!(expr instanceof IdentifierNode) && !(expr instanceof LiteralNode)) {
        violations.add(new Violation(
          id(),
          "println argument must be an identifier or literal (rule enabled).",
          null // Aquí podrías usar el rango si lo tienes
        ));
      }
    }
    return new results.CorrectResult<>("");
  }

  @Override
  public Result<String> check(LetStatementNode node) {
    return new results.CorrectResult<>("");
  }

  @Override
  public Result<String> check(IdentifierNode node) {
    return new results.CorrectResult<>("");
  }

  @Override
  public Result<String> check(BinaryExpression node) {
    return new results.CorrectResult<>("");
  }

  @Override
  public Result<String> check(LiteralNode node) {
    return new results.CorrectResult<>("");
  }
}
