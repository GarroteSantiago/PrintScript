/*package parser.ast.builders;


import factories.tokens.TokenFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import parser.ast.builders.ascription.AscriptionBuilder;
import stream.TokenStream;
import stream.TokenStreamInterface;

import java.util.List;

public class AscriptionBuilderTest {
    private static TokenStreamInterface validTokenStream;
    private static List<TokenStreamInterface> invalidTokenStreams;

    @BeforeAll
    public static void setUp() {
        TokenFactory tokenFactory = new TokenFactory();
        validTokenStream = new TokenStream(
                List.of(
                        tokenFactory.createIdentifierToken("id"),
                        tokenFactory.createTypeAssignationToken(),
                        tokenFactory.createTypeToken("type"),
                        tokenFactory.createEndOfLineToken()
                )
        );
        TokenStreamInterface invalidTokenStreamOne = new TokenStream(
                List.of(
                        tokenFactory.createTypeAssignationToken(),
                        tokenFactory.createTypeToken("type")
                )
        );
        TokenStreamInterface invalidTokenStreamTwo = new TokenStream(
                List.of(
                        tokenFactory.createIdentifierToken("id"),
                        tokenFactory.createTypeToken("type")
                )
        );
        TokenStreamInterface invalidTokenStreamThree = new TokenStream(
                List.of(
                        tokenFactory.createIdentifierToken("id"),
                        tokenFactory.createTypeAssignationToken()
                )
        );
        invalidTokenStreams = List.of(invalidTokenStreamOne, invalidTokenStreamTwo, invalidTokenStreamThree);
    }

    @Test
    public void createAscriptionBuilderTest() {
        AscriptionBuilder builder = new AscriptionBuilder();
        Assertions.assertNotNull(builder);
    }

    @Test
    public void buildAscriptionBuilderTest() {
        AscriptionBuilder builder = new AscriptionBuilder();
        Assertions.assertTrue(builder.build(validTokenStream).isSuccessful());
        for (TokenStreamInterface tokenStream : invalidTokenStreams) {
            Assertions.assertFalse(builder.build(tokenStream).isSuccessful());
        }
    }
}


 */