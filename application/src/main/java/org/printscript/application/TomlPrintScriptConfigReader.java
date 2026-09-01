package org.printscript.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.analyzer.NamingStyle;
import org.printscript.formatter.FormatterConfig;

public final class TomlPrintScriptConfigReader implements PrintScriptConfigReader {
  private static final TypeReference<Map<String, Map<String, Object>>> CONFIG_TYPE =
      new TypeReference<>() {};
  private final TomlMapper mapper;

  public TomlPrintScriptConfigReader() {
    this(new TomlMapper());
  }

  TomlPrintScriptConfigReader(TomlMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public FormatterConfig readFormatterConfig(Path path) throws IOException {
    Map<String, Object> values = readSection(path, "formatter");
    if (values == null) {
      throw new IllegalArgumentException("Missing [formatter] section in " + path);
    }
    return new FormatterConfig(
        requiredInt(values, "spaces_before_semicolon"),
        requiredInt(values, "spaces_after_semicolon"),
        requiredInt(values, "spaces_around_assignment"),
        requiredInt(values, "spaces_around_operators"),
        requiredInt(values, "blank_lines_before_println"));
  }

  @Override
  public AnalyzerConfig readAnalyzerConfig(Path path) throws IOException {
    Map<String, Object> values = readSection(path, "analyzer");
    if (values == null) {
      throw new IllegalArgumentException("Missing [analyzer] section in " + path);
    }
    return new AnalyzerConfig(
        NamingStyle.valueOf(required(values, "naming_style")),
        Boolean.parseBoolean(required(values, "restrict_println_to_simple_arguments")));
  }

  private Map<String, Object> readSection(Path path, String section) throws IOException {
    return mapper.readValue(path.toFile(), CONFIG_TYPE).get(section);
  }

  private int requiredInt(Map<String, Object> values, String key) {
    Object value = requiredValue(values, key);
    if (value instanceof Number number) return number.intValue();
    return Integer.parseInt(value.toString());
  }

  private String required(Map<String, Object> values, String key) {
    return requiredValue(values, key).toString();
  }

  private Object requiredValue(Map<String, Object> values, String key) {
    Object value = values.get(key);
    if (value == null) {
      throw new IllegalArgumentException("Missing config key: " + key);
    }
    return value;
  }
}
