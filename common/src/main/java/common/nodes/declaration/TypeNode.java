package common.nodes.declaration;

import common.nodes.LeafNode;
import common.nodes.Node;
import common.responses.Result;
import common.visitor.VisitorInterface;

public class TypeNode extends LeafNode {

    public TypeNode(String type) {
        super(type);
    }

    @Override
    public Result accept(VisitorInterface visitor) {
        return visitor.visit(this);
    }
}