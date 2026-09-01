package org.printscript.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.expressions.BinaryExpressionSyntax;
import org.printscript.syntax.nodes.expressions.CallExpressionSyntax;
import org.printscript.syntax.nodes.statements.AssignmentSyntax;
import org.printscript.syntax.nodes.statements.ExpressionStatementSyntax;
import org.printscript.syntax.nodes.statements.VariableDeclarationSyntax;
import org.printscript.testkit.TestSources;
import org.printscript.tokens.TokenType;

class ParserTest {
    @Test
    void parsesDeclarationsAssignmentsCallsAndBinaryPrecedence() {
        String source = """
                let a: number = 12;
                let b: number = 4;
                a = a / b + 1;
                println("Result: " + a);
                """;

        ProgramSyntax program = TestSources.programOf(source);

        assertEquals(4, program.statements().size());
        assertInstanceOf(VariableDeclarationSyntax.class, program.statements().get(0));
        assertInstanceOf(AssignmentSyntax.class, program.statements().get(2));
        AssignmentSyntax assignment = (AssignmentSyntax) program.statements().get(2);
        BinaryExpressionSyntax plus = assertInstanceOf(BinaryExpressionSyntax.class, assignment.value());
        assertEquals(TokenType.PLUS, plus.operator().type());
        BinaryExpressionSyntax divide = assertInstanceOf(BinaryExpressionSyntax.class, plus.left());
        assertEquals(TokenType.SLASH, divide.operator().type());
        ExpressionStatementSyntax callStatement = assertInstanceOf(ExpressionStatementSyntax.class,
                program.statements().get(3));
        assertInstanceOf(CallExpressionSyntax.class, callStatement.expression());
    }
}
