package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.IntegerValue;
import splat.executor.BooleanValue;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class BinaryOp extends Expression {
	
	private Expression left;
	private String op;
	private Expression right;
	
	public BinaryOp(Expression left, String op, Expression right, Token tok) {
		super(tok);
		this.left = left;
		this.op = op;
		this.right = right;
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public String getOp() {
		return op;
	}
	
	public Expression getRight() {
		return right;
	}
	
	public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
	                             Map<String, Type> varAndParamMap)
	                             throws SemanticAnalysisException {
		
		Type leftType = left.analyzeAndGetType(funcMap, varAndParamMap);
		Type rightType = right.analyzeAndGetType(funcMap, varAndParamMap);
		
		Type integerType = new Type("Integer");
		Type booleanType = new Type("Boolean");
		
		if (op.equals("+") || op.equals("-") || op.equals("*") || 
		    op.equals("/") || op.equals("%")) {
			if (!leftType.equals(integerType) || !rightType.equals(integerType)) {
				throw new SemanticAnalysisException(
					"Arithmetic operator '" + op + "' requires Integer operands, " +
					"got " + leftType.getName() + " and " + rightType.getName(), this);
			}
			return integerType;
		}
		
		if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
			if (!leftType.equals(integerType) || !rightType.equals(integerType)) {
				throw new SemanticAnalysisException(
					"Comparison operator '" + op + "' requires Integer operands, " +
					"got " + leftType.getName() + " and " + rightType.getName(), this);
			}
			return booleanType;
		}
		
		if (op.equals("==")) {
			if (!leftType.equals(rightType)) {
				throw new SemanticAnalysisException(
					"Equality operator '==' requires operands of the same type, " +
					"got " + leftType.getName() + " and " + rightType.getName(), this);
			}
			if (!leftType.equals(integerType) && !leftType.equals(booleanType)) {
				throw new SemanticAnalysisException(
					"Equality operator '==' requires Integer or Boolean operands, " +
					"got " + leftType.getName(), this);
			}
			return booleanType;
		}
		
		if (op.equals("and") || op.equals("or")) {
			if (!leftType.equals(booleanType) || !rightType.equals(booleanType)) {
				throw new SemanticAnalysisException(
					"Logical operator '" + op + "' requires Boolean operands, " +
					"got " + leftType.getName() + " and " + rightType.getName(), this);
			}
			return booleanType;
		}
		
		throw new SemanticAnalysisException(
			"Unknown binary operator: " + op, this);
	}
	
	public String toString() {
		return "(" + left + " " + op + " " + right + ")";
	}
	
	public Value evaluate(Map<String, FunctionDecl> funcMap,
	                     Map<String, Value> varAndParamMap) throws ExecutionException {
		
		Value leftValue = left.evaluate(funcMap, varAndParamMap);
		Value rightValue = right.evaluate(funcMap, varAndParamMap);
		
		if (op.equals("+") || op.equals("-") || op.equals("*") || 
		    op.equals("/") || op.equals("%")) {
			if (!(leftValue instanceof IntegerValue) || 
			    !(rightValue instanceof IntegerValue)) {
				throw new ExecutionException("Arithmetic operator requires Integer operands", this);
			}
			
			int leftInt = ((IntegerValue) leftValue).getValue();
			int rightInt = ((IntegerValue) rightValue).getValue();
			int result;
			
			if (op.equals("+")) {
				result = leftInt + rightInt;
			} else if (op.equals("-")) {
				result = leftInt - rightInt;
			} else if (op.equals("*")) {
				result = leftInt * rightInt;
			} else if (op.equals("/")) {
				if (rightInt == 0) {
					throw new ExecutionException("Division by zero", this);
				}
				result = leftInt / rightInt;
			} else {
				if (rightInt == 0) {
					throw new ExecutionException("Modulo by zero", this);
				}
				result = leftInt % rightInt;
			}
			
			return new IntegerValue(result);
		}
		
		if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
			if (!(leftValue instanceof IntegerValue) || 
			    !(rightValue instanceof IntegerValue)) {
				throw new ExecutionException("Comparison operator requires Integer operands", this);
			}
			
			int leftInt = ((IntegerValue) leftValue).getValue();
			int rightInt = ((IntegerValue) rightValue).getValue();
			boolean result;
			
			if (op.equals(">")) {
				result = leftInt > rightInt;
			} else if (op.equals("<")) {
				result = leftInt < rightInt;
			} else if (op.equals(">=")) {
				result = leftInt >= rightInt;
			} else {
				result = leftInt <= rightInt;
			}
			
			return new BooleanValue(result);
		}
		
		if (op.equals("==")) {
			if (leftValue instanceof IntegerValue && rightValue instanceof IntegerValue) {
				int leftInt = ((IntegerValue) leftValue).getValue();
				int rightInt = ((IntegerValue) rightValue).getValue();
				return new BooleanValue(leftInt == rightInt);
			} else if (leftValue instanceof BooleanValue && rightValue instanceof BooleanValue) {
				boolean leftBool = ((BooleanValue) leftValue).getValue();
				boolean rightBool = ((BooleanValue) rightValue).getValue();
				return new BooleanValue(leftBool == rightBool);
			} else {
				throw new ExecutionException("Equality operator requires operands of the same type", this);
			}
		}
		
		if (op.equals("and") || op.equals("or")) {
			if (!(leftValue instanceof BooleanValue) || 
			    !(rightValue instanceof BooleanValue)) {
				throw new ExecutionException("Logical operator requires Boolean operands", this);
			}
			
			boolean leftBool = ((BooleanValue) leftValue).getValue();
			boolean rightBool = ((BooleanValue) rightValue).getValue();
			boolean result;
			
			if (op.equals("and")) {
				result = leftBool && rightBool;
			} else {
				result = leftBool || rightBool;
			}
			
			return new BooleanValue(result);
		}
		
		throw new ExecutionException("Unknown binary operator: " + op, this);
	}
}

