package poker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBetting {

  @Test
  public void testPointsForPrivateHand() {
    // User 1 gets pair of aces and raises high
    final PokerGame pokerGame = PokerGame.getInstance();
    Player jorn = new Player("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
    Player staffan = new Player("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);
    List<Player> playerList = new ArrayList<>();
    playerList.add(jorn);
    playerList.add(staffan);

    pokerGame.setBlinds(playerList);
    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    final int jornsBet = pokerGame.betPrivateHand(jorn, 2);
    final int staffansBet = pokerGame.betPrivateHand(staffan, 2);
    assertEquals(jornsBet, 300);
    assertEquals(staffansBet, 50);

    pokerGame.clearGame();

  }

  @Test
  public void testBetPrivateHandsOneVeryGoodOneGoodOneBad() {
    // User 1 gets pair of aces and raises high
    final PokerGame pokerGame = PokerGame.getInstance();

    Player staffan = new Player("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);

    Player jorn = new Player("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);

    Player thomas = new Player("Thomas", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(thomas);
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
    assertEquals("Player Jörn raises 300. Player Staffan fold. Player Thomas checks. Player Jörn checks. Player Thomas checks. ", result);

    pokerGame.clearGame();
  }

  @Test
  public void testFirstPlayerRaisesSecondPlayerRaisesEvenMore() {
    // User 1 gets pair of aces and raises high
    final PokerGame pokerGame = PokerGame.getInstance();
    Player peter = new Player("Peter", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(peter);
    List<Card> petersPrivateHand = new ArrayList<>();
    petersPrivateHand.add(new Card(Color.hearts, Ordinal.two));
    petersPrivateHand.add(new Card(Color.spades, Ordinal.two));
    pokerGame.setPrivateHand(peter, petersPrivateHand);
    Player thomas = new Player("Thomas", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(thomas);
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
    assertEquals("Player Peter raises 300. Player Thomas checks. Player Peter checks. Player Thomas checks. ", result);

    pokerGame.clearGame();
  }

  @Test
  public void testBothPlayersRaisesMax() {
    // User 1 gets pair of aces and raises high
    final PokerGame pokerGame = PokerGame.getInstance();
    Player peter = new Player("Peter", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(peter);
    List<Card> petersPrivateHand = new ArrayList<>();
    petersPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    petersPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(peter, petersPrivateHand);
    Player thomas = new Player("Thomas", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerRobotPlayer(thomas);
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

    assertEquals("Player Peter raises 300. Player Thomas checks. Player Peter checks. Player Thomas checks. ", result);

    pokerGame.clearGame();
  }

//  @Test
//  public void testPot() {
//    // User 1 gets pair of aces and raises high
//    final PokerGame pokerGame = PokerGame.getInstance();
//    Player jorn = new Player("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
//    pokerGame.registerRobotPlayer(jorn);
//    List<Card> jornsPrivateHand = new ArrayList<>();
//    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.six));
//    jornsPrivateHand.add(new Card(Color.spades, Ordinal.two));
//    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
//    Player staffan = new Player("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
//    pokerGame.registerRobotPlayer(staffan);
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
//    pokerGame.clearGame();
//
//  }
}
