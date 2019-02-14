//package poker;
//
//import java.util.List;
//
//import org.junit.Test;
//
//import com.google.common.collect.Lists;
//
//import static junit.framework.TestCase.assertEquals;
//
//public class TestPot {
//
//  @Test
//  public void testTwoPlayersOneAllIn() {
//    Pot pot = new Pot();
//    Player peter = new RobotPlayer("Peter", 500);
//    Player thomas = new RobotPlayer("Thomas", 100);
//    List<Player> oldPotMembers = Lists.newArrayList();
//    oldPotMembers.add(thomas);
//    oldPotMembers.add(peter);
//    pot.addMember(peter, 500);
//    pot.addMember(thomas, 100);
//
//    final List<Pot> pots = pot.splitPot(100);
//    assertEquals(pot.getMembers(), oldPotMembers);
//    assertEquals(pot.getNumberOfMarkers(), 200);
//    assertEquals(pots.get(0).getNumberOfMarkers(), 400);
//    final List<Player> newPotMemberList = Lists.newArrayList();
//    newPotMemberList.add(peter);
//    assertEquals(pots.get(0).getMembers(), newPotMemberList);
//  }
//
//  @Test
//  public void testThreePlayersTwoSplitsOneAllIn() {
//    Pot pot = new Pot();
//    Player peter = new RobotPlayer("Peter", 500);
//    Player thomas = new RobotPlayer("Thomas", 100);
//    Player ingemar = new RobotPlayer("Ingemar", 1000);
//    List<Player> oldPotMembers = Lists.newArrayList();
//    oldPotMembers.add(thomas);
//    oldPotMembers.add(peter);
//    oldPotMembers.add(ingemar);
//    pot.addMember(peter, 500);
//    pot.addMember(thomas, 100);
//    pot.addMember(ingemar, 1000);
//
//    final List<Pot> pots = pot.splitPot(100);
//    assertEquals(pot.getMembers(), oldPotMembers);
//    assertEquals(pot.getNumberOfMarkers(), 300);
//    assertEquals(pots.get(0).getNumberOfMarkers(), 1300);
//
//    final List<Player> newPotMemberList = Lists.newArrayList();
//    newPotMemberList.add(peter);
//    newPotMemberList.add(ingemar);
//    assertEquals(pots.get(0).getMembers(), newPotMemberList);
//
//    final Pot newestPot = pots.get(0); // TODO does not work
//    List<Player> newestPotMembers = Lists.newArrayList();
//    newestPotMembers.add(ingemar);
//    assertEquals(newestPotMembers, newestPot.getMembers());
//    assertEquals(500, newestPot.getNumberOfMarkers());
//    assertEquals(newestPotMembers, newestPot.getMembers());
//  }
//
//  @Test // TODO: pot.splitPot() should return list
//  public void testThreePlayersSplitOnePlayerCantAfford() {
//    Pot pot = new Pot();
//    Player peter = new RobotPlayer("Peter", 500);
//    Player thomas = new RobotPlayer("Thomas", 100);
//    Player ingemar = new RobotPlayer("Ingemar", 1000);
//    pot.addMember(peter, 500);
//    pot.addMember(thomas, 100);
//    pot.addMember(ingemar, 1000);
//    //List<Pot> pots = pot.splitPot(200);
//    //assertEquals(pot);
//  }
//}
