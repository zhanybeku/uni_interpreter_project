package splat.parser.elements;

import java.util.List;
import splat.lexer.Token;

public class FunctionDecl extends Declaration {

	private String label;
	private List<VariableDecl> params;
	private String retType;
	private List<VariableDecl> locVarDecls;
	private List<Statement> stmts;
	
	public FunctionDecl(String label, List<VariableDecl> params, String retType,
						List<VariableDecl> locVarDecls, List<Statement> stmts, Token tok) {
		super(tok);
		this.label = label;
		this.params = params;
		this.retType = retType;
		this.locVarDecls = locVarDecls;
		this.stmts = stmts;
	}

	public String getLabel() {
		return label;
	}
	
	public List<VariableDecl> getParams() {
		return params;
	}
	
	public String getRetType() {
		return retType;
	}
	
	public List<VariableDecl> getLocVarDecls() {
		return locVarDecls;
	}
	
	public List<Statement> getStmts() {
		return stmts;
	}
	
	public String toString() {
		String result = label + "(";
		for (int i = 0; i < params.size(); i++) {
			if (i > 0) result += ", ";
			result += params.get(i).getLabel() + " : " + params.get(i).getType();
		}
		result += ") : " + retType + " is\n";
		for (VariableDecl decl : locVarDecls) {
			result += "   " + decl + "\n";
		}
		result += "begin\n";
		for (Statement stmt : stmts) {
			result += "   " + stmt + "\n";
		}
		result += "end ;";
		return result;
	}
}
