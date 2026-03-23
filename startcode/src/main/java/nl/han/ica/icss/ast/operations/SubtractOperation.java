package nl.han.ica.icss.ast.operations;

public class SubtractOperation extends AdditiveOperation {

    @Override
    public String getNodeLabel() {
        return "Subtract";
    }

    @Override
    public int eval(int a, int b) {
        return a - b;
    }
}
