package parser.Semantic.SemanticRules.PrintRule;

import parser.Semantic.SemanticRules.SemanticRulesInterface;
import common.nodes.Node;
import common.nodes.expression.literal.LiteralNode;
import common.nodes.statements.PrintStatementNode;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;

public class PrintSemanticRule implements SemanticRulesInterface {
    @Override
    public boolean match(Node operator) {
        return operator instanceof PrintStatementNode;
    }

    @Override
    public Result checkRules(Node leftLiteral, Node rightLiteral) {
        if(!(rightLiteral instanceof LiteralNode right)){
            return new IncorrectResult("Print statement must be a literal");
        }
        return new CorrectResult<>(leftLiteral);
    }
}
