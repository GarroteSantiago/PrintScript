package parser;

import results.CorrectResult;
import results.IncorrectResult;
import results.Result;

public class Parser implements CodeParserInterface {

    private final String code;
    private int index = 0;

    public Parser(String code) {
        this.code = code;
    }

    @Override
    public Result<Character> parse() {
        if(index < code.length()){
            return new CorrectResult<Character>(code.charAt(index++));
        }
        return new IncorrectResult<>("The end of the file was reached");
    }
}
