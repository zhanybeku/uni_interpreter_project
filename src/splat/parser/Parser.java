package splat.parser;

import java.util.ArrayList;
import java.util.List;

import splat.lexer.Token;
import splat.parser.elements.*;

public class Parser {

	private List<Token> tokens;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Compares the next token to an expected value, and throws
	 * an exception if they don't match.  This removes the front-most
	 * (next) token  
	 * 
	 * @param expected value of the next token
	 * @throws ParseException if the actual token doesn't match what 
	 * 			was expected
	 */
	private void checkNext(String expected) throws ParseException {

		Token tok = tokens.remove(0);
		
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" 
					+ tok.getValue()+ "'.", tok);
		}
	}
	
	/**
	 * Returns a boolean indicating whether or not the next token matches
	 * the expected String value.  This does not remove the token from the
	 * token list.
	 * 
	 * @param expected value of the next token
	 * @return true iff the token value matches the expected string
	 */
	private boolean peekNext(String expected) {
		if (tokens.isEmpty()) return false;
		return tokens.get(0).getValue().equals(expected);
	}
	
	/**
	 * Returns a boolean indicating whether or not the token directly after
	 * the front most token matches the expected String value.  This does 
	 * not remove any tokens from the token list.
	 * 
	 * @param expected value of the token directly after the next token
	 * @return true iff the value matches the expected string
	 */
	private boolean peekTwoAhead(String expected) {
		if (tokens.size() < 2) return false;
		return tokens.get(1).getValue().equals(expected);
	}
	
	private boolean hasTokens() {
		return !tokens.isEmpty();
	}
	
	private String getLabel() throws ParseException {
		Token tok = tokens.remove(0);
		String value = tok.getValue();
		if (value.equals("program") || value.equals("begin") || value.equals("end") ||
			value.equals("if") || value.equals("then") || value.equals("else") ||
			value.equals("while") || value.equals("do") || value.equals("print") ||
			value.equals("print_line") || value.equals("return") || value.equals("is") ||
			value.equals("void") || value.equals("Integer") || value.equals("Boolean") ||
			value.equals("String") || value.equals("true") || value.equals("false")) {
			throw new ParseException("Expected identifier, got keyword '" + value + "'.", tok);
		}
		return value;
	}
	
	
	/*
	 *  <program> ::= program <decls> begin <stmts> end ;
	 */
	public ProgramAST parse() throws ParseException {
		
		try {
			// Needed for 'program' token position info
			Token startTok = tokens.get(0);
			
			checkNext("program");
			
			List<Declaration> decls = parseDecls();
			
			checkNext("begin");
			
			List<Statement> stmts = parseStmts();
			
			if (peekNext("end;")) {
				checkNext("end;");
			} else {
				checkNext("end");
				if (peekNext(";")) {
					checkNext(";");
				}
			}
	
			return new ProgramAST(decls, stmts, startTok);
			
		// This might happen if we do a tokens.get(), and nothing is there!
		} catch (IndexOutOfBoundsException ex) {
			
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	
	/*
	 *  <decls> ::= (  <decl>  )*
	 */
	private List<Declaration> parseDecls() throws ParseException {
		
		List<Declaration> decls = new ArrayList<Declaration>();
		
		while (!peekNext("begin")) {
			Declaration decl = parseDecl();
			decls.add(decl);
		}
		
		return decls;
	}
	
	/*
	 * <decl> ::= <var-decl> | <func-decl>
	 */
	private Declaration parseDecl() throws ParseException {
		if (peekTwoAhead(":")) {
			return parseVarDecl();
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Declaration expected", tok);
		}
	}
	
	/*
	 * <func-decl> ::= <label> ( <params> ) : <ret-type> is 
	 * 						<loc-var-decls> begin <stmts> end ;
	 */
	private FunctionDecl parseFuncDecl() throws ParseException {
		Token startTok = tokens.get(0);
		String label = getLabel();
		checkNext("(");
		List<VariableDecl> params = parseParams();
		checkNext(")");
		checkNext(":");
		String retType = tokens.remove(0).getValue();
		checkNext("is");
		List<VariableDecl> locVarDecls = parseLocVarDecls();
		checkNext("begin");
		List<Statement> stmts = parseStmts();
		if (peekNext("end;")) {
			checkNext("end;");
		} else {
			checkNext("end");
			if (peekNext(";")) {
				checkNext(";");
			}
		}
		return new FunctionDecl(label, params, retType, locVarDecls, stmts, startTok);
	}
	
	/*
	 * <params> ::= ( <param> ( , <param> )* )?
	 */
	private List<VariableDecl> parseParams() throws ParseException {
		List<VariableDecl> params = new ArrayList<VariableDecl>();
		if (!peekNext(")")) {
			params.add(parseParam());
			while (peekNext(",")) {
				checkNext(",");
				params.add(parseParam());
			}
		}
		return params;
	}
	
	/*
	 * <param> ::= <label> : <type>
	 */
	private VariableDecl parseParam() throws ParseException {
		Token startTok = tokens.get(0);
		String label = getLabel();
		checkNext(":");
		String type = tokens.remove(0).getValue();
		return new VariableDecl(label, type, startTok);
	}
	
	/*
	 * <loc-var-decls> ::= ( <var-decl> )*
	 */
	private List<VariableDecl> parseLocVarDecls() throws ParseException {
		List<VariableDecl> decls = new ArrayList<VariableDecl>();
		while (!peekNext("begin")) {
			decls.add(parseVarDecl());
		}
		return decls;
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {
		Token startTok = tokens.get(0);
		String label = getLabel();
		checkNext(":");
		String type = tokens.remove(0).getValue();
		checkNext(";");
		return new VariableDecl(label, type, startTok);
	}
	
	/*
	 * <stmts> ::= (  <stmt>  )*
	 */
	private List<Statement> parseStmts() throws ParseException {
		List<Statement> stmts = new ArrayList<Statement>();
		while (hasTokens() && !peekNext("end") && !peekNext("end;") && !peekNext("else")) {
			if (peekNext("end") && tokens.size() > 1) {
				String next = tokens.get(1).getValue();
				if (next.equals("if") || next.equals("while")) {
					break;
				}
			}
			stmts.add(parseStmt());
		}
		return stmts;
	}
	
	/*
	 * <stmt> ::= <assignment> | <while-loop> | <if-then> | <print> | 
	 *             <print-line> | <return> | <function-call>
	 */
	private Statement parseStmt() throws ParseException {
		Token startTok = tokens.get(0);
		String first = startTok.getValue();
		
		if (first.equals("while")) {
			return parseWhileLoop();
		} else if (first.equals("if")) {
			return parseIfThen();
		} else if (first.equals("print_line")) {
			checkNext("print_line");
			checkNext(";");
			return new PrintLine(startTok);
		} else if (first.equals("print")) {
			checkNext("print");
			if (peekNext("_line") || peekNext("line")) {
				if (peekNext("_line")) {
					checkNext("_line");
				} else {
					checkNext("line");
				}
				checkNext(";");
				return new PrintLine(startTok);
			} else {
				Expression expr = parseExpression();
				checkNext(";");
				return new Print(expr, startTok);
			}
		} else if (first.equals("return")) {
			checkNext("return");
			if (peekNext(";")) {
				checkNext(";");
				return new Return(null, startTok);
			} else {
				Expression expr = parseExpression();
				checkNext(";");
				return new Return(expr, startTok);
			}
		} else {
			String label = getLabel();
			if (peekNext(":=")) {
				checkNext(":=");
				Expression expr = parseExpression();
				checkNext(";");
				return new Assignment(label, expr, startTok);
			} else if (peekNext("(")) {
				checkNext("(");
				List<Expression> args = parseArgs();
				checkNext(")");
				checkNext(";");
				return new FunctionCallStmt(label, args, startTok);
			} else {
				throw new ParseException("Expected ':=' or '(' after identifier", tokens.get(0));
			}
		}
	}
	
	/*
	 * <while-loop> ::= while ( <expr> ) do <stmts> end while ;
	 */
	private WhileLoop parseWhileLoop() throws ParseException {
		Token startTok = tokens.get(0);
		checkNext("while");
		checkNext("(");
		Expression condition = parseExpression();
		checkNext(")");
		checkNext("do");
		List<Statement> stmts = parseStmts();
		checkNext("end");
		checkNext("while");
		if (peekNext(";")) {
			checkNext(";");
		}
		return new WhileLoop(condition, stmts, startTok);
	}
	
	/*
	 * <if-then> ::= if ( <expr> ) then <stmts> [ else <stmts> ] end if ;
	 */
	private IfThen parseIfThen() throws ParseException {
		Token startTok = tokens.get(0);
		checkNext("if");
		
		Expression condition;
		if (peekNext("(")) {
			checkNext("(");
			condition = parseExpression();
			checkNext(")");
		} else {
			condition = parseExpression();
		}
		
		checkNext("then");
		List<Statement> thenStmts = parseStmts();
		List<Statement> elseStmts = null;
		
		if (peekNext("else")) {
			checkNext("else");
			elseStmts = parseStmts();
		}
		
		checkNext("end");
		checkNext("if");
		if (peekNext(";")) {
			checkNext(";");
		}
		return new IfThen(condition, thenStmts, elseStmts, startTok);
	}
	
	/*
	 * <args> ::= ( <expr> ( , <expr> )* )?
	 */
	private List<Expression> parseArgs() throws ParseException {
		List<Expression> args = new ArrayList<Expression>();
		if (!peekNext(")")) {
			args.add(parseExpression());
			while (peekNext(",")) {
				checkNext(",");
				args.add(parseExpression());
			}
		}
		return args;
	}
	
	/*
	 * Expression parsing with operator precedence
	 * <expr> ::= <or-expr>
	 * <or-expr> ::= <and-expr> ( || <and-expr> )*
	 * <and-expr> ::= <rel-expr> ( && <rel-expr> )*
	 * <rel-expr> ::= <add-expr> ( <rel-op> <add-expr> )?
	 * <add-expr> ::= <mult-expr> ( <add-op> <mult-expr> )*
	 * <mult-expr> ::= <unary-expr> ( <mult-op> <unary-expr> )*
	 * <unary-expr> ::= <unary-op> <unary-expr> | <primary-expr>
	 * <primary-expr> ::= <literal> | <variable> | <function-call> | ( <expr> )
	 */
	private Expression parseExpression() throws ParseException {
		return parseOrExpr();
	}
	
	private Expression parseOrExpr() throws ParseException {
		Expression left = parseAndExpr();
		while (peekNext("||")) {
			Token opTok = tokens.remove(0);
			Expression right = parseAndExpr();
			left = new BinaryOp(left, "||", right, opTok);
		}
		return left;
	}
	
	private Expression parseAndExpr() throws ParseException {
		Expression left = parseRelExpr();
		while (peekNext("&&")) {
			Token opTok = tokens.remove(0);
			Expression right = parseRelExpr();
			left = new BinaryOp(left, "&&", right, opTok);
		}
		return left;
	}
	
	private Expression parseRelExpr() throws ParseException {
		Expression left = parseAddExpr();
		if (peekNext("==") || peekNext("!=") || peekNext("<") || 
			peekNext(">") || peekNext("<=") || peekNext(">=")) {
			Token opTok = tokens.remove(0);
			String op = opTok.getValue();
			Expression right = parseAddExpr();
			return new BinaryOp(left, op, right, opTok);
		}
		return left;
	}
	
	private Expression parseAddExpr() throws ParseException {
		Expression left = parseMultExpr();
		while (peekNext("+") || peekNext("-")) {
			Token opTok = tokens.remove(0);
			String op = opTok.getValue();
			Expression right = parseMultExpr();
			left = new BinaryOp(left, op, right, opTok);
		}
		return left;
	}
	
	private Expression parseMultExpr() throws ParseException {
		Expression left = parseUnaryExpr();
		while (peekNext("*") || peekNext("/") || peekNext("%")) {
			Token opTok = tokens.remove(0);
			String op = opTok.getValue();
			Expression right = parseUnaryExpr();
			left = new BinaryOp(left, op, right, opTok);
		}
		return left;
	}
	
	private Expression parseUnaryExpr() throws ParseException {
		if (peekNext("-") || peekNext("!")) {
			Token opTok = tokens.remove(0);
			String op = opTok.getValue();
			Expression expr = parseUnaryExpr();
			return new UnaryOp(op, expr, opTok);
		}
		return parsePrimaryExpr();
	}
	
	private Expression parsePrimaryExpr() throws ParseException {
		Token startTok = tokens.get(0);
		String value = startTok.getValue();
		
		if (value.equals("(")) {
			checkNext("(");
			Expression expr = parseExpression();
			checkNext(")");
			return expr;
		} else if (value.equals("true") || value.equals("false") || 
				   value.startsWith("\"") || isNumeric(value)) {
			tokens.remove(0);
			return new Literal(value, startTok);
		} else if (peekTwoAhead("(")) {
			String label = getLabel();
			checkNext("(");
			List<Expression> args = parseArgs();
			checkNext(")");
			return new FunctionCallExpr(label, args, startTok);
		} else {
			String label = getLabel();
			return new Variable(label, startTok);
		}
	}
	
	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
