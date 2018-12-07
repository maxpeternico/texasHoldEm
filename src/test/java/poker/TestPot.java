package poker;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class TestPot {

  @Test
  public void testTwoPlayersOneAllIn() {
    Pot pot = new Pot();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 100);
    List<Player> oldPotMembers = Lists.newArrayList();
    oldPotMembers.add(thomas);
    oldPotMembers.add(peter);
    pot.addMember(peter, 500);
    pot.addMember(thomas, 100);

    final Pot newPot = pot.splitPot(100);
    assertEquals(pot.getMembers(), oldPotMembers);
    assertEquals(pot.getNumberOfMarkers(), 200);
    assertEquals(newPot.getNumberOfMarkers(), 400);
    final List<Player> newPotMemberList = Lists.newArrayList();
    newPotMemberList.add(peter);
    assertEquals(newPot.getMembers(), newPotMemberList);
  }

  @Test
  public void testThreePlayersTwoSplitsOneAllIn() {
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
    assertEquals(pot.getMembers(), oldPotMembers);
    assertEquals(pot.getNumberOfMarkers(), 300);
    assertEquals(newPot.getNumberOfMarkers(), 1300);

    final List<Player> newPotMemberList = Lists.newArrayList();
    newPotMemberList.add(peter);
    newPotMemberList.add(ingemar);
    assertEquals(newPot.getMembers(), newPotMemberList);

    final Pot newestPot = newPot.splitPot(400);
    List<Player> newestPotMembers = Lists.newArrayList();
    newestPotMembers.add(ingemar);
    assertEquals(newestPotMembers, newestPot.getMembers());
    assertEquals(500, newestPot.getNumberOfMarkers());
    assertEquals(newestPotMembers, newestPot.getMembers());
  }

  @Test
  public void testThreePlayersSplitWithTooHighValueNegativeTest() {
    Pot pot = new Pot();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 100);
    Player ingemar = new RobotPlayer("Ingemar", 1000);
    pot.addMember(peter, 500);
    pot.addMember(thomas, 100);
    pot.addMember(ingemar, 1000);

    try {
      pot.splitPot(200);
      fail("Exception should have been received.");
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Player :[Thomas] can't afford AllInVale!");
    }
  }
}
