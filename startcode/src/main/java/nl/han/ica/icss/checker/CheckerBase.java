package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.LinkedList.IHANLinkedList;
import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.operations.AdditiveOperation;
import nl.han.ica.icss.ast.operations.MultiplicativeOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public abstract class CheckerBase {
  protected IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
  // LinkedList is the scope of the variable: the hashmap at index 0 is a global scope, 1 is in a stylerule, 2 in an if statement etc

  protected HashMap<String, ExpressionType> getCurrentScope() {
    return variableTypes.get(variableTypes.getSize()-1);
  }

  protected ExpressionType getVariableTypeFromName(String variableName) {
    for (HashMap<String, ExpressionType> scopeVariableMap : variableTypes) {
      ExpressionType result = scopeVariableMap.get(variableName);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  // region Standard errors
  protected void addVariableNotDefinedError(ASTNode node, String variableName) {
    node.setError("Variable with name %s is not defined".formatted(variableName));
  }

  protected void addPropertyInvalidValueError(ASTNode node, String propertyName, IHANLinkedList<ExpressionType> allowedTypes, ExpressionType usedType) {
    node.setError("Property %s has an invalid value. Allowed values are %s, used property is %s".formatted(propertyName, allowedTypes.toString(", "), usedType));
  }

  protected void addPropertyInvalidValueError(ASTNode node, String propertyName, ExpressionType allowedType, ExpressionType usedType) {
    addPropertyInvalidValueError(node, propertyName, new HANLinkedList<>(allowedType), usedType);
  }
  // endregion

  // region Getting types
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
        o.setError("Using 2 non-scalar literals is not allowed in a multiplicative operation.");
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

  protected ExpressionType getType(Expression e) {
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
  // endregion
}
