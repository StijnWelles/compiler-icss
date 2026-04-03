package nl.han.ica.icss.ast.selectors;

import nl.han.ica.icss.ast.Selector;

import java.util.Objects;

public class ClassSelector extends Selector {
    private final String SELECTOR_PREFIX = ".";

    public String cls;

    public ClassSelector(String cls) {
        this.cls = cls;
    }

    @Override
    public String getNodeLabel() {
        return "ClassSelector " + cls;
    }

    public String toString() {
        return cls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassSelector that = (ClassSelector) o;
        return Objects.equals(cls, that.cls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cls);
    }

    @Override
    public void generate(StringBuilder stringBuilder) {
        stringBuilder.append(SELECTOR_PREFIX);
        stringBuilder.append(cls);
    }
}
