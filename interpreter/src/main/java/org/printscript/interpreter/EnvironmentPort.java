package org.printscript.interpreter;

import java.util.Optional;

@FunctionalInterface
public interface EnvironmentPort {
  Optional<String> get(String name);
}
