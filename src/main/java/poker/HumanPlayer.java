package poker;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static poker.Strategy.JOIN;
import static poker.Strategy.OFFENSIVE;
import static poker.Strategy.QUIT;

public class HumanPlayer extends Player {
  private static final Logger logger = LogManager.getLogger(HumanPlayer.class);

  public HumanPlayer(String playerName, int totalMarkersPerPlayer) {
    super(playerName, totalMarkersPerPlayer);
  }

  @Override
  public void decideStrategy(Turn turn, int numberOfRemainingPlayers, List<Card> commonHand, int blind) {
    String decision = KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("R", "C", "F"), "(R)aise/(C)heck/(F)old:");
    switch (decision) {
      case "R":
        strategy = OFFENSIVE;
        break;
      case "C":
        strategy = JOIN;
        break;
      case "F":
        strategy = QUIT;
        break;
      default:
        throw new RuntimeException("Illegal decision from human player :[" + decision + "]");
    }
  }

  @Override
  protected void setAction(int raiseAmount, int maxRaiseFromOtherPlayer) {
    switch (strategy) {
      case OFFENSIVE:
        action = new Action(ActionEnum.RAISE);
        action.setRaiseValue(raiseAmount);
        break;
      case JOIN:
        action = new Action(ActionEnum.CHECK);
        break;
      case QUIT:
        action = new Action(ActionEnum.FOLD);
        break;
      default:
        throw new RuntimeException("This should not happen. strategy:[" + strategy + "]");
    }
  }

  @Override
  protected int calculateRaiseAmount(int blind) {
    int raiseAmount = 0;
    if (strategy.equals(JOIN)) {
      return blind;
    }
    if (strategy.equals(QUIT)) {
      return 0;
    }
    try {
      raiseAmount = getRaiseAmount(blind);
    } catch (Exception e) {
      logger.warn(e.getMessage());
      strategy = QUIT;
      return 0;
    }
    return raiseAmount;
  }

  private int getRaiseAmount(int blind) {
    if (!hasMarkersForAmount(blind)) {
      throw new InsufficentMarkersException("You have no markers to pay the blind");
    }
    boolean hasMarkers = false;
    int desiredRaiseAmount = 0;
    do {
      desiredRaiseAmount = Integer.parseInt(KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), "Raise amount:"));
      hasMarkers = isDesiredRaiseAmountHigherThanBlind(desiredRaiseAmount, blind);
    } while (hasMarkers = false);
    return desiredRaiseAmount;
  }


  private boolean isFolding(String decision) {
    if (decision.equals("F")) {
      System.out.println("You fold! Bye");
      return true;
    }
    return false;
  }

  private boolean isRaising(String decision) {
    if (decision.equals("R")) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isChecking(String decision) {
    if (decision.equals("C")) {
      return true;
    } else {
      return false;
    }
  }
}
