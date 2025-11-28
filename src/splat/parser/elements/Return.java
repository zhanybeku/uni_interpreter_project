package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class Return extends Statement {

  private Expression expr;

  public Return(Expression expr, Token tok) {
    super(tok);
    this.expr = expr;
  }

  public Expression getExpr() {
    return expr;
  }

  public void analyze(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    if (expr != null) {
      expr.analyzeAndGetType(funcMap, varAndParamMap);
    }

  }

  public String toString() {
    if (expr != null) {
      return "return " + expr + " ;";
    } else {
      return "return ;";
    }
  }

  public void execute(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

    Value returnValue = null;
    if (expr != null) {
      returnValue = expr.evaluate(funcMap, varAndParamMap);
    }

    throw new ReturnFromCall(returnValue);
  }
}
