package common.visitor;

import common.nodes.NilNode;
import common.nodes.declaration.AscriptionNode;
import common.nodes.declaration.IdentifierNode;
import common.nodes.declaration.TypeNode;
import common.nodes.expression.binary.AdditionNode;
import common.nodes.expression.literal.LiteralNode;
import common.nodes.statements.LetStatementNode;
import common.nodes.statements.PrintStatementNode;
import common.responses.Result;

public interface VisitorInterface {
    Result visit(LetStatementNode node);
    Result visit(PrintStatementNode node);
    Result visit(AscriptionNode node);
    Result visit(AdditionNode node);
    Result visit(LiteralNode node);
    Result visit(IdentifierNode node);
    Result visit(TypeNode node);
    Result visit(NilNode node);

}
