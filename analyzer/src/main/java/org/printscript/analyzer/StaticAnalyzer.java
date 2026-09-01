package org.printscript.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.printscript.diagnostics.Diagnostic;
import org.printscript.diagnostics.Phase;
import org.printscript.semantics.SemanticModel;
import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.expressions.CallExpressionSyntax;
import org.printscript.syntax.nodes.expressions.IdentifierExpressionSyntax;
import org.printscript.syntax.nodes.expressions.LiteralExpressionSyntax;
import org.printscript.syntax.nodes.statements.AssignmentSyntax;
import org.printscript.syntax.nodes.statements.ExpressionStatementSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;
import org.printscript.syntax.nodes.statements.VariableDeclarationSyntax;

public final class StaticAnalyzer {
    private static final Pattern SNAKE_CASE = Pattern.compile("[a-z][a-z0-9]*(?:_[a-z0-9]+)*");
    private static final Pattern CAMEL_CASE = Pattern.compile("[a-z][a-zA-Z0-9]*");

    public List<Diagnostic> analyze(ProgramSyntax program, SemanticModel semanticModel, AnalyzerConfig config) {
        List<Diagnostic> diagnostics = new ArrayList<>();
        for (StatementSyntax statement : program.statements()) {
            diagnostics.addAll(analyze(statement, semanticModel, config));
        }
        return List.copyOf(diagnostics);
    }

    public List<Diagnostic> analyze(StatementSyntax statement, SemanticModel semanticModel, AnalyzerConfig config) {
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
            case VariableDeclarationSyntax declaration ->
                checkName(declaration.identifier().semanticLexeme(), declaration.identifier().span(), config,
                        diagnostics);
            case AssignmentSyntax ignored -> {
            }
            case ExpressionStatementSyntax expressionStatement -> {
                if (config.restrictPrintlnToSimpleArguments()
                        && expressionStatement.expression() instanceof CallExpressionSyntax call
                        && semanticModel.resolveCall(call).map(signature -> signature.name().equals("println"))
                                .orElse(false)
                        && !call.arguments().isEmpty()
                        && !(call.arguments().getFirst() instanceof IdentifierExpressionSyntax)
                        && !(call.arguments().getFirst() instanceof LiteralExpressionSyntax)) {
                    diagnostics.accept(error(
                            "println argument must be an identifier or literal",
                            call.arguments().getFirst().span()));
                }
            }
        }
    }

    private void checkName(
            String name,
            org.printscript.source.SourceSpan span,
            AnalyzerConfig config,
            Consumer<Diagnostic> diagnostics) {
        boolean valid = switch (config.namingStyle()) {
            case SNAKE_CASE -> SNAKE_CASE.matcher(name).matches();
            case CAMEL_CASE -> CAMEL_CASE.matcher(name).matches();
        };
        if (!valid) {
            diagnostics.accept(error("Identifier '" + name + "' does not match " + config.namingStyle(), span));
        }
    }

    private Diagnostic error(String message, org.printscript.source.SourceSpan span) {
        return Diagnostic.error(Phase.ANALYZER, message, span);
    }
}
