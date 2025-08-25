package parser.Semantic;

import parser.Semantic.Context.SemanticVisitorContext;
import parser.Semantic.SemanticHandler.SemanticHandler;
import parser.Semantic.SemanticVisitor.SemanticVisitor;
import common.nodes.Node;
import common.responses.IncorrectResult;
import common.responses.Result;

import java.util.Map;


public record SemanticAnalyzer(SemanticVisitorContext context,
                               Map<Class<?>, SemanticHandler<?>> handlerMap, Node root){
    public Result analyze(){
        if(context == null || handlerMap == null || root == null){
            return new IncorrectResult("Variables are null.");
        }
        return root.accept(new SemanticVisitor(context,handlerMap));
    }
}
