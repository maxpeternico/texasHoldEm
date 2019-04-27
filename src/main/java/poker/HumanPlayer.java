package poker;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import static poker.Strategy.ALL_IN;
import static poker.Strategy.JOIN;
import static poker.Strategy.NOT_DECIDED;
import static poker.Strategy.OFFENSIVE;
import static poker.Strategy.QUIT;

public class HumanPlayer extends Player {

  private static final Logger logger = LogManager.getLogger(HumanPlayer.class);

  HumanPlayer(String playerName, int totalMarkersPerPlayer) {
    super(playerName, totalMarkersPerPlayer);
  }

  @Override
  public void decideStrategy(Draw draw, int numberOfRemainingPlayers, List<Card> commonHand) {
    String decision = KeyboardHelper.getCharFromKeyboard(
      Lists.newArrayList("A", "R", "C", "F"),
      "(A)ll in/(R)aise/(C)heck/(F)old:",
      1);
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
  /*
    This is handled by getRaiseAmount for human player
  */
  protected int hasPlayerBlindAndIsDesiredRaiseHigher(int desiredRaiseAmount) {
    return desiredRaiseAmount;
  }

  @Override
  protected int setAction2(int desiredRaiseAmount,
                           int maxRaiseFromAPlayer,
                           int maxRaiseThisDraw,
                           int playersPartInPots) {
    int finalRaiseAmount = 0;

    if (hasToGoAllIn(desiredRaiseAmount)) {
      finalRaiseAmount = goAllIn();
    } else if (desiredRaiseAmount > maxRaiseFromAPlayer) {
      if (BetManager.shallPayToPot(playersPartInPots, desiredRaiseAmount)) {
        action = new Action(ActionEnum.RAISE);
      } else {
        action = new Action(ActionEnum.CHECK);
      }
      finalRaiseAmount = desiredRaiseAmount;
    } else if (strategy.equals(JOIN)) {
      action = new Action(ActionEnum.CHECK);
      finalRaiseAmount = maxRaiseFromAPlayer;
    } else if (strategy.equals(QUIT)) {
       if (noRaiseThisDraw(maxRaiseThisDraw)) {
        logger.trace("No raise this draw. ");
        System.out.println("You don't need to fold, there is no raise this draw. You check. ");
        action = new Action(ActionEnum.CHECK);
      } else {
        action = new Action(ActionEnum.FOLD);
      }
      finalRaiseAmount = 0;
    }
    return finalRaiseAmount;
  }

  @Override
  protected boolean isCheckSelected(int desiredRaiseAmount, int maxRaiseFromAPlayerThisRound) {
    if (strategy.equals(JOIN)) {
      action = new Action(ActionEnum.CHECK);
      return true;
    }
    return false;
  }

  private int getNewRaiseAmount(int amountToJoinPot) {
    int raiseAmount;
    do {
      System.out.println("Raise amount must be higher than " + amountToJoinPot);
      raiseAmount = (int) getRaiseAmount(blindAmount);
    } while (raiseAmount < amountToJoinPot);
    return raiseAmount;
  }

  @Override
  protected int calculateRaiseAmount(int blind) {
    int raiseAmount;
    if (strategy.equals(NOT_DECIDED)) {
      throw new RuntimeException("Strategy can not be NOT_DECIDED here. ");
    }
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
      raiseAmount = (int) getRaiseAmount(blind);
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

  private long getRaiseAmount(int raiseAmount) {
    if (hasMarkersForAmount(raiseAmount)) {
      System.out.println("You don't have markers to pay [" + raiseAmount + "], you have to go all in. ");
      return getNumberOfMarkers();
    }
    boolean hasMarkersForBlind;
    boolean hasMarkers;
    long desiredRaiseAmount;
    do {
      desiredRaiseAmount = Long.parseLong(

        KeyboardHelper.getCharFromKeyboard(
          Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
          "Raise amount:",
          null
        )
      );
      hasMarkersForBlind = isDesiredRaiseAmountHigherThanBlind(desiredRaiseAmount, raiseAmount);
      hasMarkers = isDesiredRaiseAmountHigherThanNumberOfMarkers(desiredRaiseAmount);
    } while (!hasMarkersForBlind || !hasMarkers);
    return desiredRaiseAmount;
  }

  private boolean isDesiredRaiseAmountHigherThanNumberOfMarkers(long desiredRaiseAmount) {
    return desiredRaiseAmount <= getNumberOfMarkers();
  }
}
