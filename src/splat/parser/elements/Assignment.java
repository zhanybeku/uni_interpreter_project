package splat.parser.elements;

import splat.lexer.Token;

public class Assignment extends Statement {
	
	private String label;
	private Expression expr;
	
	public Assignment(String label, Expression expr, Token tok) {
		super(tok);
		this.label = label;
		this.expr = expr;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public String toString() {
		return label + " := " + expr + " ;";
	}
}

