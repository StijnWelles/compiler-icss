package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.Stack;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	private AST ast;

	private Stack<ASTNode> stack; // todo change to own stack interface and implementation

	public ASTListener() {
		ast = new AST();

		stack = new Stack<>();
	}

	public AST getAST() {
		return ast;
	}

  @Override
  public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();

		ast.setRoot(stylesheet);
		stack.add(stylesheet);
  }

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		stack.pop();
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();

		stack.peek().addChild(stylerule);
		stack.add(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		stack.pop();
	}

	private void enterSelector(Selector s) {
		stack.peek().addChild(s);
	}

	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		enterSelector(new TagSelector(ctx.getText()));
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		String selectorText = ctx.getText().substring(1); // Todo substring voelt hacky, op een andere manier???

		enterSelector(new ClassSelector(selectorText));
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		String selectorText = ctx.getText().substring(1); // Todo substring voelt hacky, op een andere manier???

		enterSelector(new IdSelector(selectorText));
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration d = new Declaration();

		stack.peek().addChild(d);
		stack.add(d);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		stack.pop();
	}

	@Override
	public void enterProperty_name(ICSSParser.Property_nameContext ctx) {
		stack.peek().addChild(new PropertyName(ctx.getText()));
	}

	private void addOperation(Operation operation) {
		stack.peek().addChild(operation);
		stack.add(operation);
	}

	@Override
	public void enterPlusExpression(ICSSParser.PlusExpressionContext ctx) {
		addOperation(new AddOperation());
	}

	@Override
	public void exitPlusExpression(ICSSParser.PlusExpressionContext ctx) {
		stack.pop();
	}

	@Override
	public void enterMinExpression(ICSSParser.MinExpressionContext ctx) {
		addOperation(new SubtractOperation());
	}

	@Override
	public void exitMinExpression(ICSSParser.MinExpressionContext ctx) {
		stack.pop();
	}

	@Override
	public void enterMultExpression(ICSSParser.MultExpressionContext ctx) {
		addOperation(new MultiplyOperation());
	}

	@Override
	public void exitMultExpression(ICSSParser.MultExpressionContext ctx) {
		stack.pop();
	}

	// Literals
	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		stack.peek().addChild(new ColorLiteral(ctx.getText()));
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		stack.peek().addChild(new PixelLiteral(ctx.getText()));
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		stack.peek().addChild(new PercentageLiteral(ctx.getText()));
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		stack.peek().addChild(new ScalarLiteral(ctx.getText()));
	}

	@Override
	public void enterTrueLiteral(ICSSParser.TrueLiteralContext ctx) {
		stack.peek().addChild(new BoolLiteral(true));
	}

	@Override
	public void enterFalseLiteral(ICSSParser.FalseLiteralContext ctx) {
		stack.peek().addChild(new BoolLiteral(false));
	}

	@Override
	public void enterVariableReferenceLiteral(ICSSParser.VariableReferenceLiteralContext ctx) {
		stack.peek().addChild(new VariableReference(ctx.getText()));
	}

	// Variables
	@Override
	public void enterVariable(ICSSParser.VariableContext ctx) {
		VariableAssignment variableAssignment = new VariableAssignment();

		stack.peek().addChild(variableAssignment);
		stack.add(variableAssignment);
	}

	@Override
	public void exitVariable(ICSSParser.VariableContext ctx) {
		stack.pop();
	}

	@Override
	public void enterVariableName(ICSSParser.VariableNameContext ctx) {
		stack.peek().addChild(new VariableReference(ctx.getText()));
	}

	// Conditions
	@Override
	public void enterIf_clause(ICSSParser.If_clauseContext ctx) {
		IfClause ifClause = new IfClause();

		stack.peek().addChild(ifClause);
		stack.add(ifClause);
	}

	@Override
	public void exitIf_clause(ICSSParser.If_clauseContext ctx) {
		stack.pop();
	}

	@Override
	public void enterElse_clause(ICSSParser.Else_clauseContext ctx) {
		ElseClause elseClause = new ElseClause();

		stack.peek().addChild(elseClause);
		stack.add(elseClause);
	}

	@Override
	public void exitElse_clause(ICSSParser.Else_clauseContext ctx) {
		stack.pop();
	}
}