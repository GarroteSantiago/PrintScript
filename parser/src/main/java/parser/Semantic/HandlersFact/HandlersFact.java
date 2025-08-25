package parser.Semantic.HandlersFact;

import common.nodes.declaration.AscriptionNode;
import common.nodes.declaration.IdentifierNode;
import common.nodes.declaration.TypeNode;
import common.nodes.expression.binary.AdditionNode;
import common.nodes.expression.literal.LiteralNode;
import common.nodes.statements.LetStatementNode;
import common.nodes.statements.PrintStatementNode;
import parser.Semantic.SemanticHandler.*;

import java.util.HashMap;
import java.util.Map;

public class HandlersFact {

    public Map<Class<?>, SemanticHandler<?>>  createMap() {
        Map<Class<?>, SemanticHandler<?>> handlers = new HashMap<>();

        handlers.put(AdditionNode.class, new AdditionExpressionNodeHandler());
        handlers.put(AscriptionNode.class, new DeclarationNodeHandler());
        handlers.put(LetStatementNode.class, new LetStatementNodeHandler());
        handlers.put(LiteralNode.class, new LiteralNodeHandler());
        handlers.put(TypeNode.class, new TypeNodeHandler());
        handlers.put(IdentifierNode.class, new IdentifierNodeHandler());
        handlers.put(PrintStatementNode.class, new PrintStatementNodeHandler());

        return handlers;
    }

}
