package parser.Semantic.SemanticHandler;

import parser.Semantic.Context.SemanticVisitorContext;
import parser.Semantic.SemanticVisitor.SemanticVisitor;
import common.nodes.Node;
import common.nodes.expression.literal.LiteralNode;
import common.nodes.statements.PrintStatementNode;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;

public class PrintStatementNodeHandler implements SemanticHandler<PrintStatementNode> {
    @Override
    public Result handleSemantic(PrintStatementNode node, SemanticVisitorContext context, SemanticVisitor visitor) {
        Object obj = ((CorrectResult<?>) node.expression()).newObject();
        Result resolved = visitor.dispatch(obj);
        Result result = context.semanticRules().checkSemanticRules(node.expression(),resolved,node);
        if(!result.isSuccessful()){
            return new IncorrectResult("Print statement must have a literal");
        }
        return context.variablesTable().getValue((LiteralNode)((CorrectResult<?>) resolved).newObject());
    }

    @Override
    public boolean canHandle(Node node) {
        return node instanceof PrintStatementNode;
    }
}
