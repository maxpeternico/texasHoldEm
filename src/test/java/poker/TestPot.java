package poker;

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
    } while (!isAPlayerBroke(players));
//    final int jornNumberOfMarkers = jorn.getNumberOfMarkers();
//    final int staffansNumberOfMarkers = staffan.getNumberOfMarkers();
//    System.out.println("Jörn has :[" + jornNumberOfMarkers + "] markers.");
//    System.out.println("Staffan has :[" + staffansNumberOfMarkers + "] markers.");
//    assertEquals(jornNumberOfMarkers + staffansNumberOfMarkers, 2*PokerGame.TOTAL_MARKERS_PER_PLAYER);
  }

  private boolean isAPlayerBroke(List<Player> players) {
    for (Player player : players) {
      if (!player.hasAnyMarkers()) {
        return true;
      }
    }
    return false;
  }

}
