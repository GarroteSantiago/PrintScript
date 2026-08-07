package org.printscript.semantics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.printscript.syntax.SyntaxTreeBuilder;

class SemanticModelBuilderTest {
    @Test
    void rejectsAssigningStringToNumberVariable() {
        var program = new SyntaxTreeBuilder("let total: number = \"no\";").buildProgram();

        var semanticModel = new SemanticModelBuilder(BuiltinRegistry.v1()).build(program);

        assertEquals(1, semanticModel.diagnostics().size());
        assertEquals("Cannot assign string to number", semanticModel.diagnostics().getFirst().message());
    }
}
