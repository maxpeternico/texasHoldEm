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
  protected boolean isPlayerChecking(int desiredRaiseAmount, int maxRaiseFromAPlayerThisRound) {
    // Human player already made choice
    if (strategy.equals(JOIN)) {
      action = new Action(ActionEnum.CHECK);
      return true;
    }
    return false;
  }

  @Override
  protected int setOffensiveRaiseAmount(int blind) {
    int individualRaiseAmount = 0;
    if (strategy.equals(OFFENSIVE)) {
      try {
        individualRaiseAmount = (int) getRaiseAmount(blind);
      } catch (Exception e) {
        throw new RuntimeException("Exception received from reading input. ");
      }
    }
    return individualRaiseAmount;
  }

  @Override
  protected int setJoinIfCheapRaiseAmount(int blind) {
    return setJoinRaiseAmount(blind);
  }

  @Override
  protected int setJoinRaiseAmount(int blind) {
    return blind;
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
