package splat.parser.elements;

import splat.lexer.Token;

public class VariableDecl extends Declaration {

	private String label;
	private String type;
	
	public VariableDecl(String label, String type, Token tok) {
		super(tok);
		this.label = label;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return label + " : " + type + " ;";
	}
}
