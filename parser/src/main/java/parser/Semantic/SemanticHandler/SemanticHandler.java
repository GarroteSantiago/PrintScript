package parser.Semantic.SemanticHandler;

import parser.Semantic.SemanticVisitor.SemanticVisitor;
import parser.Semantic.Context.SemanticVisitorContext;
import common.nodes.Node;
import common.responses.Result;

public interface SemanticHandler<T> {

    Result handleSemantic(T node, SemanticVisitorContext context, SemanticVisitor visitor);
    boolean canHandle(Node node);

}
