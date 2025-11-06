package splat.parser.elements;

import java.util.List;
import splat.lexer.Token;

public class FunctionCallStmt extends Statement {
	
	private String label;
	private List<Expression> args;
	
	public FunctionCallStmt(String label, List<Expression> args, Token tok) {
		super(tok);
		this.label = label;
		this.args = args;
	}
	
	public String getLabel() {
		return label;
	}
	
	public List<Expression> getArgs() {
		return args;
	}
	
	public String toString() {
		String result = label + "(";
		for (int i = 0; i < args.size(); i++) {
			if (i > 0) result += ", ";
			result += args.get(i);
		}
		result += ") ;";
		return result;
	}
}

