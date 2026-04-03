package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.datastructures.Stack.HANStack;
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

	private IHANStack<ASTNode> stack;

	public ASTListener() {
		ast = new AST();

		stack = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	private void addAsChildAndSetParent(ASTNode node) {
		stack.peek().addChild(node);
		stack.add(node);
	}

	private void addAsChild(ASTNode node) {
		stack.peek().addChild(node);
	}

	// region Stylesheet/rule
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
		addAsChildAndSetParent(new Stylerule());
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		stack.pop();
	}
	// endregion

	// region Selectors
	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		addAsChild(new TagSelector(ctx.getText()));
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		String selectorText = ctx.getText().substring(1);

		addAsChild(new ClassSelector(selectorText));
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		String selectorText = ctx.getText().substring(1);

		addAsChild(new IdSelector(selectorText));
	}
	// endregion

	// region Declaration
	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		addAsChildAndSetParent(new Declaration());
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		stack.pop();
	}

	@Override
	public void enterProperty_name(ICSSParser.Property_nameContext ctx) {
		addAsChild(new PropertyName(ctx.getText()));
	}
	// endregion

	// region Expressions
	@Override
	public void enterPlusExpression(ICSSParser.PlusExpressionContext ctx) {
		addAsChildAndSetParent(new AddOperation());
	}

	@Override
	public void exitPlusExpression(ICSSParser.PlusExpressionContext ctx) {
		stack.pop();
	}

	@Override
	public void enterMinExpression(ICSSParser.MinExpressionContext ctx) {
		addAsChildAndSetParent(new SubtractOperation());
	}

	@Override
	public void exitMinExpression(ICSSParser.MinExpressionContext ctx) {
		stack.pop();
	}

	@Override
	public void enterMultExpression(ICSSParser.MultExpressionContext ctx) {
		addAsChildAndSetParent(new MultiplyOperation());
	}

	@Override
	public void exitMultExpression(ICSSParser.MultExpressionContext ctx) {
		stack.pop();
	}
	// endregion

	// region Literals
	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		addAsChild(new ColorLiteral(ctx.getText()));
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		addAsChild(new PixelLiteral(ctx.getText()));
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		addAsChild(new PercentageLiteral(ctx.getText()));
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		addAsChild(new ScalarLiteral(ctx.getText()));
	}

	@Override
	public void enterTrueLiteral(ICSSParser.TrueLiteralContext ctx) {
		addAsChild(new BoolLiteral(true));
	}

	@Override
	public void enterFalseLiteral(ICSSParser.FalseLiteralContext ctx) {
		addAsChild(new BoolLiteral(false));
	}

	@Override
	public void enterVariableReferenceLiteral(ICSSParser.VariableReferenceLiteralContext ctx) {
		addAsChild(new VariableReference(ctx.getText()));
	}
	// endregion

	// region Variables
	@Override
	public void enterVariable(ICSSParser.VariableContext ctx) {
		addAsChildAndSetParent(new VariableAssignment());
	}

	@Override
	public void exitVariable(ICSSParser.VariableContext ctx) {
		stack.pop();
	}

	@Override
	public void enterVariableName(ICSSParser.VariableNameContext ctx) {
		addAsChild(new VariableReference(ctx.getText()));
	}
	// endregion

	// region Conditions
	@Override
	public void enterIf_clause(ICSSParser.If_clauseContext ctx) {
		addAsChildAndSetParent(new IfClause());
	}

	@Override
	public void exitIf_clause(ICSSParser.If_clauseContext ctx) {
		stack.pop();
	}

	@Override
	public void enterElse_clause(ICSSParser.Else_clauseContext ctx) {
		addAsChildAndSetParent(new ElseClause());
	}

	@Override
	public void exitElse_clause(ICSSParser.Else_clauseContext ctx) {
		stack.pop();
	}
	// endregion
}