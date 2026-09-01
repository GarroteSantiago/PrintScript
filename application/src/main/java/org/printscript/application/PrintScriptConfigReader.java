package org.printscript.application;

import java.io.IOException;
import java.nio.file.Path;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.formatter.FormatterConfigProvider;

public interface PrintScriptConfigReader {
  FormatterConfigProvider readFormatterConfig(Path path) throws IOException;

  AnalyzerConfig readAnalyzerConfig(Path path) throws IOException;
}
