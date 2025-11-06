package splat.parser.elements;

import splat.lexer.Token;

public class PrintLine extends Statement {
	
	public PrintLine(Token tok) {
		super(tok);
	}
	
	public String toString() {
		return "print_line ;";
	}
}

