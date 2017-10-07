import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.LogManager;


public class Dealer {

    private static final Logger logger = LogManager.getLogger(Dealer.class.getName());
    private static final Dealer dealer = new Dealer();
    private List<Player> players = new ArrayList<Player>();
    private List<Card> deck = new ArrayList<Card>();
    private final Color[] colors = { Color.hearts, Color.spades, Color.clubs, Color.diamonds };
    private final Ordinal[] ordinals = { Ordinal.two, Ordinal.three, Ordinal.four, Ordinal.five, Ordinal.six,
            Ordinal.seven, Ordinal.eight, Ordinal.nine, Ordinal.ten, Ordinal.knight, Ordinal.queen, Ordinal.king,
            Ordinal.ace };

    private List<Card> commonHand = new ArrayList<Card>();
    private List<Card> skippedCards = new ArrayList<Card>();
    private static final int OFFSET_TWO = 2;
    private static final int NUMBER_OF_CARDS_ON_PRIVATE_HAND = 2;
    private static final int NUMBER_OF_CARD_FOR_LITTLE_BLIND = 3;
    private static final int NUMBER_OF_CARD_FOR_BIG_BLIND = 1;
    private static final int NUMBER_OF_CARD_FOR_FINAL_BLIND = 1;
    private static final int SKIP_CARD = 1;
    private static final int NUMBER_OF_CARDS_IN_DECK = 52;

    public static Dealer getInstance() {
        return dealer;
    }

    private Dealer() {
//        BasicConfigurator.resetConfiguration();
//        BasicConfigurator.configure();
        populateDeck();
        EvaluationHandler.initStatistics();
    }

    private void populateDeck() {
        for (Color color : colors) {
            for (Ordinal ordinal : ordinals) {
                deck.add(new Card(color, ordinal));
            }
        }
    }

    public Player registerPlayer(String name) {
        Player player = new Player(name);
        players.add(player);
        return player;
    }

    public List<Card> deal(int numberOfDesiredCards) {
        List<Card> cardsInHand = new ArrayList<Card>();

        while (cardsInHand.size() < numberOfDesiredCards) {
            Card drawnCard = null;
            do {
                Color color = getRandomColor();
                Ordinal ordinal = getRandomOrdinal();
                drawnCard = new Card(color, ordinal);
            } while (!deck.contains(drawnCard));
            logger.trace("Drawing card:" + drawnCard.toString() + "]");
            cardsInHand.add(drawnCard);
            boolean isCardRemovedFromDeck = deck.remove(drawnCard);
            if (!isCardRemovedFromDeck) {
                throw new RuntimeException("Card was not removed from deck!");
            }
        }
        return cardsInHand;
    }

    private Ordinal getRandomOrdinal() {
        int randomOrdinalValue = getRandomNumberUpToValue(ordinals.length - OFFSET_TWO) + OFFSET_TWO;
        Ordinal[] ordinals = Ordinal.values();
        Ordinal ordinalValueMatch = null;
        for (Ordinal ordinal : ordinals) {
            if (ordinal.getValue() == randomOrdinalValue) {
                ordinalValueMatch = ordinal;
            }
        }
        if (ordinalValueMatch == null) {
            throw new RuntimeException("No ordinal matches random ordinal:[" + randomOrdinalValue + "]");
        }
        return ordinalValueMatch;
    }

    Color getRandomColor() {
        int randomColorValue = getRandomNumberUpToValue(colors.length);
        Color[] colorValues = Color.values();
        Color colorValueMatch = null;
        for (Color colorValue : colorValues) {
            if (colorValue.getValue() == randomColorValue) {
                colorValueMatch = colorValue;
            }
        }
        if (colorValueMatch == null) {
            throw new RuntimeException("No colorvalue matches random number:[" + randomColorValue + "]");
        }
        return colorValueMatch;
    }

    int getRandomNumberUpToValue(int limit) {
        double randomNo = Math.random() * limit;
        return Double.valueOf(randomNo).intValue();
    }

    public void dealCommon(int numberOfCards) {
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

    public void play() {
        for (Player player : players) {
            player.initiateWinResultMap();
            playPrivateHand(player);
        }
        drawLittleBlind();
        for (Player player : players) {
            playLittleBlind(player);
        }
        drawBigBlind();
        for (Player player : players) {
            playBigBlind(player);
        }
        drawLastDeal();
        for (Player player : players) {
            playLastDeal(player);
        }
    }

    public void setPrivateHand(Player player, List<Card> privateHand) {
        List<Card> cardsInHand = new ArrayList<Card>();
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

    public List<Player> getPlayers() {
        return players;
    }

    public void putCardsInHandToDeck(List<Card> cardsInHand) {
        for (Card card : cardsInHand) {
            if (deck.contains(card)) {
                throw new RuntimeException("Deck already contains:[" + card.toString() + "]");
            }
            deck.add(card);
        }
        cardsInHand.clear();
    }

    public List<Card> getCommonHand() {
        return commonHand;
    }

    public List<Card> getSkippedCards() {
        return skippedCards;
    }

    public boolean isDeckFull() {
        if (deck.size() == NUMBER_OF_CARDS_IN_DECK) {
            return true;
        } else {
            logger.info("Deck contains :[" + deck.size() + "] number of cards.");
            return false;
        }
    }

    public Player getPlayer(String winnerName) {
        for (Player player:players) {
            if (player.getName().equals(winnerName)) {
                return player;
            }
        }
        return null;
    }

    public void addWin(Player winner, PokerResult result) {
        winner.addWin(result);
    }
}
