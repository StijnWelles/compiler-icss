package nl.han.ica.icss.ast.operations;

public class MultiplyOperation extends MultiplicativeOperation {
    @Override
    public String getNodeLabel() {
        return "Multiply";
    }

    @Override
    public int eval(int a, int b) {
        return a * b;
    }
}
