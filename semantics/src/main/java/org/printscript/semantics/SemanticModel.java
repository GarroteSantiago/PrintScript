package org.printscript.semantics;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.printscript.common.Diagnostic;
import org.printscript.syntax.CallExpressionSyntax;
import org.printscript.syntax.ExpressionSyntax;
import org.printscript.syntax.IdentifierExpressionSyntax;
import org.printscript.syntax.TypeName;

public final class SemanticModel {
    private final List<Diagnostic> diagnostics;
    private final Map<ExpressionSyntax, TypeName> expressionTypes;
    private final Map<IdentifierExpressionSyntax, VariableSymbol> variableReferences;
    private final Map<CallExpressionSyntax, BuiltinSignature> resolvedCalls;

    SemanticModel(
            List<Diagnostic> diagnostics,
            Map<ExpressionSyntax, TypeName> expressionTypes,
            Map<IdentifierExpressionSyntax, VariableSymbol> variableReferences,
            Map<CallExpressionSyntax, BuiltinSignature> resolvedCalls) {
        this.diagnostics = List.copyOf(diagnostics);
        this.expressionTypes = Map.copyOf(expressionTypes);
        this.variableReferences = Map.copyOf(variableReferences);
        this.resolvedCalls = Map.copyOf(resolvedCalls);
    }

    public List<Diagnostic> diagnostics() {
        return diagnostics;
    }

    public Optional<TypeName> typeOf(ExpressionSyntax expression) {
        return Optional.ofNullable(expressionTypes.get(expression));
    }

    public Optional<VariableSymbol> resolveVariable(IdentifierExpressionSyntax expression) {
        return Optional.ofNullable(variableReferences.get(expression));
    }

    public Optional<BuiltinSignature> resolveCall(CallExpressionSyntax expression) {
        return Optional.ofNullable(resolvedCalls.get(expression));
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder {
        private final List<Diagnostic> diagnostics = new java.util.ArrayList<>();
        private final Map<ExpressionSyntax, TypeName> expressionTypes = new IdentityHashMap<>();
        private final Map<IdentifierExpressionSyntax, VariableSymbol> variableReferences = new IdentityHashMap<>();
        private final Map<CallExpressionSyntax, BuiltinSignature> resolvedCalls = new IdentityHashMap<>();

        void addDiagnostic(Diagnostic diagnostic) {
            diagnostics.add(diagnostic);
        }

        void setType(ExpressionSyntax expression, TypeName type) {
            if (type != null) expressionTypes.put(expression, type);
        }

        void resolveVariable(IdentifierExpressionSyntax expression, VariableSymbol symbol) {
            variableReferences.put(expression, symbol);
        }

        void resolveCall(CallExpressionSyntax expression, BuiltinSignature signature) {
            resolvedCalls.put(expression, signature);
        }

        boolean hasErrors() {
            return !diagnostics.isEmpty();
        }

        SemanticModel build() {
            return new SemanticModel(diagnostics, expressionTypes, variableReferences, resolvedCalls);
        }
    }
}
