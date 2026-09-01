package org.printscript.semantics;

import org.printscript.syntax.TypeAnnotationTable;
import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;

public final class SemanticModelBuilder {
  private final BuiltinRegistry builtins;
  private final TypeAnnotationTable typeAnnotations;
  private final BinaryOperatorRules binaryOperatorRules;

  public SemanticModelBuilder(BuiltinRegistry builtins) {
    this(builtins, TypeAnnotationTable.v1());
  }

  public SemanticModelBuilder(BuiltinRegistry builtins, TypeAnnotationTable typeAnnotations) {
    this(builtins, typeAnnotations, BinaryOperatorRules.v1());
  }

  public SemanticModelBuilder(
      BuiltinRegistry builtins,
      TypeAnnotationTable typeAnnotations,
      BinaryOperatorRules binaryOperatorRules) {
    this.builtins = builtins;
    this.typeAnnotations = typeAnnotations;
    this.binaryOperatorRules = binaryOperatorRules;
  }

  public SemanticModel build(ProgramSyntax program) {
    SemanticContext context = SemanticContext.empty(builtins, typeAnnotations, binaryOperatorRules);
    SemanticModel.Builder combined = SemanticModel.builder();
    for (StatementSyntax statement : program.statements()) {
      SemanticStatementResult result = context.validate(statement);
      result.diagnostics().forEach(combined::addDiagnostic);
      context = result.nextContext();
      if (!result.isSuccess()) break;
    }
    return combined.build();
  }
}
