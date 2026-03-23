package nl.han.ica.icss.ast.operations;

public class AddOperation extends AdditiveOperation {

    @Override
    public String getNodeLabel() {
        return "Add";
    }

    @Override
    public int eval(int a, int b) {
        return a + b;
    }
}
