package parser;

import results.Result;
import stream.TokenStreamInterface;
import visitor.SemanticallyCheckable;

public interface SyntacticInterface {
    Result<SemanticallyCheckable> buildAbstractSyntaxTree();
}
