package parser.Semantic.SemanticRules.RulesCheck;

import parser.Semantic.RulesFactory.RulesEngine;
import common.nodes.Node;
import common.responses.CorrectResult;
import common.responses.Result;

public record CheckSemanticRules(RulesEngine semanticRules) {

    public Result checkSemanticRules(Result left, Result right, Node operator){
        Object leftLiteral =  ((CorrectResult<?>) left).newObject();
        Object rightLiteral = ((CorrectResult<?>) right).newObject();
        return semanticRules.checkRules((Node) leftLiteral,(Node) rightLiteral,operator);
    }
}
