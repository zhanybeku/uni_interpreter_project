package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class Assignment extends Statement {

  private String label;
  private Expression expr;

  public Assignment(String label, Expression expr, Token tok) {
    super(tok);
    this.label = label;
    this.expr = expr;
  }

  public String getLabel() {
    return label;
  }

  public Expression getExpr() {
    return expr;
  }

  public String toString() {
    return label + " := " + expr + " ;";
  }

  public void analyze(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    Type varType = varAndParamMap.get(label);
    if (varType == null) {
      throw new SemanticAnalysisException(
          "Variable '" + label + "' is not declared", this);
    }

    Type exprType = expr.analyzeAndGetType(funcMap, varAndParamMap);

    if (!varType.equals(exprType)) {
      throw new SemanticAnalysisException(
          "Type mismatch: cannot assign " + exprType.getName() +
              " to variable '" + label + "' of type " + varType.getName(),
          this);
    }
  }
  
  public void execute(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {
    
    Value exprValue = expr.evaluate(funcMap, varAndParamMap);
    varAndParamMap.put(label, exprValue);
  }
}
