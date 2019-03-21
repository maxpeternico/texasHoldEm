package poker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

public class PokerGame {

  private static final int MAX_NUMBER_OF_PLAYERS = 7;
  private Dealer dealer;
  private static final Logger logger = LogManager.getLogger(PokerGame.class);
  private int blind = 50;
  static final int TOTAL_MARKERS_PER_PLAYER = 2500;
  private Draw draw;
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

  void startGame() {
    createPlayers();
    List<Player> players = dealer.getPlayers();
    initBlinds(players);
    draw = Draw.BEFORE_FLOP;
    List<Player> playersStillInTheGame;
    do {
      playRound(players);
      playersStillInTheGame = playersThatCanBet(players);
    } while (!doWeHaveAWinner(playersStillInTheGame));
    final Player theWinner = playersStillInTheGame.get(0);
    System.out.println("Player :[" + theWinner.getName() + "] is the winner and won :[" + theWinner.getNumberOfMarkers() + "] markers.");
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
    System.out.println("Blind is: [" + blind / 2 + "] resp: [" + blind + "]");
    payBlinds(players, blind);
    logger.debug("Start play before flop. ");
    playBeforeFlop(players);
    logger.debug("Start play flop. ");
    increaseDraw();
    List<Player> playersWhoCanBet = getPlayersWhoCanBet(players);
    if (playersWhoCanBet.size() > 1) {
      playFlop(players);
    }
    logger.debug("Start play before turn. ");
    increaseDraw();
    playersWhoCanBet = getPlayersWhoCanBet(players);
    if (playersWhoCanBet.size() > 1) {
      playTurn(players);
    }
    logger.debug("Start play before river. ");
    increaseDraw();
    if (playersWhoCanBet.size() > 1) {
      playRiver(players);
    }
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
    for (Player player:players) {
      totalAmountOfMarkers += player.getNumberOfMarkers();
    }
    return totalAmountOfMarkers;
  }

  Player getTheWinner(PotHandler pot, List<Player> players) {
    final Player theWinner = dealer.findTheWinner(getPlayersThatDidNotFold(players));
    checkTotalHand(dealer, theWinner.getName(), theWinner.getPrivateHand());
    theWinner.addMarkers(pot.getNumberOfMarkersInAllPots());
    logger.info("Player [{}] wins pot [{}] with [{}] markers.", theWinner.getName(), pot, pot.getNumberOfMarkersInAllPots());
    int totalNumberOfMarkers = 0;
    for (Player player : players) {
      logger.info("Number of markers for player [{}] : [{}]", player.getName(), player.getNumberOfMarkers());
      totalNumberOfMarkers += player.getNumberOfMarkers();
    }
    return theWinner;
  }

  String playRiver(List<Player> players) {
    final List<Card> riverCard = dealer.drawRiver();
    betManager.addRiverCardToCommonHand(riverCard);
    logger.info("Total hand after river: ");
    printHumanHand();
    return decideBet(playersThatCanBet(players));
  }

  String playTurn(List<Player> players) {
    final List<Card> turnCard = dealer.drawTurn();
    betManager.addTurnCardToCommonHand(turnCard);
    logger.info("Total hand after draw: ");
    printHumanHand();
    return decideBet(playersThatCanBet(players));
  }

  String playFlop(List<Player> players) {
    final List<Card> flopCards = dealer.drawFlop();
    betManager.addFlopCardsToCommonhand(flopCards);
    logger.info("Total hand after flop: ");
    printHumanHand();
    return decideBet(playersThatCanBet(players));
  }

  String playBeforeFlop(List<Player> players) {
    dealer.playPrivateHands();
    logger.info("Total hand before flop: ");
    printHumanHand();
    return decideBet(playersThatCanBet(players));
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
    return players.stream().filter(e -> !e.hasFolded()).collect(Collectors.toList());
  }

