package splat.parser.elements;

import splat.lexer.Token;

public class Literal extends Expression {
	
	private String value;
	
	public Literal(String value, Token tok) {
		super(tok);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return value;
	}
}

