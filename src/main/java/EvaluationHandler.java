import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class EvaluationHandler {

    private static final Logger logger = LogManager.getLogger(EvaluationHandler.class.getName());
    private static final Integer ACE_VALUE_ONE = 1;
    private static final int NUMBER_OF_CARDS_IN_HAND = 5;
    private static final String NO_FLUSH_FOUND = "No flush found.";

    static Map<Card, Integer> drawnCardStatistics = new HashMap<Card, Integer>();

    static void initDrawnCardStatistics() {
        for (Color color : Color.values()) {
            for (Ordinal ordinal : Ordinal.values()) {
                drawnCardStatistics.put(new Card(color, ordinal), 0);
            }
        }
    }

    public static Map<Card, PokerResult> evaluateHand(String name, List<Card> cardsInHand) {
        Map<Card, PokerResult> result = new HashMap<Card, PokerResult>();
        Card highestCard = findHighestCardByColor(cardsInHand);
        if (isRoyalStraightFlush(cardsInHand)) {
            logger.debug("[" + name + "] got a royal straight flush! [" + printCards(cardsInHand) + "]");
            result.put(highestCard, PokerResult.ROYAL_STRAIGHT_FLUSH);
        } else if (isStraightFlush(cardsInHand)) {
            logger.debug("[" + name + "] got a straight flush! [" + printCards(cardsInHand) + "]");
            result.put(highestCardFromStraight(cardsInHand), PokerResult.STRAIGHT_FLUSH);
        } else if (isFour(cardsInHand)) {
            logger.debug("[" + name + "] got a fours! [" + printCards(cardsInHand) + "]");
            result.put(highestCard, PokerResult.FOURS);
        } else if (isFullHouse(cardsInHand)) {
            logger.debug("[" + name + "] got a full house! [" + printCards(cardsInHand) + "]");
            result.put(highestCard, PokerResult.FULL_HOUSE);
        } else if (isFlush(cardsInHand)) {
            logger.debug("[" + name + "] got a flush! [" + printCards(cardsInHand) + "]");
            result.put(highestCard, PokerResult.FLUSH);
        } else if (isStraight(cardsInHand)) {
            logger.debug("[" + name + "] got a straight! [" + printCards(cardsInHand) + "]");
            result.put(highestCardFromStraight(cardsInHand), PokerResult.STRAIGHT);
        } else if (isTripple(cardsInHand)) {
            logger.debug("[" + name + "] got a threes! [" + printCards(cardsInHand) + "]");
            result.put(highestCard, PokerResult.THREES);
        } else if (isTwoPair(cardsInHand)) {
            logger.debug("[" + name + "] got two pair! [" + printCards(cardsInHand) + "]");
            result.put(highestCard, PokerResult.TWO_PAIR);
        } else if (isOnePair(cardsInHand)) {
            logger.debug("[" + name + "] got a pair! [" + printCards(cardsInHand) + "]");
            result.put(highestCard, PokerResult.PAIR);
        } else {
            logger.debug("[" + name + "]:s hand [" + printCards(cardsInHand) + "] did not give any money");
            result.put(highestCard, PokerResult.NO_RESULT);
        }
        return result;
    }

    static boolean isRoyalStraightFlush(List<Card> cardsInHand) {
        if (isStraightFlush(cardsInHand) && isFlushOf(cardsInHand, Color.hearts) &&
                highestCardFromStraight(cardsInHand).equals(new Card(Color.hearts, Ordinal.ace))) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isFlushOf(List<Card> cardsInHand, Color color) {
        boolean isFlushOf = false;
        try {
            if (returnFlushColor(cardsInHand).equals(color)) {
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

    static Card findHighestCardByColor(List<Card> cardsInHand) {
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

    static Color returnFlushColor(List<Card> cardsInHand) throws Exception {
        Color flushColor = null;
        if (cardsInHand.size() >= NUMBER_OF_CARDS_IN_HAND) {
            Map<Color, Integer> flushChecker = new HashMap<Color, Integer>();
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
        cardsInHand.stream().forEach(e -> possiblePairMap.put(e.getOrdinal().getValue(), 1));
        return possiblePairMap;
    }

    public static String getHandAsString(List<Card> privateHand) {
        StringBuilder hand = new StringBuilder();
        privateHand.stream().forEach(e -> hand.append(e.toString()));
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

    public static void updateDrawnCardStatistics(Card drawnCard) {
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
        return pokerResult.getValue();
    }
}
