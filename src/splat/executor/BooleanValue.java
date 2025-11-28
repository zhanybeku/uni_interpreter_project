package splat.executor;

public class BooleanValue extends Value {
	
	private boolean value;
	
	public BooleanValue(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public String toString() {
		return Boolean.toString(value);
	}
}

