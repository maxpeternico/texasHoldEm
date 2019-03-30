package poker;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestKeyboardInput {
  @Test
  public void testChar() {
    // User 1 gets pair of aces and raises high
    List<String> list = Lists.newArrayList();
    list.add("a");
    list.add("b");
    StringBuilder expectedResult = new StringBuilder();
    for (String string:list) {
      expectedResult.append(string);
    }
    final String charFromKeyboard = getCharFromKeyboard(list, "ab");
    assertEquals(expectedResult.toString(), charFromKeyboard);

  }

  @Test
  public void testNumbers() {
    // User 1 gets pair of aces and raises high
    List<String> list = Lists.newArrayList();
    list.add("1");
    list.add("2");
    list.add("3");
    list.add("4");
    list.add("5");
    list.add("6");
    list.add("7");
    list.add("8");
    list.add("9");
    list.add("0");
    StringBuilder expectedResult = new StringBuilder();
    for (String string:list) {
      expectedResult.append(string);
    }
    final String charFromKeyboard = getCharFromKeyboard(list, "1234567890");
    assertEquals(expectedResult.toString(), charFromKeyboard);
  }

  @Test
  public void testForbiddenNumbers() {
    // User 1 gets pair of aces and raises high
    List<String> list = Lists.newArrayList();
    list.add("0");
    list.add("1");
    list.add("2");
    final String charFromKeyboard = getCharFromKeyboard(list, "01");
    assertEquals("01", charFromKeyboard);
  }

  private String getCharFromKeyboard(List<String> allowedCharacters, String askForInput) {
    String input;
    do {
      input = askForInput;
    } while (!KeyboardHelper.allowedCharacterIsPressed(input, allowedCharacters));
    return input;
  }
}
