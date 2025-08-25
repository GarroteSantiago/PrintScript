package parser.Semantic.SemanticHandler;

import parser.Semantic.SemanticVisitor.SemanticVisitor;
import parser.Semantic.Context.SemanticVisitorContext;
import common.nodes.Node;
import common.nodes.expression.literal.LiteralNode;
import common.responses.CorrectResult;
import common.responses.Result;

public class LiteralNodeHandler implements SemanticHandler<LiteralNode> {
    @Override
    public Result handleSemantic(LiteralNode node, SemanticVisitorContext context, SemanticVisitor visitor) {
        return new CorrectResult<>(node);
    }

    @Override
    public boolean canHandle(Node node) {
        return node instanceof LiteralNode;
    }
}
