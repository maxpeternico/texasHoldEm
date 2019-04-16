package poker;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;

public class TestPotBetAtFlop extends TestBase {
  private final PokerGame pokerGame = PokerGame.getInstance();

  @Override
  protected PokerGame getPokerGame() {
    return pokerGame;
  }

  @Test
  public void testOneRaiseAtFlopOneCheck() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int bigBlindAmount = 50;
    List<List<Card>> privateHands = Lists.newArrayList();
    privateHands.add(drawPairOfNinesAtFlop());
    privateHands.add(drawPairOfKnightsAtFlop());
    prepareBeforeFlop(players, bigBlindAmount, privateHands);

    BetManager betManager = new BetManager(players, 50, pokerGame.getPotHandler());
    pokerGame.setBetManager(betManager);
    String decision = pokerGame.decideBet(players);

    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerBeforeFlop = 50;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertMarkersForPlayers(players);

    final List<Card> flop = getFlopWithNineAndKnight();
    prepareFlop(flop);
    pokerGame.setFlopToBetManager(flop);
    pokerGame.updateTurnForBetManager();
    decision = pokerGame.decideBet(players);

    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 100;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertMarkersForPlayers(players);

    final List<Card> turnCard = Lists.newArrayList(new Card(Color.spades, Ordinal.five));
    prepareTurn(turnCard);
    pokerGame.setTurnToBetManager(turnCard);
    pokerGame.updateTurnForBetManager();
    decision = pokerGame.decideBet(players);

    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertMarkersForPlayers(players);

    final List<Card> riverCard = Lists.newArrayList(new Card(Color.hearts, Ordinal.queen));
    prepareRiver(riverCard);
    pokerGame.setRiverToBetManager(riverCard);
    pokerGame.updateTurnForBetManager();
    decision = pokerGame.decideBet(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 200;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertMarkersForPlayers(players);

    final int player0NumberOfMarkersAfterRound = player0.getNumberOfMarkers();
    final int player1NumberOfMarkersAfterRound = player1.getNumberOfMarkers();

    pokerGame.getTheWinner(getPokerGame().getPotHandler(), players);

    assertEquals(createMarkersDisappearErrorMessage(players),
                 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER,
                 player0.getNumberOfMarkers() + player1.getNumberOfMarkers());

    final int calculatedPot = calculatePot(potRaisePerPlayerTotalRound, players);
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot, player0),
                 player0NumberOfMarkersAfterRound + calculatedPot,
                 player0.getNumberOfMarkers());

    assertEquals(player1NumberOfMarkersAfterRound, player1.getNumberOfMarkers());
    pokerGame.resetTurn(players);
  }

  @After
  public void clearGame() {
    pokerGame.clearGameForTests();
  }
}
