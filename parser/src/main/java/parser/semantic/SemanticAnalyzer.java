package parser.semantic;

import common.Environment;
import visitor.RuleVisitor;
import visitor.SemanticallyCheckable;


public record SemanticAnalyzer(RuleVisitor rulesEnforcer) implements SemanticInterface {
    @Override
    public Boolean isSemanticallyValid(SemanticallyCheckable tree) {
        return tree.acceptCheck(rulesEnforcer()).isSuccessful();
    }
}

