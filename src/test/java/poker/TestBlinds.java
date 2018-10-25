package poker;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBlinds {
  List<Player> players = Lists.newArrayList();
  final Player peter = new Player("Peter");
  final Player thomas = new Player("Thomas");
  final Player ingemar = new Player("Ingemar");
  final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testSetBlindsAtStartup() {
    players.add(peter);
    players.add(thomas);
    players.add(ingemar);
    assertEquals(peter.hasBlind(), false);
    assertEquals(thomas.hasBlind(), false);
    assertEquals(ingemar.hasBlind(), false);
  }

  @Test
  public void testClearAndSetOfBlinds() {
    players.add(peter);
    players.add(thomas);
    players.add(ingemar);
    pokerGame.setBlinds(players);
    assertEquals(peter.hasBlind(), true);
    assertEquals(peter.hasLittleBlind(), true);
    assertEquals(thomas.hasBlind(), true);
    assertEquals(thomas.hasBigBlind(), true);
    assertEquals(ingemar.hasBlind(), false);
    pokerGame.setBlinds(players);
    assertEquals(peter.hasBlind(), false);
    assertEquals(thomas.hasLittleBlind(), true);
    assertEquals(ingemar.hasBigBlind(), true);
  }
}
