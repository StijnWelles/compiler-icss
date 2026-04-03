package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.types.IEnterScope;

import java.util.*;

public class Evaluator extends EvaluatorBase implements Transform {
  @Override
  public void apply(AST ast) {
    variableValues = new HANLinkedList<>();

    walkThroughAST(ast.root, null);
  }

  private ASTNode walkThroughAST(ASTNode curNode, ASTNode parent) {
    if (curNode instanceof IEnterScope) {
      enterScope();
    }

    ASTNode newCurNode = checkNode(curNode, parent);

    if (newCurNode == null) {
      newCurNode = curNode;
    }

    boolean hasExitedScope = false;
    for (ASTNode childNode : newCurNode.getChildren()) {
      if (curNode instanceof VariableAssignment && childNode instanceof VariableReference) {
        // Don't do checks for variable names in the assignment
        continue;
      }

      if (childNode instanceof ElseClause) {
        exitScope();
        hasExitedScope = true;
      }

      ASTNode newNode = walkThroughAST(childNode, newCurNode);

      if (newNode != null) {
        curNode.replaceChild(childNode, newNode);
      }
    }

    if (curNode instanceof IEnterScope && !hasExitedScope) {
      exitScope();
    }

    for (int i = curNode.getChildren().size() - 1; i >= 0; i--) {
      ASTNode childNode = curNode.getChildren().get(i);

      if (childNode instanceof VariableAssignment) {
        curNode.removeChild(childNode);
      }
    }

    return newCurNode;
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
   * Checks the node and returns a new node if the node was changed. Returns the original node if nothing was changed.
   * @param node
   * @return The new node.
   */
  private ASTNode checkNode(ASTNode node, ASTNode parent) {
    return switch (node) {
      case VariableAssignment variableAssignment -> handle(variableAssignment);
      case Declaration declaration -> handle(declaration, (IEnterScope) parent);
      case Expression expression -> handle(expression);
      case IfClause ifClause -> handle(ifClause, parent);
      default -> node;
    };
  }

  private ASTNode handle(VariableAssignment variableAssignment) {
    Literal value = getLiteral(variableAssignment.expression);

    if (!setVariableIfExists(variableAssignment.name.name, value)) {
      getCurrentScope().put(variableAssignment.name.name, value);
    }

    return null; // Variable doesn't need to be included in the result
  }

  private ASTNode handle(Expression expression) {
    return getLiteral(expression);
  }

  private void removePreviousDeclarationIfExists(PropertyName propertyName, IEnterScope parent, int maxIndex) {
    for (int i = 0; i < maxIndex; i++) {
      if (parent.getBody().get(i) instanceof Declaration declaration) {
        if (Objects.equals(declaration.property.name, propertyName.name)) {
          ((ASTNode) parent).removeChild(declaration);
          return; // There is always 0 or 1 of a property in the list
        }
      }
    }
  }

  private ASTNode handle(Declaration declaration, IEnterScope parent) {
    removePreviousDeclarationIfExists(declaration.property, parent, parent.getBody().indexOf(declaration));

    return declaration;
  }

  private ASTNode handle(IfClause ifClause, ASTNode parent) {
    BoolLiteral b = (BoolLiteral) getLiteral(ifClause.conditionalExpression);
    List<ASTNode> parentBody = ((IEnterScope) parent).getBody();
    List<ASTNode> toAdd;

    if (b.value) {
      toAdd = ifClause.getBody();
    } else if (ifClause.elseClause != null) {
      toAdd = ifClause.elseClause.getBody();
    } else {
      toAdd = new ArrayList<>();
    }

    int index = parentBody.indexOf(ifClause);
    parentBody.remove(ifClause);

    for (int i = 0; i < toAdd.size(); i++) {
      parentBody.add(index + i, toAdd.get(i));
    }

    return parent;
  }
}
