package poker;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPot {
  final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testTwoPlayersOneAllInOneFoldPotEqualToLittleBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    pokerGame.setPrivateHand(player0, drawPairOfAces1());
    final Player player1 = players.get(1);
    pokerGame.setPrivateHand(player1, drawKingAndQueenOfDifferentColor());

    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, 50);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals(decision, "Player Thomas Action :[ALL_IN]. Player Jörn Action :[FOLD]. ");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(getBadFlop());
    decision = pokerGame.playFlop(players);
    assertEquals(decision, "");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.hearts, Ordinal.queen)));
    pokerGame.playTurn(players);
    assertEquals(decision, "");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.spades, Ordinal.two)));
    pokerGame.playRiver(players);
    assertEquals(decision, "");

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    final int numberOfMarkersForPlayer0 = player0.getNumberOfMarkers();
    final int numberOfMarkersForPlayer1 = player1.getNumberOfMarkers();
    System.out.println("Player 0 has :[" + numberOfMarkersForPlayer0 + "] markers.");
    System.out.println("Player 1 has :[" + numberOfMarkersForPlayer1 + "] markers.");
    assertEquals(numberOfMarkersForPlayer0 + numberOfMarkersForPlayer1, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
    assertEquals(numberOfMarkersForPlayer0, 2525);
    assertEquals(numberOfMarkersForPlayer1 , 2475);
  }

  @Test
  public void testPotBothPlayersAllIn() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    pokerGame.setPrivateHand(player0, drawPairOfAces1());
    final Player player1 = players.get(1);
    pokerGame.setPrivateHand(player1, drawPairOfAces2());

    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, 50);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals(decision, "Player Thomas Action :[ALL_IN]. Player Jörn Action :[ALL_IN]. ");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(getBadFlop());
    decision = pokerGame.playFlop(players);
    assertEquals(decision, "");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.hearts, Ordinal.queen)));
    pokerGame.playTurn(players);
    assertEquals(decision, "");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.spades, Ordinal.two)));
    pokerGame.playRiver(players);
    assertEquals(decision, "");

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    final int numberOfMarkersForPlayer0 = player0.getNumberOfMarkers();
    final int numberOfMarkersForPlayer1 = player1.getNumberOfMarkers();
    System.out.println("Player 0 has :[" + numberOfMarkersForPlayer0 + "] markers.");
    System.out.println("Player 1 has :[" + numberOfMarkersForPlayer1 + "] markers.");
    assertEquals(numberOfMarkersForPlayer0 + numberOfMarkersForPlayer1, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
    assertEquals(numberOfMarkersForPlayer0, 5000);
    assertEquals(numberOfMarkersForPlayer1 , 0);
  }

  @Test
  public void testPotBothPlayersBothCheckPotEqualToBigBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    pokerGame.setPrivateHand(player0, drawBadPrivateHand1());
    final Player player1 = players.get(1);
    pokerGame.setPrivateHand(player1, drawBadPrivateHand2());

    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, 50);
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals(decision, "Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(getBadFlop());
    decision = pokerGame.playFlop(players);
    assertEquals(decision, "Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.hearts, Ordinal.queen)));
    pokerGame.playTurn(players);
    assertEquals(decision, "Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ");

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.spades, Ordinal.two)));
    pokerGame.playRiver(players);
    assertEquals(decision, "Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ");

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    final int numberOfMarkersForPlayer0 = player0.getNumberOfMarkers();
    final int numberOfMarkersForPlayer1 = player1.getNumberOfMarkers();
    System.out.println("Player 0 has :[" + numberOfMarkersForPlayer0 + "] markers.");
    System.out.println("Player 1 has :[" + numberOfMarkersForPlayer1 + "] markers.");
    assertEquals(numberOfMarkersForPlayer0 + numberOfMarkersForPlayer1, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
    assertEquals(numberOfMarkersForPlayer0, 2525);
    assertEquals(numberOfMarkersForPlayer1 , 2475);
  }

  @Test
  public void testPotOneRaiseOneCheck() { // TODO: re-write as template for other unit tests
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    pokerGame.setPrivateHand(player0, drawPairOfEights1());
    final Player player1 = players.get(1);
    pokerGame.setPrivateHand(player1, drawPairOfEights2());

    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, 50);
    pokerGame.addToCommonHand(getBadFlop());

    String decision = pokerGame.playBeforeFlop(players);
    assertEquals(decision, "Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ");
    int potRaiseBeforeFlop = 100;
    assertEquals(pokerGame.getPot(), 25+50+2*potRaiseBeforeFlop);
    assertEquals(player0.getNumberOfMarkers(), 2500-50-potRaiseBeforeFlop);
    assertEquals(player1.getNumberOfMarkers(), 2500-25-potRaiseBeforeFlop);

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.hearts, Ordinal.queen)));
    pokerGame.playTurn(players);
    assertEquals(decision, "Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ");
    int potRaiseFlop = 50;
    assertEquals(pokerGame.getPot(), 25+50+2*potRaiseBeforeFlop+2*potRaiseFlop);
    assertEquals(player0.getNumberOfMarkers(), 2500-50-potRaiseBeforeFlop-potRaiseFlop);
    assertEquals(player1.getNumberOfMarkers(), 2500-25-potRaiseBeforeFlop-potRaiseFlop);

    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(Color.spades, Ordinal.two)));
    pokerGame.playRiver(players);
    assertEquals(decision, "Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ");
    final int potRaiseTurn = 50;
    assertEquals(pokerGame.getPot(), 25 + 50 + 2* potRaiseBeforeFlop + 2*potRaiseFlop + 2*potRaiseTurn);
    final int player0ExpectedMarkersAfterRiver = 2500 - 50 - potRaiseBeforeFlop - potRaiseFlop - potRaiseTurn;
    assertEquals(player0.getNumberOfMarkers(), player0ExpectedMarkersAfterRiver);
    final int player1ExpectedMarkersAfterRiver = 2500 - 25 - potRaiseBeforeFlop - potRaiseFlop - potRaiseTurn;
    assertEquals(player1.getNumberOfMarkers(), player1ExpectedMarkersAfterRiver);

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    final int numberOfMarkersForPlayer0 = player0.getNumberOfMarkers();
    final int numberOfMarkersForPlayer1 = player1.getNumberOfMarkers();
    System.out.println("Player 0 has :[" + numberOfMarkersForPlayer0 + "] markers.");
    System.out.println("Player 1 has :[" + numberOfMarkersForPlayer1 + "] markers.");
    assertEquals(numberOfMarkersForPlayer0 + numberOfMarkersForPlayer1, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
    assertEquals(player0ExpectedMarkersAfterRiver + 25 + 50 + 2* potRaiseBeforeFlop + 2*potRaiseFlop + 2*potRaiseTurn, player0.getNumberOfMarkers());
    assertEquals(player1ExpectedMarkersAfterRiver, player1.getNumberOfMarkers());
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
      pokerGame.addToCommonHand(getBadFlop());
      fail("Multiple card not detected.");
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Card [Color:[hearts] value:[knight]] is not present in the Deck!");
    }
  }

  @Test
  public void testOneRaiseOneCheck() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    pokerGame.setPrivateHand(player0, drawPairOfNines());
    final Player player1 = players.get(1);
    pokerGame.setPrivateHand(player1, drawPairOfKnights());

    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    pokerGame.addToCommonHand(getBadFlop());
    pokerGame.playRound(players);

    final int numberOfMarkersForPlayer0 = player0.getNumberOfMarkers();
    final int numberOfMarkersForPlayer1 = player1.getNumberOfMarkers();
    System.out.println("Player 0 has :[" + numberOfMarkersForPlayer0 + "] markers.");
    System.out.println("Player 1 has :[" + numberOfMarkersForPlayer1 + "] markers.");
    assertEquals(numberOfMarkersForPlayer0 + numberOfMarkersForPlayer1, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
    assertEquals(numberOfMarkersForPlayer0, 2400);
    assertEquals(numberOfMarkersForPlayer1 , 2600);
  }

  private List<Card> drawPairOfKnightsNegative() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  private List<Card> drawPairOfKnights() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.spades, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  private List<Card> drawPairOfEights1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.eight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.eight);
    hand.add(aceOfSpades);
    return hand;
  }

  private List<Card> drawPairOfEights2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.eight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.eight);
    hand.add(aceOfSpades);
    return hand;
  }

  private List<Card> drawPairOfNines() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.nine);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  private List<Card> drawBadPrivateHand2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.knight);
    hand.add(aceOfSpades);
    return hand;
  }

  private List<Card> drawBadPrivateHand1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.clubs, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.diamonds, Ordinal.ten);
    hand.add(aceOfSpades);
    return hand;
  }

  private List<Card> drawKingAndQueenOfDifferentColor() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  private ArrayList<Card> drawPairOfAces1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.ace);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.ace);
    hand.add(aceOfSpades);
    return hand;
  }

  private ArrayList<Card> drawPairOfAces2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.ace);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.ace);
    hand.add(aceOfSpades);
    return hand;
  }

  private Card drawCard(Color color, Ordinal ordinal) {
    final Card card = new Card(color, ordinal);
    return card;
  }

  private List<Card> getBadFlop() {
    final ArrayList<Card> cards = Lists.newArrayList();
    cards.add(drawCard(Color.diamonds, Ordinal.three));
    cards.add(drawCard(Color.clubs, Ordinal.nine));
    cards.add(drawCard(Color.hearts, Ordinal.knight));
    return cards;
  }

  @Test
  public void playUntilAPlayerWinsTheGame() {
    Player jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
    Player staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);
    List<Player> players = new ArrayList<>();
    players.add(jorn);
    players.add(staffan);

    pokerGame.initBlinds(players);
    do {
      pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
      pokerGame.playRound(players);
    } while (moreThanOnePlayerHasMarkers(players));
  }

  @Test
  public void playUntilAPlayerWinsTheGameMaxPlayers() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(7, PokerGame.TOTAL_MARKERS_PER_PLAYER);
    for (Player player : players) {
      pokerGame.registerPlayer(player);
    }

    pokerGame.initBlinds(players);
    do {
      pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
      pokerGame.playRound(players);
    } while (moreThanOnePlayerHasMarkers(players));
  }

  private boolean moreThanOnePlayerHasMarkers(List<Player> players) {
    int i = 0;
    for (Player player : players) {
      if (player.hasAnyMarkers()) {
        i++;
      }
    }
    if (i > 1) {
      return true;
    }
    return false;
  }

  @After
  public void clearGame() {
    pokerGame.clearGame();
  }

}
