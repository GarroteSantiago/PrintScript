package parser.ast.builders.print;

import common.factories.nodes.NodeFactory;
import common.factories.tokens.TokenFactory;
import common.nodes.Node;
import common.nodes.statements.PrintStatementNode;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;
import common.tokens.TokenInterface;
import common.tokens.stream.TokenStreamInterface;
import parser.ast.builders.ASTreeBuilderInterface;
import parser.factories.AstBuilderFactory;
import parser.factories.AstBuilderFactoryInterface;

public record PrintBuilder(ASTreeBuilderInterface nextBuilder) implements ASTreeBuilderInterface {
    private static final AstBuilderFactoryInterface builderFactory = new AstBuilderFactory();
    private static final TokenInterface printTemplate = new TokenFactory().createPrintlnKeywordToken();
    private static final TokenInterface leftParenthesisTemplate = new TokenFactory().createLeftParenthesisToken();
    private static final TokenInterface rightParenthesisTemplate = new TokenFactory().createRightParenthesisToken();
    private static final TokenInterface eolTemplate = new TokenFactory().createEndOfLineToken();

    public PrintBuilder(){
        this(builderFactory.createFinalBuilder());
    }

    @Override
    public Boolean canBuild(TokenStreamInterface tokenStream) {
        Result peekResult = tokenStream.peek();
        if (!peekResult.isSuccessful()){
            return false;
        }
        TokenInterface token = ((CorrectResult<TokenInterface>) peekResult).newObject();
        return token.equals(printTemplate);
    }

    @Override
    public Result build(TokenStreamInterface tokenStream) {
        if (!canBuild(tokenStream)) return nextBuilder().build(tokenStream);

        if (!tokenStream.consume(printTemplate).isSuccessful()) return new IncorrectResult("Cannot consume print token");
        PrintStatementNode root = (PrintStatementNode) new NodeFactory().createPrintlnStatementNode();

        if (!tokenStream.consume(leftParenthesisTemplate).isSuccessful()) return new IncorrectResult("Cannot consume left parenthesis token");

        Result buildExpressionResult = builderFactory.createBinaryExpressionBuilder().build(tokenStream);
        if (!buildExpressionResult.isSuccessful()){return buildExpressionResult;}
        Node expression = ((CorrectResult<Node>) buildExpressionResult).newObject();
        root.setExpression(expression);

        if (!tokenStream.consume(rightParenthesisTemplate).isSuccessful()) return new IncorrectResult("Cannot consume right parenthesis token");

        if (!tokenStream.consume(eolTemplate).isSuccessful()) return new IncorrectResult("Missing EOL token.");

        return new CorrectResult<>(root);
    }
}
