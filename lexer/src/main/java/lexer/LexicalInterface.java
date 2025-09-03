package lexer;

import common.TokenInterface;
import results.Result;
import stream.TokenStreamInterface;

import java.util.List;

public interface LexicalInterface {
    Result<TokenInterface> analyze();
}
