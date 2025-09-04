package parser.ast.builders.print;

import common.Node;
import common.TokenInterface;
import expression.ExpressionNode;
import parser.ast.builders.expression.ExpressionBuilder;
import results.CorrectResult;
import results.IncorrectResult;
import results.Result;
import factories.NodeFactory;
import factories.tokens.TokenFactory;
import parser.ast.builders.ASTreeBuilderInterface;
import parser.factories.AstBuilderFactory;
import parser.factories.AstBuilderFactoryInterface;
import statements.PrintStatementNode;
import stream.TokenStreamInterface;

public record PrintBuilder(ASTreeBuilderInterface nextBuilder) implements ASTreeBuilderInterface {
    private static final AstBuilderFactoryInterface builderFactory = new AstBuilderFactory();
    private static final TokenInterface printTemplate = new TokenFactory().createPrintlnKeywordToken();
    private static final TokenInterface leftParenthesisTemplate = new TokenFactory().createLeftParenthesisToken();
    private static final TokenInterface rightParenthesisTemplate = new TokenFactory().createRightParenthesisToken();
    private static final TokenInterface eolTemplate = new TokenFactory().createEndOfLineToken();

    public PrintBuilder() {
        this(builderFactory.createFinalBuilder());
    }

    @Override
    public Boolean canBuild(TokenStreamInterface tokenStream) {
        Result<TokenInterface> peekResult = tokenStream.peek();
        if (!peekResult.isSuccessful()) {
            return false;
        }
        TokenInterface token = peekResult.result();
        return token.equals(printTemplate);
    }

    @Override
    public Result<? extends Node> build(TokenStreamInterface tokenStream) {
        if (!canBuild(tokenStream)) {
            return nextBuilder().build(tokenStream);
        }

        Result<TokenInterface> consumeResult = tokenStream.consume(printTemplate);
        if (!consumeResult.isSuccessful()) {
            return new IncorrectResult<>(consumeResult.errorMessage());
        }
        PrintStatementNode root = (PrintStatementNode) new NodeFactory().createPrintlnStatementNode();

        consumeResult = tokenStream.consume(leftParenthesisTemplate);
        if (!consumeResult.isSuccessful()) {
            return new IncorrectResult<>(consumeResult.errorMessage());
        }

        Result<ExpressionNode> buildExpressionResult =
                ((ExpressionBuilder) builderFactory.createExpressionBuilder()).build(tokenStream);
        if (!buildExpressionResult.isSuccessful()) {
            return buildExpressionResult;
        }
        ExpressionNode expression = buildExpressionResult.result();
        root.setExpression(expression);

        consumeResult = tokenStream.consume(rightParenthesisTemplate);
        if (!consumeResult.isSuccessful()) {
            return new IncorrectResult<>(consumeResult.errorMessage());
        }
        consumeResult = tokenStream.consume(eolTemplate);
        if (!consumeResult.isSuccessful()) {
            return new IncorrectResult<>(consumeResult.errorMessage());
        }
        return new CorrectResult<>(root);
    }
}
