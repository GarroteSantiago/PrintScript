package org.printscript.syntax;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class SyntaxTreeBuilder {
    private final Reader reader;

    public SyntaxTreeBuilder(String source) {
        this(new StringReader(source));
    }

    public SyntaxTreeBuilder(Reader reader) {
        this.reader = reader;
    }

    public ProgramSyntax buildProgram() {
        StatementSyntaxReader statementReader = new StatementSyntaxReader(reader);
        List<StatementSyntax> statements = new ArrayList<>();
        while (statementReader.hasNext()) statements.add(statementReader.next());
        return new ProgramSyntax(statements, statementReader.eof());
    }
}
