package poker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBetting {

  @Test
  public void testBetPrivateHands() {
    // User 1 gets pair of aces and raises high
    final PlayPoker playPoker = PlayPoker.getInstance();
    Player jorn = new Player("Jörn");
    playPoker.registerRobotPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    playPoker.setPrivateHand(jorn, jornsPrivateHand);
    Player staffan = new Player("Staffan");
    playPoker.registerRobotPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    playPoker.setPrivateHand(staffan, staffansPrivateHand);

    final int jornsBet = playPoker.betPrivateHand(jorn);
      final int staffansBet = playPoker.betPrivateHand(staffan);
    assertEquals(jornsBet, 100);
    assertEquals(staffansBet, 0);
  }
}
