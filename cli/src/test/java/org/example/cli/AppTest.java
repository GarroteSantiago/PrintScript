package org.example.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.application.PrintScriptConfigReader;
import org.printscript.formatter.FormatterConfig;

class AppTest {
  @TempDir Path tempDir;

  @Test
  void formatCommandExitsSuccessfully() throws Exception {
    FormatRun run = runFormatCommandWithInjectedConfigReader();

    assertEquals(0, run.exitCode(), "expected successful exit code");
  }

  @Test
  void formatCommandUsesInjectedConfigReader() throws Exception {
    FormatRun run = runFormatCommandWithInjectedConfigReader();

    assertEquals(
        run.config(), run.reader().formatterPath, "expected injected config path to be used");
  }

  @Test
  void formatCommandWritesFormattedSourceToStdout() throws Exception {
    FormatRun run = runFormatCommandWithInjectedConfigReader();

    assertEquals(
        "let text: string = \"hello\";\n\nprintln(text);\n",
        run.stdout(),
        "expected formatted source on stdout");
  }

  @SuppressWarnings(
      "PMD.CloseResource") // originalOut is a saved reference to restore, not ours to close
  private FormatRun runFormatCommandWithInjectedConfigReader() throws Exception {
    Path source = tempDir.resolve("source.pisp");
    Path config = tempDir.resolve("config.toml");
    Files.writeString(source, "let text: string = \"hello\";\nprintln(text);");
    Files.writeString(config, "[formatter]\n");
    FakeConfigReader reader = new FakeConfigReader();

    int exitCode;
    String stdout;
    PrintStream originalOut = System.out;
    try (ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
        PrintStream redirectedOut = new PrintStream(capturedOut, true, StandardCharsets.UTF_8)) {
      System.setOut(redirectedOut);
      exitCode =
          new App(reader)
              .run(
                  "format",
                  "--source",
                  source.toString(),
                  "--version",
                  "1.0",
                  "--config",
                  config.toString());
      stdout = capturedOut.toString(StandardCharsets.UTF_8);
    } finally {
      System.setOut(originalOut);
    }
    return new FormatRun(exitCode, config, reader, stdout);
  }

  @Test
  void formatCommandPromptsForMissingConfigFileExitsSuccessfully() throws Exception {
    PromptFormatRun run = runFormatCommandWithMissingConfigFile();

    assertEquals(0, run.exitCode(), "expected successful exit code");
  }

  @Test
  void formatCommandPromptsForMissingConfigFileUsesPromptedPath() throws Exception {
    PromptFormatRun run = runFormatCommandWithMissingConfigFile();

    assertEquals(
        run.config(), run.reader().formatterPath, "expected prompted config path to be used");
  }

  @Test
  void formatCommandPromptsForMissingConfigFileAsksExpectedPrompt() throws Exception {
    PromptFormatRun run = runFormatCommandWithMissingConfigFile();

    assertEquals(List.of("Config file: "), run.prompts(), "expected a single config-file prompt");
  }

  @Test
  void formatCommandPromptsForMissingConfigFileWritesFormattedSourceToStdout() throws Exception {
    PromptFormatRun run = runFormatCommandWithMissingConfigFile();

    assertEquals(
        "let text: string = \"hello\";\n", run.stdout(), "expected formatted source on stdout");
  }

  @SuppressWarnings(
      "PMD.CloseResource") // originalOut is a saved reference to restore, not ours to close
  private PromptFormatRun runFormatCommandWithMissingConfigFile() throws Exception {
    Path source = tempDir.resolve("source.pisp");
    Path config = tempDir.resolve("config.toml");
    Files.writeString(source, "let text: string = \"hello\";");
    Files.writeString(config, "[formatter]\n");
    FakeConfigReader reader = new FakeConfigReader();
    PromptStub prompt = new PromptStub(config);

    int exitCode;
    String stdout;
    PrintStream originalOut = System.out;
    try (ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
        PrintStream redirectedOut = new PrintStream(capturedOut, true, StandardCharsets.UTF_8)) {
      System.setOut(redirectedOut);
      exitCode =
          new App(reader, prompt).run("format", "--source", source.toString(), "--version", "1.0");
      stdout = capturedOut.toString(StandardCharsets.UTF_8);
    } finally {
      System.setOut(originalOut);
    }
    return new PromptFormatRun(exitCode, config, reader, prompt.prompts, stdout);
  }

  @Test
  void analyzeCommandPromptsForMissingConfigFileExitsSuccessfully() throws Exception {
    AnalyzeRun run = runAnalyzeCommandWithMissingConfigFile();

    assertEquals(0, run.exitCode(), "expected successful exit code");
  }

  @Test
  void analyzeCommandPromptsForMissingConfigFileUsesPromptedPath() throws Exception {
    AnalyzeRun run = runAnalyzeCommandWithMissingConfigFile();

    assertEquals(
        run.config(), run.reader().analyzerPath, "expected prompted config path to be used");
  }

  @Test
  void analyzeCommandPromptsForMissingConfigFileAsksExpectedPrompt() throws Exception {
    AnalyzeRun run = runAnalyzeCommandWithMissingConfigFile();

    assertEquals(List.of("Config file: "), run.prompts(), "expected a single config-file prompt");
  }

  private AnalyzeRun runAnalyzeCommandWithMissingConfigFile() throws Exception {
    Path source = tempDir.resolve("source.pisp");
    Path config = tempDir.resolve("config.toml");
    Files.writeString(source, "let text: string = \"hello\";");
    Files.writeString(config, "[analyzer]\n");
    FakeConfigReader reader = new FakeConfigReader();
    PromptStub prompt = new PromptStub(config);

    int exitCode =
        new App(reader, prompt).run("analyze", "--source", source.toString(), "--version", "1.0");

    return new AnalyzeRun(exitCode, config, reader, prompt.prompts);
  }

  private record FormatRun(int exitCode, Path config, FakeConfigReader reader, String stdout) {}

  private record PromptFormatRun(
      int exitCode, Path config, FakeConfigReader reader, List<String> prompts, String stdout) {}

  private record AnalyzeRun(
      int exitCode, Path config, FakeConfigReader reader, List<String> prompts) {}

  private static final class FakeConfigReader implements PrintScriptConfigReader {
    private Path formatterPath;
    private Path analyzerPath;

    @Override
    public FormatterConfig readFormatterConfig(Path path) {
      formatterPath = path;
      return new FormatterConfig(0, 0, 1, 1, 1);
    }

    @Override
    public AnalyzerConfig readAnalyzerConfig(Path path) {
      analyzerPath = path;
      return AnalyzerConfig.defaults();
    }
  }

  private static final class PromptStub implements App.ConfigPathPrompt {
    private final Path path;
    private final List<String> prompts = new ArrayList<>();

    private PromptStub(Path path) {
      this.path = path;
    }

    @Override
    public Path ask(String prompt) {
      prompts.add(prompt);
      return path;
    }
  }
}
