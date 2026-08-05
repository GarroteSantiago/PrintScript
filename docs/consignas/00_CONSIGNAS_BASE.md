# PrintScript 01

## Desarrollo

Para este trabajo se requiere implementar tres herramientas para el lenguaje

- Interpretter: Puede ejecutar programas escritos en este lenguaje. 
- Formatter: Puede tomar como entrada un programa escrito en este lenguaje y formatearlo:
  - Indentación
  - Espacios en blanco
  - Saltos de línea
  - Etc.
- Static Code Analyzer: Pueda detectar errores y posibles problemas en el código fuente
  - Errores de sintaxis
  - Uso incorrecto de variables, funciones o estructuras
  - Etc.

### Orden de implementacion

1. Parser
  1. Lexer
  2. AST Builder
2. Tooling
  - Interpreter
  - Formatter
  - Static Code Analyzer

### Requisitos implicitos

- Flexibilidad
- Manejar codigos fuente mas extensos que la capacidad de la memoria (Pull no Push)
