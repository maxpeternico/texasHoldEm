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

  public HumanPlayer(String playerName, int totalMarkersPerPlayer) {
    super(playerName, totalMarkersPerPlayer);
  }

  @Override
  public void decideStrategy(Draw draw, int numberOfRemainingPlayers, List<Card> commonHand) {
    String decision = KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("A", "R", "C", "F"), "(A)ll in/(R)aise/(C)heck/(F)old:");
    switch (decision) {
      case "A":
        strategy = ALL_IN;
        break;
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
  protected void setAction(int raiseAmount,
                           int amountToJoinPot,
                           int maxRaiseThisDraw,
                           int playersPartInPots) {
    if (strategy.equals(ALL_IN)) {
      while (raiseAmount < amountToJoinPot) {
        System.out.println("You must bet more than: [" + amountToJoinPot + "]");
        raiseAmount = getRaiseAmount(getBlindAmount());
      }
    }
    switch (strategy) {
      case ALL_IN:
        action = new Action(ActionEnum.ALL_IN);
        action.setAmount(raiseAmount);
        break;
      case OFFENSIVE:
        action = new Action(ActionEnum.RAISE);
        action.setAmount(raiseAmount);
        break;
      case JOIN:
        action = new Action(ActionEnum.CHECK);
        action.setAmount(raiseAmount);
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
    if (!hasMarkersForAmount(blind)) {
      System.out.println("You don't have markers to pay the blind [" + blind +"], you have to go all in. ");
      return getNumberOfMarkers();
    }
    boolean hasMarkersForBlind = false;
    boolean hasMarkers = false;
    int desiredRaiseAmount = 0;
    do {
      desiredRaiseAmount = Integer.parseInt(KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), "Raise amount:"));
      hasMarkersForBlind = isDesiredRaiseAmountHigherThanBlind(desiredRaiseAmount, blind);
      hasMarkers = isDesiredRaiseAmountHigherThanNumberOfMarkers(desiredRaiseAmount);
    } while (hasMarkersForBlind == false || hasMarkers == false);
    return desiredRaiseAmount;
  }

  private boolean isDesiredRaiseAmountHigherThanNumberOfMarkers(int desiredRaiseAmount) {
    if (desiredRaiseAmount > getNumberOfMarkers()) {
      return false;
    }
    return true;
  }
}
