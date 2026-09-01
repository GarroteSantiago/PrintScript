package org.printscript.semantics;

import java.util.Optional;
import org.printscript.syntax.TypeName;
import org.printscript.tokens.TokenType;

@FunctionalInterface
public interface BinaryOperatorRules {
  Optional<TypeName> resultType(TokenType operator, TypeName left, TypeName right);

  static BinaryOperatorRules v1() {
    return (operator, left, right) -> {
      if (operator == TokenType.PLUS && (left == TypeName.STRING || right == TypeName.STRING)) {
        return Optional.of(TypeName.STRING);
      }
      if (left == TypeName.NUMBER && right == TypeName.NUMBER) {
        return Optional.of(TypeName.NUMBER);
      }
      return Optional.empty();
    };
  }
}
