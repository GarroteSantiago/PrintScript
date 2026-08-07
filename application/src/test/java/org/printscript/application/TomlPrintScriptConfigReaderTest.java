package org.printscript.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.printscript.analyzer.NamingStyle;

class TomlPrintScriptConfigReaderTest {
    @TempDir
    Path tempDir;

    @Test
    void readsFormatterConfigFromTomlSection() throws Exception {
        Path config = tempDir.resolve("formatter.toml");
        Files.writeString(config, """
                [case]
                name = "metadata is ignored"

                [formatter]
                spaces_before_semicolon = 1
                spaces_after_semicolon = 0
                spaces_around_assignment = 1
                spaces_around_operators = 1
                blank_lines_before_println = 2
                """);

        var formatter = new TomlPrintScriptConfigReader().readFormatterConfig(config);

        assertEquals(1, formatter.spacesBeforeSemicolon());
        assertEquals(0, formatter.spacesAfterSemicolon());
        assertEquals(1, formatter.spacesAroundAssignment());
        assertEquals(1, formatter.spacesAroundOperators());
        assertEquals(2, formatter.blankLinesBeforePrintln());
    }

    @Test
    void readsAnalyzerConfigFromTomlSection() throws Exception {
        Path config = tempDir.resolve("analyzer.toml");
        Files.writeString(config, """
                [analyzer]
                naming_style = "CAMEL_CASE"
                restrict_println_to_simple_arguments = true
                """);

        var analyzer = new TomlPrintScriptConfigReader().readAnalyzerConfig(config);

        assertEquals(NamingStyle.CAMEL_CASE, analyzer.namingStyle());
        assertTrue(analyzer.restrictPrintlnToSimpleArguments());
    }
}
