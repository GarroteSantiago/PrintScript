package org.printscript.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.analyzer.NamingStyle;
import org.printscript.formatter.FormatterConfig;

class TomlPrintScriptConfigReaderTest {
  @TempDir Path tempDir;

  private FormatterConfig formatterConfig;
  private AnalyzerConfig analyzerConfig;

  @BeforeEach
  void readConfigsFromTomlSections() throws Exception {
    Path formatterConfigFile = tempDir.resolve("formatter.toml");
    Files.writeString(
        formatterConfigFile,
        """
            [case]
            name = "metadata is ignored"

            [formatter]
            spaces_before_semicolon = 1
            spaces_after_semicolon = 0
            spaces_around_assignment = 1
            spaces_around_operators = 1
            blank_lines_before_println = 2
            """);
    formatterConfig = new TomlPrintScriptConfigReader().readFormatterConfig(formatterConfigFile);

    Path analyzerConfigFile = tempDir.resolve("analyzer.toml");
    Files.writeString(
        analyzerConfigFile,
        """
            [analyzer]
            naming_style = "CAMEL_CASE"
            restrict_println_to_simple_arguments = true
            """);
    analyzerConfig = new TomlPrintScriptConfigReader().readAnalyzerConfig(analyzerConfigFile);
  }

  @Test
  void readsSpacesBeforeSemicolonFromFormatterSection() {
    assertEquals(1, formatterConfig.spacesBeforeSemicolon(), "spacesBeforeSemicolon");
  }

  @Test
  void readsSpacesAfterSemicolonFromFormatterSection() {
    assertEquals(0, formatterConfig.spacesAfterSemicolon(), "spacesAfterSemicolon");
  }

  @Test
  void readsSpacesAroundAssignmentFromFormatterSection() {
    assertEquals(1, formatterConfig.spacesAroundAssignment(), "spacesAroundAssignment");
  }

  @Test
  void readsSpacesAroundOperatorsFromFormatterSection() {
    assertEquals(1, formatterConfig.spacesAroundOperators(), "spacesAroundOperators");
  }

  @Test
  void readsBlankLinesBeforePrintlnFromFormatterSection() {
    assertEquals(2, formatterConfig.blankLinesBeforePrintln(), "blankLinesBeforePrintln");
  }

  @Test
  void readsNamingStyleFromAnalyzerSection() {
    assertEquals(NamingStyle.CAMEL_CASE, analyzerConfig.namingStyle(), "namingStyle");
  }

  @Test
  void readsRestrictPrintlnFlagFromAnalyzerSection() {
    assertTrue(
        analyzerConfig.restrictPrintlnToSimpleArguments(), "restrictPrintlnToSimpleArguments");
  }
}
