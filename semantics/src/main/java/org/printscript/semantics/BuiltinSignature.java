package org.printscript.semantics;

import java.util.List;
import org.printscript.syntax.TypeName;

public record BuiltinSignature(String name, List<TypeName> parameterTypes, TypeName returnType) {
  public BuiltinSignature {
    parameterTypes = List.copyOf(parameterTypes);
  }
}
