package org.printscript.application;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.analyzer.NamingStyleRules;
import org.printscript.analyzer.StaticAnalyzer;
import org.printscript.diagnostics.Diagnostic;
import org.printscript.diagnostics.Phase;
import org.printscript.diagnostics.Severity;
import org.printscript.formatter.FormatterConfig;
import org.printscript.formatter.PrintScriptFormatter;
import org.printscript.formatter.SpacingRules;
import org.printscript.interpreter.ArithmeticOperators;
import org.printscript.interpreter.Interpreter;
import org.printscript.interpreter.RuntimeEnvironment;
import org.printscript.interpreter.RuntimeFailure;
import org.printscript.lexer.KeywordTable;
import org.printscript.lexer.Lexer;
import org.printscript.semantics.BinaryOperatorRules;
import org.printscript.semantics.BuiltinRegistry;
import org.printscript.semantics.SemanticContext;
import org.printscript.semantics.SemanticStatementResult;
import org.printscript.source.SourcePosition;
import org.printscript.source.SourceSpan;
import org.printscript.syntax.StatementSource;
import org.printscript.syntax.StatementSyntaxReader;
import org.printscript.syntax.TypeAnnotationTable;
import org.printscript.syntax.nodes.statements.StatementSyntax;
import org.printscript.tokens.SyntaxException;

public final class PrintScript {
  private static final String READING_STATEMENTS = "Reading statements";

  private final BuiltinRegistry builtins = BuiltinRegistry.v1();
  private final TypeAnnotationTable typeAnnotations = TypeAnnotationTable.v1();
  private final BinaryOperatorRules binaryOperatorRules = BinaryOperatorRules.v1();
  private final KeywordTable keywords = KeywordTable.v1();
  private final ArithmeticOperators operators = ArithmeticOperators.v1();
  private final StaticAnalyzer staticAnalyzer = new StaticAnalyzer(NamingStyleRules.v1());
  private final PrintScriptFormatter formatter = new PrintScriptFormatter(SpacingRules.v1());

  public CommandResult<ExecutionResult> execute(
      String source, LanguageVersion version, ProgressReporter progress) {
    return execute(new StringReader(source), version, progress);
  }

  public CommandResult<ExecutionResult> execute(
      Reader source, LanguageVersion version, ProgressReporter progress) {
    List<String> output = new ArrayList<>();
    CommandResult<RuntimeEnvironment> result = execute(source, version, output::add, progress);
    if (!result.isSuccess()) return CommandResult.failure(result.diagnostics());
    return CommandResult.success(new ExecutionResult(output));
  }

  public CommandResult<RuntimeEnvironment> execute(
      Reader source, LanguageVersion version, Consumer<String> output, ProgressReporter progress) {
    if (!version.supportsV1()) return unsupported(version);
    SemanticContext semanticContext =
        SemanticContext.empty(builtins, typeAnnotations, binaryOperatorRules);
    RuntimeEnvironment runtimeEnvironment = RuntimeEnvironment.empty();
    Interpreter interpreter = new Interpreter(output::accept, operators);
    try {
      progress.report(READING_STATEMENTS);
      StatementSource statements = new StatementSyntaxReader(new Lexer(source, keywords));
      while (statements.hasNext()) {
        StatementSyntax statement = statements.next();
        SemanticStatementResult semantic = semanticContext.validate(statement);
        if (!semantic.isSuccess()) return CommandResult.failure(semantic.diagnostics());
        progress.report("Executing statement");
        runtimeEnvironment =
            interpreter.executeStatement(statement, runtimeEnvironment, semantic.semanticModel());
        semanticContext = semantic.nextContext();
      }
    } catch (RuntimeFailure failure) {
      return CommandResult.failure(List.of(failure.diagnostic()));
    } catch (SyntaxException exception) {
      return CommandResult.failure(List.of(exception.diagnostic()));
    }
    return CommandResult.success(runtimeEnvironment);
  }

  public CommandResult<String> format(
      String source, LanguageVersion version, FormatterConfig config, ProgressReporter progress) {
    return format(new StringReader(source), version, config, progress);
  }

  public CommandResult<String> format(
      Reader source, LanguageVersion version, FormatterConfig config, ProgressReporter progress) {
    StringWriter output = new StringWriter();
    CommandResult<Void> result;
    try {
      result = format(source, version, config, output, progress);
    } catch (IOException exception) {
      throw new IllegalStateException("StringWriter append failed", exception);
    }
    if (!result.isSuccess()) return CommandResult.failure(result.diagnostics());
    return CommandResult.success(output.toString());
  }

