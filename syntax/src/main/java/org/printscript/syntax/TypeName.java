package org.printscript.syntax;

public enum TypeName {
    NUMBER,
    STRING;

    public static TypeName fromLexeme(String lexeme) {
        return switch (lexeme) {
            case "number" -> NUMBER;
            case "string" -> STRING;
            default -> throw new IllegalArgumentException("Unknown type: " + lexeme);
        };
    }
}
