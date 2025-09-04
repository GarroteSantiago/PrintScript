package factories;

import common.ExecutionEngine;
import common.ExecutionEngineInterface;
import interpreter.Interpreter;
import interpreter.InterpreterInterface;
import interpreter.visitor.InterpretVisitor;
import lexer.Lexical;
import lexer.LexicalInterface;
import parser.*;
import parser.ast.builders.cor.ChanBuilder;
import parser.semantic.SemanticAnalyzer;
import parser.semantic.SemanticInterface;
import parser.semantic.enforcers.SemanticRulesChecker;
import repositories.CodeRepositoryInterface;
import tokenizers.TokenizerInterface;
import tokenizers.factories.TokenizerFactory;


public class DefaultEngineFactory implements EngineFactoryInterface {
    @Override
    public ExecutionEngineInterface getInterpreterEngine(CodeRepositoryInterface repository) {
        /*TokenizerInterface tokenizer = new TokenizerFactory().createDefaultTokenizer();
        CodeParserInterface parser = new Parser(tokenizer);
        CodeParserInterface codeParser = new Reader(tokenizer);
        LexicalInterface lexical = new Lexical(tokenizer);
        SyntacticInterface syntactic = new Syntactic(new ChanBuilder().createDefaultChain());
        SemanticInterface semantic = new SemanticAnalyzer(new SemanticRulesChecker());
        InterpreterInterface interpreter = new Interpreter(
                new InterpretVisitor()
        );
        return new ExecutionEngine(
                repository,
                codeParser,
                lexical,
                syntactic,
                semantic,
                interpreter
        );*/
        return null;
    }
}
