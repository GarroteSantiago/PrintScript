package lexer;


import common.TokenInterface;
import parser.Reader;
import results.CorrectResult;
import results.IncorrectResult;
import results.Result;
import tokenizers.TokenizerInterface;
import stream.TokenStream;
import stream.TokenStreamInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lexical implements LexicalInterface {

    private String word = "";
    private final Reader reader;
    private final TokenizerInterface tokenizer;

    public Lexical(Reader reader, TokenizerInterface tokenizer) {
        this.reader = reader;
        this.tokenizer = tokenizer;
        this.word += reader.peek().result();
    }

    @Override
    public Result<TokenInterface> analyze() {
        Result<TokenInterface> result = tokenizer.tokenize(word);
        if (!result.isSuccessful()) {
            if(reader.hasNext()) {
                word += reader.next().result();
                return analyze();
            }
            return new IncorrectResult<>(result.errorMessage());
        }
        word = "";
        TokenInterface token = result.result();
        return new CorrectResult<>(token);
    }
}
