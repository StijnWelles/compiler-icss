package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.LinkedList.IHANLinkedList;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.NumericLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AdditiveOperation;
import nl.han.ica.icss.ast.operations.MultiplicativeOperation;

import java.util.HashMap;

public abstract class EvaluatorBase {
  protected IHANLinkedList<HashMap<String, Literal>> variableValues;

  protected HashMap<String, Literal> getCurrentScope() {
    return variableValues.get(variableValues.getSize()-1);
  }

  protected boolean setVariableIfExists(String variableName, Literal value) {
    for (HashMap<String, Literal> scopeVariableMap : variableValues) {
      Literal result = scopeVariableMap.get(variableName);
      if (result != null) {
        scopeVariableMap.put(variableName, value);
        return true;
      }
    }

    return false;
  }

  protected Literal getVariableValueFromName(String variableName) {
    for (HashMap<String, Literal> scopeVariableMap : variableValues) {
      Literal result = scopeVariableMap.get(variableName);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  // region Getting value
  private NumericLiteral createNewInstanceWithValue(NumericLiteral type, int value) {
    return switch (type) {
      case ScalarLiteral l -> new ScalarLiteral(value);
      case PixelLiteral l -> new PixelLiteral(value);
      case PercentageLiteral l -> new PercentageLiteral(value);
      default -> throw new IllegalArgumentException("createNewInstanceWithValue does not support literal type %s.".formatted(type.getExpressionType()));
    };
  }

  private NumericLiteral getOperationResult(Operation o) {
    NumericLiteral lhsValue = (NumericLiteral) getLiteral(o.lhs);
    NumericLiteral rhsValue = (NumericLiteral) getLiteral(o.rhs);

    if (o instanceof MultiplicativeOperation) {
      if (lhsValue instanceof ScalarLiteral) {
        return createNewInstanceWithValue(rhsValue, o.eval(lhsValue.value, rhsValue.value));
      }
      return createNewInstanceWithValue(lhsValue, o.eval(lhsValue.value, rhsValue.value));
    }

    if (o instanceof AdditiveOperation) {
      return createNewInstanceWithValue(lhsValue, o.eval(lhsValue.value, rhsValue.value));
    }

    throw new IllegalArgumentException("Operation type %s unknown".formatted(o.getClass().getName()));
  }

  protected Literal getLiteral(Expression e) {
    if (e instanceof Literal l) {
      return l;
    }

    if (e instanceof VariableReference v) {
      return getVariableValueFromName(v.name);
    }

    if (e instanceof Operation o) {
      return getOperationResult(o);
    }

    throw new IllegalArgumentException("Expression type %s unknown".formatted(e.getClass().getName()));
  }
  // endregion
}
