package parser.Semantic.RulesFactory;

import common.nodes.Node;
import common.nodes.expression.binary.BinaryExpression;
import common.nodes.expression.literal.LiteralNode;
import common.responses.CorrectResult;
import common.responses.Result;
import parser.Semantic.SemanticRules.SemanticRulesInterface;

import java.util.List;

public record RulesEngine(List<SemanticRulesInterface> semanticRulesInterface) {

    public Result checkRules(Node leftLiteral, Node rightLiteral, Node operator) {
        for(SemanticRulesInterface semanticRulesInterface : semanticRulesInterface){
            if (semanticRulesInterface.match(operator)){
                return semanticRulesInterface.checkRules(leftLiteral,rightLiteral);
            }
        }
        return new CorrectResult<>(null);
    }
}
