package org.printscript.formatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.printscript.tokens.SyntaxToken;
import org.printscript.tokens.TokenType;

public final class PrintScriptFormatter {
    public Session newSession(FormatterConfig config) {
        return new Session(config);
    }

    public String format(ProgramSyntax program, FormatterConfig config) {
        Session session = newSession(config);
        StringBuilder out = new StringBuilder();
        for (StatementSyntax statement : program.statements()) {
            out.append(session.format(statement));
        }
        out.append(session.finish(program.eof()));
        return out.toString();
    }

    public final class Session {
        private final FormatterConfig config;
        private SyntaxToken previous;

        private Session(FormatterConfig config) {
            this.config = config;
        }

        public String format(StatementSyntax statement) {
            StringBuilder out = new StringBuilder();
            try {
                format(statement, out);
            } catch (IOException exception) {
                throw new IllegalStateException("StringBuilder append failed", exception);
            }
            return out.toString();
        }

        public void format(StatementSyntax statement, Appendable out) throws IOException {
            for (SyntaxToken token : flatten(statement)) {
                out.append(rewriteLeadingTrivia(previous, token, config));
                out.append(token.text());
                previous = token;
            }
        }

        public String finish(SyntaxToken eof) {
            return rewriteTrailingTrivia(previous, eof, config);
        }

        public void finish(SyntaxToken eof, Appendable out) throws IOException {
            out.append(finish(eof));
        }
    }

    private List<SyntaxToken> flatten(StatementSyntax statement) {
        List<SyntaxToken> tokens = new ArrayList<>();
        addStatement(statement, tokens);
        return List.copyOf(tokens);
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
                for (ExpressionSyntax argument : call.arguments()) {
                    addExpression(argument, tokens);
                }
                tokens.add(call.rightParen());
            }
        }
    }

    private String rewriteLeadingTrivia(SyntaxToken previous, SyntaxToken token, FormatterConfig config) {
        if (previous == null) {
            return token.leadingTrivia();
        }
        if (containsComment(token.leadingTrivia())) {
            if (previous.type() == TokenType.SEMICOLON) {
                return rewriteWhitespaceBeforeFirstComment(
                        token.leadingTrivia(), " ".repeat(config.spacesAfterSemicolon()));
            }
            return token.leadingTrivia();
        }
        if (startsPrintln(token) && previous.type() == TokenType.SEMICOLON) {
            return "\n".repeat(config.blankLinesBeforePrintln() + 1);
        }
        return switch (token.type()) {
            case SEMICOLON -> " ".repeat(config.spacesBeforeSemicolon());
            case EQUAL -> " ".repeat(config.spacesAroundAssignment());
            case PLUS, MINUS, STAR, SLASH -> " ".repeat(config.spacesAroundOperators());
            case COLON, RIGHT_PAREN, LEFT_PAREN -> "";
            default -> switch (previous.type()) {
                case SEMICOLON -> "\n" + " ".repeat(config.spacesAfterSemicolon());
                case EQUAL -> " ".repeat(config.spacesAroundAssignment());
                case PLUS, MINUS, STAR, SLASH -> " ".repeat(config.spacesAroundOperators());
                case COLON -> " ";
                case LEFT_PAREN -> "";
                default -> hasLineBreak(token.leadingTrivia()) ? token.leadingTrivia() : " ";
            };
        };
    }

    private String rewriteTrailingTrivia(SyntaxToken previous, SyntaxToken eof, FormatterConfig config) {
        if (previous == null || containsComment(eof.leadingTrivia())) {
            return eof.leadingTrivia();
        }
        if (previous.type() == TokenType.SEMICOLON && !hasLineBreak(eof.leadingTrivia())) {
            return "\n" + " ".repeat(config.spacesAfterSemicolon());
        }
        return eof.leadingTrivia();
    }

    private boolean startsPrintln(SyntaxToken token) {
        return token.type() == TokenType.IDENTIFIER && token.semanticLexeme().equals("println");
    }

    private boolean containsComment(String trivia) {
        return trivia.indexOf('#') >= 0;
    }

    private boolean hasLineBreak(String trivia) {
        return trivia.indexOf('\n') >= 0;
    }

    private String rewriteWhitespaceBeforeFirstComment(String trivia, String replacement) {
        int comment = trivia.indexOf('#');
        if (comment < 0)
            return trivia;
        return replacement + trivia.substring(comment);
    }
}
