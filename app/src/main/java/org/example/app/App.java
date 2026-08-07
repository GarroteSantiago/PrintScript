package org.example.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import org.printscript.application.AnalysisResult;
import org.printscript.application.PrintScript;
import org.printscript.application.PrintScriptConfigReader;
import org.printscript.application.TomlPrintScriptConfigReader;
import org.printscript.common.CommandResult;
import org.printscript.common.Diagnostic;
import org.printscript.common.LanguageVersion;
import org.printscript.common.ProgressReporter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "printscript",
        mixinStandardHelpOptions = true,
        subcommands = {
            App.ExecuteCommand.class,
            App.FormatCommand.class,
            App.AnalyzeCommand.class,
            App.ValidateCommand.class
        })
public class App implements Callable<Integer> {
    private final PrintScriptConfigReader configReader;
    private final PrintScript printScript;
    private final ProgressReporter progress;
    private final ConfigPathPrompt configPathPrompt;

    public App() {
        this(new TomlPrintScriptConfigReader());
    }

    App(PrintScriptConfigReader configReader) {
        this(configReader, App::promptConfigPath);
    }

    App(PrintScriptConfigReader configReader, ConfigPathPrompt configPathPrompt) {
        this(configReader, new PrintScript(), message -> System.err.println("[printscript] " + message), configPathPrompt);
    }

    App(PrintScriptConfigReader configReader, PrintScript printScript, ProgressReporter progress) {
        this(configReader, printScript, progress, App::promptConfigPath);
    }

    App(
            PrintScriptConfigReader configReader,
            PrintScript printScript,
            ProgressReporter progress,
            ConfigPathPrompt configPathPrompt) {
        this.configReader = configReader;
        this.printScript = printScript;
        this.progress = progress;
        this.configPathPrompt = configPathPrompt;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    int run(String[] args) {
        return new CommandLine(this).execute(args);
    }

    @Override
    public Integer call() {
        new CommandLine(this).usage(System.err);
        return 2;
    }

    @Command(name = "execute", mixinStandardHelpOptions = true)
    static final class ExecuteCommand implements Callable<Integer> {
        @CommandLine.ParentCommand
        private App app;

        @Option(names = {"-s", "--source"}, required = true)
        private Path sourceFile;

        @Option(names = {"-v", "--version"}, required = true)
        private String version;

        @Override
        public Integer call() throws IOException {
            CommandResult<?> result = app.printScript.execute(
                    Files.newBufferedReader(sourceFile), LanguageVersion.parse(version), System.out::println, app.progress);
            if (!result.isSuccess()) return app.printDiagnostics(result.diagnostics());
            return 0;
        }
    }

    @Command(name = "format", mixinStandardHelpOptions = true)
    static final class FormatCommand implements Callable<Integer> {
        @CommandLine.ParentCommand
        private App app;

        @Option(names = {"-s", "--source"}, required = true)
        private Path sourceFile;

        @Option(names = {"-v", "--version"}, required = true)
        private String version;

        @Option(names = {"-c", "--config"})
        private Path configFile;

        @Override
        public Integer call() throws IOException {
            Path resolvedConfigFile = app.resolveConfigFile(configFile);
            CommandResult<Void> result = app.printScript.format(
                    Files.newBufferedReader(sourceFile),
                    LanguageVersion.parse(version),
                    app.configReader.readFormatterConfig(resolvedConfigFile),
                    System.out,
                    app.progress);
            if (!result.isSuccess()) return app.printDiagnostics(result.diagnostics());
            return 0;
        }
    }

    @Command(name = "analyze", mixinStandardHelpOptions = true)
    static final class AnalyzeCommand implements Callable<Integer> {
        @CommandLine.ParentCommand
        private App app;

        @Option(names = {"-s", "--source"}, required = true)
        private Path sourceFile;

        @Option(names = {"-v", "--version"}, required = true)
        private String version;

        @Option(names = {"-c", "--config"})
        private Path configFile;

        @Override
        public Integer call() throws IOException {
            Path resolvedConfigFile = app.resolveConfigFile(configFile);
            CommandResult<AnalysisResult> result = app.printScript.analyze(
                    Files.newBufferedReader(sourceFile),
                    LanguageVersion.parse(version),
                    app.configReader.readAnalyzerConfig(resolvedConfigFile),
                    app::printDiagnostic,
                    app.progress);
            if (!result.isSuccess()) return app.printDiagnostics(result.diagnostics());
            return result.value().errorCount() == 0 ? 0 : 1;
        }
    }

    @Command(name = "validate", mixinStandardHelpOptions = true)
    static final class ValidateCommand implements Callable<Integer> {
        @CommandLine.ParentCommand
        private App app;

        @Option(names = {"-s", "--source"}, required = true)
        private Path sourceFile;

        @Option(names = {"-v", "--version"}, required = true)
        private String version;

        @Override
        public Integer call() throws IOException {
            CommandResult<Void> result =
                    app.printScript.validate(Files.newBufferedReader(sourceFile), LanguageVersion.parse(version), app.progress);
            if (!result.isSuccess()) return app.printDiagnostics(result.diagnostics());
            return 0;
        }
    }

    private Path resolveConfigFile(Path configFile) throws IOException {
        if (configFile != null) return configFile;
        return configPathPrompt.ask("Config file: ");
    }

    private static Path promptConfigPath(String prompt) throws IOException {
        System.err.print(prompt);
        String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (line == null || line.isBlank()) {
            throw new IOException("Config file is required");
        }
        return Path.of(line.trim());
    }

    private int printDiagnostics(List<Diagnostic> diagnostics) {
        for (Diagnostic diagnostic : diagnostics) {
            printDiagnostic(diagnostic);
        }
        return 1;
    }

    private void printDiagnostic(Diagnostic diagnostic) {
        var start = diagnostic.span().start();
        var end = diagnostic.span().end();
        System.err.printf(
                "%s %s at row %d column %d to row %d column %d: %s%n",
                diagnostic.severity(),
                diagnostic.phase(),
                start.row(),
                start.column(),
                end.row(),
                end.column(),
                diagnostic.message());
    }

    @FunctionalInterface
    interface ConfigPathPrompt {
        Path ask(String prompt) throws IOException;
    }
}
