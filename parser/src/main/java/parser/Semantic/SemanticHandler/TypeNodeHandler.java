package parser.Semantic.SemanticHandler;

import common.nodes.declaration.TypeNode;
import parser.Semantic.SemanticVisitor.SemanticVisitor;
import parser.Semantic.Context.SemanticVisitorContext;

import common.nodes.Node;
import common.responses.CorrectResult;
import common.responses.Result;

public class TypeNodeHandler implements SemanticHandler<TypeNode> {
    @Override
    public Result handleSemantic(TypeNode node, SemanticVisitorContext context, SemanticVisitor visitor) {
        return new CorrectResult<>(node);
    }

    @Override
    public boolean canHandle(Node node) {
        return node instanceof TypeNode;
    }
}
