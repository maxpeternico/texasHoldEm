import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static java.util.Arrays.*;


class Dealer {

    private static final Logger logger = LogManager.getLogger(Dealer.class.getName());
    private static final Dealer dealer = new Dealer();
    private List<Player> players = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private final Color[] colors = { Color.hearts, Color.spades, Color.clubs, Color.diamonds };
    private final Ordinal[] ordinals = { Ordinal.two, Ordinal.three, Ordinal.four, Ordinal.five, Ordinal.six,
            Ordinal.seven, Ordinal.eight, Ordinal.nine, Ordinal.ten, Ordinal.knight, Ordinal.queen, Ordinal.king,
            Ordinal.ace };

    private List<Card> commonHand = new ArrayList<>();
    private List<Card> skippedCards = new ArrayList<>();
    private static final int NUMBER_OF_CARDS_ON_PRIVATE_HAND = 2;
    private static final int NUMBER_OF_CARD_FOR_LITTLE_BLIND = 3;
    private static final int NUMBER_OF_CARD_FOR_BIG_BLIND = 1;
    private static final int NUMBER_OF_CARD_FOR_FINAL_BLIND = 1;
    private static final int SKIP_CARD = 1;
    private static final int NUMBER_OF_CARDS_IN_DECK = 52;
    private Map<Player, List<PokerResult>> winStatistics = new HashMap<>();

    static Dealer getInstance() {
        return dealer;
    }

    private Dealer() {
        populateDeck();
        EvaluationHandler.initDrawnCardStatistics();
    }

    private void populateDeck() {
        for (Color color : colors) {
            for (Ordinal ordinal : ordinals) {
                deck.add(new Card(color, ordinal));
            }
        }
    }

    Player registerPlayer(String name) {
        Player player = new Player(name);
        players.add(player);
        winStatistics.put(player, new ArrayList<>());
        return player;
    }

    private List<Card> deal(int numberOfDesiredCards) {
        List<Card> cardsInHand = new ArrayList<>();

        while (cardsInHand.size() < numberOfDesiredCards) {
            Card drawnCard;
            do {
                Color color = Shuffle.getRandomColor();
                Ordinal ordinal = Shuffle.getRandomOrdinal();
                drawnCard = new Card(color, ordinal);
            } while (!deck.contains(drawnCard));
            logger.trace("Drawing card:" + drawnCard.toString() + "]");
            cardsInHand.add(drawnCard);
            EvaluationHandler.updateDrawnCardStatistics(drawnCard);
            boolean isCardRemovedFromDeck = deck.remove(drawnCard);
            if (!isCardRemovedFromDeck) {
                throw new RuntimeException("Card was not removed from deck!");
            }
        }
        return cardsInHand;
    }

    private void dealCommon(int numberOfCards) {
        List<Card> drawnCards = deal(numberOfCards);
        commonHand.addAll(drawnCards);
    }

    void playPrivateHand(Player player) {
        player.addPrivateCards(getPrivateHand());
        player.evaluateHand(commonHand);
    }

    void playLittleBlind(Player player) {
        player.evaluateHand(commonHand);
    }

    void playBigBlind(Player player) {
        player.evaluateHand(commonHand);
    }

    Map<Card, PokerResult> playLastDeal(Player player) {
        return player.evaluateHand(commonHand);
    }

    void drawLastDeal() {
        skipCard();
        dealCommon(NUMBER_OF_CARD_FOR_FINAL_BLIND);
    }

    void drawBigBlind() {
        skipCard();
        dealCommon(NUMBER_OF_CARD_FOR_BIG_BLIND);
    }

    private void skipCard() {
        logger.trace("The card:");
        skippedCards.addAll(deal(SKIP_CARD));
        logger.trace(" is skipped.");
    }

    void drawLittleBlind() {
        skipCard();
        dealCommon(NUMBER_OF_CARD_FOR_LITTLE_BLIND);
    }

    private List<Card> getPrivateHand() {
        return dealer.deal(NUMBER_OF_CARDS_ON_PRIVATE_HAND);
    }

    void play() {
        rotateDealer();
        players.stream().forEach(e->playPrivateHand(e));
        drawLittleBlind();
        players.stream().forEach(e->playLittleBlind(e));
        drawBigBlind();
        players.stream().forEach(e->playBigBlind(e));
        drawLastDeal();
        players.stream().forEach(e->playLastDeal(e));
    }

