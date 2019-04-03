package poker;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PokerGame {

  private static final int MAX_NUMBER_OF_PLAYERS = 7;
  private Dealer dealer;
  private static final Logger logger = LogManager.getLogger(PokerGame.class);
  private int blind = 50;
  static final int TOTAL_MARKERS_PER_PLAYER = 2500;
  private PotHandler potHandler = new PotHandler();
  private BetManager betManager;

  public static void main(String[] args) {
    final PokerGame pokerGame = getInstance();
    pokerGame.startGame();
  }

  public PokerGame() {
    this.dealer = Dealer.getInstance();
  }

  public static PokerGame getInstance() {
    return new PokerGame();
  }

  private void startGame() {
    createPlayers();
    List<Player> players = dealer.getPlayers();
    initBlinds(players);
    List<Player> playersStillInTheGame = Lists.newLinkedList();
    List<Player> playersInBettingOrder = Lists.newLinkedList();
    playersStillInTheGame.addAll(players);
    do {
      System.out.println("Blind is: [" + blind / 2 + "] resp: [" + blind + "]");
      payBlinds(players, blind);
      playersInBettingOrder = putBigBlindLastInList(playersStillInTheGame);
      playRound(playersInBettingOrder);
      KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("O"), "Press O to continue", null);
      playersStillInTheGame = playersThatCanBet(players);
    } while (!doWeHaveAWinner(playersStillInTheGame));
    final Player theWinner = playersStillInTheGame.get(0);
    System.out.println("Player :[" + theWinner.getName() + "] is the winner and won :[" + theWinner.getNumberOfMarkers() + "] markers.");
  }

  List<Player> putBigBlindLastInList(List<Player> playersStillInTheGame) {
    // Get index of big blind player
    int indexOfBigBlindPlayer = 0;
    for (Player player:playersStillInTheGame) {
      if (player.hasBigBlind()) {
        indexOfBigBlindPlayer = playersStillInTheGame.indexOf(player);
        break;
      }
    }
    List<Player> sortedList = Lists.newLinkedList();
    // Player after blind player is put first in list and so on, big blind player should be last
    for (int i=0;i<playersStillInTheGame.size();i++) {
      sortedList.add(playersStillInTheGame.get((indexOfBigBlindPlayer + 1 + i) % playersStillInTheGame.size()));
    }
    return sortedList;
  }

  void initBlinds(List<Player> players) {
    if (players.size() < 2) {
      throw new RuntimeException("At least 2 players has to play.");
    }
    players.get(0).setLittleBlind(blind);
    logger.trace("Init blind.  Little blind is set to :[" + players.get(0).getName() + "]");
    players.get(1).setBigBlind(blind);
    logger.trace("Init blind. Big blind is set to :[" + players.get(1).getName() + "]");
  }

  void playRound(List<Player> players) {
    betManager = new BetManager(players, blind, potHandler);
    System.out.println("Start play before flop. ");
    playBeforeFlop(players);
    System.out.println("Start play flop. ");
    playFlop(players);
    System.out.println("Start play turn. ");
    playTurn(players);
    System.out.println("Start play river. ");
    playRiver(players);
    logger.debug("Get the winner. ");
    getTheWinner(potHandler, players);
    checkSoNoMarkersDisappeared();
    resetTurn(players);
  }

  private void checkSoNoMarkersDisappeared() {
    final int theoreticalNumberOfMarkers = dealer.getPlayers().size() * 2500;
    int totalNumberOfMarkers = getMarkersForAllPlayers(dealer.getPlayers());
    if (totalNumberOfMarkers != theoreticalNumberOfMarkers) {
      throw new RuntimeException("Total number of markers is :[" + totalNumberOfMarkers + "] but should be :[" + theoreticalNumberOfMarkers + "]");
    }
  }

  private int getMarkersForAllPlayers(List<Player> players) {
    int totalAmountOfMarkers = 0;
    for (Player player : players) {
      totalAmountOfMarkers += player.getNumberOfMarkers();
    }
    return totalAmountOfMarkers;
  }

  Player getTheWinner(PotHandler pot, List<Player> players) {
    final Player theWinner = dealer.findTheWinner(getPlayersThatDidNotFold(players));
    checkTotalHand(dealer, theWinner.getName(), theWinner.getPrivateHand());
    theWinner.addMarkers(pot.getNumberOfMarkersInAllPots());
    System.out.println("Player " + theWinner.getName() + " wins pot with " + pot.getNumberOfMarkersInAllPots() + " markers.");
    for (Player player : players) {
      System.out.println("Number of markers for player " + player.getName() + " : " + player.getNumberOfMarkers());
    }
    return theWinner;
  }

  private String playRiver(List<Player> players) {
    dealer.increaseDraw();
    final List<Card> riverCard = dealer.drawRiver();
    betManager.addRiverCardToCommonHand(riverCard, dealer.getDraw());
    logger.info("Total hand after river: ");
    printHumanHand();
    return decideBet(getPlayersWhoHasntFinishedBetting(players));
  }

  private String playTurn(List<Player> players) {
    dealer.increaseDraw();
    final List<Card> turnCard = dealer.drawTurn();
    betManager.addTurnCardToCommonHand(turnCard, dealer.getDraw());
    logger.info("Total hand after drawManager: ");
    printHumanHand();
    return decideBet(getPlayersWhoHasntFinishedBetting(players));
  }

  private String playFlop(List<Player> players) {
    dealer.increaseDraw();
    final List<Card> flopCards = dealer.drawFlop();
    betManager.addFlopCardsToCommonhand(flopCards, dealer.getDraw());
    logger.info("Total hand after flop: ");
    printHumanHand();
    return decideBet(getPlayersWhoHasntFinishedBetting(players));
  }

  private String playBeforeFlop(List<Player> players) {
    dealer.playPrivateHands();
    logger.info("Total hand before flop: ");
    printHumanHand();
    return decideBet(getPlayersWhoHasntFinishedBetting(players));
  }

  void resetTurn(List<Player> players) {
    for (Player player : players) {
      player.action = new Action(ActionEnum.NOT_DECIDED);
      player.previousAction = new Action(ActionEnum.NOT_DECIDED);
      player.action.setAmount(0);
      player.blindAmount = 0;
    }
    blind = increaseBlind();
    potHandler.clear();
    dealer.putCardsBackIntoDeck();
  }

  private List<Player> getPlayersThatDidNotFold(List<Player> players) {
    return players.stream().filter(e->!e.hasFolded()).collect(Collectors.toList());
  }

  private boolean doWeHaveAWinner(List<Player> playersStillInTheGame) {
    if (playersStillInTheGame.size() == 1) {
      return true;
    } else if (playersStillInTheGame.size() == 0) {
      throw new RuntimeException("How to handle drawManager?");
    }
    return false;
  }

  private List<Player> playersThatCanBet(List<Player> players) {
    final ArrayList<Player> playersStillInTheGame = Lists.newArrayList();
    players.forEach(e -> {
        if (!e.hasAnyMarkers()) {
        logger.debug("Player :[" + e.getName() + "] has no more markers.");
      } else {
        playersStillInTheGame.add(e);
      }
    });
    return playersStillInTheGame;
  }

  private List<Player> getPlayersWhoHasntFinishedBetting(List<Player> players) {
    final ArrayList<Player> playersStillInTheGame = Lists.newArrayList();
    players.forEach(e -> {
      if (e.getAction().isAllIn()) {
        logger.debug("Player :[" + e.getName() + "] is all in, no bet.");
      } else if (e.hasFolded()) {
        logger.debug("Player :[" + e.getName() + "] has folded, no bet.");
      } else {
        playersStillInTheGame.add(e);
      }
    });
    return playersStillInTheGame;
  }

  void payBlinds(List<Player> players, int blindAmount) {
    if (players.size() < 2) {
      // A player has won the game, no need to continue
      return;
    }
    System.out.println("Player to play little blind: ");
    payBlind(
      allPlayers -> getPlayerWithLittleBlind(players),
      players,
      blindAmount / 2,
      player -> player.setLittleBlind(blindAmount / 2),
      Player::clearLittleBlind
    );
    System.out.println("Player to play big blind: ");
    payBlind(
      allPlayers -> getPlayerWithBigBlind(players),
      players,
      blindAmount,
      player -> player.setBigBlind(blindAmount),
      Player::clearBigBlind
    );
  }

  private void payBlind(Function<List<Player>, Integer> getPlayerWithBlind,
                        List<Player> players,
                        int blindAmount,
                        Consumer<Player> setBlind,
                        Consumer<Player> clearBlind) {
    final int indexOfOldBlindPlayer = getPlayerWithBlind.apply(players);
    Player newBlindPlayer = players.get(getNewBlindIndex(players, indexOfOldBlindPlayer));
    final Player playerWithOldBlind = players.get(indexOfOldBlindPlayer);
    logger.debug("Clear blind for :[" + playerWithOldBlind.getName() + "]");
    setBlind.accept(newBlindPlayer);
    System.out.println("Set blind for :[" + newBlindPlayer.getName() + "] amount: " + blindAmount);
    clearBlind.accept(playerWithOldBlind);
    int raiseAmount = blindAmount;
    if (newBlindPlayer.canPay(blindAmount)) {
      newBlindPlayer.decreaseMarkers(blindAmount);
      potHandler.joinPot(newBlindPlayer, blindAmount);
    } else {
      // Go all in for player and create new pot
      raiseAmount = newBlindPlayer.getNumberOfMarkers();
      final int allInAmount = newBlindPlayer.getNumberOfMarkers();
      System.out.println("Player :[" + newBlindPlayer.getName() + "] does not have markers for blind :[" + blindAmount + "], has to go all in with :[" + allInAmount + "].");
      potHandler.joinPot(newBlindPlayer, allInAmount);

      newBlindPlayer.decreaseMarkers(allInAmount);
      newBlindPlayer.action = new Action(ActionEnum.ALL_IN);
    }
    newBlindPlayer.action.setAmount(raiseAmount);
  }

  private int getNewBlindIndex(List<Player> players, int blindIndex) {
    // New player must have markers and must not have blind
    int newIndexOfBlind;
    int tempIndexOfBlind = blindIndex;
    do {
      newIndexOfBlind = (tempIndexOfBlind + 1) % players.size();
      tempIndexOfBlind++;
      logger.info("Player {{}} index {{}} has markers {{}} ", players.get(newIndexOfBlind), newIndexOfBlind, players.get(newIndexOfBlind).getNumberOfMarkers());
      if ((tempIndexOfBlind - blindIndex) > players.size()) {
        // We've looped from all players and did not find anyone that can have blind, set original value
        return blindIndex;
      }
    } while (!players.get(newIndexOfBlind).hasAnyMarkers() || players.get(newIndexOfBlind).hasLittleBlind());  // If player is new big blind player player will have both little and big blind after little blind is payed, big blind will be moved to new player in next method call
    logger.trace("New blind index :[" + newIndexOfBlind + "]");
    return newIndexOfBlind;
  }

  private int getPlayerWithLittleBlind(List<Player> players) {
    for (Player player : players) {
      if (player.hasLittleBlind()) {
        logger.trace("Current player with little blind :[" + player.getName() + "] index :[" + players.indexOf(player) + "]");
        return players.indexOf(player);
      }
    }
    throw new RuntimeException("No player has little blind");
  }

  private int getPlayerWithBigBlind(List<Player> players) {
    for (Player player : players) {
      if (player.hasBigBlind()) {
        logger.trace("Current player with big blind :[" + player.getName() + "] index :[" + players.indexOf(player) + "]");
        return players.indexOf(player);
      }
    }
    throw new RuntimeException("No player has big blind");
  }

  private void printHumanHand() {
    final List<Player> players = dealer.getPlayers();
    String humanPlayer = "";
    for (Player player : players) {
      if (player instanceof HumanPlayer) {
        humanPlayer = player.getName();
      }
    }
    if (humanPlayer.isEmpty()) {
      logger.trace("No human player in this game.");
      return;
    }
    final List<Card> privateHand = dealer.getPlayerHand(humanPlayer);
    List<Card> commonHand = dealer.getCommonHand();
    List<Card> totalHand = new ArrayList<>(privateHand);
    totalHand.addAll(commonHand);
    final String totalHandString = EvaluationHandler.getHandAsString(totalHand);
    System.out.print("The hand for :[" + humanPlayer + "] is :[" + totalHandString + "] ");
    printCurrentResult(humanPlayer, totalHand);
  }

  private void createPlayers() {
    createHumanPlayer();
    createRobotPlayers();
  }

  private Player createHumanPlayer() {
    String playerName = KeyboardHelper.askForInput("Enter your name: ");
    System.out.println("Welcome [" + playerName + "]");
    final Player humanPlayer = new HumanPlayer(playerName, TOTAL_MARKERS_PER_PLAYER);
    dealer.registerPlayer(humanPlayer);
    return humanPlayer;
  }

  private int increaseBlind() {
    return blind * 2;
  }

  /*
   *kalle (max raise from antoher player:0 )  höjer 100. maxraise 100  pot; 100
   *thomas (max raise from another player: 100) checks 100 maxraise 100 . pot: 200

    kalle checks. maxraiserFromAnoutbherPlayer 100
    Kalle har redan 100 i pot, ska inte betala
    thomas checks Har redan 100 i pot, ska inte betala
   *
   * use case 2:
   *
   *kalle höjer 100, maxraise 100 pot 100
   * thomas höjer 200, maxraise 200, pot 200
   * kalle checkar, har 100 i pot, ska betala 100
   * thomas checkar, har 200 i pot, ska inte betlaa ngt
   *
   */
  String decideBet(List<Player> remainingPlayers) {
    if (remainingPlayers.size() < 2) {
      // A player already won
      return "";
    }
    return betManager.bet();
  }

  void clearPreviousPlayersWithActionCheck(List<Player> remainingPlayers, Player player) {
    for (int i = 0; i < remainingPlayers.indexOf(player); i++) {
      final Player tempPlayer = remainingPlayers.get(i);
      if (tempPlayer.getAction().isCheck()) {
        tempPlayer.setActionToNotDecided();
        logger.debug("Player [{}] decision chached from check to decided", player.getName());
      }
    }
  }

  boolean allPlayersSatisfied(List<Player> players) {
    return !isAnyoneNotDecided(players);
  }

  private boolean isAnyoneNotDecided(List<Player> players) {
    for (Player player : players) {
      if (player.hasNotDecided()) {
        logger.debug("Player [{}] has still not decided", player.getName());
        return true;
      }
    }
    return false;
  }

  private void createRobotPlayers() {
    System.out.println("How many players do you want to play with?");
    String numberOfPlayers = KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("1", "2", "3", "4", "5", "6"), "[1-6]:", 1);
    final List<Player> numberOfRobotPlayers = createNumberOfRobotPlayers(Integer.valueOf(numberOfPlayers), TOTAL_MARKERS_PER_PLAYER);
    for (Player robot : numberOfRobotPlayers) {
      dealer.registerPlayer(robot);
    }
  }

  List<Player> createNumberOfRobotPlayers(int numberOfPlayers, int markersPerPlayer) {
    if (numberOfPlayers > MAX_NUMBER_OF_PLAYERS) {
      throw new RuntimeException("Number of players should be between 1 and 7: " + numberOfPlayers);
    }
    final String[] name = {"Thomas", "Jörn", "Anders", "Bosse", "Ingemar", "Staffan", "Bagarn"};

    List<Player> robotPlayers = Lists.newArrayList();
    for (int i = 0; i < numberOfPlayers; i++) {
      Player player = new RobotPlayer(name[i], markersPerPlayer);
      robotPlayers.add(player);
    }
    return robotPlayers;
  }

  private void checkTotalHand(Dealer dealer, String playerName, List<Card> privateHand) {
    List<Card> commonHand = dealer.getCommonHand();
    List<Card> totalHand = new ArrayList<>(privateHand);
    totalHand.addAll(commonHand);
    printCurrentResult(playerName, totalHand);
  }

  private void printCurrentResult(String playerName, List<Card> totalHand) {
    final String totalHandString = EvaluationHandler.getHandAsString(totalHand);
    final Map<Card, PokerResult> cardPokerResultMap = EvaluationHandler.evaluateHand(playerName, totalHand);
    final Set<Card> cards = cardPokerResultMap.keySet();
    for (Card card : cards) {
      System.out.println(cardPokerResultMap.get(card).getPokerHand());
    }
  }

  public void setPrivateHand(Player player, List<Card> privateHand) {
    dealer.setPrivateHand(player, privateHand);
  }

  public void clearGameForTests() {
    dealer.clearGameForTests();
  }

  void registerPlayer(Player player) {
    dealer.registerPlayer(player);
  }

  void addToCommonHand(List<Card> cards) {
    dealer.addToCommonHand(cards);
  }

  public void setBlindAmount(int bigBlindAmount) {
    blind = bigBlindAmount;
  }

  public PotHandler getPotHandler() {
    return this.potHandler;
  }

  void setBetManager(BetManager betManagerForTest) {
    betManager = betManagerForTest;
  }

  public void setFlopToBetManager(List<Card> flopCardForTest) {
    betManager.addFlopCardsToCommonhand(flopCardForTest, dealer.getDraw());
  }

  public void updateTurnForBetManager() {
    betManager.resetMaxRaiseThisDraw();
  }

  public void setTurnToBetManager(List<Card> turnCard) {
    betManager.addTurnCardToCommonHand(turnCard, dealer.getDraw());
  }

  public void setRiverToBetManager(List<Card> riverCard) {
    betManager.addRiverCardToCommonHand(riverCard, dealer.getDraw());
  }

  void increaseDrawForTest() {
    dealer.increaseDraw();
  }
}
