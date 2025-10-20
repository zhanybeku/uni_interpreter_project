package splat.lexer;

import java.lang.String;

public class Token {

	private String value;
    private int line;
    private int column;

    public Token(String value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return "Token(" + value + ", " + line + ", " + column + ")";
    }
}
