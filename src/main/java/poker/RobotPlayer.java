package poker;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import static poker.Strategy.ALL_IN;
import static poker.Strategy.JOIN;
import static poker.Strategy.JOIN_IF_CHEAP;
import static poker.Strategy.OFFENSIVE;
import static poker.Strategy.QUIT;

public class RobotPlayer extends Player {

  private static final Logger logger = LogManager.getLogger(RobotPlayer.class);

  private Points points;

  public RobotPlayer(String playerName, int totalMarkersPerPlayer) {
    super(playerName, totalMarkersPerPlayer);
  }

  @Override
  /**
   * Since privatePoints is compensated by the number of players there is no need to consider number of players here
   */
  public void decideStrategy(Draw draw, int numberOfRemainingPlayers, List<Card> commonHand) {
    points = calculatePoints(numberOfRemainingPlayers, draw, commonHand);

    switch (draw) {
      case BEFORE_FLOP:
        // No common hand to care
        // Pair is good, getAmount or go all in if pot is big
        if (points.totalPoints > 113) {
          // Pair of aces and higher
          strategy = ALL_IN;
          // Pair
        } else if (points.totalPoints > 100) {
          strategy = OFFENSIVE;
        } else {
          strategy = JOIN_IF_CHEAP;
        }
        break;
      case FLOP:
        if (points.totalPoints > 300) {
          strategy = ALL_IN;
        } else if (points.totalPoints > 100) {
          strategy = OFFENSIVE;
        } else {
          strategy = QUIT;
        }
        // Pair of aces is good or anything higher, getAmount or go all in if pott is big
        // Low pair, join unless too expensive
        // Bad cards, try to join if cheap otherwise fold
        break;
      case TURN:
        if (points.totalPoints > 200) {
          strategy = ALL_IN;
        } else if (points.totalPoints > 100) {
          strategy = JOIN;
        } else {
          strategy = QUIT;
        }
        // Pair of aces is good or anything higher, getAmount or go all in if pott is big
        // Low pair, join unless too expensive
        // Bad cards, try to join if cheap otherwise fold
        break;
      case RIVER:
        if (points.totalPoints > 400) {
          strategy = ALL_IN;
        } else if (points.totalPoints > 200) {
          strategy = OFFENSIVE;
        } else if (points.totalPoints > 100) {
          strategy = JOIN;
        } else {
          strategy = QUIT;
        }
        break;
    }
    logger.debug("Player " + getName() + " has strategy " + strategy + ". ");
  }

  /*

  Thomas raises with 100, jörn checks wiht 100. maxRaiseFromAnotherPlayer = 0
  Thomas checks. maxraise still 100. check value should be 0.
  Jörn checks. maxRaise still 100. check value should be 0.

   */
  @Override
  protected void setAction(int raiseAmount,
                           int maxRaiseFromAPlayerThisRound,
                           int maxRaiseThisDraw,
                           int playersPartInPots) {
    logger.debug("Player :[" + getName() + "] raiseAmount: [" + raiseAmount + "] maxRaiseFromAPlayerThisRound :[" + maxRaiseFromAPlayerThisRound + "]");
    previousAction = getAction();

    // If player has no more markers player need to go all in
    if (strategy.equals(ALL_IN) || needToGoAllIn(raiseAmount)) {
      action = new Action(ActionEnum.ALL_IN);
      final int numberOfAllMarkers = getNumberOfMarkers();
      action.setAmount(numberOfAllMarkers); // TODO: number of markers?
      logger.trace("Set raise amount for player {{}} to {{}}", getName(), raiseAmount);
      partInPot += numberOfAllMarkers;
    } else if (raiseAmount > maxRaiseFromAPlayerThisRound) {
      if (BetManager.shallPayToPot(playersPartInPots, raiseAmount)) {
        action = new Action(ActionEnum.RAISE);
        action.setAmount(raiseAmount);
        logger.trace("Set raise amount for player {{}} to {{}}", getName(), raiseAmount);
      } else {
        action = new Action(ActionEnum.CHECK);
        action.setAmount(raiseAmount);
        logger.trace("Set check amount for player {{}} to {{}}", getName(), raiseAmount);
      }
      partInPot += raiseAmount;
    } else if (isWithin(raiseAmount, maxRaiseFromAPlayerThisRound)) {
      action = new Action(ActionEnum.CHECK);
      action.setAmount(maxRaiseFromAPlayerThisRound);
      logger.trace("Set check amount for player {{}} to {{}}", getName(), maxRaiseFromAPlayerThisRound);
      partInPot += maxRaiseFromAPlayerThisRound;
    } else {
      // If no one is raises there is no need to fold
      if (noRaiseThisDraw(maxRaiseFromAPlayerThisRound, maxRaiseThisDraw)) {
        action = new Action(ActionEnum.CHECK);
      } else {
        action = new Action(ActionEnum.FOLD);
      }
    }
  }

