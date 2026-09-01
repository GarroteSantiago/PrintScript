package org.printscript.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.printscript.diagnostics.Diagnostic;
import org.printscript.diagnostics.Phase;
import org.printscript.semantics.SemanticModel;
import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.expressions.BinaryExpressionSyntax;
import org.printscript.syntax.nodes.expressions.CallExpressionSyntax;
import org.printscript.syntax.nodes.expressions.ExpressionSyntax;
import org.printscript.syntax.nodes.expressions.IdentifierExpressionSyntax;
import org.printscript.syntax.nodes.expressions.LiteralExpressionSyntax;
import org.printscript.syntax.nodes.statements.AssignmentSyntax;
import org.printscript.syntax.nodes.statements.BlockStatementSyntax;
import org.printscript.syntax.nodes.statements.ExpressionStatementSyntax;
import org.printscript.syntax.nodes.statements.IfStatementSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;
import org.printscript.syntax.nodes.statements.VariableDeclarationSyntax;

public final class StaticAnalyzer {
  private final NamingStyleRules namingStyleRules;

  public StaticAnalyzer() {
    this(NamingStyleRules.v1());
  }

  public StaticAnalyzer(NamingStyleRules namingStyleRules) {
    this.namingStyleRules = namingStyleRules;
  }

  public List<Diagnostic> analyze(
      ProgramSyntax program, SemanticModel semanticModel, AnalyzerConfig config) {
    List<Diagnostic> diagnostics = new ArrayList<>();
    for (StatementSyntax statement : program.statements()) {
      diagnostics.addAll(analyze(statement, semanticModel, config));
    }
    return List.copyOf(diagnostics);
  }

  public List<Diagnostic> analyze(
      StatementSyntax statement, SemanticModel semanticModel, AnalyzerConfig config) {
    List<Diagnostic> diagnostics = new ArrayList<>();
    analyze(statement, semanticModel, config, diagnostics::add);
    return List.copyOf(diagnostics);
  }

  public void analyze(
      StatementSyntax statement,
      SemanticModel semanticModel,
      AnalyzerConfig config,
      Consumer<Diagnostic> diagnostics) {
    switch (statement) {
      case VariableDeclarationSyntax declaration -> {
        checkName(
            declaration.identifier().semanticLexeme(),
            declaration.identifier().span(),
            config,
            diagnostics);
        declaration
            .initializer()
            .ifPresent(initializer -> checkCalls(initializer, semanticModel, config, diagnostics));
      }
      case AssignmentSyntax assignment ->
          checkCalls(assignment.value(), semanticModel, config, diagnostics);
      case ExpressionStatementSyntax expressionStatement ->
          checkCalls(expressionStatement.expression(), semanticModel, config, diagnostics);
      case IfStatementSyntax ifStatement -> {
        checkCalls(ifStatement.condition(), semanticModel, config, diagnostics);
        for (StatementSyntax inner : ifStatement.thenBlock().statements()) {
          analyze(inner, semanticModel, config, diagnostics);
        }
        ifStatement
            .elseBlock()
            .ifPresent(
                elseBlock -> {
                  for (StatementSyntax inner : elseBlock.statements()) {
                    analyze(inner, semanticModel, config, diagnostics);
                  }
                });
      }
      case BlockStatementSyntax block -> {
        for (StatementSyntax inner : block.statements()) {
          analyze(inner, semanticModel, config, diagnostics);
        }
      }
    }
  }

  private void checkCalls(
      ExpressionSyntax expression,
      SemanticModel semanticModel,
      AnalyzerConfig config,
      Consumer<Diagnostic> diagnostics) {
    List<CallExpressionSyntax> calls = new ArrayList<>();
    collectCalls(expression, calls);
    for (CallExpressionSyntax call : calls) {
      checkCallArgumentShape(
          call, "println", config.restrictPrintlnToSimpleArguments(), semanticModel, diagnostics);
      checkCallArgumentShape(
          call,
          "readInput",
          config.restrictReadInputToSimpleArguments(),
          semanticModel,
          diagnostics);
    }
  }

  private void collectCalls(ExpressionSyntax expression, List<CallExpressionSyntax> out) {
    switch (expression) {
      case CallExpressionSyntax call -> {
        out.add(call);
        for (ExpressionSyntax argument : call.arguments()) {
          collectCalls(argument, out);
        }
      }
      case BinaryExpressionSyntax binary -> {
        collectCalls(binary.left(), out);
        collectCalls(binary.right(), out);
      }
      case LiteralExpressionSyntax ignored -> {}
      case IdentifierExpressionSyntax ignored -> {}
    }
  }

  private void checkCallArgumentShape(
      CallExpressionSyntax call,
      String builtinName,
      boolean enabled,
      SemanticModel semanticModel,
      Consumer<Diagnostic> diagnostics) {
    if (!enabled) return;
    if (semanticModel
            .resolveCall(call)
            .map(signature -> builtinName.equals(signature.name()))
            .orElse(false)
        && !call.arguments().isEmpty()
        && !(call.arguments().getFirst() instanceof IdentifierExpressionSyntax)
        && !(call.arguments().getFirst() instanceof LiteralExpressionSyntax)) {
      diagnostics.accept(
          error(
              builtinName + " argument must be an identifier or literal",
              call.arguments().getFirst().span()));
    }
  }

  private void checkName(
      String name,
      org.printscript.source.SourceSpan span,
      AnalyzerConfig config,
      Consumer<Diagnostic> diagnostics) {
    if (!config.checkIdentifierNaming()) return;
    boolean valid = namingStyleRules.matches(config.namingStyle(), name);
    if (!valid) {
      diagnostics.accept(
          error("Identifier '" + name + "' does not match " + config.namingStyle(), span));
    }
  }

  private Diagnostic error(String message, org.printscript.source.SourceSpan span) {
    return Diagnostic.error(Phase.ANALYZER, message, span);
  }
}
