package org.printscript.interpreter;

import java.math.BigDecimal;
import org.printscript.diagnostics.Diagnostic;
import org.printscript.diagnostics.Phase;
import org.printscript.semantics.SemanticModel;
import org.printscript.syntax.TypeName;
import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.expressions.BinaryExpressionSyntax;
import org.printscript.syntax.nodes.expressions.CallExpressionSyntax;
import org.printscript.syntax.nodes.expressions.ExpressionSyntax;
import org.printscript.syntax.nodes.expressions.IdentifierExpressionSyntax;
import org.printscript.syntax.nodes.expressions.LiteralExpressionSyntax;
import org.printscript.syntax.nodes.statements.AssignmentSyntax;
import org.printscript.syntax.nodes.statements.ExpressionStatementSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;
import org.printscript.syntax.nodes.statements.VariableDeclarationSyntax;

public final class Interpreter {
    private final OutputPort output;
    private final ArithmeticOperators operators;

    public Interpreter(OutputPort output) {
        this(output, ArithmeticOperators.v1());
    }

    public Interpreter(OutputPort output, ArithmeticOperators operators) {
        this.output = output;
        this.operators = operators;
    }

    public RuntimeEnvironment execute(ProgramSyntax program, SemanticModel semanticModel) {
        RuntimeEnvironment environment = RuntimeEnvironment.empty();
        for (StatementSyntax statement : program.statements()) {
            environment = execute(statement, environment, semanticModel);
        }
        return environment;
    }

    public RuntimeEnvironment executeStatement(
            StatementSyntax statement, RuntimeEnvironment environment, SemanticModel semanticModel) {
        return execute(statement, environment, semanticModel);
    }

    private RuntimeEnvironment execute(
            StatementSyntax statement, RuntimeEnvironment environment, SemanticModel semanticModel) {
        return switch (statement) {
            case VariableDeclarationSyntax declaration -> environment.put(
                    declaration.identifier().semanticLexeme(),
                    evaluate(declaration.initializer(), environment, semanticModel));
            case AssignmentSyntax assignment -> environment.put(
                    assignment.identifier().semanticLexeme(),
                    evaluate(assignment.value(), environment, semanticModel));
            case ExpressionStatementSyntax expressionStatement -> {
                evaluate(expressionStatement.expression(), environment, semanticModel);
                yield environment;
            }
        };
    }

    private RuntimeValue evaluate(
            ExpressionSyntax expression, RuntimeEnvironment environment, SemanticModel semanticModel) {
        return switch (expression) {
            case LiteralExpressionSyntax literal -> literal.literalType() == TypeName.NUMBER
                    ? new RuntimeValue.NumberValue(new BigDecimal(literal.literal().semanticLexeme()))
                    : new RuntimeValue.StringValue(literal.literal().semanticLexeme());
            case IdentifierExpressionSyntax identifier -> environment.find(identifier.identifier().semanticLexeme())
                    .orElseThrow(() -> runtime(
                            "Variable '" + identifier.identifier().semanticLexeme() + "' is not declared",
                            identifier));
            case BinaryExpressionSyntax binary -> evaluateBinary(binary, environment, semanticModel);
            case CallExpressionSyntax call -> evaluateCall(call, environment, semanticModel);
        };
    }

    private RuntimeValue evaluateBinary(
            BinaryExpressionSyntax binary, RuntimeEnvironment environment, SemanticModel semanticModel) {
        RuntimeValue left = evaluate(binary.left(), environment, semanticModel);
        RuntimeValue right = evaluate(binary.right(), environment, semanticModel);
        if (semanticModel.typeOf(binary).orElse(null) == TypeName.STRING) {
            return new RuntimeValue.StringValue(stringify(left) + stringify(right));
        }
        BigDecimal leftNumber = ((RuntimeValue.NumberValue) left).value();
        BigDecimal rightNumber = ((RuntimeValue.NumberValue) right).value();
        BigDecimal result;
        try {
            result = operators.apply(binary.operator().type(), leftNumber, rightNumber);
        } catch (ArithmeticException exception) {
            throw runtime(exception.getMessage(), binary);
        }
        return new RuntimeValue.NumberValue(result);
    }

    private RuntimeValue evaluateCall(
            CallExpressionSyntax call, RuntimeEnvironment environment, SemanticModel semanticModel) {
        if (semanticModel.resolveCall(call).isEmpty()) {
            throw runtime("Unknown callable '" + call.callee().semanticLexeme() + "'", call);
        }
        RuntimeValue value = evaluate(call.arguments().getFirst(), environment, semanticModel);
        output.println(stringify(value));
        return RuntimeValue.UnitValue.INSTANCE;
    }

    private String stringify(RuntimeValue value) {
        return switch (value) {
            case RuntimeValue.NumberValue number -> number.value().stripTrailingZeros().toPlainString();
            case RuntimeValue.StringValue string -> string.value();
            case RuntimeValue.UnitValue ignored -> "";
        };
    }

    private RuntimeFailure runtime(String message, ExpressionSyntax expression) {
        return new RuntimeFailure(Diagnostic.error(Phase.RUNTIME, message, expression.span()));
    }
}
