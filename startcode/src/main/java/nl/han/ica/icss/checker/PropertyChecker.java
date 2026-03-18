package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.datastructures.Pair;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.Map;

public class PropertyChecker extends CheckerBase {
  private enum ExpressionGroup {
    COLOR,
    SIZE,
  }

  // Todo eigen hashmap implementatie?
  private Map<String, ExpressionGroup> validProperties = new HashMap<>(Map.of(
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

      if (variableType != ExpressionType.COLOR) {
        declaration.setError("Variable %s (type %s) kan niet worden gebruikt voor property %s (type %s)."
                .formatted(variableReference.name, variableType, declaration.property.name, ExpressionType.COLOR));
      }

      return;
    }

    if (!(declaration.expression instanceof ColorLiteral)) {
      declaration.setError("Property %s heeft een ongeldige waarde: type %s is hier niet toegestaan.."
              .formatted(declaration.property.name, declaration.expression.getNodeLabel()));
    }
  }

  private boolean doExpressionsHaveMatchingTypes(Expression lhs, Expression rhs) {
    if (lhs instanceof ScalarLiteral || rhs instanceof ScalarLiteral) {
      return true;
    }

    return lhs.getClass() == rhs.getClass();
  }

  // todo resolving variables
  private boolean parseExpression(Expression exp, String declarationName) {
    if (exp instanceof Operation operation) {
      // Multiply requirement: At least one of the leaves must be scalar
      if (operation instanceof MultiplyOperation multiplyOperation) {
        if (!(multiplyOperation.lhs instanceof ScalarLiteral)
         && !(multiplyOperation.rhs instanceof ScalarLiteral)) {
          multiplyOperation.setError("%s: At least one of the values in the calculation must be scalar".formatted(declarationName));
          return false;
        }
      }


      return parseExpression(operation.lhs, declarationName) && parseExpression(operation.rhs, declarationName);
    }


  }

  private void checkSizeDeclaration(Declaration declaration) {
    boolean retval = parseExpression(declaration.expression, declaration.property.name);

    System.out.println(retval);
  }
}
