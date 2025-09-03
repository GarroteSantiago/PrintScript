package linter.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import linter.api.AnalyzerConfig;

public final class ConfigLoader {
  private ConfigLoader() {}

  public static AnalyzerConfig load(Path path) {
    try {
      String lower = path.toString().toLowerCase();
      ObjectMapper mapper =
          lower.endsWith(".yml") || lower.endsWith(".yaml")
              ? new ObjectMapper(new YAMLFactory())
              : new ObjectMapper();
      return mapper.readValue(Files.readAllBytes(path), AnalyzerConfig.class);
    } catch (Exception e) {
      // fallback a defaults si no hay config/archivo inválido
      return AnalyzerConfig.defaults();
    }
  }
}
