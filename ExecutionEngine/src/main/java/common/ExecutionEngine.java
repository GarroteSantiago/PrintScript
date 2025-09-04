package common;

import compiler.CompilerInterface;
import interpreter.InterpreterInterface;
import lexer.LexicalInterface;
import parser.CodeParserInterface;
import parser.SyntacticInterface;
import parser.semantic.SemanticInterface;
import repositories.CodeRepositoryInterface;
import results.CorrectResult;
import results.IncorrectResult;
import results.Result;
import stream.TokenStream;
import stream.TokenStreamInterface;
import visitor.SemanticallyCheckable;

import java.util.ArrayList;
import java.util.List;

public record ExecutionEngine(
        CodeRepositoryInterface repository,
        CodeParserInterface codeParser,
        LexicalInterface lexical,
        SyntacticInterface syntactic,
        SemanticInterface semantic,
        InterpreterInterface interpreter
) implements ExecutionEngineInterface{

    @Override
    public Result<String> execute() {
        while (repository().hasMoreCode()) {
            String code = repository.nextChunkOfCode();
            Result<Character> parseResult = codeParser().parse();
            if (!parseResult.isSuccessful()) {
                return new IncorrectResult<>(parseResult.errorMessage());
            }
            Result<TokenInterface> tokenizeResult = lexical().analyze();
            if (!tokenizeResult.isSuccessful()) {
                return new IncorrectResult<>(tokenizeResult.errorMessage());
            }
            List<TokenInterface> tokens = new ArrayList<>();
            while(tokenizeResult.isSuccessful()){
                tokens.add(tokenizeResult.result());
            }
            TokenStreamInterface tokenStream = new TokenStream(tokens);
            Result<SemanticallyCheckable> buildAstResult = syntactic().buildAbstractSyntaxTree();
            if (!buildAstResult.isSuccessful()) {
                return new IncorrectResult<>(buildAstResult.errorMessage());
            }
            if (!semantic().isSemanticallyValid(buildAstResult.result())){
                return new IncorrectResult<>("Semantic validation failed.");
            }
            Result<String> interpretResult = interpreter().interpreter((Node) buildAstResult.result());
            if (!interpretResult.isSuccessful()) {
                return new IncorrectResult<>(interpretResult.errorMessage());
            }
        }
        return new CorrectResult<>("Execution of printscript program was successful.");
    }
}