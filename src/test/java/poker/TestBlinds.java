package poker;

import com.google.common.collect.Lists;
import org.junit.Test;
import poker.Player;
import poker.PokerGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    pokerGame.clearGame();
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
    pokerGame.clearGame();
  }

  @Test
  public void testBlinds() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = getThreePlayers(pokerGame);
    pokerGame.setBlinds(players);
    int indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    int indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 0);
    assertEquals(indexOfBigBlind, 1);
    pokerGame.setBlinds(players);
    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 1);
    assertEquals(indexOfBigBlind, 2);
    pokerGame.setBlinds(players);
    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 2);
    assertEquals(indexOfBigBlind, 0);
    pokerGame.setBlinds(players);
    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 0);
    assertEquals(indexOfBigBlind, 1);
    pokerGame.clearGame();
  }

  private List<Player> getThreePlayers(PokerGame pokerGame) {
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

    return playerList;
  }

  private int getIndexOfBlind(List<Player> players, Predicate<? super Player> blindType) {
    final Optional<Player> optionalPlayer = players.stream().filter(blindType).findFirst();
    if (!optionalPlayer.isPresent()) {
      fail("Player with blind not found.");
    }
    return players.indexOf(optionalPlayer.get());
  }
}
