package splat.semanticanalyzer;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import splat.parser.elements.Declaration;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.IfThen;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.Return;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.parser.elements.VariableDecl;
import splat.parser.elements.WhileLoop;

public class SemanticAnalyzer {

  private ProgramAST progAST;

  private Map<String, FunctionDecl> funcMap;
  private Map<String, Type> progVarMap;
  private static final Type VOID_TYPE = new Type("void");

  public SemanticAnalyzer(ProgramAST progAST) {
    this.progAST = progAST;
  }

  public void analyze() throws SemanticAnalysisException {

    // Checks to make sure we don't use the same labels more than once
    // for our program functions and variables
    checkNoDuplicateProgLabels();

    // This sets the maps that will be needed later when we need to
    // typecheck variable references and function calls in the
    // program body
    setProgVarAndFuncMaps();

    // Perform semantic analysis on the functions
    for (FunctionDecl funcDecl : funcMap.values()) {
      analyzeFuncDecl(funcDecl);
    }

    checkNoFuncParamNameConflicts();
    
    // Perform semantic analysis on the program body
    for (Statement stmt : progAST.getStmts()) {
      checkNoReturnInProgramBody(stmt);
      stmt.analyze(funcMap, progVarMap);
    }

  }

  private void analyzeFuncDecl(FunctionDecl funcDecl) throws SemanticAnalysisException {

    // Checks to make sure we don't use the same labels more than once
    // among our function parameters, local variables, and function names
    checkNoDuplicateFuncLabels(funcDecl);

    // Get the types of the parameters and local variables
    Map<String, Type> varAndParamMap = getVarAndParamMap(funcDecl);

    Type funcRetType = funcDecl.getRetType();

    // Perform semantic analysis on the function body
    for (Statement stmt : funcDecl.getStmts()) {
      stmt.analyze(funcMap, varAndParamMap);
      checkReturnStatementInFunction(stmt, funcDecl, funcRetType, varAndParamMap);
    }

    boolean hasReturn = hasReturnStatement(funcDecl.getStmts());
    if (!funcRetType.equals(VOID_TYPE) && !hasReturn) {
      throw new SemanticAnalysisException(
          "Function '" + funcDecl.getLabel() + "' returns " + funcRetType.getName() +
              " but has no return statement",
          funcDecl);
    }
  }

  private Map<String, Type> getVarAndParamMap(FunctionDecl funcDecl) {

    // FIXME: Somewhat similar to setProgVarAndFuncMaps()
    Map<String, Type> varAndParamMap = new HashMap<String, Type>();

    for (VariableDecl param : funcDecl.getParams()) {
      varAndParamMap.put(param.getLabel(), param.getType());
    }

    for (VariableDecl locVar : funcDecl.getLocVarDecls()) {
      varAndParamMap.put(locVar.getLabel(), locVar.getType());
    }

    return varAndParamMap;
  }

  private void checkNoDuplicateFuncLabels(FunctionDecl funcDecl)
      throws SemanticAnalysisException {

    // FIXME: Similar to checkNoDuplicateProgLabels()
    Set<String> labels = new HashSet<String>();
    String funcName = funcDecl.getLabel();

    labels.add(funcName);

    for (VariableDecl param : funcDecl.getParams()) {
      String label = param.getLabel();
      if (labels.contains(label)) {
        throw new SemanticAnalysisException("Cannot have duplicate label '"
            + label + "' in function '" + funcName + "'", param);
      }
      labels.add(label);
    }

    for (VariableDecl locVar : funcDecl.getLocVarDecls()) {
      String label = locVar.getLabel();
      if (labels.contains(label)) {
        throw new SemanticAnalysisException("Cannot have duplicate label '"
            + label + "' in function '" + funcName + "'", locVar);
      }
      labels.add(label);
    }
  }

  private void checkNoDuplicateProgLabels() throws SemanticAnalysisException {

    Set<String> labels = new HashSet<String>();

    for (Declaration decl : progAST.getDecls()) {
      String label = decl.getLabel();

      if (labels.contains(label)) {
        throw new SemanticAnalysisException("Cannot have duplicate label '"
            + label + "' in program", decl);
      }
      labels.add(label);
    }
  }

  private void checkNoFuncParamNameConflicts() throws SemanticAnalysisException {
    
    Set<String> paramNames = new HashSet<String>();
    for (FunctionDecl funcDecl : funcMap.values()) {
      for (VariableDecl param : funcDecl.getParams()) {
        paramNames.add(param.getLabel());
      }
    }
    
    for (FunctionDecl funcDecl : funcMap.values()) {
      String funcName = funcDecl.getLabel();
      if (paramNames.contains(funcName)) {
        throw new SemanticAnalysisException(
          "Function name '" + funcName + "' conflicts with a parameter name", funcDecl);
      }
    }
  }

