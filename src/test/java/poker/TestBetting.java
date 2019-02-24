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

    pokerGame.initBlinds(playerList);
    pokerGame.payBlinds(playerList, 50);
    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    List<Card> emptyCommonhand = new ArrayList<>();
    final Points jornsPoints = ((RobotPlayer) jorn).calculatePoints(2, Draw.BEFORE_FLOP, emptyCommonhand);
    final Points staffansPoints = ((RobotPlayer) staffan).calculatePoints(2, Draw.BEFORE_FLOP, emptyCommonhand);
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

    List<Player> playerList = new ArrayList<>();
    playerList.add(jorn);
    playerList.add(staffan);
    playerList.add(thomas);

    pokerGame.initBlinds(playerList);
    pokerGame.payBlinds(playerList, 50);
    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    final String result = pokerGame.decideBet(playerList);
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

    List<Player> playerList = new ArrayList<>();
    playerList.add(peter);
    playerList.add(thomas);
    pokerGame.initBlinds(playerList);
    pokerGame.payBlinds(playerList, 50);
    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    final String result = pokerGame.decideBet(playerList);
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

    List<Player> playerList = new ArrayList<>();
    playerList.add(peter);
    playerList.add(thomas);

    pokerGame.initBlinds(playerList);
    pokerGame.payBlinds(playerList, 50);
    pokerGame.setTurnForUnitTest(Draw.BEFORE_FLOP);
    final String result = pokerGame.decideBet(playerList);

    assertEquals("Player Peter Action :[ALL_IN]. Player Thomas Action :[ALL_IN]. ", result);
  }

  @After
  public void clearGame() {
    pokerGame.clearGameForTests();
  }
}
