package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.IntegerValue;
import splat.executor.BooleanValue;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class UnaryOp extends Expression {

  private String op;
  private Expression expr;

  public UnaryOp(String op, Expression expr, Token tok) {
    super(tok);
    this.op = op;
    this.expr = expr;
  }

  public String getOp() {
    return op;
  }

  public Expression getExpr() {
    return expr;
  }

  public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    Type exprType = expr.analyzeAndGetType(funcMap, varAndParamMap);

    Type integerType = new Type("Integer");
    Type booleanType = new Type("Boolean");

    if (op.equals("not")) {
      if (!exprType.equals(booleanType)) {
        throw new SemanticAnalysisException(
            "Unary operator 'not' requires Boolean operand, " +
                "got " + exprType.getName(),
            this);
      }
      return booleanType;
    }

    if (op.equals("-")) {
      if (!exprType.equals(integerType)) {
        throw new SemanticAnalysisException(
            "Unary operator '-' requires Integer operand, " +
                "got " + exprType.getName(),
            this);
      }
      return integerType;
    }

    throw new SemanticAnalysisException(
        "Unknown unary operator: " + op, this);
  }

  public String toString() {
    return "(" + op + expr + ")";
  }

  public Value evaluate(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ExecutionException {

    Value exprValue = expr.evaluate(funcMap, varAndParamMap);

    if (op.equals("not")) {
      if (!(exprValue instanceof BooleanValue)) {
        throw new ExecutionException("Unary operator 'not' requires Boolean operand", this);
      }

      boolean boolValue = ((BooleanValue) exprValue).getValue();
      return new BooleanValue(!boolValue);
    }

    if (op.equals("-")) {
      if (!(exprValue instanceof IntegerValue)) {
        throw new ExecutionException("Unary operator '-' requires Integer operand", this);
      }

      int intValue = ((IntegerValue) exprValue).getValue();
      return new IntegerValue(-intValue);
    }

    throw new ExecutionException("Unknown unary operator: " + op, this);
  }
}
