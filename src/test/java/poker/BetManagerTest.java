package poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

public class BetManagerTest extends TestBase {

  private PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void testBettingMap() {
    List<Player> playerList = pokerGame.createNumberOfRobotPlayers(4, 2500);
    final BetManager betManager = new BetManager(playerList, 50, new PotHandler());
    betManager.createBettingDecisionList(new RobotPlayer("Jörn", 2500));
    Map<Player, Boolean> bettingMap = betManager.getBettingMap();
    assertEquals("Jörn", getNameFromBettingMapIndex(bettingMap, 0));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 0));
    assertEquals("Anders", getNameFromBettingMapIndex(bettingMap, 1));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 1));
    assertEquals("Bosse", getNameFromBettingMapIndex(bettingMap, 2));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 2));
    assertEquals("Thomas", getNameFromBettingMapIndex(bettingMap, 3));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 3));

    betManager.createBettingDecisionList(new RobotPlayer("Bosse", 2500));
    bettingMap = betManager.getBettingMap();
    assertEquals("Bosse", getNameFromBettingMapIndex(bettingMap, 0));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 0));
    assertEquals("Thomas", getNameFromBettingMapIndex(bettingMap, 1));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 1));
    assertEquals("Jörn", getNameFromBettingMapIndex(bettingMap, 2));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 2));
    assertEquals("Anders", getNameFromBettingMapIndex(bettingMap, 3));
    assertEquals(false, getBettingHistoryFromBettingMapIndex(bettingMap, 3));
  }

  private Boolean getBettingHistoryFromBettingMapIndex(Map<Player, Boolean> bettingMap, int i) {
    return new ArrayList<>(bettingMap.values()).get(i);
  }

  private String getNameFromBettingMapIndex(Map<Player, Boolean> bettingMap, int i) {
    return new ArrayList<>(bettingMap.keySet()).get(i).getName();
  }

  @Test
  public void testBetUntilAllAreSatisfiedNoOneRaises() {
    List<Player> playerList = pokerGame.createNumberOfRobotPlayers(4, 2500);
    final BetManager betManager = new BetManager(playerList, 50, new PotHandler());
    betManager.createBettingDecisionList(playerList.get(0));
    betManager.initResult();
    final Player player = betManager.betUntilAllAreSatisfied(false);
    assertEquals(player, playerList.get(0));
  }

  @Test
  public void testBetUntilAllAreSatisfiedTwoPlayerRaises() {
    List<Player> playerList = Lists.newArrayList();
    Player player1 = Mockito.mock(Player.class);
    when(player1.getName()).thenReturn("Peter");
    Player player2 = Mockito.mock(Player.class);
    when(player2.getName()).thenReturn("Thomas");
    playerList.add(player1);
    playerList.add(player2);
    when(player1.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.RAISE)).thenReturn(
      new Action(ActionEnum.CHECK));
    when(player1.getAction())
      .thenReturn(new Action(ActionEnum.RAISE))
      .thenReturn(new Action(ActionEnum.CHECK));
    when(player2.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.RAISE)).thenReturn(
      new Action(ActionEnum.CHECK));
    when(player2.getAction())
      .thenReturn(new Action(ActionEnum.RAISE))
      .thenReturn(new Action(ActionEnum.CHECK));
    final BetManager betManager = new BetManager(playerList, 50, new PotHandler());
    betManager.createBettingDecisionList(playerList.get(0));
    betManager.initResult();
    final Player player = betManager.betUntilAllAreSatisfied(false);
    assertEquals("Thomas", player.getName());
  }

  @Test
  public void testBetAllCheck() {
    List<Player> playerList = Lists.newArrayList();
    Player peter = Mockito.mock(Player.class);
    when(peter.getName()).thenReturn("Peter");
    Player thomas = Mockito.mock(Player.class);
    when(thomas.getName()).thenReturn("Thomas");
    Player anders = Mockito.mock(Player.class);
    when(anders.getName()).thenReturn("Anders");
    Player bosse = Mockito.mock(Player.class);
    when(bosse.getName()).thenReturn("Bosse");
    Player staffan = Mockito.mock(Player.class);
    when(staffan.getName()).thenReturn("Staffan");
    playerList.add(peter);
    playerList.add(thomas);
    playerList.add(anders);
    playerList.add(bosse);
    playerList.add(staffan);
    when(peter.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.CHECK));
    when(peter.getAction()).thenReturn(new Action(ActionEnum.CHECK));
    when(peter.hasFolded()).thenReturn(false);
    when(peter.isAllIn()).thenReturn(false);

    when(thomas.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.CHECK));
    when(thomas.getAction()).thenReturn(new Action(ActionEnum.CHECK));
    when(thomas.hasFolded()).thenReturn(false);
    when(thomas.isAllIn()).thenReturn(false);

    when(anders.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.FOLD));
    when(anders.getAction()).thenReturn(new Action(ActionEnum.FOLD));
    when(anders.hasFolded()).thenReturn(true);
    when(anders.isAllIn()).thenReturn(false);

    when(bosse.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.ALL_IN));
    when(bosse.getAction()).thenReturn(new Action(ActionEnum.ALL_IN));
    when(bosse.hasFolded()).thenReturn(false);
    when(bosse.isAllIn()).thenReturn(true);

    when(staffan.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.CHECK));
    when(staffan.getAction()).thenReturn(new Action(ActionEnum.CHECK));
    when(staffan.hasFolded()).thenReturn(false);
    when(staffan.isAllIn()).thenReturn(false);

    final BetManager betManager = new BetManager(playerList, 50, new PotHandler());
    String decision = betManager.bet();
    assertEquals(
      "Player Peter Action :[CHECK]. Player Thomas Action :[CHECK]. Player Anders Action :[FOLD]. Player Bosse Action :[ALL_IN]. Player Staffan Action :[CHECK]. Player Peter Action :[CHECK]. Player Thomas Action :[CHECK]. "
      , decision);
  }

  @Test
  public void testBetUntilAllAreSatisfiedFivePlayersThreeRaise() {
    List<Player> playerList = Lists.newArrayList();
    Player peter = Mockito.mock(Player.class);
    when(peter.getName()).thenReturn("Peter");
    Player thomas = Mockito.mock(Player.class);
    when(thomas.getName()).thenReturn("Thomas");
    Player anders = Mockito.mock(Player.class);
    when(anders.getName()).thenReturn("Anders");
    Player bosse = Mockito.mock(Player.class);
    when(bosse.getName()).thenReturn("Bosse");
    Player staffan = Mockito.mock(Player.class);
    when(staffan.getName()).thenReturn("Staffan");
    playerList.add(peter);
    playerList.add(thomas);
    playerList.add(anders);
    playerList.add(bosse);
    playerList.add(staffan);
    when(peter.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.RAISE)).thenReturn(
      new Action(ActionEnum.CHECK));
    when(peter.getAction()).thenReturn(new Action(ActionEnum.RAISE)).thenReturn(new Action(ActionEnum.CHECK));
    when(peter.isAllIn()).thenReturn(false);
    when(peter.hasFolded()).thenReturn(false);   // Peter, thomas Anders bosse staffan

    when(thomas.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.RAISE)).thenReturn(
      new Action(ActionEnum.CHECK));
    when(thomas.getAction()).thenReturn(new Action(ActionEnum.RAISE)).thenReturn(new Action(ActionEnum.CHECK));
    when(thomas.isAllIn()).thenReturn(false);
    when(thomas.hasFolded()).thenReturn(false);  // , thomas Anders bosse staffan, Peter

    when(anders.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.FOLD));
    when(anders.getAction()).thenReturn(new Action(ActionEnum.FOLD));
    when(anders.isAllIn()).thenReturn(false);
    when(anders.hasFolded()).thenReturn(true);

    when(bosse.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.ALL_IN)).thenReturn(
      new Action(ActionEnum.ALL_IN));
    when(bosse.getAction()).thenReturn(new Action(ActionEnum.ALL_IN));
    when(bosse.isAllIn()).thenReturn(true);
    when(bosse.hasFolded()).thenReturn(false); // ,   bosse staffan, Peter, thomas

    when(staffan.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.RAISE)).thenReturn(
      new Action(ActionEnum.RAISE));
    when(staffan.getAction()).thenReturn(new Action(ActionEnum.RAISE));
    when(staffan.isAllIn()).thenReturn(false);
    when(staffan.hasFolded()).thenReturn(false);  // ,    staffan, Peter, thomas, bosse

    final BetManager betManager = new BetManager(playerList, 50, new PotHandler());
    final String decision = betManager.bet();
    assertEquals(
      "Player Peter Action :[RAISE]. Player Thomas Action :[RAISE]. Player Anders Action :[FOLD]. Player Bosse Action :[ALL_IN]. Player Staffan Action :[RAISE]. Player Peter Action :[CHECK]. Player Thomas Action :[CHECK]. "
      , decision);
  }

  @Test
  public void testBetUntilThreePlayerAllIn() {
    List<Player> playerList = Lists.newArrayList();
    Player peter = Mockito.mock(Player.class);
    when(peter.getName()).thenReturn("Peter");
    Player thomas = Mockito.mock(Player.class);
    when(thomas.getName()).thenReturn("Thomas");
    Player anders = Mockito.mock(Player.class);
    when(anders.getName()).thenReturn("Anders");
    playerList.add(peter);
    playerList.add(thomas);
    playerList.add(anders);
    when(peter.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.ALL_IN)).thenReturn(
      new Action(ActionEnum.ALL_IN));
    when(peter.getAction()).thenReturn(new Action(ActionEnum.ALL_IN)).thenReturn(new Action(ActionEnum.ALL_IN));
    when(peter.isAllIn()).thenReturn(true);
    when(peter.hasFolded()).thenReturn(false);

    when(thomas.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.ALL_IN)).thenReturn(
      new Action(ActionEnum.ALL_IN));
    when(thomas.getAction()).thenReturn(new Action(ActionEnum.ALL_IN)).thenReturn(new Action(ActionEnum.ALL_IN));
    when(thomas.isAllIn()).thenReturn(true);
    when(thomas.hasFolded()).thenReturn(false);

    when(anders.decideAction(Matchers.any(Draw.class), anyInt(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
      new Action(ActionEnum.ALL_IN));
    when(anders.getAction()).thenReturn(new Action(ActionEnum.ALL_IN));
    when(anders.isAllIn()).thenReturn(true);
    when(anders.hasFolded()).thenReturn(false);

    final BetManager betManager = new BetManager(playerList, 50, new PotHandler());
    final String decision = betManager.bet();
    assertEquals(
      "Player Peter Action :[ALL_IN]. Player Thomas Action :[ALL_IN]. Player Anders Action :[ALL_IN]. "
      , decision);
    String decision2 = betManager.bet();
    assertEquals(
      ""
      , decision2);

  }

  @Override
  protected PokerGame getPokerGame() {
    return pokerGame;
  }
}