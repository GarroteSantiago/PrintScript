package parser.ast.builders.let;

import common.Node;
import common.TokenInterface;
import declaration.AscriptionNode;
import expression.ExpressionNode;
import parser.ast.builders.ascription.AscriptionBuilder;
import parser.ast.builders.expression.ExpressionBuilder;
import results.CorrectResult;
import results.IncorrectResult;
import results.Result;
import factories.NodeFactory;
import factories.tokens.TokenFactory;
import parser.ast.builders.ASTreeBuilderInterface;
import parser.factories.AstBuilderFactory;
import parser.factories.AstBuilderFactoryInterface;
import statements.LetStatementNode;
import stream.TokenStreamInterface;

public record LetBuilder(ASTreeBuilderInterface nextBuilder) implements ASTreeBuilderInterface {
    final private static TokenInterface letTokenTemplate = new TokenFactory().createLetKeywordToken();
    final private static TokenInterface assignationTokenTemplate = new TokenFactory().createAssignationToken();
    final private static TokenInterface eolTokenTemplate = new TokenFactory().createEndOfLineToken();
    private static final AstBuilderFactoryInterface builderFactory = new AstBuilderFactory();


    public LetBuilder() {
        this(builderFactory.createFinalBuilder());
    }

    @Override
    public Boolean canBuild(TokenStreamInterface tokenStream) {
        Result<TokenInterface> peekResult = tokenStream.peek();
        if (!peekResult.isSuccessful()) return false;
        TokenInterface token = peekResult.result();
        return token.equals(letTokenTemplate);
    }

    @Override
    public Result<? extends Node> build(TokenStreamInterface tokenStream) {
        if (!canBuild(tokenStream)) {
            return nextBuilder().build(tokenStream);
        }

        if (!tokenStream.consume(letTokenTemplate).isSuccessful()) {
            return nextBuilder().build(tokenStream);
        }

        Result<AscriptionNode> buildAscritionResult =
                ((AscriptionBuilder) builderFactory.createAscriptionBuilder()).build(tokenStream);
        if (!buildAscritionResult.isSuccessful()) {
            return new IncorrectResult<>("Cannot build let ast.");
        }

        LetStatementNode letNode = (LetStatementNode) new NodeFactory().createLetStatementNode();
        letNode.setAscription(buildAscritionResult.result());

        if (tokenStream.consume(assignationTokenTemplate).isSuccessful()) {
            Result<ExpressionNode> buildLiteralResult =
                    ((ExpressionBuilder) builderFactory.createExpressionBuilder()).build(tokenStream);
            if (!buildLiteralResult.isSuccessful()) return buildLiteralResult;
            ExpressionNode expressionNode = buildLiteralResult.result();
            letNode.setExpression(expressionNode);
        }

        if (!tokenStream.consume(eolTokenTemplate).isSuccessful()){
            return nextBuilder().build(tokenStream);
        }

        return new CorrectResult<>(letNode);
    }
}
