package parser.Semantic.Context;

import parser.Semantic.SemanticRules.RulesCheck.CheckSemanticRules;
import parser.Semantic.TableInteractions.VariablesValue;

public record SemanticVisitorContext(VariablesValue variablesTable, CheckSemanticRules semanticRules){}
