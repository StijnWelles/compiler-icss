package nl.han.ica.icss.ast;

import nl.han.ica.icss.ast.types.IEnterScope;

import java.util.ArrayList;
import java.util.Objects;

public class ElseClause extends ASTNode implements IEnterScope {

    public ArrayList<ASTNode> body = new ArrayList<>();

    public ElseClause() { }

    public ElseClause(ArrayList<ASTNode> body) {

        this.body = body;
    }

    @Override
    public ArrayList<ASTNode> getBody() {
        return body;
    }

    @Override
    public String getNodeLabel() {
        return "Else_Clause";
    }

    @Override
    public ArrayList<ASTNode> getChildren() {
        ArrayList<ASTNode> children = new ArrayList<>();
        children.addAll(body);

        return children;
    }

    @Override
    public ASTNode addChild(ASTNode child) {

        body.add(child);

        return this;
    }

    @Override
    public ASTNode replaceChild(ASTNode originalNode, ASTNode newNode) {
        int index = body.indexOf(originalNode);

        if (index != -1) {
            body.set(index, newNode);
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ElseClause ElseClause = (ElseClause) o;
        return Objects.equals(body, ElseClause.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
