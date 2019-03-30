package poker;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class TestBlinds {
  private static final int TOTAL_MARKERS_PER_PLAYER = 2500;

  private List<Player> players = Lists.newArrayList();
  private final RobotPlayer peter = new RobotPlayer("Peter", TOTAL_MARKERS_PER_PLAYER);
  private final RobotPlayer thomas = new RobotPlayer("Thomas", TOTAL_MARKERS_PER_PLAYER);
  private final RobotPlayer ingemar = new RobotPlayer("Ingemar", TOTAL_MARKERS_PER_PLAYER);
  private final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testSetBlindsAtStartup() {
    players.add(peter);
    players.add(thomas);
    players.add(ingemar);
    assertFalse(peter.hasBlind());
    assertFalse(thomas.hasBlind());
    assertFalse(ingemar.hasBlind());
  }

  @Test
  public void testInitBlinds() {
    players.add(peter);
    players.add(thomas);
    players.add(ingemar);
    pokerGame.initBlinds(players);
    assertTrue(peter.hasBlind());
    assertTrue(peter.hasLittleBlind());
    assertFalse(peter.hasBigBlind());

    assertTrue(thomas.hasBlind());
    assertFalse(thomas.hasLittleBlind());
    assertTrue(thomas.hasBigBlind());
    assertFalse(ingemar.hasBlind());
  }

  @Test
  public void testSetBlinds() {
    players.add(peter);
    players.add(thomas);
    players.add(ingemar);
    pokerGame.initBlinds(players);
    pokerGame.payBlinds(players, 50);
    assertFalse(peter.hasBlind());
    assertTrue(thomas.hasLittleBlind());
    assertTrue(ingemar.hasBigBlind());
  }

  @Test
  public void testSetBlindsToNewPlayers() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 2500);
    pokerGame.initBlinds(players);
    assertEquals(0, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(1, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(2).hasBlind());
    assertEquals(pokerGame.getPotHandler().getNumberOfMarkersInAllPots(), 0);
    pokerGame.payBlinds(players, 50);
    assertEquals(1, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(2, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(0).hasBlind());
    pokerGame.getPotHandler().clear();
    pokerGame.payBlinds(players, 50);
    assertEquals(pokerGame.getPotHandler().getNumberOfMarkersInAllPots(), 75);
    assertEquals(2, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(0, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(1).hasBlind());
    pokerGame.getPotHandler().clear();
    pokerGame.payBlinds(players, 50);
    assertEquals(pokerGame.getPotHandler().getNumberOfMarkersInAllPots(), 75);
    assertEquals(0, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(1, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(2).hasBlind());
    assertEquals(pokerGame.getPotHandler().getNumberOfMarkersInAllPots(), 75);
    pokerGame.clearGameForTests();
  }

  @Test
  public void testBlindHighBlind() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 2500);
    pokerGame.initBlinds(players);
    assertEquals(pokerGame.getPotHandler().getNumberOfMarkersInAllPots(), 0);
    pokerGame.payBlinds(players, 1200);
    assertEquals(pokerGame.getPotHandler().getNumberOfMarkersInAllPots(), 1800);
    pokerGame.clearGameForTests();
  }

  @Test
  public void testOnePlayerCantPayBigBlind() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 2500);
    players.get(2).decreaseMarkers(2499);
    pokerGame.initBlinds(players);

    pokerGame.payBlinds(players, 50);

    assertEquals(2, pokerGame.getPotHandler().getPot(0).getNumberOfMarkers());
    assertEquals(24, pokerGame.getPotHandler().getPot(1).getNumberOfMarkers());
    assertEquals(1, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(2, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(0).hasBlind());

    pokerGame.clearGameForTests();
  }

  @Test
  public void testOnePlayerCantPayLittleBlind() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(3, 2500);
    players.get(1).decreaseMarkers(2499);
    pokerGame.initBlinds(players);

    pokerGame.payBlinds(players, 50);

    assertEquals(51, pokerGame.getPotHandler().getNumberOfMarkersInAllPots());
    assertEquals(2, pokerGame.getPotHandler().getPot(0).getNumberOfMarkers());
    assertEquals(1, pokerGame.getPotHandler().getPot(0).getMarkersForMember(players.get(1)));
    assertEquals(1, pokerGame.getPotHandler().getPot(0).getMarkersForMember(players.get(2)));
    assertEquals(Lists.newArrayList(players.get(1), players.get(2)), pokerGame.getPotHandler().getPot(0).getMembers());
    assertEquals(49, pokerGame.getPotHandler().getPot(1).getNumberOfMarkers());
    assertEquals(Lists.newArrayList(players.get(2)), pokerGame.getPotHandler().getPot(1).getMembers());
    assertEquals(2, pokerGame.getPotHandler().getPots().size());
    assertEquals(1, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(2, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(0).hasBlind());

    pokerGame.clearGameForTests();
  }

  private int getIndexOfBlind(List<Player> players, Predicate<? super Player> blindType) {
    final Optional<Player> optionalPlayer = players.stream().filter(blindType).findFirst();
    if (!optionalPlayer.isPresent()) {
      fail("Player with blind not found.");
    }
    return players.indexOf(optionalPlayer.get());
  }

  @Test
  public void testPlayersWithOldBlindGetsOutOfMarkers() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(4, 2500);
    pokerGame.initBlinds(players);
    players.get(0).decreaseMarkers(2500);
    players.get(1).decreaseMarkers(2500);

    pokerGame.payBlinds(players, 50);

    assertEquals(2, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(3, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(0).hasBlind());
    assertFalse(players.get(1).hasBlind());

    pokerGame.clearGameForTests();
  }

  @Test
  public void testPlayersWithNewBlindGetsOutOfMarkers() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(4, 2500);
    pokerGame.initBlinds(players);
    players.get(2).decreaseMarkers(2500);
    players.get(3).decreaseMarkers(2500);

    pokerGame.payBlinds(players, 50);

    assertEquals(1, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(0, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(2).hasBlind());
    assertFalse(players.get(3).hasBlind());

    pokerGame.clearGameForTests();
  }

  @Test
  public void testFourPlayersEvenPlayerIndexGetsOutOfMarkers() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(4, 2500);
    pokerGame.initBlinds(players);
    players.get(1).decreaseMarkers(2500);
    players.get(3).decreaseMarkers(2500);

    pokerGame.payBlinds(players, 50);

    assertEquals(2, getIndexOfBlind(players, Player::hasLittleBlind));
    assertEquals(0, getIndexOfBlind(players, Player::hasBigBlind));
    assertFalse(players.get(1).hasBlind());
    assertFalse(players.get(3).hasBlind());

    pokerGame.clearGameForTests();
  }

  @Test
  public void testAllButOnePlayersGetsOutOfMarkers() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(4, 2500);
    pokerGame.initBlinds(players);
    players.get(0).decreaseMarkers(2500);
    players.get(1).decreaseMarkers(2500);
    players.get(2).decreaseMarkers(2500);

    pokerGame.payBlinds(players, 50);

    assertEquals(3, getIndexOfBlind(players, Player::hasLittleBlind));
    assertFalse(players.get(0).hasBlind());
    assertFalse(players.get(1).hasBlind());
    assertFalse(players.get(2).hasBlind());

    pokerGame.clearGameForTests();
  }

  @Test
  public void testAllPlayersGetsOutOfMarkers() {
    final PokerGame pokerGame = PokerGame.getInstance();
    List<Player> players = pokerGame.createNumberOfRobotPlayers(4, 2500);
    pokerGame.initBlinds(players);
    players.get(0).decreaseMarkers(2500);
    players.get(1).decreaseMarkers(2500);
    players.get(2).decreaseMarkers(2500);
    players.get(3).decreaseMarkers(2500);

    pokerGame.payBlinds(players, 50);

    assertFalse(players.get(0).hasBlind());
    assertFalse(players.get(1).hasBlind());
    assertFalse(players.get(2).hasBlind());
    assertFalse(players.get(3).hasBlind());

    pokerGame.clearGameForTests();
  }
}
