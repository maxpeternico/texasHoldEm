package poker;

import java.util.function.BiPredicate;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestPotHandler {
  @Test
  public void testTwoPlayersNoSplit() {
    PotHandler potHandler = new PotHandler();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 500);
    potHandler.joinPot(peter, peter.getNumberOfMarkers());
    potHandler.joinPot(thomas, thomas.getNumberOfMarkers());
    assertEquals(1, potHandler.getPots().size());
    assertEquals(500, potHandler.getPlayerPartInPots(peter));
    assertEquals(500, potHandler.getPlayerPartInPots(thomas));
  }

  @Test
  public void testTwoPlayersOneSplit() {
    PotHandler potHandler = new PotHandler();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 100);
    potHandler.joinPot(peter, peter.getNumberOfMarkers());
    potHandler.joinPot(thomas, thomas.getNumberOfMarkers());
    assertEquals(2, potHandler.getPots().size());
    assertEquals(500, potHandler.getPlayerPartInPots(peter));
    assertEquals(100, potHandler.getPlayerPartInPots(thomas));
    assertEquals(200, potHandler.getPots().get(0).getNumberOfMarkers());
    assertEquals(400, potHandler.getPots().get(1).getNumberOfMarkers());
  }

  @Test
  public void testThreePlayersNoSplit() {
    PotHandler potHandler = new PotHandler();
    Player peter = new RobotPlayer("Peter", 500);
    Player thomas = new RobotPlayer("Thomas", 500);
    Player ingemar = new RobotPlayer("Ingemar", 500);
    potHandler.joinPot(peter, peter.getNumberOfMarkers());
    potHandler.joinPot(thomas, thomas.getNumberOfMarkers());
    potHandler.joinPot(ingemar, ingemar.getNumberOfMarkers());
    assertEquals(1, potHandler.getPots().size());
    assertEquals(500, potHandler.getPlayerPartInPots(peter));
    assertEquals(500, potHandler.getPlayerPartInPots(peter));
    assertEquals(500, potHandler.getPlayerPartInPots(thomas));
  }

  @Test
  public void testThreePlayersTwoSplits() {
    PotHandler potHandler = new PotHandler();
    Player peter = new RobotPlayer("Peter", 800);
    Player thomas = new RobotPlayer("Thomas", 300);
    Player ingemar = new RobotPlayer("Ingemar", 100);
    potHandler.joinPot(peter, peter.getNumberOfMarkers());
    potHandler.joinPot(thomas, thomas.getNumberOfMarkers());
    potHandler.joinPot(ingemar, ingemar.getNumberOfMarkers());
    assertEquals(3, potHandler.getPots().size());
    assertEquals(800, potHandler.getPlayerPartInPots(peter));
    assertEquals(300, potHandler.getPlayerPartInPots(thomas));
    assertEquals(100, potHandler.getPlayerPartInPots(ingemar));
    assertEquals(3*100, potHandler.getPots().get(0).getNumberOfMarkers());
    assertEquals(2*(300-100), potHandler.getPots().get(1).getNumberOfMarkers());
    assertEquals(800-100-200, potHandler.getPots().get(2).getNumberOfMarkers());
  }

  @Test
  public void testSplitAfterRaiseAndThreeAllIns() {
    PotHandler potHandler = new PotHandler();
    Player peter = new RobotPlayer("Peter", 550);
    Player thomas = new RobotPlayer("Thomas", 500);
    Player ingemar = new RobotPlayer("Ingemar", 125);
    potHandler.joinPot(peter, 100);
    potHandler.joinPot(thomas, 100);
    potHandler.joinPot(ingemar, 125);
    potHandler.joinPot(peter, 450);
    potHandler.joinPot(thomas, 400);
    assertEquals(3, potHandler.getPots().size());
    assertEquals(550, potHandler.getPlayerPartInPots(peter));
    assertEquals(500, potHandler.getPlayerPartInPots(thomas));
    assertEquals(125, potHandler.getPlayerPartInPots(ingemar));
    assertEquals(50, potHandler.getPots().get(0).getNumberOfMarkers());
    assertEquals((500-125)*3, potHandler.getPots().get(1).getNumberOfMarkers());
    assertEquals(125*3, potHandler.getPots().get(2).getNumberOfMarkers());
  }
}