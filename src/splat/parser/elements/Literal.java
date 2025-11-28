package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.IntegerValue;
import splat.executor.BooleanValue;
import splat.executor.StringValue;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class Literal extends Expression {

  private String value;

  public Literal(String value, Token tok) {
    super(tok);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    if (value.equals("true") || value.equals("false")) {
      return new Type("Boolean");
    } else if (value.startsWith("\"") && value.endsWith("\"")) {
      return new Type("String");
    } else {

      try {
        Integer.parseInt(value);
        return new Type("Integer");
      } catch (NumberFormatException e) {
        throw new SemanticAnalysisException("Invalid literal value: " + value, this);
      }
    }
  }

  public String toString() {
    return value;
  }

  public Value evaluate(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ExecutionException {

    if (value.equals("true")) {
      return new BooleanValue(true);
    } else if (value.equals("false")) {
      return new BooleanValue(false);
    } else if (value.startsWith("\"") && value.endsWith("\"")) {
      String stringValue = value.substring(1, value.length() - 1);
      return new StringValue(stringValue);
    } else {
      try {
        int intValue = Integer.parseInt(value);
        return new IntegerValue(intValue);
      } catch (NumberFormatException e) {
        throw new ExecutionException("Invalid literal value: " + value, this);
      }
    }
  }
}
