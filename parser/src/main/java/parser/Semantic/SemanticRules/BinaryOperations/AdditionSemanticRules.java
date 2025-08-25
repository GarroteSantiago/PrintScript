package parser.Semantic.SemanticRules.BinaryOperations;

import parser.Semantic.SemanticRules.SemanticRulesInterface;
import common.nodes.Node;
import common.nodes.expression.binary.AdditionNode;
import common.nodes.expression.literal.LiteralNode;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;

public class AdditionSemanticRules implements SemanticRulesInterface {

    @Override
    public boolean match(Node operator) {
        return operator instanceof AdditionNode;
    }

    @Override
    public Result checkRules(Node leftLiteral, Node rightLiteral) {
        if(!(leftLiteral instanceof LiteralNode left) || !(rightLiteral instanceof LiteralNode right)){
            return new IncorrectResult("Addition operation must be between two literals");
        }
        return new CorrectResult<>(left.value() + right.value());
    }
}
