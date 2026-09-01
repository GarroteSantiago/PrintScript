package org.printscript.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.printscript.analyzer.AnalyzerConfig;
import org.printscript.analyzer.NamingStyle;
import org.printscript.formatter.FormatterConfig;
import org.printscript.formatter.FormatterConfigProvider;

public final class TomlPrintScriptConfigReader implements PrintScriptConfigReader {
  private static final TypeReference<Map<String, Map<String, Object>>> CONFIG_TYPE =
      new TypeReference<>() {};
  private static final boolean DEFAULT_RESTRICT_READ_INPUT = true;
  private final TomlMapper mapper;

  public TomlPrintScriptConfigReader() {
    this(new TomlMapper());
  }

  TomlPrintScriptConfigReader(TomlMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public FormatterConfigProvider readFormatterConfig(Path path) throws IOException {
    Map<String, Object> values = readSection(path, "formatter");
    if (values == null) {
      throw new IllegalArgumentException("Missing [formatter] section in " + path);
    }
    return new FormatterConfig(
        optionalInt(values, "spaces_before_semicolon"),
        optionalInt(values, "spaces_after_semicolon"),
        optionalInt(values, "spaces_around_assignment"),
        optionalInt(values, "spaces_around_operators"),
        optionalInt(values, "blank_lines_before_println"),
        optionalInt(values, "block_indent_spaces"),
        optionalInt(values, "spaces_before_colon"),
        optionalInt(values, "spaces_after_colon"),
        optionalBool(values, "mandatory_single_space_separation"),
        optionalBool(values, "mandatory_line_break_after_statement"),
        optionalBool(values, "if_brace_on_same_line"));
  }

  @Override
  public AnalyzerConfig readAnalyzerConfig(Path path) throws IOException {
    Map<String, Object> values = readSection(path, "analyzer");
    if (values == null) {
      throw new IllegalArgumentException("Missing [analyzer] section in " + path);
    }
    return new AnalyzerConfig(
        NamingStyle.valueOf(required(values, "naming_style")),
        true,
        Boolean.parseBoolean(required(values, "restrict_println_to_simple_arguments")),
        optionalBoolean(
            values, "restrict_readinput_to_simple_arguments", DEFAULT_RESTRICT_READ_INPUT));
  }

  private Map<String, Object> readSection(Path path, String section) throws IOException {
    return mapper.readValue(path.toFile(), CONFIG_TYPE).get(section);
  }

  private Optional<Integer> optionalInt(Map<String, Object> values, String key) {
    Object value = values.get(key);
    if (value == null) return Optional.empty();
    if (value instanceof Number number) return Optional.of(number.intValue());
    return Optional.of(Integer.parseInt(value.toString()));
  }

  private boolean optionalBoolean(Map<String, Object> values, String key, boolean defaultValue) {
    Object value = values.get(key);
    if (value == null) return defaultValue;
    return Boolean.parseBoolean(value.toString());
  }

  private Optional<Boolean> optionalBool(Map<String, Object> values, String key) {
    Object value = values.get(key);
    if (value == null) return Optional.empty();
    return Optional.of(Boolean.parseBoolean(value.toString()));
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
