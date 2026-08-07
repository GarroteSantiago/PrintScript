package org.printscript.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.printscript.syntax.StatementSyntaxReader;
import org.printscript.syntax.SyntaxTreeBuilder;

class PrintScriptFormatterTest {
    @Test
    void normalizesControlledSpacingAndPreservesCommentsAndTokenText() {
        String source = "let   a:string='value'; # keep\nprintln(a);";

        String formatted =
                new PrintScriptFormatter().format(new SyntaxTreeBuilder(source).buildProgram(), FormatterConfig.defaults());

        assertEquals("let a: string = 'value';# keep\nprintln(a);\n", formatted);
    }

    @Test
    void insertsBlankLinesBeforePrintlnWithoutLeadingSpace() {
        String source = "let text: string = \"hello\";\nprintln(text);";

        String formatted = new PrintScriptFormatter()
                .format(new SyntaxTreeBuilder(source).buildProgram(), new FormatterConfig(0, 0, 1, 1, 1));

        assertEquals("let text: string = \"hello\";\n\nprintln(text);\n", formatted);
    }

    @Test
    void writesFormattedStatementsToAppendable() throws Exception {
        var statements = new StatementSyntaxReader("let text:string=\"hello\";\nprintln(text);");
        var session = new PrintScriptFormatter().newSession(new FormatterConfig(0, 0, 1, 1, 1));
        var output = new StringWriter();

        while (statements.hasNext()) {
            session.format(statements.next(), output);
        }
        session.finish(statements.eof(), output);

        assertEquals("let text: string = \"hello\";\n\nprintln(text);\n", output.toString());
    }
}
