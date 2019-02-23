package poker;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPotMoreThanTwoPlayers extends TestBase {

  final PokerGame pokerGame = PokerGame.getInstance();

  @Override
  protected PokerGame getPokerGame() {
    return pokerGame;
  }

  @Test
  public void testPotOneRaiseTwoCheck() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);
    final Player player2 = players.get(2);

    final int bigBlindAmount = 50;
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(drawPairOfKnights());
    privateHands.add(drawPairOfEights1());
    privateHands.add(drawPairOfEights2());
    prepareBeforeFlop(players, bigBlindAmount, privateHands);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. Player Anders Action :[CHECK]. ", decision);
    int potRaisePerPlayerBeforeFlop = 100;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertMarkersForPlayers(players);

    prepareFlop(getBadFlop());
    decision = pokerGame.playFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. Player Anders Action :[CHECK]. ", decision);
    assertMarkersForPlayers(players);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. Player Anders Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertMarkersForPlayers(players);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. Player Anders Action :[CHECK]. ", decision);
    final int potRaiseRiver = 200;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertMarkersForPlayers(players);

    final int player0NumberOfMarkersAfterRound = player0.getNumberOfMarkers();
    final int player1NumberOfMarkersAfterRound = player1.getNumberOfMarkers();
    final int player2NumberOfMarkersAfterRound = player2.getNumberOfMarkers();

    pokerGame.getTheWinner(getPokerGame().getPotHandler(), players);

    assertEquals(createMarkersDisappearErrorMessage(players),
        3 * PokerGame.TOTAL_MARKERS_PER_PLAYER,
        player0.getNumberOfMarkers() + player1.getNumberOfMarkers() + player2.getNumberOfMarkers());

    final int calculatedPot = calculatePot(potRaisePerPlayerTotalRound, players, bigBlindAmount);
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot, player0),
        player0NumberOfMarkersAfterRound + calculatedPot,
        player0.getNumberOfMarkers());
    assertEquals(player1NumberOfMarkersAfterRound, player1.getNumberOfMarkers());
    assertEquals(player2NumberOfMarkersAfterRound, player2.getNumberOfMarkers());
    pokerGame.resetTurn(players);
  }

  @After
  public void clearGame() {
    pokerGame.clearGame();
  }
}
