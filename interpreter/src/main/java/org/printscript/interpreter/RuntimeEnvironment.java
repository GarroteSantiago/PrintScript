package org.printscript.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record RuntimeEnvironment(Map<String, RuntimeValue> values) {
  public RuntimeEnvironment {
    values = Map.copyOf(values);
  }

  public static RuntimeEnvironment empty() {
    return new RuntimeEnvironment(Map.of());
  }

  public Optional<RuntimeValue> find(String name) {
    return Optional.ofNullable(values.get(name));
  }

  public RuntimeEnvironment put(String name, RuntimeValue value) {
    Map<String, RuntimeValue> next = new HashMap<>(values);
    next.put(name, value);
    return new RuntimeEnvironment(next);
  }
}
