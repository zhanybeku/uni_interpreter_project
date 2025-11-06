package splat.parser.elements;

import splat.lexer.Token;

public class Print extends Statement {
	
	private Expression expr;
	
	public Print(Expression expr, Token tok) {
		super(tok);
		this.expr = expr;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public String toString() {
		return "print " + expr + " ;";
	}
}

