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
import org.printscript.analyzer.StaticAnalyzer;
import org.printscript.common.CommandResult;
import org.printscript.common.Diagnostic;
import org.printscript.common.LanguageVersion;
import org.printscript.common.Phase;
import org.printscript.common.ProgressReporter;
import org.printscript.formatter.FormatterConfig;
import org.printscript.formatter.PrintScriptFormatter;
import org.printscript.interpreter.Interpreter;
import org.printscript.interpreter.RuntimeEnvironment;
import org.printscript.interpreter.RuntimeFailure;
import org.printscript.semantics.BuiltinRegistry;
import org.printscript.semantics.SemanticContext;
import org.printscript.semantics.SemanticStatementResult;
import org.printscript.syntax.StatementSyntax;
import org.printscript.syntax.StatementSyntaxReader;
import org.printscript.syntax.SyntaxException;

public final class PrintScript {
    private final BuiltinRegistry builtins = BuiltinRegistry.v1();
    private final StaticAnalyzer staticAnalyzer = new StaticAnalyzer();
    private final PrintScriptFormatter formatter = new PrintScriptFormatter();

    public CommandResult<ExecutionResult> execute(String source, LanguageVersion version, ProgressReporter progress) {
        return execute(new StringReader(source), version, progress);
    }

    public CommandResult<ExecutionResult> execute(Reader source, LanguageVersion version, ProgressReporter progress) {
        List<String> output = new ArrayList<>();
        CommandResult<RuntimeEnvironment> result = execute(source, version, output::add, progress);
        if (!result.isSuccess()) return CommandResult.failure(result.diagnostics());
        return CommandResult.success(new ExecutionResult(output));
    }

    public CommandResult<RuntimeEnvironment> execute(
            Reader source, LanguageVersion version, Consumer<String> output, ProgressReporter progress) {
        if (!version.supportsV1()) return unsupported(version);
        SemanticContext semanticContext = SemanticContext.empty(builtins);
        RuntimeEnvironment runtimeEnvironment = RuntimeEnvironment.empty();
        Interpreter interpreter = new Interpreter(output::accept);
        try {
            progress.report("Reading statements");
            StatementSyntaxReader statements = new StatementSyntaxReader(source);
            while (statements.hasNext()) {
                StatementSyntax statement = statements.next();
                SemanticStatementResult semantic = semanticContext.validate(statement);
                if (!semantic.isSuccess()) return CommandResult.failure(semantic.diagnostics());
                progress.report("Executing statement");
                runtimeEnvironment = interpreter.executeStatement(statement, runtimeEnvironment, semantic.semanticModel());
                semanticContext = semantic.nextContext();
            }
        } catch (RuntimeFailure failure) {
            return CommandResult.failure(List.of(failure.diagnostic()));
        } catch (SyntaxException exception) {
            return CommandResult.failure(List.of(exception.diagnostic()));
        }
        return CommandResult.success(runtimeEnvironment);
    }

    public CommandResult<String> format(String source, LanguageVersion version, FormatterConfig config, ProgressReporter progress) {
        return format(new StringReader(source), version, config, progress);
    }

    public CommandResult<String> format(Reader source, LanguageVersion version, FormatterConfig config, ProgressReporter progress) {
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
            Reader source, LanguageVersion version, FormatterConfig config, Appendable output, ProgressReporter progress)
            throws IOException {
        if (!version.supportsV1()) return unsupported(version);
        try {
            progress.report("Reading statements");
            StatementSyntaxReader statements = new StatementSyntaxReader(source);
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
        CommandResult<AnalysisResult> result = analyze(source, version, config, diagnostics::add, progress);
        return new CommandResult<>(diagnostics, result.isSuccess() ? diagnostics : result.diagnostics());
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
        SemanticContext semanticContext = SemanticContext.empty(builtins);
        try {
            progress.report("Reading statements");
            StatementSyntaxReader statements = new StatementSyntaxReader(source);
            while (statements.hasNext()) {
                StatementSyntax statement = statements.next();
                SemanticStatementResult semantic = semanticContext.validate(statement);
                if (!semantic.isSuccess()) return CommandResult.failure(semantic.diagnostics());
                progress.report("Analyzing statement");
                staticAnalyzer.analyze(statement, semantic.semanticModel(), config, diagnostic -> {
                    diagnosticSink.accept(diagnostic);
                    diagnosticCount.incrementAndGet();
                    if (diagnostic.severity() == org.printscript.common.Severity.ERROR) {
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

    public CommandResult<Void> validate(String source, LanguageVersion version, ProgressReporter progress) {
        return validate(new StringReader(source), version, progress);
    }

    public CommandResult<Void> validate(Reader source, LanguageVersion version, ProgressReporter progress) {
        if (!version.supportsV1()) return unsupported(version);
        SemanticContext semanticContext = SemanticContext.empty(builtins);
        try {
            progress.report("Reading statements");
            StatementSyntaxReader statements = new StatementSyntaxReader(source);
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
        Diagnostic diagnostic = Diagnostic.error(
                Phase.APPLICATION,
                "Unsupported PrintScript version: " + version.major() + "." + version.minor() + "." + version.patch(),
                new org.printscript.common.SourceSpan(
                        new org.printscript.common.SourcePosition(1, 1, 0),
                        new org.printscript.common.SourcePosition(1, 1, 0)));
        return CommandResult.failure(List.of(diagnostic));
    }
}
