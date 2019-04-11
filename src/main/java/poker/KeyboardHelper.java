package poker;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeyboardHelper {
  private static Keyboard keyboard;

  private static final Logger logger = LogManager.getLogger(KeyboardHelper.class);

  public KeyboardHelper(Keyboard keyboard) {
    this.keyboard = keyboard;
  }

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
    return keyboard.getCharacter();
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

  public static void setKeyBoard(Keyboard keyboard) {
    new KeyboardHelper(keyboard);
  }
}
