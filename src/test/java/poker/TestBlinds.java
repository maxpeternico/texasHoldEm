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
  static final int TOTAL_MARKERS_PER_PLAYER = 2500;

  List<Player> players = Lists.newArrayList();
  final RobotPlayer peter = new RobotPlayer("Peter", TOTAL_MARKERS_PER_PLAYER);
  final RobotPlayer thomas = new RobotPlayer("Thomas", TOTAL_MARKERS_PER_PLAYER);
  final RobotPlayer ingemar = new RobotPlayer("Ingemar", TOTAL_MARKERS_PER_PLAYER);
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
    pokerGame.initBlinds(players);
//    pokerGame.payBlinds(players, 50);
    assertEquals(peter.hasBlind(), true);
    assertEquals(peter.hasLittleBlind(), true);
    assertEquals(peter.hasBigBlind(), false);

    assertEquals(thomas.hasBlind(), true);
    assertEquals(thomas.hasLittleBlind(), false);
    assertEquals(thomas.hasBigBlind(), true);
    assertEquals(ingemar.hasBlind(), false);

    pokerGame.payBlinds(players, 50);
    assertEquals(peter.hasBlind(), false);
    assertEquals(thomas.hasLittleBlind(), true);
    assertEquals(ingemar.hasBigBlind(), true);
  }

  @Test
  public void testBlinds() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = getThreePlayers(pokerGame);
    pokerGame.initBlinds(players);
//    pokerGame.payBlinds(players, 50);
    int indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    int indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 0);
    assertEquals(indexOfBigBlind, 1);
    pokerGame.payBlinds(players, 50);
    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 1);
    assertEquals(indexOfBigBlind, 2);
    pokerGame.payBlinds(players, 50);
    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 2);
    assertEquals(indexOfBigBlind, 0);
    pokerGame.payBlinds(players, 50);
    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfLittleBlind, 0);
    assertEquals(indexOfBigBlind, 1);
    pokerGame.clearGame();
  }

  @Test
  public void testOnePlayerCantPayBlinds() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = getTwoPlayers();

    Player thomas = new RobotPlayer("Thomas", 10);
    List<Card> thomasPrivateHand = new ArrayList<>();
    thomasPrivateHand.add(new Card(Color.clubs, Ordinal.king));
    thomasPrivateHand.add(new Card(Color.diamonds, Ordinal.king));
    pokerGame.setPrivateHand(thomas, thomasPrivateHand);
    players.add(thomas);

    pokerGame.initBlinds(players);

    final Player staffan = players.get(0);
    final Player jorn = players.get(1);

    assertEquals(staffan.hasLittleBlind(), true);
    assertEquals(jorn.hasBigBlind(), true);
    assertEquals(thomas.hasBlind(), false);

    int blindPot = pokerGame.payBlinds(players, 50);

    assertEquals(staffan.hasBigBlind(), true);
    assertEquals(jorn.hasLittleBlind(), true);
    assertEquals(thomas.hasBlind(), false);
    assertEquals(blindPot, 85);

    pokerGame.clearGame();
  }

  private List<Player> getThreePlayers(PokerGame pokerGame) {
    List<Player> playerList = getTwoPlayers();

    Player thomas = new RobotPlayer("Thomas", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    List<Card> thomasPrivateHand = new ArrayList<>();
    thomasPrivateHand.add(new Card(Color.clubs, Ordinal.king));
    thomasPrivateHand.add(new Card(Color.diamonds, Ordinal.king));
    pokerGame.setPrivateHand(thomas, thomasPrivateHand);

    playerList.add(thomas);

    return playerList;
  }

  private List<Player> getTwoPlayers() {
    Player staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);

    Player jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);

    List<Player> playerList = new ArrayList<>();
    playerList.add(jorn);
    playerList.add(staffan);

    return playerList;
  }

  private int getIndexOfBlind(List<Player> players, Predicate<? super Player> blindType) {
    final Optional<Player> optionalPlayer = players.stream().filter(blindType).findFirst();
    if (!optionalPlayer.isPresent()) {
      fail("Player with blind not found.");
    }
    return players.indexOf(optionalPlayer.get());
  }

  @Test
  public void testTwoPlayersOneGetsBrokeAtLittleBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 100);
    // Set player 1 so he can not pay little blind
    players.get(1).decreaseMarkers(90);
    pokerGame.initBlinds(players);
    final int blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(10, blindPot);
  }

  @Test
  public void testTwoPlayersOneGetsBrokeAtBigBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(2, 100);
    // Set player 0 so he can not pay big blind
    players.get(0).decreaseMarkers(90);
    pokerGame.initBlinds(players);
    final int blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(35, blindPot);
  }

  @Test
  public void testBlindThreePlayersOneGetsBrokeAtBigBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 100);
    pokerGame.initBlinds(players);
    int indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(indexOfLittleBlind, 0);
    int indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfBigBlind, 1);

    int blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(75, blindPot);

    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(indexOfLittleBlind, 1);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfBigBlind, 2);

    // Player 1 shall nog be able to pay blind
    players.get(0).decreaseMarkers(70);
    blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(105, blindPot);

    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(2, indexOfLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(1, indexOfBigBlind);

  }

  @Test
  public void testBlindThreePlayersOneGetsBrokeAtLittleBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 100);
    pokerGame.initBlinds(players);
    int indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(indexOfLittleBlind, 0);
    int indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfBigBlind, 1);

    int blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(75, blindPot);

    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(indexOfLittleBlind, 1);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfBigBlind, 2);

    // Player 2 shall nog be able to pay blind
    players.get(2).decreaseMarkers(30);
    blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(95, blindPot);

    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(0, indexOfLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(1, indexOfBigBlind);
  }

  @Test
  public void testBlindThreePlayersTwoGetsBrokeByBlind() {
    final List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 100);
    pokerGame.initBlinds(players);
    int indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(indexOfLittleBlind, 0);
    int indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfBigBlind, 1);

    int blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(75, blindPot);

    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(indexOfLittleBlind, 1);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(indexOfBigBlind, 2);

    // Player 0 shall nog be able to pay big blind
    players.get(0).decreaseMarkers(80);
    // Player 2 shall not be able to pay little blind
    players.get(2).decreaseMarkers(30);
    blindPot = pokerGame.payBlinds(players, 50);
    assertEquals(40, blindPot);

    indexOfLittleBlind = getIndexOfBlind(players, Player::hasLittleBlind);
    assertEquals(1, indexOfLittleBlind);
    indexOfBigBlind = getIndexOfBlind(players, Player::hasBigBlind);
    assertEquals(1, indexOfBigBlind);
  }
}
