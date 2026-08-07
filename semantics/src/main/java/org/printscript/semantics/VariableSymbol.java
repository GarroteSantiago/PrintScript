package org.printscript.semantics;

import org.printscript.syntax.TypeName;
import org.printscript.syntax.VariableDeclarationSyntax;

public record VariableSymbol(String name, TypeName type, VariableDeclarationSyntax declaration) {}
