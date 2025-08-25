package parser.Semantic.SemanticHandler;

import parser.Semantic.SemanticVisitor.SemanticVisitor;
import parser.Semantic.Context.SemanticVisitorContext;
import common.nodes.Node;
import common.nodes.declaration.AscriptionNode;
import common.nodes.expression.literal.LiteralNode;
import common.nodes.statements.LetStatementNode;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;

public class LetStatementNodeHandler implements SemanticHandler<LetStatementNode> {

    @Override
    public Result handleSemantic(LetStatementNode node, SemanticVisitorContext context, SemanticVisitor visitor) {
        Result rightChild = node.expression();
        Result leftChild = node.ascription();
        if(!leftChild.isSuccessful()){
            return new IncorrectResult("Need DeclarationNode");
        }
        AscriptionNode declarationNode = (AscriptionNode) ((CorrectResult<?>) leftChild).newObject();
        if(!(rightChild.isSuccessful())){
            return context.variablesTable().addOnlyVariable(declarationNode);
        }
        Object obj = ((CorrectResult<?>) rightChild).newObject();
        Result resolved = visitor.dispatch(obj);

        Result typeCheck = context.semanticRules().checkSemanticRules(declarationNode.type(), resolved, node);
        if(!typeCheck.isSuccessful()){
            return typeCheck;
        }
        return context.variablesTable().addValue(declarationNode, (LiteralNode) obj);
    }

    @Override
    public boolean canHandle(Node node) {
        return node instanceof LetStatementNode;
    }
}