    private void rotateDealer() {
        Player playerToRotate = players.get(0);
        players.remove(0);
        players.add(playerToRotate);
    }

    void setPrivateHand(Player player, List<Card> privateHand) {
        List<Card> cardsInHand = new ArrayList<>();
        for (Card card : privateHand) {
            if (!deck.contains(card)) {
                throw new RuntimeException("Card [" + card.toString() + "] is not present in the Deck!");
            }
            logger.debug("Drawing card:" + card.toString() + "]");
            cardsInHand.add(card);
            boolean isCardRemovedFromDeck = deck.remove(card);
            if (!isCardRemovedFromDeck) {
                throw new RuntimeException("Card [" + card + "] was not removed from deck!");
            }
        }
        player.addPrivateCards(cardsInHand);
    }

    List<Player> getPlayers() {
        return players;
    }

    void putCardsInHandToDeck(List<Card> cardsInHand) {
        for (Card card : cardsInHand) {
            if (deck.contains(card)) {
                throw new RuntimeException("Deck already contains:[" + card.toString() + "]");
            }
            deck.add(card);
        }
        cardsInHand.clear();
    }

    List<Card> getCommonHand() {
        return commonHand;
    }

    List<Card> getSkippedCards() {
        return skippedCards;
    }

    boolean isDeckFull() {
        if (deck.size() == NUMBER_OF_CARDS_IN_DECK) {
            return true;
        } else {
            logger.info("Deck contains :[" + deck.size() + "] number of cards.");
            return false;
        }
    }

    Player getPlayer(String winnerName) {
        Player requestedPlayer = null;
        for (Player player:players) {
            if (player.getName().equals(winnerName)) {
                requestedPlayer = player;
            }
        }
        if (requestedPlayer == null) {
            throw new RuntimeException("Could not find player :[" + winnerName + "]");
        }
        return requestedPlayer;
    }

    void updateWinStatistics(Player winner, Map<Card, PokerResult> highScore) {
        List<PokerResult> pokerResults = winStatistics.get(winner);
        PokerResult pokerResult = EvaluationHandler.getResultFromCardPokerResultMap(highScore);
        pokerResults.add(pokerResult);
        winStatistics.put(winner, pokerResults);
    }

    void printWinStatistics() {
        int numberOfPairs = 0;
        Map<PokerResult, Integer> allPlayerWinStatistics = initAllPlayerWinStatistics();
        Set<Player> playerSet = winStatistics.keySet();
        Iterator<Player> players = playerSet.iterator();
        while (players.hasNext()) {
            Player player = players.next();
            List<PokerResult> resultStatistics = winStatistics.get(player);
            logger.info("[" + player.getName() + "] won [" + resultStatistics.size() + "] number of times on [");
            for (PokerResult pokerResult:PokerResult.values()) {
                int numberOfMatches = getNumberOfPokerResults(pokerResult, resultStatistics);
                if (numberOfMatches > 0) {
                    logger.info(pokerResult.toString() + ": [" + numberOfMatches + "] times.");
                    Integer allPlayerNumberOfMatches = allPlayerWinStatistics.get(pokerResult);
                    allPlayerNumberOfMatches = allPlayerNumberOfMatches + numberOfMatches;
                    allPlayerWinStatistics.put(pokerResult, allPlayerNumberOfMatches);
                }
            }
            winStatistics.put(player, new ArrayList<>());
        }
        printAllPlayerStatistics(allPlayerWinStatistics);
    }

    private void printAllPlayerStatistics(Map<PokerResult, Integer> allPlayerWinStatistics) {
        logger.info("Total game statistics for this round for all players:");
        stream(PokerResult.values()).forEach(e -> {
            logger.info("Number of wins on :[" + e.toString() + "] : [" + allPlayerWinStatistics.get(e) + "]");
        });
    }


    private Map<PokerResult, Integer> initAllPlayerWinStatistics() {
        Map<PokerResult, Integer> allPlayerWinStatistics = new HashMap<>();
        stream(PokerResult.values()).forEach(e->allPlayerWinStatistics.put(e, 0));
        return allPlayerWinStatistics;
    }

    private int getNumberOfPokerResults(PokerResult result, List<PokerResult> pokerResultList) {
        int numberOfMatches = 0;
        for (PokerResult pokerResult:pokerResultList) {
            if (result.equals(pokerResult)) {
                numberOfMatches++;
            }
        }
        return numberOfMatches;
    }
}
