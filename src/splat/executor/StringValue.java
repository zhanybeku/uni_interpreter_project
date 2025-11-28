package splat.executor;

public class StringValue extends Value {
	
	private String value;
	
	public StringValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return value;
	}
}

