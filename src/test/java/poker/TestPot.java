package poker;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPot {
  final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testTransferOfMarkersToPotAndToPlayerNoRaise() {
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

    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    pokerGame.initBlinds(players);
    pokerGame.playRound(players);
    final int jornNumberOfMarkers = jorn.getNumberOfMarkers();
    final int staffansNumberOfMarkers = staffan.getNumberOfMarkers();
    System.out.println("Jörn has :[" + jornNumberOfMarkers + "] markers.");
    System.out.println("Staffan has :[" + staffansNumberOfMarkers + "] markers.");
    assertEquals(jornNumberOfMarkers + staffansNumberOfMarkers, 2 * PokerGame.TOTAL_MARKERS_PER_PLAYER);
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
      pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
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
      pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
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

  @Test
  public void testPotOnePlayerRaiseOtherFold() {
    Player jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
    Player staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.seven));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.two));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);
    List<Player> players = new ArrayList<>();
    players.add(jorn);
    players.add(staffan);

    pokerGame.initBlinds(players);
    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    pokerGame.playRound(players);
  //  assertEquals(2525, jorn.getNumberOfMarkers());
    //   assertEquals(2475, staffan.getNumberOfMarkers());
  }

  @Test
  public void testPotBothPlayersAllIn() {
    Player jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
    Player staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.diamonds, Ordinal.ace));
    staffansPrivateHand.add(new Card(Color.clubs, Ordinal.ace));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);
    List<Player> players = new ArrayList<>();
    players.add(jorn);
    players.add(staffan);

    pokerGame.initBlinds(players);
    pokerGame.setTurnForUnitTest(Turn.BEFORE_FLOP);
    pokerGame.playRound(players);
    assertEquals(5000, jorn.getNumberOfMarkers() + staffan.getNumberOfMarkers());
  }

  @After
  public void clearGame() {
    pokerGame.clearGame();
  }

}
