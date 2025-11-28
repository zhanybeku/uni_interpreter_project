package splat.parser.elements;

import java.util.List;
import java.util.Map;
import splat.executor.BooleanValue;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

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

  public void analyze(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    Type conditionType = condition.analyzeAndGetType(funcMap, varAndParamMap);
    Type booleanType = new Type("Boolean");
    if (!conditionType.equals(booleanType)) {
      throw new SemanticAnalysisException(
          "Condition in while loop must be Boolean, got " + conditionType.getName(),
          condition);
    }

    for (Statement stmt : stmts) {
      stmt.analyze(funcMap, varAndParamMap);
    }
  }

  public String toString() {
    String result = "while (" + condition + ") do\n";
    for (Statement stmt : stmts) {
      result += "   " + stmt + "\n";
    }
    result += "end while ;";
    return result;
  }

  public void execute(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

    while (true) {
      Value conditionValue = condition.evaluate(funcMap, varAndParamMap);

      if (!(conditionValue instanceof BooleanValue)) {
        throw new ExecutionException("Condition must be Boolean", this);
      }

      BooleanValue boolValue = (BooleanValue) conditionValue;

      if (!boolValue.getValue()) {
        break;
      }

      for (Statement stmt : stmts) {
        stmt.execute(funcMap, varAndParamMap);
      }
    }
  }
}
