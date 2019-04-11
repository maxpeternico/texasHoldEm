package poker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

public class KeyboardHelper {
  private static final Logger logger = LogManager.getLogger(KeyboardHelper.class);

  private KeyboardHelper() {}

  static boolean allowedCharacterIsPressed(String input, List<String> allowedCharacters) {
    final char[] inputCharArray = input.toCharArray();
    for (char inputChar : inputCharArray) {   // "D"
      if (!isInputCharAllowed(allowedCharacters, inputChar)) return false;

    }
    return true;
  }

  private static boolean isInputCharAllowed(List<String> allowedCharacters, char inputChar) {
    for (String allowedCharacter : allowedCharacters) { // "[A, B, C]
      if (allowedCharacter.contains(String.valueOf(inputChar))) {
        return true;
      }
    }
    return false;
  }

  static String askForInput(String message) {
    System.out.println(message);
    Scanner keyboard = new Scanner(System.in, "UTF-8");
    return keyboard.next();
  }

  static String getCharFromKeyboard(List<String> allowedCharacters, String displayMessage, Integer inputLength) {
    logger.debug("Allowed characters: [" + allowedCharacters.toString() + "] displayMessage :[" + displayMessage + "]. ");
    String input = "";
    do {
      input = askForInput(displayMessage);
    } while (!allowedCharacterIsPressed(input, allowedCharacters) || !hasCorrectLength(input, inputLength));
    return input;
  }

  private static boolean hasCorrectLength(String input, Integer inputLength) {
    if (inputLength == null) return true;

    return (input.length() == inputLength);
  }
}
