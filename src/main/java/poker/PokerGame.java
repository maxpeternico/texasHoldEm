package poker;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class PokerGame {
  private static final int FOLD_LEVEL = -100;
  private static final int MAX_NUMBER_OF_PLAYERS = 7;
  private Dealer dealer;
  private static final Logger logger = LogManager.getLogger(PokerGame.class);
  private int blind = 50;
  static final int TOTAL_MARKERS_PER_PLAYER = 2500;
  private Turn turn;
  private int pot = 0;

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
    turn = Turn.BEFORE_FLOP;
    List<Player> playersStillInTheGame;
    do {
      playersStillInTheGame = getPlayersStillInTheGame(players);
      playRound(playersStillInTheGame);
    } while (!doWeHaveAWinner(getPlayersStillInTheGame(players)));
    final Player theWinner = playersStillInTheGame.get(0);
    System.out.println("Player :[" + theWinner.getName() + "] is the winner and won :[" + theWinner.getNumberOfMarkers() + "] markers.");
  }

  void initBlinds(List<Player> players) {
    if (players.size() < 2) {
      throw new RuntimeException("At least 2 players has to play.");
    }
    players.get(0).setLittleBlind();
    logger.debug("Init blind.  Little blind is set to :[" + players.get(0).getName() + "]");
    players.get(1).setBigBlind();
    logger.debug("Init blind. Big blind is set to :[" + players.get(1).getName() + "]");
  }

  void playRound(List<Player> players) {
    System.out.println("Blind is: [" + blind / 2 + "] resp: [" + blind + "]");
    pot = payBlinds(getPlayersStillInTheGame(players), blind);

    dealer.playPrivateHands();
    printHumanHand();
    decideBet(getPlayersStillInTheGame(players));

    /********************************* FLOP *************************************************/

    dealer.drawFlop();
    System.out.println("Total hand after flop: ");
    printHumanHand();
    decideBet(getPlayersStillInTheGame(players));

    /********************************* TURN *************************************************/

    dealer.drawTurn();
    System.out.println("Total hand after turn: ");
    printHumanHand();
    decideBet(getPlayersStillInTheGame(players));

    /********************************* RIVER *************************************************/

    dealer.drawRiver();
    System.out.println("Total hand after river: ");
    printHumanHand();
    decideBet(getPlayersStillInTheGame(players));

    /********************************* FIND THE WINNER *************************************************/

    final Player theWinner = dealer.findTheWinner(getPlayersThatDidNotFold(players));
    checkTotalHand(dealer, theWinner.getName(), theWinner.getPrivateHand());
    theWinner.addMarkers(this.pot);
    System.out.println("Player :[" + theWinner.getName() + "] wins :[" + this.pot + "] markers.");
    int totalNumberOfMarkers = 0;
    for (Player player : players) {
      System.out.println("Number of markers for :[" + player.getName() + "] : [" + player.getNumberOfMarkers() + "]");
      totalNumberOfMarkers += player.getNumberOfMarkers();
    }
    final int theoreticalNumberOfMarkers = dealer.getPlayers().size() * 2500;
    if (totalNumberOfMarkers != theoreticalNumberOfMarkers) {
      throw new RuntimeException("Total number of markers is :[" + totalNumberOfMarkers + "] but should be :[" + theoreticalNumberOfMarkers + "]");
    }
    clearActions(players);
    turn = increaseTurn(turn);
    blind = increaseBlind();
    this.pot = 0;
    dealer.putCardsBackIntoDeck();
  }

  private void clearActions(List<Player> players) {
    for (Player player:players) {
      player.action = new Action(ActionEnum.NOT_DECIDED);
    }
  }

  private List<Player> getPlayersThatDidNotFold(List<Player> players) {
    return players.stream().filter(e->!e.hasFolded()).collect(Collectors.toList());
  }

  private boolean doWeHaveAWinner(List<Player> playersStillInTheGame) {
    if (playersStillInTheGame.size() == 1) {
      return true;
    } else if (playersStillInTheGame.size() == 0) {
      throw new RuntimeException("How to handle draw?");
    }
    return false;
  }

  private List<Player> getPlayersStillInTheGame(List<Player> players) {
    final ArrayList<Player> playersStillInTheGame = Lists.newArrayList();
    players.stream().forEach(e -> {
      if (e.hasFolded()) {
        System.out.println("Player :[" + e.getName() + "] has folded.");
      } else if (!e.hasAnyMarkers()){
        System.out.println("Player :[" + e.getName() + "] has no more markers.");
      } else {
        playersStillInTheGame.add(e);
      }
    });
    return playersStillInTheGame;
  }

  int payBlinds(List<Player> players, int blindAmount) {
    if (players.size() <2) {
      // A player has won the game, no need to continue
      return 0;
    }
    int blindPot = 0;
    blindPot += payLittleBlind(players, blindAmount / 2);
    blindPot += payBigBlind(players, blindAmount, getPlayerWithLittleBlind(players));
    return blindPot;
  }

  private int payLittleBlind(List<Player> players, int blindAmount) {
    int pot = 0;
    boolean littleBlindNotFound = true;
    Player playerWithOldLittleBlind = players.get(getPlayerWithLittleBlind(players));
    do {
      Player newLittleBlindPlayer = getNewLittleBlindPlayer(players.stream().filter(Player::hasAnyMarkers).collect(Collectors.toList()));
      logger.debug("Trying to set big blind to :[" + newLittleBlindPlayer.getName() + "]");
      if (newLittleBlindPlayer.canPay(blindAmount)) {
        playerWithOldLittleBlind.clearLittleBlind();
        logger.debug("Clear little blind for :[" + playerWithOldLittleBlind.getName() + "]");

        newLittleBlindPlayer.setLittleBlind();
        newLittleBlindPlayer.decreaseMarkers(blindAmount);
        logger.debug("Set little blind for :[" + newLittleBlindPlayer.getName() + "]");

        pot += blindAmount;
        littleBlindNotFound = false;
      } else {
        // Set player to broke and put all money to pot
        System.out.println("Player :[" + newLittleBlindPlayer.getName() + "] has to fold due to insufficient number of markers. All markers are paid to the pot :[" + newLittleBlindPlayer.getNumberOfMarkers() + "]");
        pot += newLittleBlindPlayer.getNumberOfMarkers();
        newLittleBlindPlayer.decreaseMarkers(newLittleBlindPlayer.getNumberOfMarkers());
        newLittleBlindPlayer.action = new Action(ActionEnum.FOLD);

        // Set big blind to next in line
        final int indexOfTempBigBlindPlayer = (players.indexOf(newLittleBlindPlayer) + 1) % (players.size() - 1);
        players.get(indexOfTempBigBlindPlayer).setBigBlind();
        logger.debug("Setting big blind temporary to " + players.get(indexOfTempBigBlindPlayer).getName());
      }
    } while (littleBlindNotFound && (getPlayersStillInTheGame(players).size() > 1));
    return pot;
  }

  private int payBigBlind(List<Player> players, int blindAmount, int indexOfOldBigBlind) {
    if (players.size() < 2) {
      // A player has won, no need to continue
      return 0;
    }
    int pot = 0;
    int indexOfNewBigBlind = (indexOfOldBigBlind +1) % players.size();
    boolean bigBlindNotFound = true;
    do {
      Player newBigBlindPlayer = players.get(indexOfNewBigBlind);
      logger.debug("Trying to set big blind to :[" + newBigBlindPlayer.getName() + "]");
      if (newBigBlindPlayer.canPay(blindAmount)) {
        Player playerWithOldBigBlind = players.get(indexOfOldBigBlind);
        playerWithOldBigBlind.clearBigBlind();
        logger.debug("Clear big blind for :[" + playerWithOldBigBlind.getName() + "]");

        newBigBlindPlayer.setBigBlind();
        newBigBlindPlayer.decreaseMarkers(blindAmount);
        logger.debug("Set big blind for :[" + newBigBlindPlayer.getName() + "]");

        pot += blindAmount;
        bigBlindNotFound = false;
      } else {
        // Set player to broke and put all money to pot
        pot += newBigBlindPlayer.getNumberOfMarkers();
        newBigBlindPlayer.decreaseMarkers(newBigBlindPlayer.getNumberOfMarkers());
        System.out.println("Player :[" + newBigBlindPlayer.getName() + "] has to fold due to insufficient number of markers.  All markers are paid to the pot :[" + newBigBlindPlayer.getNumberOfMarkers() + "]");
        newBigBlindPlayer.action = new Action(ActionEnum.FOLD);
        indexOfNewBigBlind = (indexOfNewBigBlind +1) % players.size();
      }
    } while (bigBlindNotFound && (getPlayersStillInTheGame(players).size() > 1));
    return pot;
  }

  private Player getNewBigBlindPlayer(List<Player> players) {
    int indexOfBigBlind = getPlayerWithBigBlind(players);
    int newIndexOfBigBlind = 0;
    if (indexOfBigBlind < players.size() - 1) {
      newIndexOfBigBlind = indexOfBigBlind + 1;
    }
    logger.debug("New big blind index :[" + newIndexOfBigBlind + "]");
    return players.get(newIndexOfBigBlind);
  }

  private Player getNewLittleBlindPlayer(List<Player> players) {
    int indexOfLittleBlind = getPlayerWithLittleBlind(players);
    int newIndexOfLittleBlind = 0;
    if (indexOfLittleBlind < players.size() - 1) {
      newIndexOfLittleBlind = indexOfLittleBlind + 1;
    }
    logger.debug("New little blind index :[" + newIndexOfLittleBlind + "]");
    return players.get(newIndexOfLittleBlind);
  }

  private int getPlayerWithLittleBlind(List<Player> players) {
    for (Player player : players) {
      if (player.hasLittleBlind()) {
        logger.debug("Current player with little blind :[" + player.getName() + "] index :[" + players.indexOf(player) + "]");
        return players.indexOf(player);
      }
    }
    throw new RuntimeException("No player has little blind");
  }

  private int getPlayerWithBigBlind(List<Player> players) {
    for (Player player : players) {
      if (player.hasBigBlind()) {
        logger.debug("Current player with big blind :[" + player.getName() + "] index :[" + players.indexOf(player) + "]");
        return players.indexOf(player);
      }
    }
    throw new RuntimeException("No player has big blind");
  }

  private boolean noPlayerHasBlind(List<Player> players) {
    for (Player player : players) {
      if (player.hasBlind()) {
        logger.debug("[" + players.get(0).getName() + "] has blind");
        return true;
      }
    }
    logger.debug("No player has blind");
    return false;
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
      logger.debug("No human player in this game.");
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

  private Turn increaseTurn(Turn turn) {
    Turn newTurn = null;
    switch (turn) {
      case BEFORE_FLOP:
        newTurn = Turn.BEFORE_TURN;
        break;
      case BEFORE_TURN:
        newTurn = Turn.BEFORE_RIVER;
        break;
      case BEFORE_RIVER:
        newTurn = Turn.END_GAME;
        break;
      case END_GAME:
        newTurn = Turn.BEFORE_FLOP;
        break;
    }
    return newTurn;
  }

  int increaseBlind() {
    return blind * 2;
  }

  String decideBet(List<Player> remainingPlayers) {
    if (remainingPlayers.size() < 2) {
      // A player already won
      return "";
    }
    StringBuffer result = new StringBuffer();
    Boolean[] doRaise = initDoRaise(remainingPlayers);

    int maxRaiseFromOtherplayer = 0;
    do {
      for (Player player : remainingPlayers) {
        int totalRaiseAmount = 0;
        if (getPlayersStillInTheGame(player)) {
          logger.debug("player :[" + player + "] doRaise: [" + doRaise[remainingPlayers.indexOf(player)] + "]. maxRaiseFromOtherPlayer:[" + maxRaiseFromOtherplayer + "] numbersOfMarkers :[" + player.getNumberOfMarkers() + "] pot :[" + pot + "]");
          Boolean isRaise = false;

          player.decideAction(turn, remainingPlayers.size(), dealer.getCommonHand(), blind, maxRaiseFromOtherplayer);
          final Action action = player.getAction();
          if (action.isRaise()) {
            int raiseAmount = action.getRaiseAmount();
            totalRaiseAmount += raiseAmount - maxRaiseFromOtherplayer;
            logger.debug("Player :[" + player.getName() + "] raises with totalRaiseAmount :[" + totalRaiseAmount + "]. ");
          } else if (action.isCheck()) {
            totalRaiseAmount = maxRaiseFromOtherplayer;
            logger.debug("Player :[" + player.getName() + "] checks with totalRaiseAmount :[" + totalRaiseAmount + "]. ");
          }
          pot += totalRaiseAmount;
          logger.debug("Pot size :[" + pot + "]. ");
          doRaise[remainingPlayers.indexOf(player)] = isRaise;
          result.append("Player " + player.getName() + " " + action.toString() + ". ");
        }
      }
    }
    while (anyOneIsRaising(doRaise));
    return result.toString();
  }

  private int getValueOfLittleAndBigBlind() {
    return blind + blind / 2;
  }

  private Boolean[] initDoRaise(List<Player> remainingPlayers) {
    Boolean[] doRaise = new Boolean[remainingPlayers.size()];
    for (int i = 0; i < remainingPlayers.size(); i++) {
      doRaise[i] = false;
    }
    return doRaise;
  }

  private String fold(Player player, List<Player> removePlayers) {
    removePlayers.add(player);
    return "Player " + player + " folds";
  }

  private boolean canPayBlind(Player player) {
    try {
      withdrawBlindMarkers(player);
    } catch (OutOfMarkersException e) {
      logger.info("Player :[" + player.getName() + "] is out of markers.");
      return false;
    }
    return true;
  }

  private void withdrawBlindMarkers(Player player) {
    int blindPayment = 0;
    if (player.hasLittleBlind()) {
      blindPayment = blind / 2;
    } else if (player.hasBigBlind()) {
      blindPayment = blind;
    }
    if (blindPayment > 0) {
      if (player.canPay(blindPayment)) {
        player.decreaseMarkers(blindPayment);
        logger.info("Player " + player + " pays the blind " + blindPayment);
        logger.debug("Number of markers :[" + player.getNumberOfMarkers() + "].");
      } else {
        throw new OutOfMarkersException("Player :[" + player.getName() + "] has to fold due to insufficient number of markers. ");
      }
    }
  }

  private boolean anyOneIsRaising(Boolean[] doRaise) {
    for (Boolean b : doRaise) {
      if (b.equals(true)) {
        return true;
      }
    }
    return false;
  }

  private boolean getPlayersStillInTheGame(Player player) {
    if (player.hasFolded()) {
      return false;
    }
    if (!player.hasAnyMarkers()) {
      return false;
    }
    return true;
  }

  private boolean doFold(int raiseByEvaluatingOwnHand, int maxRaiseFromOtherplayer) {
    int risk = raiseByEvaluatingOwnHand - maxRaiseFromOtherplayer;
    logger.debug("raiseByEvaluatingOwnHand: [" + raiseByEvaluatingOwnHand + "] maxRaiseFromOtherplayer: [" + maxRaiseFromOtherplayer + "] fold level: [" + FOLD_LEVEL + "]");
    if (risk > FOLD_LEVEL) {
      return false;
    } else {
      return true;
    }
  }

  private void createRobotPlayers() {
    System.out.println("How many players do you want to play with?");
    String numberOfPlayers = KeyboardHelper.getCharFromKeyboard(Lists.newArrayList("1", "2", "3", "4", "5", "6"), "[1-6]:");
    final List<Player> numberOfRobotPlayers = createNumberOfRobotPlayers(Integer.valueOf(numberOfPlayers), TOTAL_MARKERS_PER_PLAYER);
    for (Player robot:numberOfRobotPlayers) {
      dealer.registerPlayer(robot);
    }
  }

  List<Player> createNumberOfRobotPlayers(int numberOfPlayers, int markersPerPlayer) {
    if (numberOfPlayers > MAX_NUMBER_OF_PLAYERS) {
      throw new RuntimeException("Number of players should be between 1 and 7: " + numberOfPlayers);
    }
    final String[] name = {"Thomas", "Jörn", "Anders", "Bosse", "Ingemar", "Staffan", "Bagarn"};

    List<Player> robotPlayers = Lists.newArrayList();
    for (int i=0;i<numberOfPlayers;i++) {
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

  public void clearGame() {
    dealer.clearGame();
  }

  void setTurnForUnitTest(Turn beforeFlop) {
    turn = beforeFlop;
  }

  void registerPlayer(Player player) {
    dealer.registerPlayer(player);
  }
}
