package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.types.EnterScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator extends EvaluatorBase implements Transform {

  @Override
  public void apply(AST ast) {
    variableValues = new HANLinkedList<>();
//    variableValues.insert(new HashMap<>()); // Global scope

    walkThroughAST(ast.root, null);
  }

  private ASTNode walkThroughAST(ASTNode curNode, ASTNode parent) {
    if (curNode instanceof EnterScope) {
      enterScope();
    }

    ASTNode v = checkNode(curNode, parent);

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

      ASTNode newNode = walkThroughAST(childNode, curNode);

      if (newNode != null) {
        curNode.replaceChild(childNode, newNode);
      }
    }

    if (curNode instanceof EnterScope && !hasExitedScope) {
      exitScope();
    }

    return v;
  }

  private void enterScope() {
    variableValues.insert(new HashMap<>());
  }

  private void exitScope() {
    HashMap<String, Literal> h = variableValues.delete(variableValues.getSize()-1);

    for (HashMap<String, Literal> scopeVariableMap : variableValues) {
      for (Map.Entry<String, Literal> entry : h.entrySet()) {
        if (scopeVariableMap.containsKey(entry.getKey())) {
          scopeVariableMap.put(entry.getKey(), entry.getValue());
        }
      }
    }
  }

  /**
   * Checks the node and returns a new node if the node was changed. Returns null if the node was not changed.
   * @param node
   * @return The new node, or null.
   */
  private ASTNode checkNode(ASTNode node, ASTNode parent) {
    return switch (node) {
      case VariableAssignment variableAssignment -> handle(variableAssignment);
//      case Declaration declaration -> handle(declaration);
      case Expression expression -> handle(expression);
      case IfClause ifClause -> handle(ifClause, (EnterScope) parent);
      default -> node;
    };
  }

  private ASTNode handle(VariableAssignment variableAssignment) {
    Literal value = getLiteral(variableAssignment.expression);

    getCurrentScope().put(variableAssignment.name.name, value);

    return null; // Variable doesn't need to be included in the result
  }

  private ASTNode handle(Expression expression) {
    return getLiteral(expression);
  }

//  private ASTNode handle(Declaration declaration) {
//    declaration.expression = getLiteral(declaration.expression);
//
//    return null;
//  }

  private ASTNode handle(IfClause ifClause, EnterScope parent) {
    BoolLiteral b = (BoolLiteral) getLiteral(ifClause.conditionalExpression);
    List<ASTNode> parentBody = parent.getBody();
    List<ASTNode> toAdd;

    if (b.value) {
      toAdd = ifClause.getBody();
    } else {
      toAdd = ifClause.elseClause.getBody();
    }

    int index = parentBody.indexOf(ifClause);
    parentBody.remove(ifClause);

    for (ASTNode n : toAdd) {
      parentBody.add(index, n);
    }

    return null;
  }
}
