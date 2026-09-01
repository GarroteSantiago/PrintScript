package org.printscript.syntax;

@FunctionalInterface
public interface TypeAnnotationTable {
  TypeName resolve(String lexeme);

  static TypeAnnotationTable v1() {
    return lexeme ->
        switch (lexeme) {
          case "number" -> TypeName.NUMBER;
          case "string" -> TypeName.STRING;
          default -> throw new IllegalArgumentException("Unknown type: " + lexeme);
        };
  }
}
