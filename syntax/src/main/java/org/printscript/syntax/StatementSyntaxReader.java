package org.printscript.syntax;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.printscript.common.Diagnostic;
import org.printscript.common.Phase;
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
import org.printscript.syntax.tokens.Token;
import org.printscript.syntax.lexer.Lexer;

public final class StatementSyntaxReader {
    private final Lexer lexer;
    private Token current;
    private Token next;
    private Token previous;

    public StatementSyntaxReader(String source) {
        this(new StringReader(source));
    }

    public StatementSyntaxReader(Reader reader) {
        this.lexer = new Lexer(reader);
        this.current = lexer.next();
        this.next = lexer.next();
    }

    public boolean hasNext() {
        return !check(TokenType.EOF);
    }

    public StatementSyntax next() {
        if (!hasNext()) {
            throw error(current, "Expected statement");
        }
        return statement();
    }

    public SyntaxToken eof() {
        return syntax(current);
    }

    private StatementSyntax statement() {
        if (match(TokenType.LET)) {
            return variableDeclaration(previous);
        }
        if (check(TokenType.IDENTIFIER) && checkNext(TokenType.EQUAL)) {
            return assignment();
        }
        ExpressionSyntax expression = expression();
        Token semicolon = consume(TokenType.SEMICOLON, "Expected ';' after statement");
        return new ExpressionStatementSyntax(expression, syntax(semicolon));
    }

    private StatementSyntax variableDeclaration(Token let) {
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name");
        Token colon = consume(TokenType.COLON, "Expected ':' after variable name");
        Token type = consume(TokenType.TYPE, "Expected type annotation");
        Token equals = consume(TokenType.EQUAL, "Expected '=' after type annotation");
        ExpressionSyntax initializer = expression();
        Token semicolon = consume(TokenType.SEMICOLON, "Expected ';' after declaration");
        return new VariableDeclarationSyntax(
                syntax(let), syntax(name), syntax(colon), syntax(type), syntax(equals), initializer, syntax(semicolon));
    }

    private StatementSyntax assignment() {
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name");
        Token equals = consume(TokenType.EQUAL, "Expected '=' after variable name");
        ExpressionSyntax value = expression();
        Token semicolon = consume(TokenType.SEMICOLON, "Expected ';' after assignment");
        return new AssignmentSyntax(syntax(name), syntax(equals), value, syntax(semicolon));
    }

    private ExpressionSyntax expression() {
        return addition();
    }

    private ExpressionSyntax addition() {
        ExpressionSyntax expression = multiplication();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous;
            ExpressionSyntax right = multiplication();
            expression = new BinaryExpressionSyntax(expression, syntax(operator), right);
        }
        return expression;
    }

    private ExpressionSyntax multiplication() {
        ExpressionSyntax expression = primary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous;
            ExpressionSyntax right = primary();
            expression = new BinaryExpressionSyntax(expression, syntax(operator), right);
        }
        return expression;
    }

    private ExpressionSyntax primary() {
        if (match(TokenType.NUMBER)) {
            return new LiteralExpressionSyntax(syntax(previous), TypeName.NUMBER);
        }
        if (match(TokenType.STRING)) {
            return new LiteralExpressionSyntax(syntax(previous), TypeName.STRING);
        }
        if (match(TokenType.IDENTIFIER)) {
            Token identifier = previous;
            if (match(TokenType.LEFT_PAREN)) {
                Token leftParen = previous;
                List<ExpressionSyntax> arguments = new ArrayList<>();
                if (!check(TokenType.RIGHT_PAREN)) {
                    arguments.add(expression());
                }
                Token rightParen = consume(TokenType.RIGHT_PAREN, "Expected ')' after call arguments");
                return new CallExpressionSyntax(syntax(identifier), syntax(leftParen), arguments, syntax(rightParen));
            }
            return new IdentifierExpressionSyntax(syntax(identifier));
        }
        throw error(current, "Expected expression");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();
        throw error(current, message);
    }

    private boolean check(TokenType type) {
        return current.type() == type;
    }

    private boolean checkNext(TokenType type) {
        return next.type() == type;
    }

    private Token advance() {
        previous = current;
        current = next;
        next = lexer.next();
        return previous;
    }

    private SyntaxToken syntax(Token token) {
        return SyntaxToken.from(token);
    }

    private SyntaxException error(Token token, String message) {
        return new SyntaxException(Diagnostic.error(Phase.SYNTAX, message, token.span()));
    }
}
