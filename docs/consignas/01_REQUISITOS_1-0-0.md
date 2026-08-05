# Requisitos Version 1.0.0

- Declaracion de variables
- Asignacion de variables ya declaradas
- Soporte solo de tipos basicos
  - "number"
  - "string"
- string puede ser con "" o ''
- Todas las sentencias se terminan con ";"
- Las variables y literales de tipo "number" deben soportar operaciones aritmeticas binarias:
  - suma
  - resta
  - multiplicacion
  - division
- "number" incluye enteros y decimales
- concatenacion de variables y literales de tipo "string"
  - con el simbolo +
  - Esto incluye la concatenacion de "number" y "string"
- "println(text: string)" -> una expresion que imprime text y luego un salto de linea

## Interpreter

```PrintScript
let name: string = "Joe";
let lastName: string = "Doe";

println(name + " " + lastName); # Salida esperada = "Joe Doe"

let a = 12;
let b = 4;
let c = a / b;

println("Result: " + c); # Salida esperada = "Result: 3"

let d = 12;
let e = 4;
d = d / e;

println("Result: " + d); # Salida esperada = "Result: 3"
```

## Formatter

- Configurable
- Espacios
  - Antes de los ";", 0 o 1
  - Despues de los ";", 0 o 1
  - Antes y Despues del "=", 0 o 1
  - Entre tokens, 1 y solo 1
  - Antes y Despues de un operador, 1
- Salto de linea
  - Antes de llamar a "println"; 0, 1 o 2
  - Luego de ";". 1

## Static Code Analyzer

- Debe Reportar incumplimientos y la posicion exacta de estos.
- Configurable
- Formato de los identificadores (snake_case o CamelCase)
- "println" solo puede llamarse con identificadores o literales, no expresiones

## CLI

- Argumentos
  - Comandos
    - Validation
    - Execution
    - Formatting
    - Analyzing
  - Archivo fuente
  - Version del archivo a interpretar (Unicamente 1.0 por ahora)
  - Argumentos particulares de los comandos, como la configuracion
- Frente a errores mostrar un mensaje y su ubicacion (column, start y end row) al usuario
- Al hacer parsing se muestra progreso al usuario en pantalla

## Requisitos

- Modulos
- Java/Kotlin/Scala
- Tests automaticos y escalables
- Herramientas a presentar en clase
- Criterios de DisSis


## Entregables

- Diagrama de componentes
- Explicacion de responsabilidad de modulos y su interaccion
- Codigo fuente que cumpla con los requisitos
- Tests automaticos para cada componente del sistema
