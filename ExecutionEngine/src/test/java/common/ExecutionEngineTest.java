/*package common;


import interpreter.Interpreter;
import interpreter.InterpreterInterface;
import interpreter.visitor.InterpretVisitor;
import iterator.CodeIteratorInterface;
import iterator.MockCodeIterator;
import lexer.Lexical;
import lexer.LexicalInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import parser.CodeParser;
import parser.CodeParserInterface;
import parser.Syntactic;
import parser.SyntacticInterface;
import parser.ast.builders.cor.ChanBuilder;
import parser.semantic.SemanticAnalyzer;
import parser.semantic.SemanticInterface;
import parser.semantic.enforcers.SemanticRulesChecker;
import repositories.CodeRepository;
import repositories.CodeRepositoryInterface;
import tokenizers.TokenizerInterface;
import tokenizers.factories.TokenizerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExecutionEngineTest {
    private static CodeRepositoryInterface repository;
    private static CodeParserInterface codeParser;
    private static LexicalInterface lexical;
    private static SyntacticInterface syntactic;
    private static SemanticInterface semantic;
    private static InterpreterInterface interpreter;

    @BeforeAll
    static void setUp() {
        List<String> code = List.of(
                "let varOne:String = \"varA\";",
                "let varTwo:String = \"varB\";",
                "println(varOne + varTwo);"
        );
        CodeIteratorInterface iterator = new MockCodeIterator(code);
        repository = new CodeRepository(iterator);
        TokenizerInterface tokenizer = new TokenizerFactory().createDefaultTokenizer();
        codeParser = new CodeParser(tokenizer);
        lexical = new Lexical(tokenizer);
        syntactic = new Syntactic(new ChanBuilder().createDefaultChain());
        semantic = new SemanticAnalyzer(new SemanticRulesChecker());
        interpreter = new Interpreter(
                new InterpretVisitor()
        );
    }

    @Test
    public void createExecutionEngineTest() {
        ExecutionEngineInterface engine = new ExecutionEngine(
                repository,
                codeParser,
                lexical,
                syntactic,
                semantic,
                interpreter
        );
        Assertions.assertNotNull(engine);
    }

    @Test
    public void executeExecutionEngineTest(){
        ExecutionEngineInterface engine = new ExecutionEngine(
                repository,
                codeParser,
                lexical,
                syntactic,
                semantic,
                interpreter
        );
        Assertions.assertTrue(engine.execute().isSuccessful());
    }
}

 */
