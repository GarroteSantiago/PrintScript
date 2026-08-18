package org.printscript.semantics;

import java.util.HashMap;
import java.util.Map;
import org.printscript.common.Diagnostic;
import org.printscript.common.Phase;
import org.printscript.syntax.TokenType;
import org.printscript.syntax.TypeName;
import org.printscript.syntax.nodes.expressions.BinaryExpressionSyntax;
import org.printscript.syntax.nodes.expressions.CallExpressionSyntax;
import org.printscript.syntax.nodes.expressions.ExpressionSyntax;
import org.printscript.syntax.nodes.expressions.IdentifierExpressionSyntax;
import org.printscript.syntax.nodes.expressions.LiteralExpressionSyntax;
import org.printscript.syntax.nodes.statements.AssignmentSyntax;
import org.printscript.syntax.nodes.statements.ExpressionStatementSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;
import org.printscript.syntax.nodes.statements.VariableDeclarationSyntax;

public final class SemanticContext {
    private final BuiltinRegistry builtins;
    private final Map<String, VariableSymbol> symbols;

    private SemanticContext(BuiltinRegistry builtins, Map<String, VariableSymbol> symbols) {
        this.builtins = builtins;
        this.symbols = Map.copyOf(symbols);
    }

    public static SemanticContext empty(BuiltinRegistry builtins) {
        return new SemanticContext(builtins, Map.of());
    }

    public SemanticStatementResult validate(StatementSyntax statement) {
        SemanticModel.Builder model = SemanticModel.builder();
        Map<String, VariableSymbol> nextSymbols = new HashMap<>(symbols);
        validateStatement(statement, nextSymbols, model);
        SemanticContext next = model.hasErrors() ? this : new SemanticContext(builtins, nextSymbols);
        SemanticModel semanticModel = model.build();
        return new SemanticStatementResult(next, semanticModel, semanticModel.diagnostics());
    }

    private void validateStatement(
            StatementSyntax statement, Map<String, VariableSymbol> nextSymbols, SemanticModel.Builder model) {
        switch (statement) {
            case VariableDeclarationSyntax declaration -> {
                String name = declaration.identifier().semanticLexeme();
                TypeName declaredType = TypeName.fromLexeme(declaration.type().semanticLexeme());
                if (nextSymbols.containsKey(name)) {
                    model.addDiagnostic(error("Variable '" + name + "' is already declared", declaration.span()));
                    return;
                }
                TypeName initializerType = typeOf(declaration.initializer(), nextSymbols, model);
                if (initializerType == null)
                    return;
                if (initializerType != declaredType) {
                    model.addDiagnostic(error("Cannot assign " + printable(initializerType) + " to "
                            + printable(declaredType), declaration.initializer().span()));
                    return;
                }
                nextSymbols.put(name, new VariableSymbol(name, declaredType, declaration));
            }
            case AssignmentSyntax assignment -> {
                String name = assignment.identifier().semanticLexeme();
                VariableSymbol symbol = nextSymbols.get(name);
                if (symbol == null) {
                    model.addDiagnostic(error("Variable '" + name + "' is not declared", assignment.span()));
                    return;
                }
                TypeName valueType = typeOf(assignment.value(), nextSymbols, model);
                if (valueType != null && valueType != symbol.type()) {
                    model.addDiagnostic(error("Cannot assign " + printable(valueType) + " to "
                            + printable(symbol.type()), assignment.value().span()));
                }
            }
            case ExpressionStatementSyntax expressionStatement ->
                typeOf(expressionStatement.expression(), nextSymbols, model);
        }
    }

    private TypeName typeOf(
            ExpressionSyntax expression, Map<String, VariableSymbol> symbols, SemanticModel.Builder model) {
        TypeName type = switch (expression) {
            case LiteralExpressionSyntax literal -> literal.literalType();
            case IdentifierExpressionSyntax identifier -> identifierType(identifier, symbols, model);
            case BinaryExpressionSyntax binary -> binaryType(binary, symbols, model);
            case CallExpressionSyntax call -> callType(call, symbols, model);
        };
        model.setType(expression, type);
        return type;
    }

    private TypeName identifierType(
            IdentifierExpressionSyntax identifier, Map<String, VariableSymbol> symbols, SemanticModel.Builder model) {
        String name = identifier.identifier().semanticLexeme();
        VariableSymbol symbol = symbols.get(name);
        if (symbol == null) {
            model.addDiagnostic(error("Variable '" + name + "' is not declared", identifier.span()));
            return null;
        }
        model.resolveVariable(identifier, symbol);
        return symbol.type();
    }

    private TypeName binaryType(
            BinaryExpressionSyntax binary, Map<String, VariableSymbol> symbols, SemanticModel.Builder model) {
        TypeName left = typeOf(binary.left(), symbols, model);
        if (left == null)
            return null;
        TypeName right = typeOf(binary.right(), symbols, model);
        if (right == null)
            return null;
        if (binary.operator().type() == TokenType.PLUS && (left == TypeName.STRING || right == TypeName.STRING)) {
            return TypeName.STRING;
        }
        if (left == TypeName.NUMBER && right == TypeName.NUMBER) {
            return TypeName.NUMBER;
        }
        model.addDiagnostic(error("Operator '" + binary.operator().text() + "' cannot be applied to "
                + printable(left) + " and " + printable(right), binary.span()));
        return null;
    }

    private TypeName callType(
            CallExpressionSyntax call, Map<String, VariableSymbol> symbols, SemanticModel.Builder model) {
        String callee = call.callee().semanticLexeme();
        BuiltinSignature signature = builtins.find(callee).orElse(null);
        if (signature == null) {
            model.addDiagnostic(error("Unknown callable '" + callee + "'", call.span()));
            return null;
        }
        model.resolveCall(call, signature);
        if (call.arguments().size() != signature.parameterTypes().size()) {
            model.addDiagnostic(error("Callable '" + callee + "' expects "
                    + signature.parameterTypes().size() + " argument(s)", call.span()));
            return null;
        }
        for (int i = 0; i < call.arguments().size(); i++) {
            TypeName actual = typeOf(call.arguments().get(i), symbols, model);
            if (actual == null)
                return null;
            TypeName expected = signature.parameterTypes().get(i);
            if (actual != expected) {
                model.addDiagnostic(error("Callable '" + callee + "' expects "
                        + printable(expected) + " but received " + printable(actual), call.arguments().get(i).span()));
                return null;
            }
        }
        return signature.returnType();
    }

    private Diagnostic error(String message, org.printscript.common.SourceSpan span) {
        return Diagnostic.error(Phase.SEMANTIC, message, span);
    }

    private String printable(TypeName typeName) {
        return typeName == null ? "unit" : typeName.name().toLowerCase();
    }
}
