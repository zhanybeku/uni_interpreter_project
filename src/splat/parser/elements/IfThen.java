package splat.parser.elements;

import java.util.List;
import java.util.Map;
import splat.executor.BooleanValue;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

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

  public void analyze(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    Type conditionType = condition.analyzeAndGetType(funcMap, varAndParamMap);
    Type booleanType = new Type("Boolean");
    if (!conditionType.equals(booleanType)) {
      throw new SemanticAnalysisException(
          "Condition in if statement must be Boolean, got " + conditionType.getName(),
          condition);
    }

    for (Statement stmt : thenStmts) {
      stmt.analyze(funcMap, varAndParamMap);
    }

    if (elseStmts != null) {
      for (Statement stmt : elseStmts) {
        stmt.analyze(funcMap, varAndParamMap);
      }
    }
  }

  public String toString() {
    String result = "if (" + condition + ") then\n";
    for (Statement stmt : thenStmts) {
      result += "   " + stmt + "\n";
    }
    if (elseStmts != null) {
      result += "else\n";
      for (Statement stmt : elseStmts) {
        result += "   " + stmt + "\n";
      }
    }
    result += "end if ;";
    return result;
  }

  public void execute(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

    Value conditionValue = condition.evaluate(funcMap, varAndParamMap);

    if (!(conditionValue instanceof BooleanValue)) {
      throw new ExecutionException("Condition must be Boolean", this);
    }

    BooleanValue boolValue = (BooleanValue) conditionValue;

    if (boolValue.getValue()) {
      for (Statement stmt : thenStmts) {
        stmt.execute(funcMap, varAndParamMap);
      }
    } else if (elseStmts != null) {
      for (Statement stmt : elseStmts) {
        stmt.execute(funcMap, varAndParamMap);
      }
    }
  }
}
