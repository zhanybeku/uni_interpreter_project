package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class PrintLine extends Statement {

  public PrintLine(Token tok) {
    super(tok);
  }

  public void analyze(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

  }

  public String toString() {
    return "print_line ;";
  }

  public void execute(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

    System.out.println();
  }
}