  private void checkNoReturnInProgramBody(Statement stmt) throws SemanticAnalysisException {
    
    if (stmt instanceof Return) {
      throw new SemanticAnalysisException(
        "Return statement not allowed in program body", stmt);
    }
    
    if (stmt instanceof IfThen) {
      IfThen ifThen = (IfThen) stmt;
      for (Statement nestedStmt : ifThen.getThenStmts()) {
        checkNoReturnInProgramBody(nestedStmt);
      }
      if (ifThen.getElseStmts() != null) {
        for (Statement nestedStmt : ifThen.getElseStmts()) {
          checkNoReturnInProgramBody(nestedStmt);
        }
      }
    } else if (stmt instanceof WhileLoop) {
      WhileLoop whileLoop = (WhileLoop) stmt;
      for (Statement nestedStmt : whileLoop.getStmts()) {
        checkNoReturnInProgramBody(nestedStmt);
      }
    }
  }

  private void checkReturnStatementInFunction(Statement stmt, FunctionDecl funcDecl,
      Type funcRetType, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
    
    if (stmt instanceof Return) {
      Return returnStmt = (Return) stmt;
      Expression returnExpr = returnStmt.getExpr();

      if (funcRetType.equals(VOID_TYPE)) {
        if (returnExpr != null) {
          throw new SemanticAnalysisException(
              "Function '" + funcDecl.getLabel() + "' returns void, " +
                  "cannot return a value",
              returnStmt);
        }
      } else {
        if (returnExpr == null) {
          throw new SemanticAnalysisException(
              "Function '" + funcDecl.getLabel() + "' returns " + funcRetType.getName() +
                  ", must return a value",
              returnStmt);
        } else {
          Type returnType = returnExpr.analyzeAndGetType(funcMap, varAndParamMap);
          if (!returnType.equals(funcRetType)) {
            throw new SemanticAnalysisException(
                "Return type mismatch in function '" + funcDecl.getLabel() +
                    "': expected " + funcRetType.getName() + ", got " + returnType.getName(),
                returnStmt);
          }
        }
      }
    }
    
    if (stmt instanceof IfThen) {
      IfThen ifThen = (IfThen) stmt;
      for (Statement nestedStmt : ifThen.getThenStmts()) {
        checkReturnStatementInFunction(nestedStmt, funcDecl, funcRetType, varAndParamMap);
      }
      if (ifThen.getElseStmts() != null) {
        for (Statement nestedStmt : ifThen.getElseStmts()) {
          checkReturnStatementInFunction(nestedStmt, funcDecl, funcRetType, varAndParamMap);
        }
      }
    } else if (stmt instanceof WhileLoop) {
      WhileLoop whileLoop = (WhileLoop) stmt;
      for (Statement nestedStmt : whileLoop.getStmts()) {
        checkReturnStatementInFunction(nestedStmt, funcDecl, funcRetType, varAndParamMap);
      }
    }
  }

  private boolean hasReturnStatement(List<Statement> stmts) {
    for (Statement stmt : stmts) {
      if (stmt instanceof Return) {
        return true;
      }
      if (stmt instanceof IfThen) {
        IfThen ifThen = (IfThen) stmt;
        if (hasReturnStatement(ifThen.getThenStmts())) {
          return true;
        }
        if (ifThen.getElseStmts() != null && hasReturnStatement(ifThen.getElseStmts())) {
          return true;
        }
      } else if (stmt instanceof WhileLoop) {
        WhileLoop whileLoop = (WhileLoop) stmt;
        if (hasReturnStatement(whileLoop.getStmts())) {
          return true;
        }
      }
    }
    return false;
  }

  private void setProgVarAndFuncMaps() {

    funcMap = new HashMap<String, FunctionDecl>();
    progVarMap = new HashMap<String, Type>();

    for (Declaration decl : progAST.getDecls()) {

      String label = decl.getLabel();

      if (decl instanceof FunctionDecl) {
        FunctionDecl funcDecl = (FunctionDecl) decl;
        funcMap.put(label, funcDecl);

      } else if (decl instanceof VariableDecl) {
        VariableDecl varDecl = (VariableDecl) decl;
        progVarMap.put(label, varDecl.getType());
      }
    }
  }
}
