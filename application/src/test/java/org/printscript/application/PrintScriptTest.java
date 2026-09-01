package org.printscript.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.diagnostics.Diagnostic;

class PrintScriptTest {
    @Test
    void executesVersionOneProgram() {
        String source = """
                let name: string = "Joe";
                let lastName: string = 'Doe';
                println(name + " " + lastName);
                let a: number = 12;
                let b: number = 4;
                let c: number = a / b;
                println("Result: " + c);
                """;

        CommandResult<ExecutionResult> result =
                new PrintScript().execute(source, LanguageVersion.V1_0_0, ProgressReporter.NONE);

        assertTrue(result.isSuccess());
        assertEquals(java.util.List.of("Joe Doe", "Result: 3"), result.value().outputLines());
    }

    @Test
    void executesIntoInjectedOutputPort() {
        List<String> output = new ArrayList<>();

        var result = new PrintScript()
                .execute(
                        new StringReader("""
                                let value: number = 2;
                                println("value: " + value);
                                """),
                        LanguageVersion.V1_0_0,
                        output::add,
                        ProgressReporter.NONE);

        assertTrue(result.isSuccess());
        assertEquals(List.of("value: 2"), output);
    }

    @Test
    void streamsAnalyzerDiagnosticsToSink() {
        List<Diagnostic> diagnostics = new ArrayList<>();

        var result = new PrintScript()
                .analyze(
                        new StringReader("let badName: string = \"hello\";"),
                        LanguageVersion.V1_0_0,
                        AnalyzerConfig.defaults(),
                        diagnostics::add,
                        ProgressReporter.NONE);

        assertTrue(result.isSuccess());
        assertEquals(1, result.value().diagnosticCount());
        assertEquals("Identifier 'badName' does not match SNAKE_CASE", diagnostics.getFirst().message());
    }
}
