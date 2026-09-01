package org.printscript.interpreter;

import java.math.BigDecimal;
import java.math.MathContext;
import org.printscript.tokens.TokenType;

@FunctionalInterface
public interface ArithmeticOperators {
  BigDecimal apply(TokenType operator, BigDecimal left, BigDecimal right);

  static ArithmeticOperators v1() {
    return (operator, left, right) ->
        switch (operator) {
          case PLUS -> left.add(right);
          case MINUS -> left.subtract(right);
          case STAR -> left.multiply(right);
          case SLASH -> {
            if (right.compareTo(BigDecimal.ZERO) == 0) {
              throw new ArithmeticException("Division by zero");
            }
            yield left.divide(right, MathContext.DECIMAL128).stripTrailingZeros();
          }
          default -> throw new IllegalStateException("Unsupported binary operator: " + operator);
        };
  }
}
