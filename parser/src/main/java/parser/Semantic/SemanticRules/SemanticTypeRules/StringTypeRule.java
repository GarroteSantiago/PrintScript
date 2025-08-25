package parser.Semantic.SemanticRules.SemanticTypeRules;

import common.nodes.declaration.TypeNode;
import parser.Semantic.SemanticRules.SemanticRulesInterface;
import common.nodes.Node;
import common.nodes.expression.literal.LiteralNode;
import common.nodes.statements.LetStatementNode;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;

public class StringTypeRule implements SemanticRulesInterface {

    @Override
    public boolean match(Node operator) {
        return operator instanceof LetStatementNode;
    }

    @Override
    public Result checkRules(Node leftLiteral, Node rightLiteral) {
        TypeNode leftType = (TypeNode) leftLiteral;
        String rightNode = ((LiteralNode) rightLiteral).value();
        if ("String".equalsIgnoreCase(leftType.value())) {
            try {
                Double.parseDouble(rightNode);
                return new IncorrectResult("Type mismatch: expected Number but got " + rightNode);

            } catch (NumberFormatException e) {
                return new CorrectResult<>(rightNode);
            }
        }
        return new IncorrectResult("Unsupported type in NumericTypeRules: " + leftType.value());
    }
}
