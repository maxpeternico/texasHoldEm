package poker;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class TestHumanPlayer {

  private final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testHumanPlayerGoesAllInEveryTimeMaxNumberOfPlayers() {
    Keyboard keyboardMock = Mockito.mock(Keyboard.class);
    defineHumanPlayerActionsAllIn(keyboardMock);
    KeyboardHelper.setKeyBoard(keyboardMock);
    //assertEquals(KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("A"), "", 1), "A");
    pokerGame.startGame();
  }

  private void defineHumanPlayerActionsAllIn(Keyboard keyboardMock) {
    // What's your name?
    when(keyboardMock.getCharacter())
      .thenReturn("Ulf")

      // How many people do you want to play with?
      .thenReturn("9")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O")

      // Select action : (A)ll in
      .thenReturn("A")

      // Press O to continue
      .thenReturn("O");
  }

  @Test
  public void testHumanPlayerFoldsEveryTimeMaxNumberOfPlayers() {
    Keyboard keyboardMock = Mockito.mock(Keyboard.class);
    defineHumanPlayerActionsFold(keyboardMock);
    KeyboardHelper.setKeyBoard(keyboardMock);
    pokerGame.startGame();
  }

  private void defineHumanPlayerActionsFold(Keyboard keyboardMock) {
    // What's your name?
    when(keyboardMock.getCharacter())
      .thenReturn("Ulf")

      // How many people do you want to play with?
      .thenReturn("9")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O")

      // Select action : (F)old
      .thenReturn("F")

      // Press O to continue
      .thenReturn("O");
  }

  @Test
  public void testHumanPlayerChecksEveryTimeMaxNumberOfPlayers() {
    Keyboard keyboardMock = Mockito.mock(Keyboard.class);
    defineHumanPlayerActionsCheck(keyboardMock);
    KeyboardHelper.setKeyBoard(keyboardMock);
    pokerGame.startGame();
  }

  private void defineHumanPlayerActionsCheck(Keyboard keyboardMock) {
    // What's your name?
    when(keyboardMock.getCharacter())
      .thenReturn("Ulf")

      // How many people do you want to play with?
      .thenReturn("9")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")
      .thenReturn("C")

      // Press O to continue
      .thenReturn("O");
  }

  @After
  public void clearGame() {
    pokerGame.clearGameForTests();
  }
}
