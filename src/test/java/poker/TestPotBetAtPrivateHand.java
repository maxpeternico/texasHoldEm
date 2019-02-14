package poker;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
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
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(drawPairOfAces1());
    privateHands.add(drawKingAndQueenOfDifferentColor());

    prepareBeforeFlop(players, bigBlindAmount, privateHands);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[ALL_IN]. Player Jörn Action :[FOLD]. ", decision);
    assertEquals(2525, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(0, player0.getNumberOfMarkers());
    assertEquals(2475, player1.getNumberOfMarkers());

    // No turn or river since only one player left

    pokerGame.getPotHandler().getPots().stream().forEach(e-> pokerGame.getTheWinner(e, players));
    assertEquals(2525, player0.getNumberOfMarkers());
    assertEquals(2475, player1.getNumberOfMarkers());

    pokerGame.resetTurn(players);
  }

  @Test
  public void testPotBothPlayersAllIn() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(drawPairOfAces1());
    privateHands.add(drawPairOfAces2());

    prepareBeforeFlop(players, bigBlindAmount, privateHands);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[ALL_IN]. Player Jörn Action :[ALL_IN]. ", decision);
    assertEquals(5000, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(0, player0.getNumberOfMarkers());
    assertEquals(0, player1.getNumberOfMarkers());

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("", decision);
    assertEquals(5000, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(0, player0.getNumberOfMarkers());
    assertEquals(0, player1.getNumberOfMarkers());

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("", decision);
    assertEquals(5000, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(0, player0.getNumberOfMarkers());
    assertEquals(0, player1.getNumberOfMarkers());

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("", decision);
    assertEquals(5000, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(0, player0.getNumberOfMarkers());
    assertEquals(0, player1.getNumberOfMarkers());

    pokerGame.getPotHandler().getPots().stream().forEach(e-> pokerGame.getTheWinner(e, players));

    assertEquals(5000, player0.getNumberOfMarkers());
    assertEquals(0, player1.getNumberOfMarkers());

    pokerGame.resetTurn(players);
  }

  @Test
  public void testPotBothPlayersBothCheckPotEqualToBigBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(drawBadPrivateHand1());
    privateHands.add(drawBadPrivateHand2());

    prepareBeforeFlop(players, bigBlindAmount, privateHands);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(100, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2450, player0.getNumberOfMarkers());
    assertEquals(2450, player1.getNumberOfMarkers());

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(100, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2450, player0.getNumberOfMarkers());
    assertEquals(2450, player1.getNumberOfMarkers());

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(100, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2450, player0.getNumberOfMarkers());
    assertEquals(2450, player1.getNumberOfMarkers());

    prepareRiver(Color.spades, Ordinal.seven);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(100, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2450, player0.getNumberOfMarkers());
    assertEquals(2450, player1.getNumberOfMarkers());

    pokerGame.getPotHandler().getPots().stream().forEach(e-> pokerGame.getTheWinner(e, players));
    assertEquals(2450, player0.getNumberOfMarkers());
    assertEquals(2550, player1.getNumberOfMarkers());

    pokerGame.resetTurn(players);
  }

  @Test
  public void testPotOneRaiseOneCheck() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(drawPairOfEights1());
    privateHands.add(drawPairOfEights2());
    prepareBeforeFlop(players, bigBlindAmount, privateHands);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(300, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2350, player0.getNumberOfMarkers());
    assertEquals(2350, player1.getNumberOfMarkers());

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(500, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2250, player0.getNumberOfMarkers());
    assertEquals(2250, player1.getNumberOfMarkers());

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(600, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2200, player0.getNumberOfMarkers());
    assertEquals(2200, player1.getNumberOfMarkers());

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(700, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2150, player0.getNumberOfMarkers());
    assertEquals(2150, player1.getNumberOfMarkers());

    pokerGame.getPotHandler().getPots().stream().forEach(e-> pokerGame.getTheWinner(e, players));

    assertEquals(2850, player0.getNumberOfMarkers());
    assertEquals(2150, player1.getNumberOfMarkers());

    pokerGame.resetTurn(players);
  }

  @Test
  public void testPotOneRaiseOneCheckBlindAmountHigh() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 600;
    pokerGame.setBlindAmount(600);
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(drawPairOfEights1());
    privateHands.add(drawPairOfEights2());
    prepareBeforeFlop(players, bigBlindAmount, privateHands);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    assertEquals(3300, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(700, player0.getNumberOfMarkers());
    assertEquals(1000, player1.getNumberOfMarkers());

    int potRaisePerPlayerBeforeFlop = 1200;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertMarkersForPlayers(players);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[ALL_IN]. Player Jörn Action :[ALL_IN]. ", decision);
    int potRaisePerPlayerFlop = 700;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertEquals(5000, getPokerGame().getPotHandler().getNumberOfMarkersInAllPots());
    assertMarkersForPlayers(players);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals(ALL_IN_PREVIOUSLY, decision);
    int potRaisePerPlayerTurn = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertMarkersForPlayers(players);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals(ALL_IN_PREVIOUSLY, decision);
    final int potRaiseRiver = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertMarkersForPlayers(players);

    final int player0NumberOfMarkersAfterRound = player0.getNumberOfMarkers();
    final int player1NumberOfMarkersAfterRound = player1.getNumberOfMarkers();

    pokerGame.getTheWinner(getPokerGame().getPotHandler().getPots().get(0), players);

    assertEquals(createMarkersDisappearErrorMessage(players),
        2 * PokerGame.TOTAL_MARKERS_PER_PLAYER,
        player0.getNumberOfMarkers() + player1.getNumberOfMarkers());

    final int calculatedPot = calculatePot(potRaisePerPlayerTotalRound, players, bigBlindAmount);
    assertEquals(5000, player0.getNumberOfMarkers());
    assertEquals(0, player1.getNumberOfMarkers());
    pokerGame.resetTurn(players);
  }

  @Test
  public void testDetectMultipleCard() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    pokerGame.setPrivateHand(player0, drawPairOfNinesAtFlop());
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