  private boolean doWeHaveAWinner(List<Player> playersStillInTheGame) {
    if (playersStillInTheGame.size() == 1) {
      return true;
    } else if (playersStillInTheGame.size() == 0) {
      throw new RuntimeException("How to handle draw?");
    }
    return false;
  }

  private List<Player> playersThatCanBet(List<Player> players) {
    final ArrayList<Player> playersStillInTheGame = Lists.newArrayList();
    players.stream().forEach(e -> {
      if (e.hasFolded()) {
        logger.debug("Player :[" + e.getName() + "] has folded.");
      } else if (!e.hasAnyMarkers()) {
        logger.debug("Player :[" + e.getName() + "] has no more markers.");
      } else {
        playersStillInTheGame.add(e);
      }
    });
    return playersStillInTheGame;
  }

  private List<Player> getPlayersWhoCanBet(List<Player> players) {
    final ArrayList<Player> playersStillInTheGame = Lists.newArrayList();
    players.stream().forEach(e -> {
      if (e.getAction().isAllIn()) {
        logger.debug("Player :[" + e.getName() + "] is all in, no bet.");
      } else if (e.hasFolded()) {
        logger.debug("Player :[" + e.getName() + "] has folded, no bet.");
      } else if (!e.hasAnyMarkers()) {
        logger.debug("Player :[" + e.getName() + "] has no markers and can not bet.");
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
    payBlind(
        allPlayers -> getPlayerWithLittleBlind(players),
        players,
        blindAmount / 2,
        player->player.setLittleBlind(blindAmount / 2),
        Player::clearLittleBlind
    );
    payBlind(
        allPlayers -> getPlayerWithBigBlind(players),
        players,
        blindAmount,
        player->player.setBigBlind(blindAmount),
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
    logger.debug("Set blind to :[" + newBlindPlayer.getName() + "]");
    final Player playerWithOldBlind = players.get(indexOfOldBlindPlayer);
    logger.debug("Clear blind for :[" + playerWithOldBlind.getName() + "]");
    setBlind.accept(newBlindPlayer);
    logger.debug("Set blind for :[" + newBlindPlayer.getName() + "]");
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
    } while(!players.get(newIndexOfBlind).hasAnyMarkers() || players.get(newIndexOfBlind).hasLittleBlind());  // If player is new big blind player player will have both little and big blind after little blind is payed, big blind will be moved to new player in next method call
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

  int increaseBlind() {
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
    if (isAnyoneNotDecided(players)) {
      return false;
    }
    return true;
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
    String numberOfPlayers = KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("1", "2", "3", "4", "5", "6"), "[1-6]:");
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
    final String totalHandString = EvaluationHandler.getHandAsString(totalHand);
    System.out.print(totalHandString + " ");
    printCurrentResult(playerName, totalHand);
  }

  private void printCurrentResult(String playerName, List<Card> totalHand) {
    final Map<Card, PokerResult> cardPokerResultMap = EvaluationHandler.evaluateHand(playerName, totalHand);
    final Set<Card> cards = cardPokerResultMap.keySet();
    final Iterator<Card> iterator = cards.iterator();
    while (iterator.hasNext()) {
      System.out.println(cardPokerResultMap.get(iterator.next()).getPokerHand());
    }
  }

  public void setPrivateHand(Player player, List<Card> privateHand) {
    dealer.setPrivateHand(player, privateHand);
  }

  public void clearGameForTests() {
    dealer.clearGameForTests();
  }

  void setTurnForUnitTest(Draw beforeFlop) {
    draw = beforeFlop;
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
    betManager.addFlopCardsToCommonhand(flopCardForTest);
  }

  public void updateTurnForBetManager() {
    betManager.updateTurn();
  }

  public void increaseDraw() {
    draw = Draw.increaseDraw(draw);
  }

  public void setTurnToBetManager(List<Card> turnCard) {
    betManager.addTurnCardToCommonHand(turnCard);
  }

  public void setRiverToBetManager(List<Card> riverCard) {
    betManager.addRiverCardToCommonHand(riverCard);
  }
}
