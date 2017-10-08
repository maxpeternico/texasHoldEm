import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class EvaluationHandler {

    private static final Logger logger = LogManager.getLogger(EvaluationHandler.class.getName());
    private static final Integer ACE_VALUE_ONE = 1;
    private static final int NUMBER_OF_CARDS_IN_HAND = 5;

    static Map<PokerResult, Integer> statistics = new HashMap<PokerResult, Integer>();

    static void initStatistics() {
//        BasicConfigurator.configure();
//        BasicConfigurator.resetConfiguration();
        PokerResult[] values = PokerResult.values();
        for (PokerResult value : values) {
            statistics.put(value, 0);
        }
    }

    public static Map<Card, PokerResult> evaluateHand(String name, List<Card> cardsInHand) {
        Map<Card, PokerResult> result = new HashMap<Card, PokerResult>();
        Card highestCard = findHighestCardByColor(cardsInHand);
        if (isRoyalStraightFlush(cardsInHand)) {
            logger.debug("[" + name + "] got a royal straight flush! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.ROYAL_STRAIGHT_FLUSH);
            statistics.put(PokerResult.ROYAL_STRAIGHT_FLUSH, ++numberOfTimes);
            result.put(highestCard, PokerResult.ROYAL_STRAIGHT_FLUSH);
        } else if (isStraightFlush(cardsInHand)) {
            logger.debug("[" + name + "] got a straight flush! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.STRAIGHT_FLUSH);
            statistics.put(PokerResult.STRAIGHT_FLUSH, ++numberOfTimes);
            result.put(highestCard, PokerResult.STRAIGHT_FLUSH);
        } else if (isFour(cardsInHand)) {
            logger.debug("[" + name + "] got a fours! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.FOURS);
            statistics.put(PokerResult.FOURS, ++numberOfTimes);
            result.put(highestCard, PokerResult.FOURS);
        } else if (isFullHouse(cardsInHand)) {
            logger.debug("[" + name + "] got a full house! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.FULL_HOUSE);
            statistics.put(PokerResult.FULL_HOUSE, ++numberOfTimes);
            result.put(highestCard, PokerResult.FULL_HOUSE);
        } else if (isFlush(cardsInHand)) {
            logger.debug("[" + name + "] got a flush! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.FLUSH);
            statistics.put(PokerResult.FLUSH, ++numberOfTimes);
            result.put(highestCard, PokerResult.FLUSH);
        } else if (isStraight(cardsInHand)) {
            logger.debug("[" + name + "] got a straight! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.STRAIGHT);
            statistics.put(PokerResult.STRAIGHT, ++numberOfTimes);
            result.put(highestCardFromStraight(cardsInHand), PokerResult.STRAIGHT);
        } else if (isTripple(cardsInHand)) {
            logger.debug("[" + name + "] got a threes! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.THREES);
            statistics.put(PokerResult.THREES, ++numberOfTimes);
            result.put(highestCard, PokerResult.THREES);
        } else if (isTwoPair(cardsInHand)) {
            logger.debug("[" + name + "] got two pair! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.TWO_PAIR);
            statistics.put(PokerResult.TWO_PAIR, ++numberOfTimes);
            result.put(highestCard, PokerResult.TWO_PAIR);
        } else if (isOnePair(cardsInHand)) {
            logger.debug("[" + name + "] got a pair! [" + printCards(cardsInHand) + "]");
            Integer numberOfTimes = statistics.get(PokerResult.PAIR);
            statistics.put(PokerResult.PAIR, ++numberOfTimes);
            result.put(highestCard, PokerResult.PAIR);
        } else {
            logger.debug("[" + name + "]:s hand [" + printCards(cardsInHand) + "] did not give any money");
            Integer numberOfTimes = statistics.get(PokerResult.NO_RESULT);
            statistics.put(PokerResult.NO_RESULT, ++numberOfTimes);
            result.put(highestCard, PokerResult.NO_RESULT);
        }
        return result;
    }

    private static boolean isRoyalStraightFlush(List<Card> cardsInHand) {
        if (isFlush(cardsInHand) && cardsInHand.get(0).getColor().equals(Color.hearts) &&
                isStraight(cardsInHand) && highestCardFromStraight(cardsInHand).equals(new Card(Color.hearts, Ordinal.ace))) {
            return true;
        } else {
            return false;
        }
    }

    private static Card highestCardFromStraight(List<Card> cardsInHand) {
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
                highestOrdinalValue = value;
            }
        }
        Card highestCard = null;
        for (Card card:cardsInHand) {
            if (card.getOrdinal().getValue() == highestOrdinalValue) {
                highestCard = card;
            }
        }
        return highestCard;
    }

    static Card findHighestCardByColor(List<Card> cardsInHand) {
        Card highestCard = new Card(Color.clubs, Ordinal.two);
        for (Card card:cardsInHand) {
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
        if (card.getColor().getValue() > highestCard.getColor().getValue()) {
            return true;
        } else {
            return false;
        }
    }

    static boolean isOnePair(List<Card> cardsInHand) {
        if (countPair(cardsInHand) == 1) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * print by numeric index so user can select cards to throw away
     */
    private static String printCards(List<Card> cardsInHand) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cardsInHand.size(); i++) {
            buffer.append("\nCard:[" + cardsInHand.get(i).toString() + "]\n");
        }
        return buffer.toString();
    }

    static boolean isStraight(List<Card> cardsInHand) {
        boolean isStraight = false;
        if (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND) {
            List<Integer> valueList = new ArrayList<Integer>();
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

    static boolean isFlush(List<Card> cardsInHand) {
        boolean isFlush = false;
        if (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND) {
            Map<Color, Integer> flushChecker = new HashMap<Color, Integer>();
            initFlushChecker(flushChecker);
            for (Card card : cardsInHand) {
                int oldValue = flushChecker.get(card.getColor());
                flushChecker.put(card.getColor(), ++oldValue);
                logger.trace("FlushCheck:[" + card.getColor().toString() + "] no:[" + oldValue + "]");
            }
            for (Color color:Color.values()) {
                if (flushChecker.get(color) == NUMBER_OF_CARDS_IN_HAND) {
                    isFlush = true;
                    logger.debug("Got a flush of:[" + color + "]");
                }
            }
        } else {
            isFlush = false;
        }

        return isFlush;
    }

    private static void initFlushChecker(Map<Color, Integer> flushChecker) {
        for (Color color:Color.values()) {
            flushChecker.put(color, 0);
        }

    }

    static boolean isTwoPair(List<Card> cardsInHand) {
        if (countPair(cardsInHand) == 2) {
            return true;
        } else {
            return false;
        }
    }

    static int countPair(List<Card> cardsInHand) {
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

    static boolean isFullHouse(List<Card> cardsInHand) {
        if (hasMultiple(cardsInHand, Multiple.THREES) && hasMultiple(cardsInHand, Multiple.PAIR)
                && (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND)) {
            return true;
        } else {
            return false;
        }
    }

    static boolean isTripple(List<Card> cardsInHand) {
        return hasMultiple(cardsInHand, Multiple.THREES);
    }

    static boolean isFour(List<Card> cardsInHand) {
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

    static boolean isStraightFlush(List<Card> cardsInHand) {
        if (isStraight(cardsInHand) && isFlush(cardsInHand) && (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND)) {
            return true;
        } else {
            return false;
        }
    }

    static Map<Multiple, Integer> getMultiple(List<Card> cardsInHand) {
        Map<Multiple, Integer> multiple = new HashMap<Multiple, Integer>();
        Map<Integer, Integer> possiblePair = initPossiblePairMap(cardsInHand);
        List<Card> restOfHand = new ArrayList<Card>(cardsInHand);
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
        for (Integer key : keySet) {
            Integer value = possiblePair.get(key);
            if (value.toString().equals("4")) {
                fours++;
                // No need to check more cards
                break;
            } else if (value.toString().equals("3")) {
                threes++;
            } else if (value.toString().equals("2")) {
                pairs++;
            } else if (value.toString().equals("1")) {
                ones++;
            } else {
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
        Map<Integer, Integer> possiblePairMap = new HashMap<Integer, Integer>();
        for (Card card : cardsInHand) {
            possiblePairMap.put(card.getOrdinal().getValue(), 1);
        }
        return possiblePairMap;
    }

    public static String printHand(List<Card> privateHand) {
        StringBuilder hand = new StringBuilder();
        for (Card card:privateHand) {
            hand.append(card.toString());
        }
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

        if (pokerResult.getValue() > highScorePokerResult.getValue()) {
            isHigher = true;
        } else if (pokerResult.getValue() == highScorePokerResult.getValue()) {
            if (isTopCardFromLatestPlayerHigherThanTopCardFromHighScore(result, highScore)) {
                isHigher = true;
            }
        }
        return isHigher;
    }

    static boolean isTopCardFromLatestPlayerHigherThanTopCardFromHighScore(Map<Card, PokerResult> result,
                                                                           Map<Card, PokerResult> highScore) {
        Card topCard = getTopCardFromResult(result);
        Card highScoreTopCard = getTopCardFromResult(highScore);

        if (topCard.isHigher(highScoreTopCard)) {
            return true;
        } else {
            return false;
        }
    }

    static PokerResult getResultFromCardPokerResultMap(Map<Card, PokerResult> result) {
        Card topCard = getTopCardFromResult(result);
        PokerResult pokerResult = result.get(topCard);
        return pokerResult;
    }
}
