package poker;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class TestHumanPlayer extends TestBase {

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

  @Test
  public void testHumanPlayerNegative() {
    Keyboard keyboardMock = Mockito.mock(Keyboard.class);
    defineHumanPlayerActionsNegative(keyboardMock);
    KeyboardHelper.setKeyBoard(keyboardMock);
    pokerGame.createHumanPlayer();
    pokerGame.createRobotPlayers();
    final List<Player> players = pokerGame.getRegisteredPlayers();
    pokerGame.initBlinds(players);
    final List<Card> humanPlayerHand = drawBadPrivateHand1();
    final List<Card> robotHand1 = drawPairOfEights1();
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(humanPlayerHand);
    privateHands.add(robotHand1);
    final int blindAmount = 50;
    prepareBeforeFlop(players, blindAmount, privateHands);
    BetManager betManager = new BetManager(players, blindAmount, pokerGame.getPotHandler());
    String decision = betManager.bet();
    assertEquals("Player Nahasapeemapetipul Aplymfopadskf Action :[ALL_IN]. Player Thomas Action :[FOLD]. ", decision);
  }

  private void defineHumanPlayerActionsNegative(Keyboard keyboardMock) {
    // What's your name?
    when(keyboardMock.getCharacter())
      .thenReturn("Nahasapeemapetipul Aplymfopadskf")

      // How many people do you want to play with?
      .thenReturn("+")
      // How many people do you want to play with?
      .thenReturn("U")
      // How many people do you want to play with?
      .thenReturn("t9")
      // How many people do you want to play with?
      .thenReturn("1")

      // Select action : (R)aise
      .thenReturn("G")
      .thenReturn("R")
      .thenReturn("100000000000")
      .thenReturn("2450")

      .thenReturn("C");
  }

  @Test
  public void testHumanPlayerBetting() {
    Keyboard keyboardMock = Mockito.mock(Keyboard.class);
    defineHumanPlayerActionsBetting(keyboardMock);
    KeyboardHelper.setKeyBoard(keyboardMock);
    pokerGame.createHumanPlayer();
    pokerGame.createRobotPlayers();
    final List<Player> players = pokerGame.getRegisteredPlayers();
    pokerGame.initBlinds(players);
    final List<Card> humanPlayerHand = drawBadPrivateHand1();
    final List<Card> robotHand1 = drawPairOfEights1();
    final List<Card> robotHand2 = drawPairOfKnights();
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(humanPlayerHand);
    privateHands.add(robotHand1);
    privateHands.add(robotHand2);
    final int blindAmount = 50;
    prepareBeforeFlop(players, blindAmount, privateHands);
    BetManager betManager = new BetManager(players, blindAmount, pokerGame.getPotHandler());
    String decision = betManager.bet();
    assertEquals("Player Ulf Action :[CHECK]. Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. Player Ulf Action :[CHECK]. ", decision);
    assertEquals(300, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());

    System.out.println("Play flop. ");
    prepareFlop(getBadFlop());
    betManager = new BetManager(players, blindAmount, pokerGame.getPotHandler());
    decision = betManager.bet();
    assertEquals("Player Ulf Action :[RAISE]. Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(450, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());

    prepareTurn(Lists.newArrayList(new Card(Color.diamonds, Ordinal.king)));
    betManager = new BetManager(players, blindAmount, pokerGame.getPotHandler());
    decision = betManager.bet();
    assertEquals("Player Ulf Action :[RAISE]. Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals("Player Ulf Action :[RAISE]. Player Thomas Action :[CHECK    assertEquals(300, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());\n]. Player Jörn Action :[CHECK]. ", decision);

    prepareRiver(Lists.newArrayList(new Card(Color.clubs, Ordinal.four)));
    betManager = new BetManager(players, blindAmount, pokerGame.getPotHandler());
    decision = betManager.bet();
    assertEquals("Player Ulf Action :[CHECK]. Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals("Player Ulf Action :[RAISE]. Player Thomas Action :[CHECK    assertEquals(300, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());\n]. Player Jörn Action :[CHECK]. ", decision);

    pokerGame.getTheWinner(pokerGame.getPotHandler(), players);

    assertEquals(players.get(0).getNumberOfMarkers(), 2700);
    assertEquals(players.get(1).getNumberOfMarkers(), 2400);
    assertEquals(players.get(2).getNumberOfMarkers(), 2400);
  }

  private void defineHumanPlayerActionsBetting(Keyboard keyboardMock) {
    // What's your name?
    when(keyboardMock.getCharacter())
      .thenReturn("Ulf")

      // How many people do you want to play with?
      .thenReturn("2")

      // Select action : (C)heck
      .thenReturn("C")
      .thenReturn("C")

      // Select action : (R)aise 100
      .thenReturn("R")
      .thenReturn("100")

      // Select action : (R)aise 100
      .thenReturn("R")
      .thenReturn("100")

      // Select action : (C)heck
      .thenReturn("C");
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

  @Override
  protected PokerGame getPokerGame() {
    return pokerGame;
  }
}
