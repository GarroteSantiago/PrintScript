package org.printscript.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.printscript.syntax.nodes.ProgramSyntax;
import org.printscript.syntax.nodes.expressions.BinaryExpressionSyntax;
import org.printscript.syntax.nodes.expressions.CallExpressionSyntax;
import org.printscript.syntax.nodes.expressions.ExpressionSyntax;
import org.printscript.syntax.nodes.expressions.IdentifierExpressionSyntax;
import org.printscript.syntax.nodes.expressions.LiteralExpressionSyntax;
import org.printscript.syntax.nodes.statements.AssignmentSyntax;
import org.printscript.syntax.nodes.statements.ExpressionStatementSyntax;
import org.printscript.syntax.nodes.statements.StatementSyntax;
import org.printscript.syntax.nodes.statements.VariableDeclarationSyntax;
import org.printscript.syntax.tokens.SyntaxToken;

class SyntaxTreeBuilderTest {
    @Test
    void preservesEverySourceCharacterAsTriviaOrTokenText() {
        String source = """
                # file comment
                let   name:string='Ada'; # inline comment

                println(name);\s\s
                """;

        StringBuilder rebuilt = new StringBuilder();
        for (SyntaxToken token : flatten(new SyntaxTreeBuilder(source).buildProgram())) {
            rebuilt.append(token.leadingTrivia()).append(token.text());
        }

        assertEquals(source, rebuilt.toString());
    }

    private List<SyntaxToken> flatten(ProgramSyntax program) {
        List<SyntaxToken> tokens = new ArrayList<>();
        for (StatementSyntax statement : program.statements())
            addStatement(statement, tokens);
        tokens.add(program.eof());
        return tokens;
    }

    private void addStatement(StatementSyntax statement, List<SyntaxToken> tokens) {
        switch (statement) {
            case VariableDeclarationSyntax declaration -> {
                tokens.add(declaration.letKeyword());
                tokens.add(declaration.identifier());
                tokens.add(declaration.colon());
                tokens.add(declaration.type());
                tokens.add(declaration.equals());
                addExpression(declaration.initializer(), tokens);
                tokens.add(declaration.semicolon());
            }
            case AssignmentSyntax assignment -> {
                tokens.add(assignment.identifier());
                tokens.add(assignment.equals());
                addExpression(assignment.value(), tokens);
                tokens.add(assignment.semicolon());
            }
            case ExpressionStatementSyntax expressionStatement -> {
                addExpression(expressionStatement.expression(), tokens);
                tokens.add(expressionStatement.semicolon());
            }
        }
    }

    private void addExpression(ExpressionSyntax expression, List<SyntaxToken> tokens) {
        switch (expression) {
            case LiteralExpressionSyntax literal -> tokens.add(literal.literal());
            case IdentifierExpressionSyntax identifier -> tokens.add(identifier.identifier());
            case BinaryExpressionSyntax binary -> {
                addExpression(binary.left(), tokens);
                tokens.add(binary.operator());
                addExpression(binary.right(), tokens);
            }
            case CallExpressionSyntax call -> {
                tokens.add(call.callee());
                tokens.add(call.leftParen());
                for (ExpressionSyntax argument : call.arguments())
                    addExpression(argument, tokens);
                tokens.add(call.rightParen());
            }
        }
    }
}
