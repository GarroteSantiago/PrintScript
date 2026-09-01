package org.printscript.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TomlPrintScriptConfigReaderV11Test {
  @TempDir Path tempDir;

  @Test
  void leavesBlockIndentSpacesUnconfiguredWhenKeyIsAbsent() throws Exception {
    Path configFile = tempDir.resolve("formatter.toml");
    Files.writeString(
        configFile,
        """
            [formatter]
            spaces_before_semicolon = 0
            spaces_after_semicolon = 0
            spaces_around_assignment = 1
            spaces_around_operators = 1
            blank_lines_before_println = 0
            """);

    var config = new TomlPrintScriptConfigReader().readFormatterConfig(configFile);

    assertEquals(
        Optional.empty(), config.blockIndentSpaces(), "expected no configured block indent");
  }

  @Test
  void leavesAnyFormatterRuleUnconfiguredWhenItsKeyIsAbsent() throws Exception {
    Path configFile = tempDir.resolve("formatter.toml");
    Files.writeString(
        configFile,
        """
            [formatter]
            spaces_around_assignment = 1
            spaces_around_operators = 1
            """);

    var config = new TomlPrintScriptConfigReader().readFormatterConfig(configFile);

    assertEquals(
        Optional.empty(),
        config.spacesBeforeSemicolon(),
        "expected an omitted key to leave the rule unconfigured");
  }

  @Test
  void readsBlockIndentSpacesWhenPresent() throws Exception {
    Path configFile = tempDir.resolve("formatter.toml");
    Files.writeString(
        configFile,
        """
            [formatter]
            spaces_before_semicolon = 0
            spaces_after_semicolon = 0
            spaces_around_assignment = 1
            spaces_around_operators = 1
            blank_lines_before_println = 0
            block_indent_spaces = 4
            """);

    var config = new TomlPrintScriptConfigReader().readFormatterConfig(configFile);

    assertEquals(
        Optional.of(4), config.blockIndentSpaces(), "expected the configured block indent");
  }

  @Test
  void defaultsRestrictReadInputWhenKeyIsAbsent() throws Exception {
    Path configFile = tempDir.resolve("analyzer.toml");
    Files.writeString(
        configFile,
        """
            [analyzer]
            naming_style = "SNAKE_CASE"
            restrict_println_to_simple_arguments = true
            """);

    var config = new TomlPrintScriptConfigReader().readAnalyzerConfig(configFile);

    assertTrue(config.restrictReadInputToSimpleArguments(), "expected the default toggle");
  }

  @Test
  void readsRestrictReadInputWhenPresent() throws Exception {
    Path configFile = tempDir.resolve("analyzer.toml");
    Files.writeString(
        configFile,
        """
            [analyzer]
            naming_style = "SNAKE_CASE"
            restrict_println_to_simple_arguments = true
            restrict_readinput_to_simple_arguments = false
            """);

    var config = new TomlPrintScriptConfigReader().readAnalyzerConfig(configFile);

    assertFalse(config.restrictReadInputToSimpleArguments(), "expected the configured toggle");
  }
}
