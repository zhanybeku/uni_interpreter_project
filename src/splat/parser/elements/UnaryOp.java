package splat.parser.elements;

import splat.lexer.Token;

public class UnaryOp extends Expression {
	
	private String op;
	private Expression expr;
	
	public UnaryOp(String op, Expression expr, Token tok) {
		super(tok);
		this.op = op;
		this.expr = expr;
	}
	
	public String getOp() {
		return op;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public String toString() {
		return "(" + op + expr + ")";
	}
}

