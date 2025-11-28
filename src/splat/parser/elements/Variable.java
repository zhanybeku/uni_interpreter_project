package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class Variable extends Expression {

  private String label;

  public Variable(String label, Token tok) {
    super(tok);
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    Type varType = varAndParamMap.get(label);
    if (varType == null) {
      throw new SemanticAnalysisException(
          "Variable '" + label + "' is not declared", this);
    }
    return varType;
  }

  public String toString() {
    return label;
  }

  public Value evaluate(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ExecutionException {

    Value varValue = varAndParamMap.get(label);
    if (varValue == null) {
      throw new ExecutionException("Variable '" + label + "' is not declared", this);
    }
    return varValue;
  }
}
