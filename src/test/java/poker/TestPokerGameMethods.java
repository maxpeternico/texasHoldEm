package poker;

import java.util.List;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class TestPokerGameMethods {
  PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testFivePlayersFirstTwoChecksThirdRaises() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.stream().forEach(e->e.setActionToCheck());
    pokerGame.clearPreviousPlayersWithActionCheck(players, players.get(2));
    assertEquals(players.get(0).getAction().isNotDecided(), true);
    assertEquals(players.get(0).getAction().isCheck(), false);
    assertEquals(players.get(1).getAction().isNotDecided(), true);
    assertEquals(players.get(1).getAction().isCheck(), false);
    assertEquals(players.get(2).getAction().isCheck(), true);
    assertEquals(players.get(3).getAction().isCheck(), true);
    assertEquals(players.get(4).getAction().isCheck(), true);
  }

  @Test
  public void testAllPlayersSatisfied() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.stream().forEach(e->e.setActionToCheck());
    assertTrue(pokerGame.allPlayersSatisfied(players));
  }

  @Test // TODO: Do we need this test?
  public void testAllPlayersSatisfiedOnePlayerRaises() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.stream().forEach(Player::setActionToCheck);
    players.get(1).setAction(100, 0, 0);
    assertTrue(pokerGame.allPlayersSatisfied(players));
  }

  @Test
  public void testAllPlayersSatisfiedOnePlayerNotDecided() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.stream().forEach(e->e.setActionToCheck());
    players.get(1).setActionToNotDecided();
    assertFalse(pokerGame.allPlayersSatisfied(players));
  }
}
