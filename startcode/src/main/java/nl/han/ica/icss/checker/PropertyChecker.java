package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AdditiveOperation;
import nl.han.ica.icss.ast.operations.MultiplicativeOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.Map;

public class PropertyChecker extends CheckerBase {
  private enum ExpressionGroup {
    COLOR,
    SIZE,
  }

  private final IHANLinkedList<ExpressionType> allowedSizeTypes = new HANLinkedList<>(
          ExpressionType.PIXEL,
          ExpressionType.PERCENTAGE);

  // Todo eigen hashmap implementatie?
  private final Map<String, ExpressionGroup> validProperties = new HashMap<>(Map.of(
          "color", ExpressionGroup.COLOR,
          "background-color", ExpressionGroup.COLOR,
          "width", ExpressionGroup.SIZE,
          "height", ExpressionGroup.SIZE
  ));

  public PropertyChecker(IHANLinkedList<HashMap<String, ExpressionType>> variableTypes) {
    this.variableTypes = variableTypes;
  }

  public void check(Declaration declaration) {
    ExpressionGroup expressionGroup = validProperties.get(declaration.property.name);

    if (expressionGroup == null) {
      declaration.setError("Property %s is niet toegestaan.".formatted(declaration.property.name));
      return;
    }

    switch (expressionGroup) {
      case COLOR -> checkColorDeclaration(declaration);
      case SIZE -> checkSizeDeclaration(declaration);
    }
  }

  private void checkColorDeclaration(Declaration declaration) {
    if (declaration.expression instanceof VariableReference variableReference) {
      ExpressionType variableType = getVariableTypeFromName(variableReference.name);

      if (variableType == null) {
        return;
      }

      if (variableType != ExpressionType.COLOR) {
        declaration.setError("Variable %s (type %s) cannot be used for property %s (type %s)."
                .formatted(variableReference.name, variableType, declaration.property.name, ExpressionType.COLOR));
      }

      return;
    }

    if (!(declaration.expression instanceof ColorLiteral)) {
      declaration.setError("Property %s heeft een ongeldige waarde: type %s is hier niet toegestaan.."
              .formatted(declaration.property.name, declaration.expression.getNodeLabel()));
    }
  }

  private ExpressionType getAndValidateOperationType(Operation o) {
    ExpressionType lhsType = getType(o.lhs);
    ExpressionType rhsType = getType(o.rhs);

    // If both values are not scalar and not the same type, it's a type mismatch (ex 4px+3%)
    if (lhsType != rhsType && lhsType != ExpressionType.SCALAR && rhsType != ExpressionType.SCALAR) {
      o.setError("Cannot mix different non-scalar types %s and %s in an operation.".formatted(lhsType, rhsType));
    }

    if (o instanceof MultiplicativeOperation) {
      // For multiplication, at least one of the leaves have to be scalar
      if (lhsType != ExpressionType.SCALAR && rhsType != ExpressionType.SCALAR) {
        o.setError("Using 2 non-scalar literals is not allowed in a multiplicative operation."); // todo betere foutmelding
        return lhsType;
      }

      // One or two of the types are scalar
      if (lhsType == ExpressionType.SCALAR) {
        return rhsType;
      }
      return lhsType;
    }

    if (o instanceof AdditiveOperation) {
      if (lhsType == ExpressionType.SCALAR || rhsType == ExpressionType.SCALAR) {
        o.setError("Using a scalar value in an additive operation is not allowed.");
        return lhsType;
      }

      // Types must match and are not scalar
      return lhsType;
    }

    throw new IllegalArgumentException("Operation type %s unknown".formatted(o.getClass().getName()));
  }

  private ExpressionType getType(Expression e) {
    if (e instanceof Literal l) {
      return l.getExpressionType();
    }

    if (e instanceof VariableReference v) {
      return getVariableTypeFromName(v.name);
    }

    if (e instanceof Operation o) {
      return getAndValidateOperationType(o);
    }

    throw new IllegalArgumentException("Expression type %s unknown".formatted(e.getClass().getName()));
  }

  // todo resolving variables
  private void validateExpression(Expression exp, String propertyName) {
    ExpressionType resultType = getType(exp);

    if (!allowedSizeTypes.has(resultType)) {
      exp.setError("Type %s not allowed in property %s.".formatted(resultType, propertyName));
    }
  }

  private void checkSizeDeclaration(Declaration declaration) {
    validateExpression(declaration.expression, declaration.property.name);
  }
}
