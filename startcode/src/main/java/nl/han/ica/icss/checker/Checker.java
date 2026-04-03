package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.IEnterScope;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker extends CheckerBase {
  private PropertyChecker propertyChecker;

  public void check(AST ast) {
    variableTypes = new HANLinkedList<>();

    propertyChecker = new PropertyChecker(variableTypes);

    walkThroughAST(ast.root);
  }

  private void walkThroughAST(ASTNode curNode) {
    if (curNode instanceof IEnterScope) {
      enterScope();
    }

    checkNode(curNode);

    boolean hasExitedScope = false;
    for (ASTNode childNode : curNode.getChildren()) {
      if (curNode instanceof VariableAssignment && childNode instanceof VariableReference) {
        // Don't do checks for variable names in the assignment
        continue;
      }

      if (childNode instanceof ElseClause) {
        exitScope();
        hasExitedScope = true;
      }

      walkThroughAST(childNode);
    }

    if (curNode instanceof IEnterScope && !hasExitedScope) {
      exitScope();
    }
  }

  private void checkNode(ASTNode node) {
    switch (node) {
      case VariableAssignment variableAssignment -> handle(variableAssignment);
      case VariableReference variableReference -> handle(variableReference);
      case Declaration declaration -> propertyChecker.check(declaration);
      case IfClause ifClause -> handle(ifClause);
      default -> {}
    }
  }

  private void enterScope() {
    variableTypes.insert(new HashMap<>());
  }

  private void exitScope() {
    variableTypes.delete(variableTypes.getSize()-1);
  }

  // Helpers
  private void addErrorIfVariableNotDefined(ASTNode node, String variableName) {
    if (getVariableTypeFromName(variableName) == null) {
      addVariableNotDefinedError(node, variableName);
    }
  }

  private void handle(VariableAssignment variableAssignment) {
    ExpressionType type = getType(variableAssignment.expression);

    getCurrentScope().put(variableAssignment.name.name, type);
  }

  private void handle(VariableReference variableReference) {
    addErrorIfVariableNotDefined(variableReference, variableReference.name);
  }

  private void handle(IfClause ifClause) {
    ExpressionType type = getType(ifClause.conditionalExpression);

    if (type != ExpressionType.BOOL) {
      ifClause.setError("If clause condition must be of type %s, given type is %s".formatted(ExpressionType.BOOL, type));
    }
  }
}
