package splat.parser.elements;

import splat.lexer.Token;

public class BinaryOp extends Expression {
	
	private Expression left;
	private String op;
	private Expression right;
	
	public BinaryOp(Expression left, String op, Expression right, Token tok) {
		super(tok);
		this.left = left;
		this.op = op;
		this.right = right;
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public String getOp() {
		return op;
	}
	
	public Expression getRight() {
		return right;
	}
	
	public String toString() {
		return "(" + left + " " + op + " " + right + ")";
	}
}

