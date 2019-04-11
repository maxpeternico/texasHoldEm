package poker;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class TestHumanPlayer {
  private final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void test1() {
    Keyboard keyboardMock = Mockito.mock(Keyboard.class);
    when(keyboardMock.getCharacter()).thenReturn("U");
    when(keyboardMock.getCharacter()).thenReturn("L");
    when(keyboardMock.getCharacter()).thenReturn("F");
    KeyboardHelper.setKeyBoard(keyboardMock);
    assertEquals(KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("A"), "", 1), "A");
    pokerGame.createHumanPlayer();
    pokerGame.createNumberOfRobotPlayers(1, 1);


  }
}