  private boolean noRaiseThisDraw(int maxRaiseFromAPlayerThisRound, int maxRaiseThisDraw) {
    if (maxRaiseThisDraw == 0) return true;
    logger.debug("Raise this draw: {{}}", maxRaiseThisDraw);
    return false;
  }

  private boolean isWithin(int raiseAmount, int maxRaiseFromOtherPlayer) {
    int raiseAmountIncludingBlind = calculateRaiseAmountIncludingBlind(raiseAmount);
    if (raiseAmountIncludingBlind >= maxRaiseFromOtherPlayer * 0.9) {
      return true;
    }
    return Math.abs(raiseAmountIncludingBlind - maxRaiseFromOtherPlayer) <= 25;
  }

  @Override
  protected int calculateRaiseAmount(int blind) {
    int individualRaiseAmount = 0;

    // Depending on strategy, pot and blind
    // low blind, offensive raises more, the rest joins
    // medium blind, offensive raises more, join joins, join if cheap drops
    // high blind offensive sets rise as blind, rest folds
    switch (strategy) {
      case ALL_IN:
        individualRaiseAmount = getNumberOfMarkers();
        break;
      case OFFENSIVE:
        // TODO: Set percentage of number of markers instead
        if (points.totalPoints > 113) { // Pair of aces and higher
          individualRaiseAmount = blind * 4;
        } else if (points.totalPoints > 100) {
          individualRaiseAmount = blind * 2;
        } else if (points.totalPoints > 5) {
          individualRaiseAmount = blind;
        } else {
          individualRaiseAmount = 0;
        }
        break;
      case JOIN:
        individualRaiseAmount = blind * 2;
        break;
      case JOIN_IF_CHEAP:
        if (blind <= 50) {
          individualRaiseAmount = blind;
        }
        break;
    }
    if (individualRaiseAmount > getNumberOfMarkers()) {
      individualRaiseAmount = getNumberOfMarkers();
    }
    logger.debug(getName() + " getAmount amount: " + individualRaiseAmount);
    return individualRaiseAmount;
  }

  private int calculateEventualBlindCost(int blind) {
    if (hasLittleBlind()) {
      return blind / 2;
    }
    if (hasBigBlind()) {
      return blind;
    }
    return 0;
  }

  Points calculatePoints(int numberOfRemainingPlayers, Draw draw, List<Card> commonHand) {
    Points points = new Points();
    int commonPoints = 0;
    int privatePoints = calculatePrivatePoints(getPrivateHand());
    logger.trace(getName() + " private points: " + privatePoints);
    // privatePoints = compensatePrivateHandWithNumberOfPlayers(privatePoints, numberOfRemainingPlayers);
    if (draw != Draw.BEFORE_FLOP) {
      commonPoints = calculateCommonPoints(numberOfRemainingPlayers, commonHand);
    }
    List<Card> totalHand = Lists.newArrayList();
    totalHand.addAll(getPrivateHand());
    totalHand.addAll(commonHand);
    int totalHandPoints = calculateTotalHandPoints(totalHand);
    logger.debug(getName() + " total points: " + totalHandPoints + " private points compensated: " + privatePoints + " common points compensated: " + commonPoints);
    points.privatePoints = privatePoints;
    points.commonPoints = commonPoints;
    points.totalPoints = totalHandPoints;
    return points;
  }

  private int calculateTotalHandPoints(List<Card> totalHand) {
    final Map<Card, PokerResult> totalPointsMap = EvaluationHandler.evaluateHand("common", totalHand);
    int totalHandPoints = EvaluationHandler.getResultFromCardPokerResultMap(totalPointsMap).getPoints();
    logger.trace("Total hand result for player {{}} : {{}}", getName(), EvaluationHandler.calculateResultFromHand(totalHand));
    return totalHandPoints;
  }

  private int calculatePrivatePoints(List<Card> hand) {
    final Map<Card, PokerResult> cardPokerResultMap = EvaluationHandler.evaluateHand(getName(), hand);
    return EvaluationHandler.calculatePointsFromHand(cardPokerResultMap);
  }

  /**
   * If lower than 4 players, an average hand may be a real good one
   */
  private int compensatePrivateHandWithNumberOfPlayers(int privatePoints, int numberOfRemainingPlayers) {
    if (numberOfRemainingPlayers == 3) {
      privatePoints = privatePoints * 2;
    } else if (numberOfRemainingPlayers == 2) {
      privatePoints = privatePoints * 3;
    }
    return privatePoints;
  }

  private int calculateCommonPoints(int numberOfRemainingPlayers, List<Card> commonHand) {
    final Map<Card, PokerResult> commonPointsMap = EvaluationHandler.evaluateHand("common", commonHand);
    int commonHandPoints = EvaluationHandler.getResultFromCardPokerResultMap(commonPointsMap).getPoints();
    // less probability that a common hand might fit another players hand
    if (numberOfRemainingPlayers < 4) {
      commonHandPoints = commonHandPoints / 2;
    }
    return commonHandPoints;
  }
}
