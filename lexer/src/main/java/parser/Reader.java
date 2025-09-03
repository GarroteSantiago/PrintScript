package parser;

import results.CorrectResult;
import results.Result;

import java.util.Iterator;

public class Reader implements Iterator<Result<Character>> {

    private final Parser parser;
    private Result<Character> character;

    public Reader(Parser parser) {
        this.parser = parser;
    }
    @Override
    public boolean hasNext() {
        return character.isSuccessful();
    }

    @Override
    public Result<Character> next() {
        if(!hasNext()){
            return new CorrectResult<Character>(' ');
        }
        character = parser.parse();
        return new CorrectResult<Character>(character.result());
    }

    public Result<Character> peek(){
        return character;
    }

}
