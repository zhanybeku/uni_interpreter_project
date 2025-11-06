package splat.parser.elements;

import splat.lexer.Token;

public class Variable extends Expression {
	
	private String label;
	
	public Variable(String label, Token tok) {
		super(tok);
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return label;
	}
}

