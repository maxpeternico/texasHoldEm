package poker;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static java.util.Arrays.stream;


class Dealer {

  private static final Logger logger = LogManager.getLogger(Dealer.class.getName());
  private static final Dealer dealer = new Dealer();
  private List<Player> players = new ArrayList<>();
  private List<Card> deck = new ArrayList<>();
  private final Color[] colors = {Color.hearts, Color.spades, Color.clubs, Color.diamonds};
  private final Ordinal[] ordinals = {Ordinal.two, Ordinal.three, Ordinal.four, Ordinal.five, Ordinal.six,
          Ordinal.seven, Ordinal.eight, Ordinal.nine, Ordinal.ten, Ordinal.knight, Ordinal.queen, Ordinal.king,
          Ordinal.ace};

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

  void removeCardFromDeck(Card drawnCard) {
    boolean isCardRemovedFromDeck = deck.remove(drawnCard);
    if (!isCardRemovedFromDeck) {
      throw new RuntimeException("Card was not removed from deck!");
    }
  }

  private void dealCommon(int numberOfCards) {
    List<Card> drawnCards = dealRandomCard(numberOfCards);
    commonHand.addAll(drawnCards);
  }

  private boolean isCommonHandForFlopTest() {
    // At test common hand is already set
    if (commonHand.size() == 3) {
      return true;
    }
    return false;
  }

  private boolean isCommonHandForTurnTest() {
    // At test common hand is already set
    if (commonHand.size() == 4) {
      return true;
    }
    return false;
  }

  private boolean isCommonHandForRiverTest() {
    // At test common hand is already set
    if (commonHand.size() == 5) {
      return true;
    }
    return false;
  }

  void playPrivateHand(Player player) {
    if (!isTest(player)) {
      player.addPrivateCards(dealPrivateHand());
    }
    player.evaluateHand(commonHand);
  }

