package common.nodes.statements;

import common.nodes.CompositeNode;
import common.nodes.Node;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;
import common.visitor.VisitorInterface;

public class LetStatementNode extends CompositeNode {
    public LetStatementNode() {
        super(2);
    }
    @Override
    public Result accept(VisitorInterface visitor) {
        return visitor.visit(this);
    }

    public Boolean hasAscription(){
        return !this.children.get(0).isNil();
    }
    public Boolean hasExpression(){
        return !this.children.get(1).isNil();
    }

    public Result expression(){
        if(hasExpression()){
            return new CorrectResult<>(children.get(1));
        }  else {
            return new IncorrectResult("Let statement has no expression.");
        }
    }
    public Result ascription(){
        if(hasAscription()){
            return new CorrectResult<>(children.get(0));
        } else {
            return new IncorrectResult("Let statement has no declaration.");
        }
    }

    public Result setAscription(Node declaration){
        this.children.set(0, declaration);
        return new CorrectResult<>(declaration);
    }
    public Result setExpression(Node expression){
        this.children.set(1, expression);
        return new CorrectResult<>(expression);
    }
}