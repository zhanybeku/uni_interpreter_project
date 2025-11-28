package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class Print extends Statement {

  private Expression expr;

  public Print(Expression expr, Token tok) {
    super(tok);
    this.expr = expr;
  }

  public Expression getExpr() {
    return expr;
  }

  public void analyze(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    expr.analyzeAndGetType(funcMap, varAndParamMap);
  }

  public String toString() {
    return "print " + expr + " ;";
  }

  public void execute(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

    Value exprValue = expr.evaluate(funcMap, varAndParamMap);
    System.out.print(exprValue.toString());
  }
}
