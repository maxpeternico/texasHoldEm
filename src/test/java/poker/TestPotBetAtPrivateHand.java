package poker;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPotBetAtPrivateHand {

  final PokerGame pokerGame = PokerGame.getInstance();

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
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot),
                 player0NumberOfMarkersAfterRound + calculatedPot,
                 player0.getNumberOfMarkers());
    assertEquals(player1NumberOfMarkersAfterRound, player1.getNumberOfMarkers());
    pokerGame.resetTurn(players);
  }

  private String createIncorrectNumberOfMarkersForWinnerMessage(int potRaisePerPlayerTotalRound, int calculatedPot) {
    return "Incorrect number of markers for player, potRaisePerPlayerTotalRound :[" + potRaisePerPlayerTotalRound + "] calculated pot :[" + calculatedPot + "]";
  }

  private String createMarkersDisappearErrorMessage(Player player0, Player player1) {
    return "Pot size not equal. Number of markers for player0 :[" + player0.getNumberOfMarkers() + "] number of markers for player1 :[" + player1.getNumberOfMarkers() + "]";
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
    assertEquals(createIncorrectNumberOfMarkersForWinnerMessage(potRaisePerPlayerTotalRound, calculatedPot), player0NumberOfMarkersAfterRound + calculatedPot, player0.getNumberOfMarkers());
    assertEquals(player1NumberOfMarkersAfterRound, player1.getNumberOfMarkers());

    pokerGame.resetTurn(players);
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
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[CHECK]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 0;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    final int numberOfMarkersForPlayer0 = player0.getNumberOfMarkers();
    final int numberOfMarkersForPlayer1 = player1.getNumberOfMarkers();
    System.out.println("Player 0 has :[" + numberOfMarkersForPlayer0 + "] markers.");
    System.out.println("Player 1 has :[" + numberOfMarkersForPlayer1 + "] markers.");
    assertEquals(numberOfMarkersForPlayer0 + numberOfMarkersForPlayer1, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
    assertEquals(numberOfMarkersForPlayer0, 2525);
    assertEquals(numberOfMarkersForPlayer1, 2475);
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
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    final int player0ExpectedMarkersAfterRiver = player0.getNumberOfMarkers();
    final int player1ExpectedMarkersAfterRiver = player1.getNumberOfMarkers();

    System.out.println("pot : " + pokerGame.getPot());
    System.out.println("Player 0 has :[" + player0.getNumberOfMarkers() + "] markers. has little blind: " + player0.hasLittleBlind());
    System.out.println("Player 1 has :[" + player1.getNumberOfMarkers() + "] markers. has big blind; " + player1.hasLittleBlind());

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    assertEquals(2 * PokerGame.TOTAL_MARKERS_PER_PLAYER, player0.getNumberOfMarkers() + player1.getNumberOfMarkers());
    assertEquals(player0.getNumberOfMarkers(), player0ExpectedMarkersAfterRiver + 2 * potRaisePerPlayerTotalRound + calculateBlindCost(player0, blindAmount));
    assertEquals(player1.getNumberOfMarkers(), player1ExpectedMarkersAfterRiver);
  }

  @Test
  public void testOneRaiseAtFlopOneCheck() {
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
    int totalPotRaise = calculatePotRaise(players, potRaisePerPlayerTotalRound);
    assertPotAndMarkers(player0, player1, totalPotRaise, blindAmount);

    prepareFlop(getBadFlop());
    decision = pokerGame.playBeforeFlop(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerFlop = 50;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerFlop;
    totalPotRaise = calculatePotRaise(players, potRaisePerPlayerTotalRound);
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareTurn(Color.hearts, Ordinal.queen);
    decision = pokerGame.playTurn(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    int potRaisePerPlayerTurn = 200;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaisePerPlayerTurn;
    totalPotRaise = calculatePotRaise(players, potRaisePerPlayerTotalRound);
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    prepareRiver(Color.spades, Ordinal.two);
    decision = pokerGame.playRiver(players);
    assertEquals("Player Thomas Action :[RAISE]. Player Jörn Action :[CHECK]. ", decision);
    final int potRaiseRiver = 200;
    potRaisePerPlayerTotalRound = potRaisePerPlayerTotalRound + potRaiseRiver;
    totalPotRaise = calculatePotRaise(players, potRaisePerPlayerTotalRound);
    assertPotAndMarkers(player0, player1, blindAmount, blindAmount);

    final int player0ExpectedMarkersAfterRiver = player0.getNumberOfMarkers();
    final int player1ExpectedMarkersAfterRiver = player1.getNumberOfMarkers();

    System.out.println("pot : " + pokerGame.getPot());
    System.out.println("Player 0 has :[" + player0.getNumberOfMarkers() + "] markers. has little blind: " + player0.hasLittleBlind());
    System.out.println("Player 1 has :[" + player1.getNumberOfMarkers() + "] markers. has big blind; " + player1.hasLittleBlind());

    pokerGame.getTheWinner(players);
    pokerGame.resetTurn(players);

    assertEquals(2 * PokerGame.TOTAL_MARKERS_PER_PLAYER, player0.getNumberOfMarkers() + player1.getNumberOfMarkers());
    assertEquals(player0ExpectedMarkersAfterRiver + totalPotRaise, player0.getNumberOfMarkers());
    assertEquals(player1ExpectedMarkersAfterRiver, player1.getNumberOfMarkers());
  }

  private int calculatePotRaise(List<Player> players, int potRaisePerPlayerTotalRound) {
    int currentPot = 0;
    for (Player player:players) {
      currentPot += getPlayersPartInPot(player, potRaisePerPlayerTotalRound);
    }
    System.out.println("Current pot: " +currentPot);
    return currentPot;
  }

  private void assertPotAndMarkers(Player player0,
                                   Player player1,
                                   int totalPotRaisePerPlayer,
                                   int bigBlind) {
    List<Player> players = Lists.newArrayList();
    players.add(player0);
    players.add(player1);
    assertEquals(calculatePot(totalPotRaisePerPlayer, players, bigBlind), pokerGame.getPot());
    for (Player player : players) {
      final int blindCost = calculateBlindCost(player, bigBlind);
      final int playersPartInPot = getPlayersPartInPot(player, totalPotRaisePerPlayer);
      assertEquals("Number of markers not correct for player :[" + player.getName() + "] blindCost :[" + blindCost + "] playersPartInPot :[" + playersPartInPot + "]",
                   PokerGame.TOTAL_MARKERS_PER_PLAYER - blindCost - playersPartInPot,
                   player.getNumberOfMarkers());
    }
  }

  private int calculatePot(int totalPotRaisePerPlayer, List<Player> players, int bigBlindAmount) {
    System.out.println(getTotalBlindCost(bigBlindAmount));
    System.out.println(getTotalPotRaiseForAllPlayers(totalPotRaisePerPlayer, players));
    return getTotalBlindCost(bigBlindAmount) + getTotalPotRaiseForAllPlayers(totalPotRaisePerPlayer, players);
  }

  private int getTotalBlindCost(int bigBlindAmount) {
    return (int)(1.5 * bigBlindAmount);
  }

  private int getTotalPotRaiseForAllPlayers(int totalPotRaisePerPlayer, List<Player> players) {
    int totalPot = 0;
   for (Player player : players) {
      if (!player.getAction().isFold()) {
        totalPot += totalPotRaisePerPlayer;
      }
    }
    return totalPot;
  }

  private int getPlayersPartInPot(Player player, int potRaisePerPlayer) {
    if (player.getAction().isFold()) {
      return 0;
    }
    return potRaisePerPlayer;
  }

  private boolean anyPlayerFolds(List<Player> players) {
    for (Player player : players) {
      if (player.getAction().isFold()) {
        return true;
      }
    }
    return false;
  }

  private int calculateBlindCost(Player player, int bigBlind) {
    int littleBlind = (int) (bigBlind / 2);
    if (player.hasLittleBlind()) {
      return littleBlind;
    }
    if (player.hasBigBlind()) {
      return bigBlind;
    }
    return 0;
  }

  private void prepareBeforeFlop(List<Player> players,
                                 int blindAmount,
                                 List<Card> privateHandPlayer0,
                                 List<Card> privateHandPlayer1) {
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
