package poker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static java.util.Arrays.stream;


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
    private static final int NUMBER_OF_CARD_FOR_FLOP = 3;
    private static final int NUMBER_OF_CARD_FOR_TURN = 1;
    private static final int NUMBER_OF_CARD_FOR_RIVER = 1;
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

    Player registerPlayer(Player player) {
        players.add(player);
        winStatistics.put(player, new ArrayList<>());
        return player;
    }

    private List<Card> dealRandomCard(int numberOfDesiredCards) {
        List<Card> cardsInHand = new ArrayList<>();

        while (cardsInHand.size() < numberOfDesiredCards) {
            Card drawnCard;
            do {
                drawnCard = getRandomCard();
            } while (!deck.contains(drawnCard));
            logger.trace("Drawing card:" + drawnCard.toString() + "]");
            cardsInHand.add(drawnCard);
            EvaluationHandler.updateDrawnCardStatistics(drawnCard);
            removeCardFromDeck(drawnCard);
        }
        return cardsInHand;
    }

    private Card getRandomCard() {
        Color color = Shuffle.getRandomColor();
        Ordinal ordinal = Shuffle.getRandomOrdinal();
        return new Card(color, ordinal);
    }

    private void removeCardFromDeck(Card drawnCard) {
        boolean isCardRemovedFromDeck = deck.remove(drawnCard);
        if (!isCardRemovedFromDeck) {
            throw new RuntimeException("Card was not removed from deck!");
        }
    }

    private void dealCommon(int numberOfCards) {
        List<Card> drawnCards = dealRandomCard(numberOfCards);
        commonHand.addAll(drawnCards);
    }

    void playPrivateHand(Player player) {
        player.addPrivateCards(dealPrivateHand());
        player.evaluateHand(commonHand);
    }

    void playFlop(Player player) {
        player.evaluateHand(commonHand);
    }

    void playTurn(Player player) {
        player.evaluateHand(commonHand);
    }

    Map<Card, PokerResult> playRiver(Player player) {
        return player.evaluateHand(commonHand);
    }

    void drawRiver() {
        skipCard();
        dealCommon(NUMBER_OF_CARD_FOR_RIVER);
    }

    void drawTurn() {
        skipCard();
        dealCommon(NUMBER_OF_CARD_FOR_TURN);
    }

    private void skipCard() {
        logger.trace("The card:");
        skippedCards.addAll(dealRandomCard(SKIP_CARD));
        logger.trace(" is skipped.");
    }

    void drawFlop() {
        skipCard();
        dealCommon(NUMBER_OF_CARD_FOR_FLOP - getEventuallyReservedCardsForFlop());
    }

    private int getEventuallyReservedCardsForFlop() {
        if (commonHand.size() > 3) {
            throw new RuntimeException("Number of reserved card for flop cannot be higher than 3. Size :[" + commonHand.size() + "]");
        }
        return commonHand.size();
    }

    private List<Card> dealPrivateHand() {
        return dealer.dealRandomCard(NUMBER_OF_CARDS_ON_PRIVATE_HAND);
    }

    void play() {
        rotateDealer();
        playPrivateHands();
        drawFlop();
        players.stream().forEach(e-> playFlop(e));
        drawTurn();
        players.stream().forEach(e-> playTurn(e));
        drawRiver();
        players.stream().forEach(e-> playRiver(e));
    }

    void playPrivateHands() {
        players.stream().forEach(e->playPrivateHand(e));
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

    public void reserveCardToFlop(Color color, Ordinal ordinal) {
        final Card desiredCard = new Card(color, ordinal);
        if (!deck.contains(desiredCard)) {
            throw new RuntimeException("Deck does not contain :[" + desiredCard.toString() + "]");
        }
        commonHand.add(desiredCard);
        removeCardFromDeck(desiredCard);
    }

    public Ordinal getAnotherOrdinal(Ordinal ordinal) {
        Card randomCard;
        boolean hasFoundAnotherOrdinal = true;
        do {
            randomCard = getRandomCard();
            if (randomCard.getOrdinal() == ordinal) {
                hasFoundAnotherOrdinal = false;
            } else {
                hasFoundAnotherOrdinal = true;
            }
        } while (!hasFoundAnotherOrdinal);
        return randomCard.getOrdinal();
    }

    public List<Card> getPlayerHand(String playerName) {
        Player player = findPlayer(playerName);
        return player.getPrivateHand();
    }

    private Player findPlayer(String playerName) {
        for (Player player:players) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        throw new RuntimeException("Could not find player :[" + playerName + "]");
    }

    public Player findTheWinner() {
        Player winner = null;
        Map<Card, PokerResult> highScore = new HashMap<Card, PokerResult>();
        highScore.put(EvaluationHandler.getLeastValueableCard(), PokerResult.NO_RESULT);
        for (Player player : dealer.getPlayers()) {
            Map<Card, PokerResult> result = dealer.playRiver(player);
            logResult(player, result, highScore);
            if (EvaluationHandler.isResultFromLatestPlayerHigherThanHighScore(result, highScore)) {
                highScore.clear();
                highScore.putAll(result);
                winner = player;
            }
        }
        dealer.updateWinStatistics(winner, highScore);
        logger.info(
                "And the winner is:[" + winner.getName() + "] with highscore :[" + printPokerResult(highScore) + "]");
        return winner;
    }

    private void logResult(Player player, Map<Card, PokerResult> result, Map<Card, PokerResult> highScore) {
        logger.debug("[" + player.toString() + "] got [" + EvaluationHandler.getResultFromCardPokerResultMap(result)
                + "] with top card [" + EvaluationHandler.getTopCardFromResult(result) + "]");
        logger.trace(" from hand:[" + EvaluationHandler.getHandAsString(player.getPrivateHand()) + "]");
        logger.trace("Highscore is:[" + highScore.toString() + "]");
    }

    private String printPokerResult(Map<Card, PokerResult> highScore) {
        StringBuilder result = new StringBuilder();
        Card topCardFromResult = EvaluationHandler.getTopCardFromResult(highScore);
        result.append(highScore.get(topCardFromResult)).append(" top card: ").append(topCardFromResult.toString());

        return result.toString();
    }

    public void putCardsBackIntoDeck() {
        dealer.getPlayers().stream().forEach(e->dealer.putCardsInHandToDeck(e.getPrivateHand()));
        dealer.putCardsInHandToDeck(dealer.getCommonHand());
        dealer.putCardsInHandToDeck(dealer.getSkippedCards());
        if (!dealer.isDeckFull()) {
            throw new RuntimeException("Cards were lost!");
        }
    }
}
