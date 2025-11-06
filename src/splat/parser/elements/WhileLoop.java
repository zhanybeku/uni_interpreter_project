package splat.parser.elements;

import java.util.List;
import splat.lexer.Token;

public class WhileLoop extends Statement {
	
	private Expression condition;
	private List<Statement> stmts;
	
	public WhileLoop(Expression condition, List<Statement> stmts, Token tok) {
		super(tok);
		this.condition = condition;
		this.stmts = stmts;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public List<Statement> getStmts() {
		return stmts;
	}
	
	public String toString() {
		String result = "while (" + condition + ") do\n";
		for (Statement stmt : stmts) {
			result += "   " + stmt + "\n";
		}
		result += "end while ;";
		return result;
	}
}

