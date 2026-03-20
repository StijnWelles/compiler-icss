package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker extends CheckerBase {
  private PropertyChecker propertyChecker;

  public void check(AST ast) {
    variableTypes = new HANLinkedList<>();
    variableTypes.insert(new HashMap<>()); // Global scope

    propertyChecker = new PropertyChecker(variableTypes);

    walkThroughAST(ast.root);
  }

  private void walkThroughAST(ASTNode curNode) {
    checkNode(curNode);

    for (ASTNode childNode : curNode.getChildren()) {
      if (curNode instanceof VariableAssignment && childNode instanceof VariableReference) {
        // Don't do checks for variable names in the assignment
        continue;
      }

      walkThroughAST(childNode);
    }
  }

  // Helpers
  private boolean addErrorIfVariableNotDefined(ASTNode node, String variableName) {
    if (getVariableTypeFromName(variableName) == null) {
      addVariableNotDefinedError(node, variableName);
    }
    return getVariableTypeFromName(variableName) != null;
  }

  private boolean addErrorIfVariableTypeMismatch(ASTNode node, String variableName, ExpressionType targetType) {
    ExpressionType actualType = getVariableTypeFromName(variableName);

    if (actualType == null) {
      addVariableNotDefinedError(node, variableName);
      return false;
    }

    if (actualType != targetType) {
      addVariableTypeMismatchError(node, variableName, actualType, targetType);
    }

    return actualType == targetType;
  }

  private void checkNode(ASTNode node) {
    if (node instanceof VariableAssignment variableAssignment) {
      handle(variableAssignment);
    } else if (node instanceof VariableReference variableReference) {
      handle(variableReference);
    } else if (node instanceof Declaration declaration) {
      propertyChecker.check(declaration);
    } else if (node instanceof IfClause ifClause) {
      handle(ifClause);
    }
  }

  private void handle(VariableAssignment variableAssignment) {
    if (variableAssignment.expression instanceof Literal literal) {
      variableTypes.get(0).put(variableAssignment.name.name, literal.getExpressionType());
    } else if (variableAssignment.expression instanceof VariableReference variableReference) {
      ExpressionType result = getVariableTypeFromName(variableReference.name);

      if (result == null) {
        addVariableNotDefinedError(variableAssignment, variableReference.name);
      }

      variableTypes.get(0).put(variableAssignment.name.name, result);
    }
    // Todo calculations in assignment
//      variableTypes.get(0).put(variableAssignment.name, )
  }

  private void handle(VariableReference variableReference) {
    addErrorIfVariableNotDefined(variableReference, variableReference.name);
  }

  private void handle(IfClause ifClause) {
    ExpressionType type = propertyChecker.getType(ifClause.conditionalExpression);

    if (type != ExpressionType.BOOL) {
      ifClause.setError("If clause condition must be of type %s, given type is %s".formatted(ExpressionType.BOOL, type));
    }
  }
}
