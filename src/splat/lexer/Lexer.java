package splat.lexer;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Lexer {

	private File progFile;

	public Lexer(File progFile) {
		this.progFile = progFile;
	}

	public List<Token> tokenize() throws LexException {
		List<Token> tokens = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(this.progFile))) {
			int lineNumber = 1;
			int columnNumber = 1;

			String line;
			while ((line = reader.readLine()) != null) {      // TO DO: Fix the logic here. Something is definitely wrong...
				System.out.println("Line " + lineNumber + ": " + line);

				for (int i = 0; i < line.length(); i++) {
					char currentChar = line.charAt(i);
					if (Character.isWhitespace(currentChar)) {
						columnNumber++;
						continue;
					} else {
						StringBuilder currentString = new StringBuilder(); 
						int startColumn = columnNumber; 
						
						while (i < line.length() && !Character.isWhitespace(line.charAt(i))) { 
							currentString.append(line.charAt(i)); 
							i++;
							columnNumber++;
						}
						
						String tokenValue = currentString.toString();
						tokens.add(new Token(tokenValue, lineNumber, startColumn));
						
						i--;
					}

				}
				lineNumber++;
			}

		} catch (FileNotFoundException e) {
			System.out.println("Error reading file: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error reading file: " + e.getMessage());
		}

		return tokens;
	}

}
