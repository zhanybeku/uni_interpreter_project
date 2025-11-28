package splat.executor;

public class IntegerValue extends Value {
	
	private int value;
	
	public IntegerValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}
}

