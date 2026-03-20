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
      declaration.setError("Property %s does not exist.".formatted(declaration.property.name));
      return;
    }

    switch (expressionGroup) {
      case COLOR -> checkColorDeclaration(declaration);
      case SIZE -> checkSizeDeclaration(declaration);
    }
  }

  private void checkColorDeclaration(Declaration declaration) {
    ExpressionType type = getType(declaration.expression);

    if (type != ExpressionType.COLOR) {
      addPropertyInvalidValueError(declaration, declaration.property.name, ExpressionType.COLOR, type);
    }
  }

  private void validateExpression(Expression exp, String propertyName) {
    ExpressionType resultType = getType(exp);

    if (!allowedSizeTypes.has(resultType)) {
      addPropertyInvalidValueError(exp, propertyName, allowedSizeTypes, resultType);
    }
  }

  private void checkSizeDeclaration(Declaration declaration) {
    validateExpression(declaration.expression, declaration.property.name);
  }
}
