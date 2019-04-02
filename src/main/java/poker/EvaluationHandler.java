package poker;

import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class EvaluationHandler {

  private static final Logger logger = LogManager.getLogger(EvaluationHandler.class.getName());
  private static final Integer ACE_VALUE_ONE = 1;
  private static final int NUMBER_OF_CARDS_IN_HAND = 5;
  private static final String NO_FLUSH_FOUND = "No flush found.";

  private static Map<Card, Integer> drawnCardStatistics = new HashMap<>();

  static void initDrawnCardStatistics() {
    for (Color color : Color.values()) {
      for (Ordinal ordinal : Ordinal.values()) {
        drawnCardStatistics.put(new Card(color, ordinal), 0);
      }
    }
  }

  public static Map<Card, PokerResult> evaluateHand(String name, List<Card> cardsInHand) {
    Map<Card, PokerResult> result = new HashMap<>();
    Card highestCard = findHighestCardByColor(cardsInHand);
    if (isRoyalStraightFlush(cardsInHand)) {
      logger.debug("[" + name + "] got a royal straight flush! [" + printCards(cardsInHand) + "]");
      result.put(highestCard, new PokerResult(PokerHand.ROYAL_STRAIGHT_FLUSH));
    } else if (isStraightFlush(cardsInHand)) {
      logger.debug("[" + name + "] got a straight flush! [" + printCards(cardsInHand) + "]");
      result.put(highestCardFromStraight(cardsInHand), new PokerResult(PokerHand.STRAIGHT_FLUSH));
    } else if (isFour(cardsInHand)) {
      logger.debug("[" + name + "] got a fours! [" + printCards(cardsInHand) + "]");
      result.put(highestCard, new PokerResult(PokerHand.FOURS));
    } else if (isFullHouse(cardsInHand)) {
      logger.debug("[" + name + "] got a full house! [" + printCards(cardsInHand) + "]");
      result.put(highestCard, new PokerResult(PokerHand.FULL_HOUSE));
    } else if (isFlush(cardsInHand)) {
      logger.debug("[" + name + "] got a flush! [" + printCards(cardsInHand) + "]");
      result.put(highestCard, new PokerResult(PokerHand.FLUSH));
    } else if (isStraight(cardsInHand)) {
      logger.debug("[" + name + "] got a straight! [" + printCards(cardsInHand) + "]");
      result.put(highestCardFromStraight(cardsInHand), new PokerResult(PokerHand.STRAIGHT));
    } else if (isTripple(cardsInHand)) {
      logger.debug("[" + name + "] got a threes! [" + printCards(cardsInHand) + "]");
      result.put(highestCard, new PokerResult(PokerHand.THREES));
    } else if (isTwoPair(cardsInHand)) {
      logger.debug("[" + name + "] got two pair! [" + printCards(cardsInHand) + "]");
      result.put(highestCard, new PokerResult(PokerHand.TWO_PAIR));
    } else if (isOnePair(cardsInHand)) {
      logger.debug("[" + name + "] got a pair! [" + printCards(cardsInHand) + "]");
      result.put(highestCard,
              new PokerResult(
                      PokerHand.PAIR,
                      getPokerResultPointsFromPair(cardsInHand)
              )
      );
    } else {
      logger.debug("[" + name + "]:s hand [" + printCards(cardsInHand) + "] did not give any money");
      result.put(highestCard,
              new PokerResult(
                      PokerHand.NO_RESULT,
                      getPokerResultPointsFromNoResult(highestCard)
              )
      );
    }
    return result;
  }

  private static int getPokerResultPointsFromNoResult(Card highestCard) {
    // TODO: Find possible straights and flushes
    return highestCard.getOrdinal().getValue();
  }

  private static int getPokerResultPointsFromPair(List<Card> cardsInHand) {
    // Find value of pair
    for (Card card : cardsInHand) {
      List<Card> restOfHand = new ArrayList<>(cardsInHand);
      restOfHand.remove(card);
      for (Card cardFromRestOfHand : restOfHand) {
        if (cardFromRestOfHand.getOrdinal().equals(card.getOrdinal())) {
          return card.getOrdinal().getValue();
        }
      }
    }
    throw new RuntimeException("Did not find any pair in this hand:");
  }

  private static boolean isRoyalStraightFlush(List<Card> cardsInHand) {
    if (isStraightFlush(cardsInHand) && isFlushOfHearts(cardsInHand) &&
            highestCardFromStraight(cardsInHand).equals(new Card(Color.hearts, Ordinal.ace))) {
      return true;
    } else {
      return false;
    }
  }

  private static boolean isFlushOfHearts(List<Card> cardsInHand) {
    boolean isFlushOf = false;
    try {
      if (returnFlushColor(cardsInHand).equals(Color.hearts)) {
        isFlushOf = true;
      }
    } catch (Exception e) {
      checkExceptionMessage(e);
    }
    return isFlushOf;
  }

  static Card highestCardFromStraight(List<Card> cardsInHand) {
    List<Integer> valueList = new ArrayList<Integer>();
    int highestOrdinalValue = 0;

    for (Card card : cardsInHand) {
      // Special handling for ace, is considered both value 1 and 14. Add one card of both
      if (card.getOrdinal() == Ordinal.ace) {
        valueList.add(ACE_VALUE_ONE);
      }
      valueList.add(card.getOrdinal().getValue());
    }
    Collections.sort(valueList);
    int oldValue = 99;
    int straightCounter = 1;
    for (Integer value : valueList) {
      logger.trace("checking if value:[" + value + "] is part of a straight.");
      if (value == (oldValue + 1)) {
        straightCounter++;
        highestOrdinalValue = value;
        logger.trace("Value:[" + value + "] could be part of a possible straight. Straight counter:[" + straightCounter + "]");
        if (straightCounter >= NUMBER_OF_CARDS_IN_HAND) {
          logger.debug("Highest ordinal in straight:[" + value + "]");
        }
      } else if (value == oldValue) {
        // Do nothing
      } else {
        logger.trace("Value:[" + value + "] could be the lowest card in a straight.");
        straightCounter = 1;
      }
      oldValue = value;
    }
    Card highestCard = null;
    for (Card card : cardsInHand) {
      if (card.getOrdinal().getValue() == highestOrdinalValue) {
        highestCard = card;
      }
    }
    return highestCard;
  }

  private static Card findHighestCardByColor(List<Card> cardsInHand) {
    Card highestCard = new Card(Color.clubs, Ordinal.two);
    for (Card card : cardsInHand) {
      if (card.getOrdinal().getValue() > highestCard.getOrdinal().getValue()) {
        highestCard = card;
      } else if (card.getOrdinal().getValue() == highestCard.getOrdinal().getValue()) {
        if (isColorOfCardHigherThanColorOfHighestCard(highestCard, card)) {
          highestCard = card;
        }
      }
    }
    return highestCard;
  }

  private static boolean isColorOfCardHigherThanColorOfHighestCard(Card highestCard, Card card) {
    return card.getColor().getValue() > highestCard.getColor().getValue();
  }

  private static boolean isOnePair(List<Card> cardsInHand) {
    return countPair(cardsInHand) == 1;
  }

  /*
   * print by numeric index so user can select cards to throw away
   */
  private static String printCards(List<Card> cardsInHand) {
    StringBuilder buffer = new StringBuilder();
    for (Card card : cardsInHand) {
      buffer.append("\nCard:[").append(card.toString()).append("]\n");
    }
    return buffer.toString();
  }

  private static boolean isStraight(List<Card> cardsInHand) {
    boolean isStraight = false;
    if (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND) {
      List<Integer> valueList = new ArrayList<>();
      for (Card card : cardsInHand) {
        // Special handling for ace, is considered both value 1 and 14. Add one card of both
        if (card.getOrdinal() == Ordinal.ace) {
          valueList.add(ACE_VALUE_ONE);
        }
        valueList.add(card.getOrdinal().getValue());
      }
      Collections.sort(valueList);
      int oldValue = valueList.get(0) - 1;
      int straightCounter = 0;
      for (Integer value : valueList) {
        logger.trace("checking if value:[" + value + "] is part of a straight.");
        if (value == (oldValue + 1)) {
          straightCounter++;
          logger.trace("Value:[" + value + "] could be part of a possible straight. Straight counter:[" + straightCounter + "]");
        } else {
          logger.trace("Value:[" + value + "] could be the lowest card in a straight.");
          straightCounter = 1;
        }
        oldValue = value;
        if (straightCounter >= NUMBER_OF_CARDS_IN_HAND) {
          isStraight = true;
          logger.trace("Got a straight.");
        }
      }
    }
    return isStraight;
  }

  private static boolean isFlush(List<Card> cardsInHand) {
    boolean isFlush = false;
    try {
      Color flushColor = returnFlushColor(cardsInHand);
      if (flushColor != null) {
        isFlush = true;
      }
    } catch (Exception e) {
      checkExceptionMessage(e);
    }
    return isFlush;
  }

  private static void checkExceptionMessage(Exception e) {
    if (e.getMessage().equals(NO_FLUSH_FOUND)) {
      logger.trace(NO_FLUSH_FOUND);
    } else {
      throw new RuntimeException("Unexcpected exception received from returnFlushColor");
    }
  }

  private static Color returnFlushColor(List<Card> cardsInHand) throws Exception {
    Color flushColor = null;
    if (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND) {
      Map<Color, Integer> flushChecker = new HashMap<>();
      initFlushChecker(flushChecker);
      for (Card card : cardsInHand) {
        int oldValue = flushChecker.get(card.getColor());
        flushChecker.put(card.getColor(), ++oldValue);
        logger.trace("FlushCheck:[" + card.getColor().toString() + "] no:[" + oldValue + "]");
      }
      for (Color color : Color.values()) {
        if (flushChecker.get(color) == NUMBER_OF_CARDS_IN_HAND) {
          flushColor = color;
          logger.debug("Got a flush of:[" + color + "]");
        }
      }
      if (flushColor == null) {
        throw new Exception("No flush found.");
      }
    }
    return flushColor;
  }

  private static void initFlushChecker(Map<Color, Integer> flushChecker) {
    Arrays.stream(Color.values()).forEach(e -> flushChecker.put(e, 0));
  }

  private static boolean isTwoPair(List<Card> cardsInHand) {
    return countPair(cardsInHand) == 2;
  }

  private static int countPair(List<Card> cardsInHand) {
    Map<Multiple, Integer> multiple = getMultiple(cardsInHand);
    Set<Multiple> keySet = multiple.keySet();
    int numberOfPairs = 0;
    for (Multiple key : keySet) {
      if (key == Multiple.PAIR) {
        numberOfPairs = multiple.get(key);
        logger.trace("Found a pair");
      }
    }
    return numberOfPairs;
  }

  private static boolean isFullHouse(List<Card> cardsInHand) {
    return hasMultiple(cardsInHand, Multiple.THREES) && hasMultiple(cardsInHand, Multiple.PAIR)
        && (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND);
  }

  private static boolean isTripple(List<Card> cardsInHand) {
    return hasMultiple(cardsInHand, Multiple.THREES);
  }

  private static boolean isFour(List<Card> cardsInHand) {
    return hasMultiple(cardsInHand, Multiple.FOURS);
  }

  private static boolean hasMultiple(List<Card> cardsInHand, Multiple expectedMultiple) {
    Map<Multiple, Integer> multiple = getMultiple(cardsInHand);
    Set<Multiple> keySet = multiple.keySet();
    boolean hasMultiple = false;
    for (Multiple key : keySet) {
      if (key.getValue() == expectedMultiple.getValue()) {
        hasMultiple = true;
      }
    }
    return hasMultiple;
  }

  private static boolean isStraightFlush(List<Card> cardsInHand) {
    return isStraight(cardsInHand) && isFlush(cardsInHand) && (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND);
  }

  private static Map<Multiple, Integer> getMultiple(List<Card> cardsInHand) {
    Map<Multiple, Integer> multiple = new HashMap<>();
    Map<Integer, Integer> possiblePair = initPossiblePairMap(cardsInHand);
    List<Card> restOfHand = new ArrayList<>(cardsInHand);
    for (Card card : cardsInHand) {
      boolean isSuccesful = restOfHand.remove(card);
      if (!isSuccesful) {
        throw new RuntimeException(
                "Unable to remove card:[" + card.toString() + "] from hand [" + printCards(restOfHand) + "]");
      }
      for (Card restOfCard : restOfHand) {
        if (restOfCard.getOrdinal() == card.getOrdinal()) {
          int totalCount = possiblePair.get(card.getOrdinal().getValue());
          possiblePair.put(card.getOrdinal().getValue(), ++totalCount);
          logger.trace("Found multiple of:[" + card.getOrdinal().getValue() + "]");
          logger.trace("putting:[" + card.getOrdinal().getValue() + "] to possiblepair, count[" + totalCount
                  + "]");
          break; // For threes and fours we will get double hits if we continue
        }
      }

    }
    int ones = 0;
    int pairs = 0;
    int threes = 0;
    int fours = 0;
    Set<Integer> keySet = possiblePair.keySet();
    label:
    for (Integer key : keySet) {
      Integer value = possiblePair.get(key);
      switch (value.toString()) {
        case "4":
          fours++;
          // No need to check more cards
          break label;
        case "3":
          threes++;
          break;
        case "2":
          pairs++;
          break;
        case "1":
          ones++;
          break;
        default:
          throw new RuntimeException("We got 5 of [" + key.toString() + "]");
      }
    }
    if (fours != 0) {
      multiple.put(Multiple.FOURS, fours);
    }
    if (threes != 0) {
      multiple.put(Multiple.THREES, threes);
    }
    if (pairs != 0) {
      multiple.put(Multiple.PAIR, pairs);
    }
    if (ones != 0) {
      multiple.put(Multiple.SINGLE, ones);
    }
    return multiple;
  }

  private static Map<Integer, Integer> initPossiblePairMap(List<Card> cardsInHand) {
    Map<Integer, Integer> possiblePairMap = new HashMap<>();
    cardsInHand.forEach(e -> possiblePairMap.put(e.getOrdinal().getValue(), 1));
    return possiblePairMap;
  }

  static String getHandAsString(List<Card> privateHand) {
    StringBuilder hand = new StringBuilder();
    privateHand.forEach(e -> hand.append(e.toString()));
    return hand.toString();
  }

  static Card getTopCardFromResult(Map<Card, PokerResult> result) {
    Set<Card> cardSet = result.keySet();
    logger.debug("checking card:[" + cardSet.toString() + "]");
    Iterator<Card> iterator = cardSet.iterator();
    Card topCard = getLeastValueableCard();
    while (iterator.hasNext()) {
      Card card = iterator.next();
      if (card.isHigher(topCard)) {
        topCard = card;
      }
    }
    return topCard;
  }

  static Card getLeastValueableCard() {
    return new Card(Color.clubs, Ordinal.two);
  }

  static boolean isResultFromLatestPlayerHigherThanHighScore(Map<Card, PokerResult> result, Map<Card, PokerResult> highScore) {
    boolean isHigher = false;

    PokerResult pokerResult = getResultFromCardPokerResultMap(result);
    PokerResult highScorePokerResult = getResultFromCardPokerResultMap(highScore);

    if (pokerResult.getPokerHand().getValue() > highScorePokerResult.getPokerHand().getValue()) {
      isHigher = true;
    } else if (pokerResult.getPokerHand().getValue() == highScorePokerResult.getPokerHand().getValue()) {
      if (isTopCardFromLatestPlayerHigherThanTopCardFromHighScore(result, highScore)) {
        isHigher = true;
      }
    }
    return isHigher;
  }

  private static boolean isTopCardFromLatestPlayerHigherThanTopCardFromHighScore(Map<Card, PokerResult> result,
                                                                                 Map<Card, PokerResult> highScore) {
    Card topCard = getTopCardFromResult(result);
    Card highScoreTopCard = getTopCardFromResult(highScore);

    return topCard.isHigher(highScoreTopCard);
  }

  static PokerResult getResultFromCardPokerResultMap(Map<Card, PokerResult> result) {
    Card topCard = getTopCardFromResult(result);
    return result.get(topCard);
  }

  public static int getNumberOfDrawnCardsWithOrdinal() {
    int numberOfCards = 0;
    for (Color color : Color.values()) {
      for (Ordinal ordinal : Ordinal.values()) {
        numberOfCards += drawnCardStatistics.get(new Card(color, ordinal));
      }
    }
    return numberOfCards;
  }

  public static int getNumberOfDrawnCardsWithColor(Color requestedColor) {
    int numberOfColor = 0;
    for (Ordinal ordinal : Ordinal.values()) {
      numberOfColor += drawnCardStatistics.get(new Card(requestedColor, ordinal));
    }
    return numberOfColor;
  }

  static void updateDrawnCardStatistics(Card drawnCard) {
    int numberOfDrawnTimes = drawnCardStatistics.get(drawnCard);
    drawnCardStatistics.put(drawnCard, ++numberOfDrawnTimes);
  }

  public static int getNumberOfOrdinal(Ordinal requestedOrdinal) {
    int numberOfOrdinal = 0;
    for (Color color : Color.values()) {
      numberOfOrdinal += drawnCardStatistics.get(new Card(color, requestedOrdinal));
    }
    return numberOfOrdinal;
  }

  public static int calculatePointsFromHand(Map<Card, PokerResult> cardPokerResultMap) {
    final Set<Card> cardSet = cardPokerResultMap.keySet();
    if (cardSet.size() != 1) {
      throw new RuntimeException("cardPokerResultMap should have size of 1");
    }
    final Iterator<Card> iterator = cardSet.iterator();
    final PokerResult pokerResult = cardPokerResultMap.get(iterator.next());
    return pokerResult.getPoints();
  }

  static String calculateResultFromHand(List<Card> totalHand) {
    final Map<Card, PokerResult> cardPokerResultMap = evaluateHand("Player", totalHand);
    final Iterator<Map.Entry<Card, PokerResult>> iterator = cardPokerResultMap.entrySet().iterator();
    StringBuilder result = new StringBuilder();
    while (iterator.hasNext()) {
      final PokerResult pokerResult = iterator.next().getValue();
      result.append(pokerResult.getPokerHand().name());
    }
    return result.toString();
  }
}
