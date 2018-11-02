package poker;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBetting {
  final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testPointsForPrivateHand() {
    // User 1 gets pair of aces and raises high
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
    List<Player> playerList = new ArrayList<>();
    playerList.add(jorn);
    playerList.add(staffan);

    pokerGame.setBlinds(playerList);
    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    List<Card> emptyCommonhand = new ArrayList<>();
    final Points jornsPoints = ((RobotPlayer) jorn).calculatePoints(2, Turn.BEFORE_FLOP, emptyCommonhand);
    final Points staffansPoints = ((RobotPlayer) staffan).calculatePoints(2, Turn.BEFORE_FLOP, emptyCommonhand);
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
    thomasPrivateHand.add(new Card(Color.clubs, Ordinal.king));
    thomasPrivateHand.add(new Card(Color.diamonds, Ordinal.king));
    pokerGame.setPrivateHand(thomas, thomasPrivateHand);

    List<Player> playerList = new ArrayList<>();
    playerList.add(jorn);
    playerList.add(staffan);
    playerList.add(thomas);

    pokerGame.setBlinds(playerList);
    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    final String result = pokerGame.decideBet(playerList);
    assertEquals("Player Jörn Decision :[RAISE]. Player Staffan Decision :[FOLD]. Player Thomas Decision :[RAISE]. ", result);

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

    List<Player> playerList = new ArrayList<>();
    playerList.add(peter);
    playerList.add(thomas);
    pokerGame.setBlinds(playerList);
    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    final String result = pokerGame.decideBet(playerList);
    assertEquals("Player Peter Decision :[RAISE]. Player Thomas Decision :[RAISE]. ", result);
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

    List<Player> playerList = new ArrayList<>();
    playerList.add(peter);
    playerList.add(thomas);

    pokerGame.setBlinds(playerList);
    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    final String result = pokerGame.decideBet(playerList);

    assertEquals("Player Peter Decision :[RAISE]. Player Thomas Decision :[RAISE]. ", result);
  }

//  @Test
//  public void testPot() {
//    // User 1 gets pair of aces and raises high
//    Player jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
//    pokerGame.registerPlayer(jorn);
//    List<Card> jornsPrivateHand = new ArrayList<>();
//    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.six));
//    jornsPrivateHand.add(new Card(Color.spades, Ordinal.two));
//    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
//    Player staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
//    pokerGame.registerPlayer(staffan);
//    List<Card> staffansPrivateHand = new ArrayList<>();
//    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.seven));
//    staffansPrivateHand.add(new Card(Color.spades, Ordinal.three));
//    pokerGame.setPrivateHand(staffan, staffansPrivateHand);
//    List<Player> playerList = new ArrayList<>();
//    playerList.add(jorn);
//    playerList.add(staffan);
//
//    pokerGame.setBlinds(playerList);
//    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
//    pokerGame.playRound(playerList);
//    final int jornsMarkers = jorn.getNumberOfMarkers();
//    final int staffansMarkers = staffan.getNumberOfMarkers();
//
//    assertEquals(jornsMarkers, 2450);
//    assertEquals(staffansMarkers, 2400);
//
//  }

  @After
  public void clearGame() {
    pokerGame.clearGame();
  }
}