  public CommandResult<Void> format(
      Reader source,
      LanguageVersion version,
      FormatterConfig config,
      Appendable output,
      ProgressReporter progress)
      throws IOException {
    if (!version.supportsV1()) return unsupported(version);
    try {
      progress.report(READING_STATEMENTS);
      StatementSource statements = new StatementSyntaxReader(new Lexer(source, keywords));
      PrintScriptFormatter.Session session = formatter.newSession(config);
      while (statements.hasNext()) {
        progress.report("Formatting statement");
        session.format(statements.next(), output);
      }
      session.finish(statements.eof(), output);
      return CommandResult.success(null);
    } catch (SyntaxException exception) {
      return CommandResult.failure(List.of(exception.diagnostic()));
    }
  }

  public CommandResult<List<Diagnostic>> analyze(
      String source, LanguageVersion version, AnalyzerConfig config, ProgressReporter progress) {
    return analyze(new StringReader(source), version, config, progress);
  }

  public CommandResult<List<Diagnostic>> analyze(
      Reader source, LanguageVersion version, AnalyzerConfig config, ProgressReporter progress) {
    List<Diagnostic> diagnostics = new ArrayList<>();
    CommandResult<AnalysisResult> result =
        analyze(source, version, config, diagnostics::add, progress);
    return new CommandResult<>(
        diagnostics, result.isSuccess() ? diagnostics : result.diagnostics());
  }

  public CommandResult<AnalysisResult> analyze(
      Reader source,
      LanguageVersion version,
      AnalyzerConfig config,
      Consumer<Diagnostic> diagnosticSink,
      ProgressReporter progress) {
    if (!version.supportsV1()) return unsupported(version);
    AtomicInteger diagnosticCount = new AtomicInteger();
    AtomicInteger errorCount = new AtomicInteger();
    SemanticContext semanticContext =
        SemanticContext.empty(builtins, typeAnnotations, binaryOperatorRules);
    try {
      progress.report(READING_STATEMENTS);
      StatementSource statements = new StatementSyntaxReader(new Lexer(source, keywords));
      while (statements.hasNext()) {
        StatementSyntax statement = statements.next();
        SemanticStatementResult semantic = semanticContext.validate(statement);
        if (!semantic.isSuccess()) return CommandResult.failure(semantic.diagnostics());
        progress.report("Analyzing statement");
        staticAnalyzer.analyze(
            statement,
            semantic.semanticModel(),
            config,
            diagnostic -> {
              diagnosticSink.accept(diagnostic);
              diagnosticCount.incrementAndGet();
              if (diagnostic.severity() == Severity.ERROR) {
                errorCount.incrementAndGet();
              }
            });
        semanticContext = semantic.nextContext();
      }
      return CommandResult.success(new AnalysisResult(diagnosticCount.get(), errorCount.get()));
    } catch (SyntaxException exception) {
      return CommandResult.failure(List.of(exception.diagnostic()));
    }
  }

  public CommandResult<Void> validate(
      String source, LanguageVersion version, ProgressReporter progress) {
    return validate(new StringReader(source), version, progress);
  }

  public CommandResult<Void> validate(
      Reader source, LanguageVersion version, ProgressReporter progress) {
    if (!version.supportsV1()) return unsupported(version);
    SemanticContext semanticContext =
        SemanticContext.empty(builtins, typeAnnotations, binaryOperatorRules);
    try {
      progress.report(READING_STATEMENTS);
      StatementSource statements = new StatementSyntaxReader(new Lexer(source, keywords));
      while (statements.hasNext()) {
        SemanticStatementResult semantic = semanticContext.validate(statements.next());
        if (!semantic.isSuccess()) return CommandResult.failure(semantic.diagnostics());
        semanticContext = semantic.nextContext();
      }
      return CommandResult.success(null);
    } catch (SyntaxException exception) {
      return CommandResult.failure(List.of(exception.diagnostic()));
    }
  }

  private <T> CommandResult<T> unsupported(LanguageVersion version) {
    Diagnostic diagnostic =
        Diagnostic.error(
            Phase.APPLICATION,
            "Unsupported PrintScript version: "
                + version.major()
                + "."
                + version.minor()
                + "."
                + version.patch(),
            new SourceSpan(new SourcePosition(1, 1, 0), new SourcePosition(1, 1, 0)));
    return CommandResult.failure(List.of(diagnostic));
  }
}
