package poker;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static junit.framework.TestCase.assertEquals;

public class TestPot {

  @Test
  public void testTwoPlayersOnePotSplit() {
    Pot pot = new Pot();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 100);
    List<Player> oldPotMembers = Lists.newArrayList();
    oldPotMembers.add(thomas);
    oldPotMembers.add(peter);
    pot.addMember(peter, 500);
    pot.addMember(thomas, 100);

   final Pot newPot = pot.splitPot(100);
    assertEquals(oldPotMembers, pot.getMembers());
    assertEquals(200, pot.getNumberOfMarkers());
    assertEquals(100, pot.getMarkersForMember(peter));
    assertEquals(100, pot.getMarkersForMember(thomas));
    assertEquals(400, newPot.getNumberOfMarkers());
    final List<Player> newPotMemberList = Lists.newArrayList();
    newPotMemberList.add(peter);
    assertEquals(newPot.getMembers(), newPotMemberList);
  }

  @Test
  public void testThreePlayersTwoPotSplits() {
    Pot pot = new Pot();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 100);
    Player ingemar = new RobotPlayer("Ingemar", 1000);
    List<Player> oldPotMembers = Lists.newArrayList();
    oldPotMembers.add(thomas);
    oldPotMembers.add(peter);
    oldPotMembers.add(ingemar);
    pot.addMember(peter, 500);
    pot.addMember(thomas, 100);
    pot.addMember(ingemar, 1000);

    final Pot newPot = pot.splitPot(100);
    assertEquals(oldPotMembers, pot.getMembers());
    assertEquals(300, pot.getNumberOfMarkers());
    assertEquals(100, pot.getMarkersForMember(peter));
    assertEquals(100, pot.getMarkersForMember(thomas));
    assertEquals(100, pot.getMarkersForMember(ingemar));
    assertEquals(newPot.getNumberOfMarkers(), 1300);

    final List<Player> newPotMemberList = Lists.newArrayList();
    newPotMemberList.add(peter);
    newPotMemberList.add(ingemar);
    assertEquals(newPot.getMembers(), newPotMemberList);
    assertEquals(400, newPot.getMarkersForMember(peter));
    assertEquals(900, newPot.getMarkersForMember(ingemar));

    final Pot newestPot = newPot.splitPot(400);
    List<Player> newestPotMembers = Lists.newArrayList();
    newestPotMembers.add(ingemar);
    assertEquals(newestPotMembers, newestPot.getMembers());
    assertEquals(500, newestPot.getNumberOfMarkers());
    assertEquals(newestPotMembers, newestPot.getMembers());
  }

  @Test
  public void testThreePlayersSplitOnePlayerCantAfford() {
    Pot pot = new Pot();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 100);
    Player ingemar = new RobotPlayer("Ingemar", 1000);
    pot.addMember(peter, 500);
    pot.addMember(thomas, 100);
    pot.addMember(ingemar, 1000);
    Pot newPot = pot.splitPot(200);
    List<Player> newPotMembers = Lists.newArrayList();
    newPotMembers.add(peter);
    newPotMembers.add(ingemar);
    List<Player> oldPotMembers = Lists.newArrayList();
    oldPotMembers.add(thomas);
    oldPotMembers.add(peter);
    oldPotMembers.add(ingemar);
    assertEquals(oldPotMembers, pot.getMembers());
    assertEquals(500, pot.getNumberOfMarkers());
    assertEquals(200, pot.getHighestAmount());
    assertEquals(200, pot.getMarkersForMember(peter));
    assertEquals(100, pot.getMarkersForMember(thomas));
    assertEquals(200, pot.getMarkersForMember(ingemar));
    assertEquals(newPotMembers, newPot.getMembers());
    assertEquals(1100, newPot.getNumberOfMarkers());
    assertEquals(300, newPot.getMarkersForMember(peter));
    assertEquals(800, newPot.getMarkersForMember(ingemar));
    assertEquals(800, newPot.getHighestAmount());
  }
}
