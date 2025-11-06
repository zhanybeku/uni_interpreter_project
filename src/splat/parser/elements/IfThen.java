package splat.parser.elements;

import java.util.List;
import splat.lexer.Token;

public class IfThen extends Statement {
	
	private Expression condition;
	private List<Statement> thenStmts;
	private List<Statement> elseStmts;
	
	public IfThen(Expression condition, List<Statement> thenStmts, 
				  List<Statement> elseStmts, Token tok) {
		super(tok);
		this.condition = condition;
		this.thenStmts = thenStmts;
		this.elseStmts = elseStmts;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public List<Statement> getThenStmts() {
		return thenStmts;
	}
	
	public List<Statement> getElseStmts() {
		return elseStmts;
	}
	
	public String toString() {
		String result = "if (" + condition + ") then\n";
		for (Statement stmt : thenStmts) {
			result += "   " + stmt + "\n";
		}
		if (elseStmts != null && !elseStmts.isEmpty()) {
			result += "else\n";
			for (Statement stmt : elseStmts) {
				result += "   " + stmt + "\n";
			}
		}
		result += "end if ;";
		return result;
	}
}

