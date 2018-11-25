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
    final Player player1 = players.get(1);

    final int blindAmount = 50;
    prepareBeforeFlop(players, blindAmount, drawPairOfAces1(), drawKingAndQueenOfDifferentColor());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[ALL_IN]. Player Jörn Action :[FOLD]. ", decision);
    int potRaisePerPlayerBeforeFlop = 2450;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerBeforeFlop, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("", decision);
    int potRaisePerPlayerFlop = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareRiver(Color.hearts, Ordinal.queen);
    pokerGame.playTurn(players);
    assertEquals(decision, "");

    prepareRiver(Color.spades, Ordinal.two);
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

  //@Test TODO: Fix this test
  public void testPotBothPlayersAllIn() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int blindAmount = 50;
    prepareBeforeFlop(players, blindAmount, drawPairOfAces1(), drawPairOfAces2());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[ALL_IN]. Player Jörn Action :[ALL_IN]. ", decision);
    int potRaisePerPlayerBeforeFlop = 2450;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerBeforeFlop, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("", decision);
    int potRaisePerPlayerFlop = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    pokerGame.playTurn(players);
    assertEquals(decision, "");

    prepareRiver(Color.spades, Ordinal.two);
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
    final Player player1 = players.get(1);

    final int blindAmount = 50;
    prepareBeforeFlop(players, blindAmount, drawBadPrivateHand1(), drawBadPrivateHand2());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerBeforeFlop = 0;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerBeforeFlop, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

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
    final Player player1 = players.get(1);

    final int blindAmount = 50;
    prepareBeforeFlop(players, blindAmount, drawPairOfEights1(), drawPairOfEights2());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerBeforeFlop = 100;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerBeforeFlop, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    final int player0ExpectedMarkersAfterRiver = player0.getNumberOfMarkers();
    final int player1ExpectedMarkersAfterRiver = player1.getNumberOfMarkers();

    System.out.println("pot : " + pokerGame.getPot());
    System.out.println("Player 0 has :[" + player0.getNumberOfMarkers() + "] markers. has little blind: " + player0.hasLittleBlind() );
    System.out.println("Player 1 has :[" + player1.getNumberOfMarkers() + "] markers. has big blind; " + player1.hasLittleBlind());

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    assertEquals(2 * PokerGame.TOTAL_MARKERS_PER_PLAYER, player0.getNumberOfMarkers() + player1.getNumberOfMarkers());
    assertEquals(player0.getNumberOfMarkers(), player0ExpectedMarkersAfterRiver + 2 * potRaisePerPlayerTotalRound + (int)(blindAmount*1.5));
    assertEquals(player1.getNumberOfMarkers(), player1ExpectedMarkersAfterRiver);
  }

  @Test
  public void testOneRaiseOneCheck() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 2500);
    players.stream().forEach(pokerGame::registerPlayer);
    final Player player0 = players.get(0);
    final Player player1 = players.get(1);

    final int blindAmount = 50;
    prepareBeforeFlop(players, blindAmount, drawPairOfNines(), drawPairOfKnights());
    String decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerBeforeFlop = 0;
    int potRaisePerPlayerTotalRound = potRaisePerPlayerBeforeFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerBeforeFlop, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, potRaisePerPlayerTotalRound, blindAmount);

    final int player0ExpectedMarkersAfterRiver = player0.getNumberOfMarkers();
    final int player1ExpectedMarkersAfterRiver = player1.getNumberOfMarkers();

    System.out.println("pot : " + pokerGame.getPot());
    System.out.println("Player 0 has :[" + player0.getNumberOfMarkers() + "] markers. has little blind: " + player0.hasLittleBlind() );
    System.out.println("Player 1 has :[" + player1.getNumberOfMarkers() + "] markers. has big blind; " + player1.hasLittleBlind());

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    assertEquals(2 * PokerGame.TOTAL_MARKERS_PER_PLAYER, player0.getNumberOfMarkers() + player1.getNumberOfMarkers());
    assertEquals(player0.getNumberOfMarkers(), player0ExpectedMarkersAfterRiver + 2 * potRaisePerPlayerTotalRound + (int)(blindAmount*1.5));
    assertEquals(player1.getNumberOfMarkers(), player1ExpectedMarkersAfterRiver);
  }

  private void assertPotAndMarkers(Player player0, Player player1, int potRaise, int bigBlind) {
    List<Player> players = Lists.newArrayList();
    players.add(player0);
    players.add(player1);
    assertEquals(pokerGame.getPot(), calculatePot(bigBlind, players));
    for (Player player:players) {
      assertEquals(PokerGame.TOTAL_MARKERS_PER_PLAYER - calculateBlindCost(player, bigBlind) - getPlayersPartInPot(player, players, potRaise), player.getNumberOfMarkers());
    }
  }

  private int calculatePot(int bigBlind, List<Player> players) {
    int totalPot = (int)(bigBlind * 1.5);
    System.out.println(totalPot);
    for (Player player:players) {
      if (!player.getAction().isFold()) {
        totalPot += player.getPartInPot();
      }
    }
    return totalPot;
  }

  private int getPlayersPartInPot(Player player, List<Player> players, int potRaisePerPlayer) {
    if (player.getAction().isFold()) {
      return 0;
    }
    int totalPotRaise = 0;
      if (!player.getAction().isFold()) {
        totalPotRaise += potRaisePerPlayer;
        System.out.println("potraiseperplayer: " + potRaisePerPlayer);
    }
    return totalPotRaise;
  }

  private boolean anyPlayerFolds(List<Player> players) {
    for (Player player:players) {
      if (player.getAction().isFold()) {
        return true;
      }
    }
    return false;
  }

  private int calculateBlindCost(Player player, int bigBlind) {
    int littleBlind = (int)(bigBlind / 2);
    if (player.hasLittleBlind()) {
      return littleBlind;
    }
    if (player.hasBigBlind()) {
      return bigBlind;
    }
    return 0;
  }

  private void prepareBeforeFlop(List<Player> players, int blindAmount, List<Card> privateHandPlayer0, List<Card> privateHandPlayer1) {
    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, blindAmount);
    pokerGame.setPrivateHand(players.get(0), privateHandPlayer0);
    pokerGame.setPrivateHand(players.get(1), privateHandPlayer1);
  }

  private void prepareFlop(List<Card> flopCards) {
    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(flopCards);
  }

  private void prepareRiver(Color spades, Ordinal two) {
    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(spades, two)));
  }

  private void prepareTurn(Color hearts, Ordinal queen) {
    pokerGame.increaseDraw();
    pokerGame.addToCommonHand(Arrays.asList(drawCard(hearts, queen)));
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
