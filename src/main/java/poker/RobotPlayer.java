package poker;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static poker.Strategy.ALL_IN;
import static poker.Strategy.OFFENSIVE;

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
  public void decideStrategy(Draw draw, int numberOfRemainingPlayers, List<Card> commonHand, int blind) {
    points = calculatePoints(numberOfRemainingPlayers, draw, commonHand);

    switch (draw) {
      case BEFORE_FLOP:
        // No common hand to care
        // Pair is good, getRaiseAmount or go all in if pot is big
        if (points.totalPoints > 113) {
          // Pair of aces and higher
          strategy = ALL_IN;
          // Pair
        } else if (points.totalPoints > 100) {
          strategy = OFFENSIVE;
        }
        break;
      case BEFORE_TURN:
        // Pair of aces is good or anything higher, getRaiseAmount or go all in if pott is big
        // Low pair, join unless too expensive
        // Bad cards, try to join if cheap otherwise fold
        break;
      case BEFORE_RIVER:
        // Pair of aces is good or anything higher, getRaiseAmount or go all in if pott is big
        // Low pair, join unless too expensive
        // Bad cards, try to join if cheap otherwise fold
        break;
      case END_GAME:
        break;
    }
    logger.debug("Player " + getName() + " has strategy " + strategy + ". ");
  }

  @Override
  protected int setAction(int raiseAmount, int maxRaiseFromOtherPlayer) {
    logger.debug("Player :[" + getName() + "] raiseAmount: [" + raiseAmount + "] maxRaiseFromOtherPlayer :[" + maxRaiseFromOtherPlayer + "]");
    int finalRaiseAmount = 0;
    if (raiseAmount > maxRaiseFromOtherPlayer) {
      action = new Action(ActionEnum.RAISE);
      action.setRaiseValue(raiseAmount);
      finalRaiseAmount = raiseAmount;
    } else if (isWithin(raiseAmount, maxRaiseFromOtherPlayer)) {
      action = new Action(ActionEnum.CHECK);
      finalRaiseAmount = maxRaiseFromOtherPlayer;
    } else {
      action = new Action(ActionEnum.FOLD);
    }
    return finalRaiseAmount;
  }

  private boolean isWithin(int raiseAmount, int maxRaiseFromOtherPlayer) {
    if (raiseAmount >= maxRaiseFromOtherPlayer * 0.9 && raiseAmount <= maxRaiseFromOtherPlayer * 1.1) {
      return true;
    }
    return false;
  }

  @Override
  protected int calculateRaiseAmount(int blind) {
    int raiseAmount = 0;

    // Depending on strategy, pot and blind
    // low blind, offensive raises more, the rest joins
    // medium blind, offensive raises more, join joins, join if cheap drops
    // high blind offensive sets rise as blind, rest folds
    switch (strategy) {
      case ALL_IN:
        raiseAmount = getNumberOfMarkers();
        break;
      case OFFENSIVE:
        // TODO: Set percentage of number of markers instead
        if (points.privatePoints > 113) { // Pair of aces and higher
          if (points.commonPoints < 50) {
            // TODO: calculateRaiseAmount(privatePoints, commonPoints, sizeOfBlind, moneyLeft)
            // getRaiseAmount
            raiseAmount = 300;
          } else {
            // getRaiseAmount only if no other raises
            raiseAmount = 50;
          }
        } else if (points.privatePoints > 100) {
          if (points.commonPoints < 5) {
            // getRaiseAmount if no one else has raised
            raiseAmount = 75;
          } else {
            // don't getRaiseAmount, join if blind is cheap otherwise fold
            raiseAmount = 5;
          }
        } else if (points.privatePoints > 5) {
          if (points.commonPoints < 5) {
            // getRaiseAmount if no one else has raised
            raiseAmount = 5;
          } else {
            // don't getRaiseAmount, join if blind is cheap otherwise fold
            raiseAmount = 0;
          }
        } else {
          // don't getRaiseAmount, join if blind is cheap otherwise fold
          raiseAmount = 0;
        }
        break;
      case JOIN:
        raiseAmount = 100;
        break;
      case JOIN_IF_CHEAP:
        raiseAmount = 50;
        break;
    }
    if (raiseAmount > getNumberOfMarkers()) {
      raiseAmount = getNumberOfMarkers();
    }
    logger.debug(getName() + " getRaiseAmount amount: " + raiseAmount);
    return raiseAmount;
  }

  Points calculatePoints(int numberOfRemainingPlayers, Draw draw, List<Card> commonHand) {
    Points points = new Points();
    int commonPoints = 0;
    int privatePoints = calculatePrivatePoints(getPrivateHand());
    logger.debug(getName() + " private points: " + privatePoints);
   // privatePoints = compensatePrivateHandWithNumberOfPlayers(privatePoints, numberOfRemainingPlayers);
    if (draw != Draw.BEFORE_FLOP) {
      commonPoints = calculateCommonPoints(numberOfRemainingPlayers, commonHand);
    }
    List<Card> totalHand = Lists.newArrayList();
    totalHand.addAll(getPrivateHand());
    totalHand.addAll(commonHand);
    int totalHandPoints = calculateTotalHandPoints(totalHand);
    logger.debug(getName() + " private points compensated: " + privatePoints + " common points compensated: " + commonPoints);
    points.privatePoints = privatePoints;
    points.commonPoints = commonPoints;
    points.totalPoints = totalHandPoints;
    return points;
  }

  private int calculateTotalHandPoints(List<Card> totalHand) {
    final Map<Card, PokerResult> commonPointsMap = EvaluationHandler.evaluateHand("common", totalHand);
    int commonHandPoints = EvaluationHandler.getResultFromCardPokerResultMap(commonPointsMap).getPoints();
    return commonHandPoints;
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
