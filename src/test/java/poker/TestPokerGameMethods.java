package poker;

import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class TestPokerGameMethods {
  private PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testFivePlayersFirstTwoChecksThirdRaises() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.forEach(Player::setActionToCheck);
    pokerGame.clearPreviousPlayersWithActionCheck(players, players.get(2));
    assertTrue(players.get(0).getAction().isNotDecided());
    assertFalse(players.get(0).getAction().isCheck());
    assertTrue(players.get(1).getAction().isNotDecided());
    assertFalse(players.get(1).getAction().isCheck());
    assertTrue(players.get(2).getAction().isCheck());
    assertTrue(players.get(3).getAction().isCheck());
    assertTrue(players.get(4).getAction().isCheck());
  }

  @Test
  public void testAllPlayersSatisfied() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.forEach(Player::setActionToCheck);
    assertTrue(pokerGame.allPlayersSatisfied(players));
  }

  @Test // TODO: Do we need this test?
  public void testAllPlayersSatisfiedOnePlayerRaises() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.forEach(Player::setActionToCheck);
    players.get(1).setAction(100, 0, 0, 0);
    assertTrue(pokerGame.allPlayersSatisfied(players));
  }

  @Test
  public void testAllPlayersSatisfiedOnePlayerNotDecided() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(5, 2500);
    players.forEach(Player::setActionToCheck);
    players.get(1).setActionToNotDecided();
    assertFalse(pokerGame.allPlayersSatisfied(players));
  }
}
