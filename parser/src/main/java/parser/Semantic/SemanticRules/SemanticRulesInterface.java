package parser.Semantic.SemanticRules;

import common.nodes.Node;
import common.nodes.expression.binary.BinaryExpression;
import common.nodes.expression.literal.LiteralNode;
import common.responses.Result;

import java.util.List;
import java.util.function.BinaryOperator;

public interface SemanticRulesInterface {

    boolean match(Node operator);
    Result checkRules(Node leftLiteral, Node rightLiteral);
}
