package org.printscript.application;

import java.io.IOException;
import java.nio.file.Path;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.formatter.FormatterConfig;

public interface PrintScriptConfigReader {
    FormatterConfig readFormatterConfig(Path path) throws IOException;

    AnalyzerConfig readAnalyzerConfig(Path path) throws IOException;
}
