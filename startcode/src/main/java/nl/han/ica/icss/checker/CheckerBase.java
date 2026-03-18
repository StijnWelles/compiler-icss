package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public abstract class CheckerBase {
  protected IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
  // LinkedList is the scope of the variable: the hashmap at index 0 is a global scope, 1 is in a stylerule, 2 in an if statement etc

  protected ExpressionType getVariableTypeFromName(String variableName) {
    for (HashMap<String, ExpressionType> scopeVariableMap : variableTypes) {
      ExpressionType result = scopeVariableMap.get(variableName);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  protected void addVariableNotDefinedError(ASTNode node, String variableName) {
    node.setError("Variable with name %s is not defined".formatted(variableName));
  }

  protected void addVariableTypeMismatchError(ASTNode node, String variableName, ExpressionType actualType, ExpressionType targetType) {
    node.setError("Variable %s of type %s does not match the target type %s".formatted(variableName, actualType, targetType));
  }

  protected void addVariableTypeMismatchError(ASTNode node, String variableName, ExpressionType actualType) {
    node.setError("Variable %s of type %s does not match the target type".formatted(variableName, actualType));
  }
}