  private boolean isTest(Player player) {
    // At test private hand is already set
    if (player.getPrivateHand().size() == 2) {
      return true;
    }
    return false;
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

  void drawFlop() {
    if (isCommonHandForFlopTest()) {
      return;
    }
    skipCard();
    dealCommon(NUMBER_OF_CARD_FOR_FLOP);
//    dealCommon(NUMBER_OF_CARD_FOR_FLOP - getEventuallyReservedCardsForFlop());
  }

  void drawRiver() {
    if (isCommonHandForRiverTest()) {
      return;
    }
    skipCard();
    dealCommon(NUMBER_OF_CARD_FOR_RIVER);
  }

  void drawTurn() {
    if (isCommonHandForTurnTest()) {
      return;
    }

    skipCard();
    dealCommon(NUMBER_OF_CARD_FOR_TURN);
  }

  private void skipCard() {
    logger.trace("The card:");
    skippedCards.addAll(dealRandomCard(SKIP_CARD));
    logger.trace(" is skipped.");
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
    players.stream().forEach(e -> playFlop(e));
    drawTurn();
    players.stream().forEach(e -> playTurn(e));
    drawRiver();
    players.stream().forEach(e -> playRiver(e));
  }

  void playPrivateHands() {
    players.stream().forEach(e -> playPrivateHand(e));
  }

  private void rotateDealer() {
    Player playerToRotate = players.get(0);
    players.remove(0);
    players.add(playerToRotate);
  }

  void setPrivateHand(Player player, List<Card> privateHand) {
    List<Card> cardsInHand = drawCardsFromDeck(privateHand);
    player.addPrivateCards(cardsInHand);
  }

  private List<Card> drawCardsFromDeck(List<Card> privateHand) {
    List<Card> drawnCards = new ArrayList<>();
    for (Card card : privateHand) {
      if (!deck.contains(card)) {
        throw new RuntimeException("Card [" + card.toString() + "] is not present in the Deck!");
      }
      logger.debug("Drawing card:" + card.toString() + "]");
      drawnCards.add(card);
      boolean isCardRemovedFromDeck = deck.remove(card);
      if (!isCardRemovedFromDeck) {
        throw new RuntimeException("Card [" + card + "] was not removed from deck!");
      }
    }
    return drawnCards;
  }

  List<Card> getCommonHand() {
    return commonHand;
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
    for (Player player : players) {
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
    List<PokerResult> pokerHands = winStatistics.get(winner);
    PokerResult pokerResult = EvaluationHandler.getResultFromCardPokerResultMap(highScore);
    pokerHands.add(pokerResult);
    winStatistics.put(winner, pokerHands);
  }

  void printWinStatistics() {
    Map<PokerHand, Integer> allPlayerWinStatistics = initAllPlayerWinStatistics();
    Set<Player> playerSet = winStatistics.keySet();
    Iterator<Player> players = playerSet.iterator();
    while (players.hasNext()) {
      Player player = players.next();
      List<PokerResult> resultStatistics = winStatistics.get(player);
      logger.info("[" + player.getName() + "] won [" + resultStatistics.size() + "] number of times on [");
      for (PokerHand pokerHand : PokerHand.values()) {
        int numberOfMatches = getNumberOfPokerResults(pokerHand, resultStatistics);
        if (numberOfMatches > 0) {
          logger.info(pokerHand.toString() + ": [" + numberOfMatches + "] times.");
          Integer allPlayerNumberOfMatches = allPlayerWinStatistics.get(pokerHand);
          allPlayerNumberOfMatches = allPlayerNumberOfMatches + numberOfMatches;
          allPlayerWinStatistics.put(pokerHand, allPlayerNumberOfMatches);
        }
      }
      winStatistics.put(player, new ArrayList<>());
    }
    printAllPlayerStatistics(allPlayerWinStatistics);
  }

  private void printAllPlayerStatistics(Map<PokerHand, Integer> allPlayerWinStatistics) {
    logger.info("Total game statistics for this round for all players:");
    stream(PokerHand.values()).forEach(e -> {
      logger.info("Number of wins on :[" + e.toString() + "] : [" + allPlayerWinStatistics.get(e) + "]");
    });
  }


  private Map<PokerHand, Integer> initAllPlayerWinStatistics() {
    Map<PokerHand, Integer> allPlayerWinStatistics = new HashMap<>();
    stream(PokerHand.values()).forEach(e -> allPlayerWinStatistics.put(e, 0));
    return allPlayerWinStatistics;
  }

  private int getNumberOfPokerResults(PokerHand result, List<PokerResult> pokerResultList) {
    int numberOfMatches = 0;
    for (PokerResult pokerResult : pokerResultList) {
      if (result.equals(pokerResult.getPokerHand())) {
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
    for (Player player : players) {
      if (player.getName().equals(playerName)) {
        return player;
      }
    }
    throw new RuntimeException("Could not find player :[" + playerName + "]");
  }

  public Player findTheWinner() {
    return findTheWinner(null);
  }

  public Player findTheWinner(List<Player> playersStillInTheGame) {
    Player winner = null;
    Map<Card, PokerResult> highScore = new HashMap<Card, PokerResult>();
    highScore.put(EvaluationHandler.getLeastValueableCard(), new PokerResult(PokerHand.NO_RESULT));
    for (Player player : dealer.getPlayers()) {
      if (isPlayerStillInTheGame(playersStillInTheGame, player)) {
        Map<Card, PokerResult> result = player.evaluateHand(commonHand);
        logResult(player, result, highScore);
        if (EvaluationHandler.isResultFromLatestPlayerHigherThanHighScore(result, highScore)) {
          highScore.clear();
          highScore.putAll(result);
          winner = player;
        }
      }
    }
    dealer.updateWinStatistics(winner, highScore);
    logger.info(
            "And the winner is:[" + winner.getName() + "] with highscore :[" + printPokerResult(highScore) + "]");
    return winner;
  }

  private boolean isPlayerStillInTheGame(List<Player> playersStillInTheGame, Player player) {
    if (playersStillInTheGame == null) {
      return true;
    }
    if (playersStillInTheGame.contains(player)) {
      return true;
    }
    return false;
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
    result.append(highScore.get(topCardFromResult).getPokerHand()).append(" top card: ").append(topCardFromResult.toString());

    return result.toString();
  }

  public void putCardsBackIntoDeck() {
    dealer.getPlayers().stream().forEach(e -> dealer.putCardsInHandToDeck(e.getPrivateHand()));
    dealer.putCardsInHandToDeck(dealer.getCommonHand());
    dealer.putCardsInHandToDeck(dealer.getSkippedCards());
    if (!dealer.isDeckFull()) {
      throw new RuntimeException("Cards were lost!");
    }
  }

  void clearGame() {
    players.clear();
    deck.clear();
    populateDeck();
  }

  void addToCommonHand(List<Card> cards) {
    commonHand.addAll(drawCardsFromDeck(cards));
  }
}
