package splat.parser.elements;

import splat.lexer.Token;

public class Return extends Statement {
	
	private Expression expr;
	
	public Return(Expression expr, Token tok) {
		super(tok);
		this.expr = expr;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public String toString() {
		if (expr != null) {
			return "return " + expr + " ;";
		} else {
			return "return ;";
		}
	}
}

