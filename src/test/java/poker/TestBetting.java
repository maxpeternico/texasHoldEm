package poker;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBetting {
  private final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testPointsForPrivateHand() {
    // User 1 gets pair of aces and raises high
    RobotPlayer jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
    RobotPlayer staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);
    List<Player> playerList = new ArrayList<>();
    playerList.add(jorn);
    playerList.add(staffan);

    pokerGame.initBlinds(playerList);
    pokerGame.payBlinds(playerList, pokerGame.playersThatCanBet(playerList), 50);
    List<Card> emptyCommonhand = new ArrayList<>();
    final Points jornsPoints = jorn.calculatePoints(2, Draw.BEFORE_FLOP, emptyCommonhand);
    final Points staffansPoints = staffan.calculatePoints(2, Draw.BEFORE_FLOP, emptyCommonhand);
    assertEquals(jornsPoints.toInt(), 114);
    assertEquals(staffansPoints.toInt(), 13);
  }

  @Test
  public void testBetPrivateHandsOneVeryGoodOneGoodOneBad() {
    // User 1 gets pair of aces and raises high
    Player staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);

    Player jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);

    Player thomas = new RobotPlayer("Thomas", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(thomas);
    List<Card> thomasPrivateHand = new ArrayList<>();
    thomasPrivateHand.add(new Card(Color.clubs, Ordinal.ace));
    thomasPrivateHand.add(new Card(Color.diamonds, Ordinal.ace));
    pokerGame.setPrivateHand(thomas, thomasPrivateHand);

    List<Player> players = new ArrayList<>();
    players.add(jorn);
    players.add(staffan);
    players.add(thomas);

    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, pokerGame.playersThatCanBet(players), 50);

    BetManager betManager = new BetManager(players, 50, pokerGame.getPotHandler());
    pokerGame.setBetManager(betManager);
    String result = pokerGame.decideBet(players);

    assertEquals("Player Jörn Action :[ALL_IN]. Player Staffan Action :[FOLD]. Player Thomas Action :[ALL_IN]. ", result);

  }

  @Test
  public void testFirstPlayerRaisesSecondPlayerRaisesEvenMore() {
    // User 1 gets pair of aces and raises high
    Player peter = new RobotPlayer("Peter", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(peter);
    List<Card> petersPrivateHand = new ArrayList<>();
    petersPrivateHand.add(new Card(Color.hearts, Ordinal.two));
    petersPrivateHand.add(new Card(Color.spades, Ordinal.two));
    pokerGame.setPrivateHand(peter, petersPrivateHand);
    Player thomas = new RobotPlayer("Thomas", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(thomas);
    List<Card> thomasPrivateHand = new ArrayList<>();
    thomasPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    thomasPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(thomas, thomasPrivateHand);

    List<Player> players = new ArrayList<>();
    players.add(peter);
    players.add(thomas);
    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, pokerGame.playersThatCanBet(players), 50);

    BetManager betManager = new BetManager(players, 50, pokerGame.getPotHandler());
    pokerGame.setBetManager(betManager);
    String result = pokerGame.decideBet(players);

    assertEquals("Player Peter Action :[RAISE]. Player Thomas Action :[ALL_IN]. Player Peter Action :[FOLD]. ", result);
  }

  @Test
  public void testBothPlayersRaisesMax() {
    // User 1 gets pair of aces and raises high
    Player peter = new RobotPlayer("Peter", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(peter);
    List<Card> petersPrivateHand = new ArrayList<>();
    petersPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    petersPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(peter, petersPrivateHand);
    Player thomas = new RobotPlayer("Thomas", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(thomas);
    List<Card> thomasPrivateHand = new ArrayList<>();
    thomasPrivateHand.add(new Card(Color.diamonds, Ordinal.ace));
    thomasPrivateHand.add(new Card(Color.clubs, Ordinal.ace));
    pokerGame.setPrivateHand(thomas, thomasPrivateHand);

    List<Player> players = new ArrayList<>();
    players.add(peter);
    players.add(thomas);

    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, pokerGame.playersThatCanBet(players), 50);

    BetManager betManager = new BetManager(players, 50, pokerGame.getPotHandler());
    pokerGame.setBetManager(betManager);
    String result = pokerGame.decideBet(players);

    assertEquals("Player Peter Action :[ALL_IN]. Player Thomas Action :[ALL_IN]. ", result);
  }

  @After
  public void clearGame() {
    pokerGame.clearGameForTests();
  }
}
