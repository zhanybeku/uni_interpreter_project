package splat.parser.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.executor.IntegerValue;
import splat.executor.BooleanValue;
import splat.executor.StringValue;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

public class FunctionCallExpr extends Expression {

  private String label;
  private List<Expression> args;

  public FunctionCallExpr(String label, List<Expression> args, Token tok) {
    super(tok);
    this.label = label;
    this.args = args;
  }

  public String getLabel() {
    return label;
  }

  public List<Expression> getArgs() {
    return args;
  }

  public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
      Map<String, Type> varAndParamMap)
      throws SemanticAnalysisException {

    FunctionDecl funcDecl = funcMap.get(label);
    if (funcDecl == null) {
      throw new SemanticAnalysisException(
          "Function '" + label + "' is not declared", this);
    }

    List<VariableDecl> params = funcDecl.getParams();
    if (args.size() != params.size()) {
      throw new SemanticAnalysisException(
          "Function '" + label + "' expects " + params.size() +
              " argument(s), got " + args.size(),
          this);
    }

    for (int i = 0; i < args.size(); i++) {
      Type argType = args.get(i).analyzeAndGetType(funcMap, varAndParamMap);
      Type paramType = params.get(i).getType();
      if (!argType.equals(paramType)) {
        throw new SemanticAnalysisException(
            "Type mismatch in argument " + (i + 1) + " of function '" + label +
                "': expected " + paramType.getName() + ", got " + argType.getName(),
            args.get(i));
      }
    }

    return funcDecl.getRetType();
  }

  public Value evaluate(Map<String, FunctionDecl> funcMap,
      Map<String, Value> varAndParamMap) throws ExecutionException {
    
    FunctionDecl funcDecl = funcMap.get(label);
    if (funcDecl == null) {
      throw new ExecutionException("Function '" + label + "' is not declared", this);
    }
    
    List<Value> argValues = new java.util.ArrayList<Value>();
    for (Expression arg : args) {
      Value argValue = arg.evaluate(funcMap, varAndParamMap);
      argValues.add(argValue);
    }
    
    Map<String, Value> funcScope = new HashMap<String, Value>();
    
    List<VariableDecl> params = funcDecl.getParams();
    for (int i = 0; i < params.size(); i++) {
      VariableDecl param = params.get(i);
      funcScope.put(param.getLabel(), argValues.get(i));
    }
    
    for (VariableDecl locVar : funcDecl.getLocVarDecls()) {
      Value defaultValue = createDefaultValue(locVar.getType());
      funcScope.put(locVar.getLabel(), defaultValue);
    }
    
    try {
      for (Statement stmt : funcDecl.getStmts()) {
        stmt.execute(funcMap, funcScope);
      }
      
      return null;
      
    } catch (ReturnFromCall ex) {
      return ex.getReturnVal();
    }
  }
  
  private Value createDefaultValue(Type type) throws ExecutionException {
    String typeName = type.getName();
    
    if (typeName.equals("Integer")) {
      return new IntegerValue(0);
    } else if (typeName.equals("Boolean")) {
      return new BooleanValue(false);
    } else if (typeName.equals("String")) {
      return new StringValue("");
    } else {
      throw new ExecutionException("Unknown type: " + typeName, this);
    }
  }

  public String toString() {
    String result = label + "(";
    for (int i = 0; i < args.size(); i++) {
      if (i > 0)
        result += ", ";
      result += args.get(i);
    }
    result += ")";
    return result;
  }
}
