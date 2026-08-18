package org.printscript.interpreter;

import java.math.BigDecimal;
import java.math.MathContext;
import org.printscript.common.Diagnostic;
import org.printscript.common.Phase;
import org.printscript.semantics.SemanticModel;
import org.printscript.syntax.TokenType;
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

    public Interpreter(OutputPort output) {
        this.output = output;
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
        if (binary.operator().type() == TokenType.PLUS
                && (left instanceof RuntimeValue.StringValue || right instanceof RuntimeValue.StringValue)) {
            return new RuntimeValue.StringValue(stringify(left) + stringify(right));
        }
        BigDecimal leftNumber = ((RuntimeValue.NumberValue) left).value();
        BigDecimal rightNumber = ((RuntimeValue.NumberValue) right).value();
        BigDecimal result = switch (binary.operator().type()) {
            case PLUS -> leftNumber.add(rightNumber);
            case MINUS -> leftNumber.subtract(rightNumber);
            case STAR -> leftNumber.multiply(rightNumber);
            case SLASH -> {
                if (rightNumber.compareTo(BigDecimal.ZERO) == 0) {
                    throw runtime("Division by zero", binary);
                }
                yield leftNumber.divide(rightNumber, MathContext.DECIMAL128).stripTrailingZeros();
            }
            default -> throw new IllegalStateException("Unsupported binary operator: " + binary.operator().type());
        };
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
