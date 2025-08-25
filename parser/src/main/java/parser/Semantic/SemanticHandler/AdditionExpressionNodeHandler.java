package parser.Semantic.SemanticHandler;

import parser.Semantic.SemanticVisitor.SemanticVisitor;
import parser.Semantic.Context.SemanticVisitorContext;
import common.nodes.Node;
import common.nodes.expression.binary.AdditionNode;
import common.responses.Result;

public class AdditionExpressionNodeHandler implements SemanticHandler<AdditionNode>{
    @Override
    public Result handleSemantic(AdditionNode node, SemanticVisitorContext context, SemanticVisitor visitor) {
        return new BinaryExpressionHandler().handleSemantic(node, context, visitor);
    }

    @Override
    public boolean canHandle(Node node) {
        return node instanceof AdditionNode;
    }
}
