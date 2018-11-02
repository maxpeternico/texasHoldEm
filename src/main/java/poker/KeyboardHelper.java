package poker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

public class KeyboardHelper {
  private static final Logger logger = LogManager.getLogger(KeyboardHelper.class);

  static boolean allowedCharacterIsPressed(String input, List<String> allowedCharacters) {
    final char[] inputCharArray = input.toCharArray();
    for (char inputChar : inputCharArray) {
      for (String allowedCharacter : allowedCharacters) {
        if (allowedCharacter.contains(String.valueOf(inputChar))) {
          return true;
        }
      }
    }
    return false;
  }

  public static String askForInput(String message) {
    System.out.println(message);
    Scanner keyboard = new Scanner(System.in);
    String input = keyboard.next();
    return input;
  }

  public static String getCharFromKeyboard(List<String> allowedCharacters, String displayMessage) {
    logger.debug("Allowed characters: [" + allowedCharacters.toString() + "] displayMessage :[" + displayMessage + "]. ");
    String input = "";
    do {
      input = askForInput(displayMessage);
    } while (!allowedCharacterIsPressed(input, allowedCharacters));
    return input;
  }

}
