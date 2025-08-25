package parser.Semantic.SemanticVisitor;

import common.nodes.declaration.TypeNode;
import parser.Semantic.SemanticHandler.SemanticHandler;
import parser.Semantic.Context.SemanticVisitorContext;
import common.nodes.NilNode;
import common.nodes.Node;
import common.nodes.declaration.AscriptionNode;
import common.nodes.declaration.IdentifierNode;
import common.nodes.expression.binary.AdditionNode;
import common.nodes.expression.literal.LiteralNode;
import common.nodes.statements.LetStatementNode;
import common.nodes.statements.PrintStatementNode;
import common.responses.IncorrectResult;
import common.responses.Result;
import common.visitor.VisitorInterface;

import java.util.Map;

public record SemanticVisitor(SemanticVisitorContext context, Map<Class<?>, SemanticHandler<?>> handlerMap) implements VisitorInterface {

    @Override
    public Result visit(LetStatementNode node) {
        return dispatch(node);
    }

    @Override
    public Result visit(PrintStatementNode node) {return dispatch(node);}

    @Override
    public Result visit(AdditionNode node){
        return dispatch(node);
    }

    @Override
    public Result visit(LiteralNode node) {
        return dispatch(node);
    }

    @Override
    public Result visit(IdentifierNode node) {
        return dispatch(node);
    }

    @Override
    public Result visit(AscriptionNode node) {
        return dispatch(node);
    }

    @Override
    public Result visit(TypeNode node) {
        return dispatch(node);
    }

    @Override
    public Result visit(NilNode node) {
        return dispatch(node);
    }


    public <T> Result dispatch(T node){
        for(Map.Entry<Class<?>, SemanticHandler<?>> entry : handlerMap.entrySet()){
            SemanticHandler<?> handler = entry.getValue();
            if(handler.canHandle((Node) node)){
                return ((SemanticHandler<Node>) handler).handleSemantic((Node) node, context,this);
            }
        }
        return new IncorrectResult("No handler found for node: " + node.getClass().getSimpleName());
    }

}
