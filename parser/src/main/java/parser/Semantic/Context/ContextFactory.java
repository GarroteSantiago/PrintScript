package parser.Semantic.Context;

import parser.Semantic.SemanticRules.BinaryOperations.AdditionSemanticRules;
import parser.Semantic.SemanticRules.RulesCheck.CheckSemanticRules;
import parser.Semantic.SemanticRules.SemanticTypeRules.NumericTypeRules;
import parser.Semantic.SemanticRules.SemanticTypeRules.StringTypeRule;
import parser.Semantic.TableInteractions.VariablesValue;
import parser.Semantic.RulesFactory.RulesEngine;
import common.Symbol.Symbol;
import common.VariablesTable.VariablesTable;

import java.util.HashMap;
import java.util.List;

public class ContextFactory {

    public SemanticVisitorContext DefaultFactory() {
        CheckSemanticRules semanticRules = new CheckSemanticRules(
                new RulesEngine(
                        List.of(new AdditionSemanticRules(), new NumericTypeRules(), new StringTypeRule())));
        VariablesValue variablesValue = new VariablesValue(new VariablesTable(new HashMap<String, Symbol>()));
        return new SemanticVisitorContext(variablesValue, semanticRules);
    }

}
