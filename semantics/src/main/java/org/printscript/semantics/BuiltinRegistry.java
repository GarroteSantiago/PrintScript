package org.printscript.semantics;

import java.util.Map;
import java.util.Optional;
import org.printscript.syntax.TypeName;

public final class BuiltinRegistry {
  private static final TypeName UNIT = null;
  private final Map<String, BuiltinSignature> signatures;

  private BuiltinRegistry(Map<String, BuiltinSignature> signatures) {
    this.signatures = Map.copyOf(signatures);
  }

  public static BuiltinRegistry v1() {
    return new BuiltinRegistry(
        Map.of(
            "println", new BuiltinSignature("println", java.util.List.of(TypeName.STRING), UNIT)));
  }

  public Optional<BuiltinSignature> find(String name) {
    return Optional.ofNullable(signatures.get(name));
  }
}
