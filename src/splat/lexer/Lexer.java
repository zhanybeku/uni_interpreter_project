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
			while ((line = reader.readLine()) != null) {      
				// Reset column number at the start of the line:
        columnNumber = 1;

				for (int i = 0; i < line.length(); i++) {
					char currentChar = line.charAt(i);
					if (Character.isWhitespace(currentChar)) {
						columnNumber++;
						continue;
					} else {
						StringBuilder currentString = new StringBuilder(); 
						int startColumn = columnNumber; 
						
						// Check for string literals (enclosed in double quotes)
						if (currentChar == '"') {
							currentString.append(currentChar);
							i++;
							columnNumber++;
							
							// Find the closing quote
							boolean foundClosingQuote = false;
							while (i < line.length()) {
								char nextChar = line.charAt(i);
								
								// Check for invalid characters in string literal
								if (nextChar == '"' || nextChar == '\\' || nextChar == '\n' || nextChar == '\r') {
									if (nextChar == '"') {
										currentString.append(nextChar);
										foundClosingQuote = true;
										i++;
										columnNumber++;
										break;
									} else {
										throw new LexException("Invalid character in string literal: " + nextChar, lineNumber, columnNumber);
									}
								}
								
								currentString.append(nextChar);
								i++;
								columnNumber++;
							}
							
							if (!foundClosingQuote) {
								throw new LexException("Unfinished string literal", lineNumber, startColumn);
							}
							
							String tokenValue = currentString.toString();
							tokens.add(new Token(tokenValue, lineNumber, startColumn));
						} else {
							// Regular token (not a string)
							while (i < line.length() && !Character.isWhitespace(line.charAt(i)) && line.charAt(i) != '"') { 
								char ch = line.charAt(i);
								
								// Check for invalid characters
								if (!isValidCharacter(ch)) {
									throw new LexException("Invalid character: " + ch, lineNumber, columnNumber);
								}
								
								// Check for multi-character operators
								if (i + 1 < line.length()) {
									char nextCh = line.charAt(i + 1);
									if (isMultiCharOperator(ch, nextCh)) {
										// Check for invalid 3-character sequences like <==
										if (i + 2 < line.length()) {
											char thirdCh = line.charAt(i + 2);
											if (isInvalidThreeCharSequence(ch, nextCh, thirdCh)) {
												throw new LexException("Invalid operator sequence: " + ch + nextCh + thirdCh, lineNumber, columnNumber);
											}
										}
										currentString.append(ch);
										currentString.append(nextCh);
										i += 2;
										columnNumber += 2;
										break;
									}
								}
								
								currentString.append(ch); 
								i++;
								columnNumber++;
							}
							
							String tokenValue = currentString.toString();
							if (!tokenValue.isEmpty()) {
								tokens.add(new Token(tokenValue, lineNumber, startColumn));
							}
						}
						
            // Remove extra increment:
						i--;
					}

				}
				lineNumber++;
			}

		} catch (FileNotFoundException e) {
			throw new LexException("File not found: " + e.getMessage(), 1, 1);
		} catch (IOException e) {
			throw new LexException("Error reading file: " + e.getMessage(), 1, 1);
		}

		return tokens;
	}
	
	private boolean isValidCharacter(char ch) {
		// Based on SPLAT grammar: allow alphanumeric, underscore, and SPLAT operators/symbols
		return Character.isLetterOrDigit(ch) || 
		       ch == '_' ||  // for labels
		       ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' ||  // arithmetic operators
		       ch == '=' || ch == '<' || ch == '>' ||  // comparison operators  
		       ch == '(' || ch == ')' ||  // parentheses
		       ch == ';' || ch == ',' || ch == ':' ||  // punctuation
		       ch == '"';  // for string literals
	}
	
	private boolean isMultiCharOperator(char first, char second) {
		// Based on SPLAT grammar: ==, <=, >= are valid multi-character operators
		return (first == '=' && second == '=') ||  // ==
		       (first == '<' && second == '=') ||  // <=
		       (first == '>' && second == '=');    // >=
	}
	
	private boolean isInvalidThreeCharSequence(char first, char second, char third) {
		// Check for invalid 3-character operator sequences
		// <== is invalid (should be <= followed by =, but <== is not a valid operator)
		return (first == '<' && second == '=' && third == '=') ||  // <==
		       (first == '>' && second == '=' && third == '=') ||  // >==
		       (first == '=' && second == '=' && third == '=');    // ===
	}

}
