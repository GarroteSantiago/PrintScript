package lexer;

import common.Token;
import common.TokenInterface;
import results.CorrectResult;
import results.Result;


import java.util.Iterator;
import java.util.List;

public class NextToken implements Iterator<Result<TokenInterface>> {

    private final LexicalInterface lexical;
    private Result<TokenInterface> token;

    public NextToken(LexicalInterface lexical) {
        this.lexical = lexical;
        this.token = lexical.analyze();
    }

    @Override
    public boolean hasNext() {
        return token.isSuccessful();
    }

    @Override
    public Result<TokenInterface> next() {
        if(!hasNext()){
            return new CorrectResult<TokenInterface>(new Token("",""));
        }
        token = lexical.analyze();
        return new CorrectResult<TokenInterface>(token.result());
    }

    public Result<TokenInterface> peek(){
        return token;
    }

}
