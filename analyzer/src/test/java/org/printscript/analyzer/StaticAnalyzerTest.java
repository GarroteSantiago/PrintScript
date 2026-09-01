package org.printscript.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.printscript.diagnostics.Diagnostic;
import org.printscript.semantics.BuiltinRegistry;
import org.printscript.semantics.SemanticContext;
import org.printscript.testkit.TestSources;

class StaticAnalyzerTest {
    @Test
    void reportsConfiguredStyleAndPrintlnPolicyViolations() {
        var statements = TestSources.statementsOf("""
                let badName: string = "hello";
                println("hello " + badName);
                """);
        var semanticContext = SemanticContext.empty(BuiltinRegistry.v1());
        List<Diagnostic> diagnostics = new ArrayList<>();

        while (statements.hasNext()) {
            var statement = statements.next();
            var semantic = semanticContext.validate(statement);
            new StaticAnalyzer().analyze(statement, semantic.semanticModel(), AnalyzerConfig.defaults(), diagnostics::add);
            semanticContext = semantic.nextContext();
        }

        assertEquals(2, diagnostics.size());
        assertEquals("Identifier 'badName' does not match SNAKE_CASE", diagnostics.get(0).message());
        assertEquals("println argument must be an identifier or literal", diagnostics.get(1).message());
    }
}
