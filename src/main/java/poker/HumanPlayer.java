package poker;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import static poker.Strategy.ALL_IN;
import static poker.Strategy.JOIN;
import static poker.Strategy.OFFENSIVE;
import static poker.Strategy.QUIT;

public class HumanPlayer extends Player {

  private static final Logger logger = LogManager.getLogger(HumanPlayer.class);

  HumanPlayer(String playerName, int totalMarkersPerPlayer) {
    super(playerName, totalMarkersPerPlayer);
  }

  @Override
  public void decideStrategy(Draw draw, int numberOfRemainingPlayers, List<Card> commonHand) {
    String decision = KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("A", "R", "C", "F"), "(A)ll in/(R)aise/(C)heck/(F)old:");
    switch (decision.charAt(0)) {
      case 'A':
        strategy = ALL_IN;
        break;
      case 'R':
        strategy = OFFENSIVE;
        break;
      case 'C':
        strategy = JOIN;
        break;
      case 'F':
        strategy = QUIT;
        break;
      default:
        throw new RuntimeException("Illegal decision from human player :[" + decision + "]");
    }
  }

  @Override
  protected void setAction(int raiseAmount,
                           int amountToJoinPot,
                           int maxRaiseThisDraw,
                           int playersPartInPots) {
    switch (strategy) {
      case ALL_IN:
        action = new Action(ActionEnum.ALL_IN);
        action.setAmount(raiseAmount);
        break;
      case OFFENSIVE:
        if (canPlayerAffordToDoAction(amountToJoinPot)) {
          if (raiseAmount > amountToJoinPot) {
            action = new Action(ActionEnum.RAISE);
          } else {
            do {
              System.out.println("Raise amount must be higher than " + amountToJoinPot);
              raiseAmount = getRaiseAmount(blindAmount);
            }while (raiseAmount < amountToJoinPot);
          }
        } else {
          action = new Action(ActionEnum.ALL_IN);
          raiseAmount = getNumberOfMarkers();
        }
        action.setAmount(raiseAmount);
        break;
      case JOIN:
        if (canPlayerAffordToDoAction(amountToJoinPot)) {
          action = new Action(ActionEnum.ALL_IN);
          amountToJoinPot = getNumberOfMarkers();
        } else {
          action = new Action(ActionEnum.CHECK);
        }
        action.setAmount(amountToJoinPot);
        break;
      case QUIT:
        action = new Action(ActionEnum.FOLD);
        break;
      default:
        throw new RuntimeException("This should not happen. strategy:[" + strategy + "]");
    }
  }

  private boolean canPlayerAffordToDoAction(int amountToJoinPot) {
    if (amountToJoinPot >= getNumberOfMarkers()) {
      System.out.println("You do not have markers enough for action, you have to go all in. ");
      return false;
    }
    return true;
  }

  @Override
  protected int calculateRaiseAmount(int blind) {
    int raiseAmount;
    if (strategy.equals(ALL_IN)) {
      return getNumberOfMarkers();
    }
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
    if (raiseAmount == getNumberOfMarkers()) {
      System.out.println("You have no markers left. You have to go all-in. ");
      strategy = ALL_IN;
    }
    return raiseAmount;
  }

  private int getRaiseAmount(int blind) {
    if (hasMarkersForAmount(blind)) {
      System.out.println("You don't have markers to pay the blind [" + blind +"], you have to go all in. ");
      return getNumberOfMarkers();
    }
    boolean hasMarkersForBlind;
    boolean hasMarkers;
    int desiredRaiseAmount;
    do {
      desiredRaiseAmount = Integer.parseInt(KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), "Raise amount:"));
      hasMarkersForBlind = isDesiredRaiseAmountHigherThanBlind(desiredRaiseAmount, blind);
      hasMarkers = isDesiredRaiseAmountHigherThanNumberOfMarkers(desiredRaiseAmount);
    } while (!hasMarkersForBlind || !hasMarkers);
    return desiredRaiseAmount;
  }

  private boolean isDesiredRaiseAmountHigherThanNumberOfMarkers(int desiredRaiseAmount) {
    return desiredRaiseAmount <= getNumberOfMarkers();
  }
}
