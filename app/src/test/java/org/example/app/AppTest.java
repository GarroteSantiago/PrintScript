package org.example.app;

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
import org.printscript.analyzer.NamingStyle;
import org.printscript.application.PrintScriptConfigReader;
import org.printscript.formatter.FormatterConfig;

class AppTest {
    @TempDir
    Path tempDir;

    @Test
    void formatCommandUsesInjectedConfigReader() throws Exception {
        Path source = tempDir.resolve("source.pisp");
        Path config = tempDir.resolve("config.toml");
        Files.writeString(source, "let text: string = \"hello\";\nprintln(text);");
        Files.writeString(config, "[formatter]\n");
        FakeConfigReader reader = new FakeConfigReader();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(stdout, true, StandardCharsets.UTF_8));

            int exitCode = new App(reader)
                    .run(new String[] {
                        "format",
                        "--source",
                        source.toString(),
                        "--version",
                        "1.0",
                        "--config",
                        config.toString()
                    });

            assertEquals(0, exitCode);
            assertEquals(config, reader.formatterPath);
            assertEquals("let text: string = \"hello\";\n\nprintln(text);\n", stdout.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void formatCommandPromptsForMissingConfigFile() throws Exception {
        Path source = tempDir.resolve("source.pisp");
        Path config = tempDir.resolve("config.toml");
        Files.writeString(source, "let text: string = \"hello\";");
        Files.writeString(config, "[formatter]\n");
        FakeConfigReader reader = new FakeConfigReader();
        PromptStub prompt = new PromptStub(config);
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(stdout, true, StandardCharsets.UTF_8));

            int exitCode = new App(reader, prompt)
                    .run(new String[] {"format", "--source", source.toString(), "--version", "1.0"});

            assertEquals(0, exitCode);
            assertEquals(config, reader.formatterPath);
            assertEquals(List.of("Config file: "), prompt.prompts);
            assertEquals("let text: string = \"hello\";\n", stdout.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void analyzeCommandPromptsForMissingConfigFile() throws Exception {
        Path source = tempDir.resolve("source.pisp");
        Path config = tempDir.resolve("config.toml");
        Files.writeString(source, "let text: string = \"hello\";");
        Files.writeString(config, "[analyzer]\n");
        FakeConfigReader reader = new FakeConfigReader();
        PromptStub prompt = new PromptStub(config);

        int exitCode = new App(reader, prompt)
                .run(new String[] {"analyze", "--source", source.toString(), "--version", "1.0"});

        assertEquals(0, exitCode);
        assertEquals(config, reader.analyzerPath);
        assertEquals(List.of("Config file: "), prompt.prompts);
    }

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
