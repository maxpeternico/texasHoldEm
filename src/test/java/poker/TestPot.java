package poker;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static junit.framework.TestCase.assertEquals;

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
    final Pot newPot = Pot.splitPot(pot, 100);
    assertEquals(pot.getMembers(), oldPotMembers);
    assertEquals(pot.getNumberOfMarkers(), 200);
    assertEquals(pot.getNumberOfMarkers(), 200);
    assertEquals(newPot.getNumberOfMarkers(), 400);
    final List<Player> newPotMemberList = Lists.newArrayList();
    newPotMemberList.add(peter);
    assertEquals(newPot.getMembers(), newPotMemberList);
  }
}
