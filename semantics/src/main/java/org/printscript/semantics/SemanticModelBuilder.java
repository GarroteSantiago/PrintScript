package org.printscript.semantics;

import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;

public final class SemanticModelBuilder {
    private final BuiltinRegistry builtins;

    public SemanticModelBuilder(BuiltinRegistry builtins) {
        this.builtins = builtins;
    }

    public SemanticModel build(ProgramSyntax program) {
        SemanticContext context = SemanticContext.empty(builtins);
        SemanticModel.Builder combined = SemanticModel.builder();
        for (StatementSyntax statement : program.statements()) {
            SemanticStatementResult result = context.validate(statement);
            result.diagnostics().forEach(combined::addDiagnostic);
            context = result.nextContext();
            if (!result.isSuccess())
                break;
        }
        return combined.build();
    }
}
