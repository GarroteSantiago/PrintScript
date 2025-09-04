package parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tokenizers.TokenizerInterface;
import tokenizers.factories.TokenizerFactory;


public class CodeParserTest {
    private static TokenizerInterface tokenizer;
    private static String letValidString;
    private static String printValidString;
    private static String expressionValidString;
    private static String invalidString;

    @BeforeAll
    static void setup() {
        tokenizer = new TokenizerFactory().createDefaultTokenizer();
        letValidString = "let var:String=\"aString\";";
        printValidString = "println(id);";
        expressionValidString = "4+4;";
        invalidString = "let varString=\"aString\"";
    }

    @Test
    public void createCodeParserTest(){
        CodeParserInterface parser = new Reader(tokenizer);
        Assertions.assertNotNull(parser);
    }

    @Test
    public void parseTest(){
        CodeParserInterface parser = new Reader(tokenizer);
        Assertions.assertTrue(parser.parse(letValidString).isSuccessful());
        Assertions.assertTrue(parser.parse(printValidString).isSuccessful());
        Assertions.assertTrue(parser.parse(expressionValidString).isSuccessful());
        Assertions.assertFalse(parser.parse(invalidString).isSuccessful());
    }

}
