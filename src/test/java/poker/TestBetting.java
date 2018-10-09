package poker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBetting {

  @Test
  public void testPointsForPrivateHand() {
    // User 1 gets pair of aces and raises high
    final PlayPoker playPoker = PlayPoker.getInstance();
    Player jorn = new Player("Jörn", PlayPoker.TOTAL_MARKERS_PER_PLAYER);
    playPoker.registerRobotPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    playPoker.setPrivateHand(jorn, jornsPrivateHand);
    Player staffan = new Player("Staffan", PlayPoker.TOTAL_MARKERS_PER_PLAYER);
    playPoker.registerRobotPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    playPoker.setPrivateHand(staffan, staffansPrivateHand);

    final int jornsBet = playPoker.betPrivateHand(jorn, 2);
    final int staffansBet = playPoker.betPrivateHand(staffan, 2);
    assertEquals(jornsBet, 100);
    assertEquals(staffansBet, 10);

    playPoker.clearGame();
  }

  @Test
  public void testBetPrivateHandsFirstPlayerHasGoodCards() {
    // User 1 gets pair of aces and raises high
    final PlayPoker playPoker = PlayPoker.getInstance();

    Player staffan = new Player("Staffan", PlayPoker.TOTAL_MARKERS_PER_PLAYER);
    playPoker.registerRobotPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    playPoker.setPrivateHand(staffan, staffansPrivateHand);

    Player jorn = new Player("Jörn", PlayPoker.TOTAL_MARKERS_PER_PLAYER);
    playPoker.registerRobotPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    playPoker.setPrivateHand(jorn, jornsPrivateHand);

    Player thomas = new Player("Thomas", PlayPoker.TOTAL_MARKERS_PER_PLAYER);
    playPoker.registerRobotPlayer(thomas);
    List<Card> thomasPrivateHand = new ArrayList<>();
    thomasPrivateHand.add(new Card(Color.clubs, Ordinal.king));
    thomasPrivateHand.add(new Card(Color.diamonds, Ordinal.king));
    playPoker.setPrivateHand(thomas, thomasPrivateHand);

    List<Player> playerList = new ArrayList<>();
    playerList.add(jorn);
    playerList.add(staffan);
    playerList.add(thomas);
    final String result = playPoker.decideBet(playerList, Turn.BEFORE_FLOP);
    assertEquals("Player Jörn raises 100. Player Staffan fold. Player Thomas checks. ", result);

    playPoker.clearGame();
//    playPoker.putCardsBackInDeck(jornsPrivateHand);
//    playPoker.putCardsBackInDeck(thomasPrivateHand);
    // Players who folds (staffan) are put back in the deck in playpoker
  }
}
