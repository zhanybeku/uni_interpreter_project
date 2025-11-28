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
        columnNumber = 1;

        for (int i = 0; i < line.length(); i++) {
          char currentChar = line.charAt(i);
          if (Character.isWhitespace(currentChar)) {
            columnNumber++;
            continue;
          } else {
            StringBuilder currentString = new StringBuilder();
            int startColumn = columnNumber;

            if (currentChar == '"') {
              currentString.append(currentChar);
              i++;
              columnNumber++;

              boolean foundClosingQuote = false;
              while (i < line.length()) {
                char nextChar = line.charAt(i);

                if (nextChar == '"') {
                  currentString.append(nextChar);
                  foundClosingQuote = true;
                  i++;
                  columnNumber++;
                  break;
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
              i--;
            } else {
              if (isOperatorOrDelimiter(currentChar)) {
                if (i + 1 < line.length()) {
                  char nextCh = line.charAt(i + 1);
                  if (isMultiCharOperator(currentChar, nextCh)) {
                    if (i + 2 < line.length()) {
                      char thirdCh = line.charAt(i + 2);
                      if (isInvalidThreeCharSequence(currentChar, nextCh, thirdCh)) {
                        if (currentChar == '<' && nextCh == '=' && thirdCh == '=') {
                          if (i + 3 < line.length() && line.charAt(i + 3) == '=') {
                            tokens.add(new Token(String.valueOf(currentChar) + nextCh, lineNumber, columnNumber));
                            i += 1;
                            columnNumber += 2;
                            continue;
                          } else {
                            throw new LexException("Invalid operator sequence: " + currentChar + nextCh + thirdCh,
                                lineNumber,
                                columnNumber);
                          }
                        } else {
                          throw new LexException("Invalid operator sequence: " + currentChar + nextCh + thirdCh,
                              lineNumber,
                              columnNumber);
                        }
                      }
                    }
                    tokens.add(new Token(String.valueOf(currentChar) + nextCh, lineNumber, columnNumber));
                    i += 1;
                    columnNumber += 2;
                    continue;
                  }
                }
                tokens.add(new Token(String.valueOf(currentChar), lineNumber, columnNumber));
                columnNumber++;
                continue;
              }

              while (i < line.length() && !Character.isWhitespace(line.charAt(i)) &&
                  line.charAt(i) != '"' && !isOperatorOrDelimiter(line.charAt(i))) {
                char ch = line.charAt(i);

                if (!isValidCharacter(ch)) {
                  throw new LexException("Invalid character: " + ch, lineNumber, columnNumber);
                }

                currentString.append(ch);
                i++;
                columnNumber++;
              }

              String tokenValue = currentString.toString();
              if (!tokenValue.isEmpty()) {
                tokens.add(new Token(tokenValue, lineNumber, startColumn));
              }

              i--;
            }

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
    return Character.isLetterOrDigit(ch) || ch == '_';
  }

  private boolean isMultiCharOperator(char first, char second) {
    return (first == '=' && second == '=') ||
        (first == '<' && second == '=') ||
        (first == '>' && second == '=') ||
        (first == ':' && second == '=');
  }

  private boolean isInvalidThreeCharSequence(char first, char second, char third) {
    return (first == '<' && second == '=' && third == '=') ||
        (first == '>' && second == '=' && third == '=') ||
        (first == '=' && second == '=' && third == '=');
  }

  private boolean isOperatorOrDelimiter(char ch) {
    return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' ||
        ch == '=' || ch == '<' || ch == '>' ||
        ch == '(' || ch == ')' ||
        ch == ';' || ch == ',' || ch == ':';
  }

}
