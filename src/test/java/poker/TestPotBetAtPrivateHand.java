package poker;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPotBetAtPrivateHand extends TestBase {

  final PokerGame pokerGame = PokerGame.getInstance();

  @Override
  protected PokerGame getPokerGame() {
    return pokerGame;
  }

  @Test
  public void testTwoPlayersOneAllInOneFoldPotEqualToLittleBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    prepareBeforeFlop(players, bigBlindAmount, drawPairOfAces1(), drawKingAndQueenOfDifferentColor());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[ALL_IN]. Player Jörn Action :[FOLD]. ", decision);
    int potRaisePerPlayerBeforeFlop = 2450;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    // No turn or river since only one player left

    final int player0NumberOfMarkersAfterRound = player0.getNumberOfMarkers();
    final int player1NumberOfMarkersAfterRound = player1.getNumberOfMarkers();

    pokerGame.getTheWinner(players);

    assertEquals(createMarkersDisappearErrorMessage(player0, player1),
                 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER,
                 player0.getNumberOfMarkers() + player1.getNumberOfMarkers());

    final int calculatedPot = calculatePot(potRaisePerPlayerTotalRound, players, bigBlindAmount);
    System.out.println(calculatedPot);
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot, player0),
                 player0NumberOfMarkersAfterRound + calculatedPot,
                 player0.getNumberOfMarkers());
    assertEquals(player1NumberOfMarkersAfterRound, player1.getNumberOfMarkers());
    pokerGame.resetTurn(players);
  }

  // @Test TODO: Fix bug counterplauer checks when other player goes all-in
  public void testPotBothPlayersAllIn() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    prepareBeforeFlop(players, bigBlindAmount, drawPairOfAces1(), drawPairOfAces2());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[ALL_IN]. Player Jörn Action :[ALL_IN]. ", decision);
    int potRaisePerPlayerBeforeFlop = 2450;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerBeforeFlop, bigBlindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("", decision);
    int potRaisePerPlayerFlop = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("", decision);
    int potRaisePerPlayerTurn = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("", decision);
    final int potRaiseRiver = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    final int player0NumberOfMarkersAfterRound = player0.getNumberOfMarkers();
    final int player1NumberOfMarkersAfterRound = player1.getNumberOfMarkers();

    pokerGame.getTheWinner(players);

    final int numberOfMarkersForPlayer0 = player0.getNumberOfMarkers();
    final int numberOfMarkersForPlayer1 = player1.getNumberOfMarkers();
    System.out.println("Player 0 has :[" + numberOfMarkersForPlayer0 + "] markers.");
    System.out.println("Player 1 has :[" + numberOfMarkersForPlayer1 + "] markers.");
    final int calculatedPot = calculatePot(potRaisePerPlayerTotalRound, players, bigBlindAmount);

    assertEquals(createMarkersDisappearErrorMessage(player0, player1), numberOfMarkersForPlayer0 + numberOfMarkersForPlayer1, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot, player0),
        player0NumberOfMarkersAfterRound + calculatedPot,
        player0.getNumberOfMarkers());
    assertEquals(player1NumberOfMarkersAfterRound, player1.getNumberOfMarkers());

    pokerGame.resetTurn(players);
  }

  @Test
  public void testPotBothPlayersBothCheckPotEqualToBigBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    prepareBeforeFlop(players, bigBlindAmount, drawBadPrivateHand1(), drawBadPrivateHand2());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerBeforeFlop = 0;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareRiver(Color.spades, Ordinal.seven);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaiseRiver = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    final int player0NumberOfMarkersAfterRound = player0.getNumberOfMarkers();
    final int player1NumberOfMarkersAfterRound = player1.getNumberOfMarkers();

    pokerGame.getTheWinner(players);

    assertEquals(createMarkersDisappearErrorMessage(player0, player1),
        2 * PokerGame.TOTAL_MARKERS_PER_PLAYER,
        player0.getNumberOfMarkers() + player1.getNumberOfMarkers());

    final int calculatedPot = calculatePot(potRaisePerPlayerTotalRound, players, bigBlindAmount);
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot, player1),
        player1NumberOfMarkersAfterRound + calculatedPot,
        player1.getNumberOfMarkers());
    assertEquals(player0NumberOfMarkersAfterRound, player0.getNumberOfMarkers());
    pokerGame.resetTurn(players);
  }

  @Test
  public void testPotOneRaiseOneCheck() { // TODO: re-write as template for other unit tests
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    prepareBeforeFlop(players, bigBlindAmount, drawPairOfEights1(), drawPairOfEights2());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerBeforeFlop = 100;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 100;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, bigBlindAmount);

    final int player0NumberOfMarkersAfterRound = player0.getNumberOfMarkers();
    final int player1NumberOfMarkersAfterRound = player1.getNumberOfMarkers();

    pokerGame.getTheWinner(players);

    assertEquals(createMarkersDisappearErrorMessage(player0, player1),
                 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER,
                 player0.getNumberOfMarkers() + player1.getNumberOfMarkers());

    final int calculatedPot = calculatePot(potRaisePerPlayerTotalRound, players, bigBlindAmount);
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot, player0),
                 player0NumberOfMarkersAfterRound + calculatedPot,
                 player0.getNumberOfMarkers());
    assertEquals(player1NumberOfMarkersAfterRound, player1.getNumberOfMarkers());
    pokerGame.resetTurn(players);
  }

  @Test
  public void testDetectMultipleCard() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    pokerGame.setPrivateHand(player0, drawPairOfNines());
    final Player player1 = players.get(1);
    pokerGame.setPrivateHand(player1, drawPairOfKnightsNegative());

    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    try {
      pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.hearts, Ordinal.knight)));
      fail("Multiple card not detected.");
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Card [Color:[hearts] value:[knight]] is not present in the Deck!");
    }
  }

  @After
  public void clearGame() {
    pokerGame.clearGame();
  }
}
