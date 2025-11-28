package splat.parser.elements;

import java.util.Map;
import splat.executor.ExecutionException;
import splat.executor.IntegerValue;
import splat.executor.BooleanValue;
import splat.executor.StringValue;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;

public class VariableDecl extends Declaration {

	private String label;
	private Type type;
	
	public VariableDecl(String label, Type type, Token tok) {
		super(tok);
		this.label = label;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		return label + " : " + type + " ;";
	}
	
	public void execute(Map<String, FunctionDecl> funcMap,
	                   Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {
		
		Value defaultValue = createDefaultValue(type);
		varAndParamMap.put(label, defaultValue);
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
}
