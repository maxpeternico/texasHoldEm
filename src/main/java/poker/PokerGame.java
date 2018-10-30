package poker;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static poker.Strategy.OFFENSIVE;

public class PokerGame {
  private static final int FOLD_LEVEL = -100;
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

  private void startGame() {
    createPlayers();
    List<Player> players = dealer.getPlayers();
    turn = Turn.BEFORE_FLOP;
    playRound(players);
  }

  void playRound(List<Player> players) {
    turn = increaseTurn(turn);
    blind = increaseBlind();
    List<Player> playersStillInTheGame = Lists.newArrayList();
    players.stream().forEach(e -> {
      if (e.hasMarkers()) {
        playersStillInTheGame.add(e);
      } else {
        System.out.println("Player :[" + e.getName() + "] left the game.");
      }
    });
    if (playersStillInTheGame.size() == 1) {
      System.out.println("Player :[" + playersStillInTheGame.get(0).getName() + "] is the winner and won :[" + playersStillInTheGame.get(0).getNumberOfMarkers() + "] markers.");
      return;
    } else if (playersStillInTheGame.size() == 0) {
      throw new RuntimeException("How to handle draw?");
    }
    System.out.println("Blind is: [" + blind / 2 + "] resp: [" + blind + "]");
    setBlinds(players);
    dealer.playPrivateHands();
    printHumanHand();
    decideBet(playersStillInTheGame);

    /********************************* FLOP *************************************************/

    dealer.drawFlop();
    System.out.println("Total hand after flop: ");
    printHumanHand();
    String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;
    decideBet(playersStillInTheGame);

    /********************************* TURN *************************************************/

    dealer.drawTurn();
    System.out.println("Total hand after turn: ");
    printHumanHand();
    decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;
    decideBet(playersStillInTheGame);

    /********************************* RIVER *************************************************/

    dealer.drawRiver();
    System.out.println("Total hand after river: ");
    printHumanHand();
    decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;
    decideBet(playersStillInTheGame);

    /********************************* FIND THE WINNER *************************************************/

    final Player theWinner = dealer.findTheWinner();
    checkTotalHand(dealer, theWinner.getName(), theWinner.getPrivateHand());
    dealer.putCardsBackIntoDeck();
  }

  void setBlinds(List<Player> players) {
    // has any player blinds?
    if (!noPlayerHasBlind(players)) {
      players.get(0).setLittleBlind(blind);
      pot += blind / 2;
      logger.debug("Set little blind to :[" + players.get(0).getName() + "]");
      players.get(1).setBigBlind(blind);
      pot += blind;
      logger.debug("Set big blind to :[" + players.get(1).getName() + "]");
    } else {
      int indexOfLittleBlind = getPlayerWithLittleBlind(players);
      int newIndexOfLittleBlind = 0;
      if (indexOfLittleBlind < players.size() - 1) {
        newIndexOfLittleBlind = indexOfLittleBlind + 1;
      }
      int indexOfBigBlind = getPlayerWithBigBlind(players);
      int newIndexOfBigBlind = 0;
      if (indexOfBigBlind < players.size() - 1) {
        newIndexOfBigBlind = indexOfBigBlind + 1;
      }
      players.get(indexOfLittleBlind).clearLittleBlind();
      logger.debug("Clear little blind for :[" + players.get(indexOfLittleBlind).getName() + "]");
      players.get(indexOfBigBlind).clearBigBlind();
      logger.debug("Clear big blind for :[" + players.get(indexOfBigBlind).getName() + "]");

      players.get(newIndexOfLittleBlind).setLittleBlind(blind);
      logger.debug("Set little blind to :[" + players.get(newIndexOfLittleBlind).getName() + "]");
      players.get(newIndexOfBigBlind).setBigBlind(blind);
      logger.debug("Set big blind to :[" + players.get(newIndexOfBigBlind).getName() + "]");
    }
  }

  private int getPlayerWithLittleBlind(List<Player> players) {
    for (Player player : players) {
      if (player.hasLittleBlind()) {
        return players.indexOf(player);
      }
    }
    throw new RuntimeException("No player has little blind");


  }

  private int getPlayerWithBigBlind(List<Player> players) {
    for (Player player : players) {
      if (player.hasBigBlind()) {
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
      if (player.isHuman()) {
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
    String playerName = askForInput("Enter your name: ");
    System.out.println("Welcome [" + playerName + "]");
    final Player humanPlayer = new Player(playerName, TOTAL_MARKERS_PER_PLAYER);
    humanPlayer.setToHuman();
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
    }
    return newTurn;
  }

  int increaseBlind() {
    return blind * 2;
  }

  String decideBet(List<Player> remainingPlayers) {
    StringBuffer result = new StringBuffer();
    List<Player> removePlayers = new ArrayList<>();
    Boolean[] doRaise = initDoRaise(remainingPlayers);

    payBlinds(result, remainingPlayers, removePlayers);

    int maxRaiseFromOtherplayer = 0;
    do {
      for (Player player : remainingPlayers) {
        int totalRaiseAmount = 0;
        if (isPlayerStillInTheGame(player, removePlayers)) {
          logger.debug("player :[" + player + "] doRaise: [" + doRaise[remainingPlayers.indexOf(player)] + "]. maxRaiseFromOtherPlayer:[" + maxRaiseFromOtherplayer + "] numbersOfMarkers :[" + player.getNumberOfMarkers() + "] pot :[" + pot + "]");
          Boolean isRaise = false;
          String playerDecision = "";

          if (player.isHuman()) {
            String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
            if (isFolding(decision)) {
              isRaise = false;
              removePlayers.add(player);
            } else if (isChecking(decision)) {
              // Put player money in pot
              isRaise = false;
            } else if (isRaising(decision)) {
              isRaise = true;
              int raiseAmount = getRaiseAmount(player);
              totalRaiseAmount = raiseAmount - maxRaiseFromOtherplayer;
              maxRaiseFromOtherplayer += totalRaiseAmount;
            }
          } else {
            int raiseAmount = evaluateOwnHand(player, remainingPlayers.size());
            boolean fold = doFold(raiseAmount, maxRaiseFromOtherplayer);
            if (fold) {
              isRaise = false;
              playerDecision = "Player " + player.getName() + " fold. ";
              System.out.println(playerDecision);
              removePlayers.add(player);
            } else {
              isRaise = doRaise(raiseAmount, maxRaiseFromOtherplayer);
              if (isRaise) {
                totalRaiseAmount = raiseAmount - maxRaiseFromOtherplayer;
                maxRaiseFromOtherplayer += totalRaiseAmount;
                playerDecision = "Player " + player.getName() + " raises " + totalRaiseAmount + ". ";
                System.out.println(playerDecision);
              } else {
                playerDecision = "Player " + player.getName() + " checks. ";
                System.out.println(playerDecision);
              }
            }
          }
          pot += totalRaiseAmount;
          doRaise[remainingPlayers.indexOf(player)] = isRaise;
          result.append(playerDecision);
        }
      }
    } while (anyOneIsRaising(doRaise));
    putCardsFromHandOfRemovedPlayersBackInDeck(remainingPlayers, removePlayers);
    return result.toString();

  }

  private void putCardsFromHandOfRemovedPlayersBackInDeck(List<Player> remainingPlayers, List<Player> removePlayers) {
    removePlayers.stream().forEach(e -> {
      if (remainingPlayers.contains(e)) {
        remainingPlayers.remove(e);
        dealer.putCardsInHandToDeck(e.getPrivateHand());
      } else {
        throw new RuntimeException("Player [" + e.getName() + "] should not be in this game");
      }
    });
  }

  private void payBlinds(StringBuffer result, List<Player> remainingPlayers, List<Player> removePlayers) {
    String playerDecision = "";
    for (Player player : remainingPlayers) {
      if (!payEventualBlindMarkers(player)) {
        playerDecision = fold(player, removePlayers);
        result.append(playerDecision);
      }
    }
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

  private boolean payEventualBlindMarkers(Player player) {
    try {
      withdrawBlindMarkers(player);
    } catch (Exception e) {
      logger.info(e.getMessage());
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

  private boolean isPlayerStillInTheGame(Player player, List<Player> removePlayers) {
    return !removePlayers.contains(player);
  }

  private int getRaiseAmount(Player player) {
    final int desiredRaiseAmount = Integer.parseInt(getCharFromKeyboard(Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")));
    isDesiredRaiseAmountHigherThanBlind();
    hasMarkersForAmount();
    return desiredRaiseAmount;
  }

  private void hasMarkersForAmount() {

  }

  private void isDesiredRaiseAmountHigherThanBlind() {
  }

  private boolean isRaising(String decision) {
    if (decision.equals("R")) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isChecking(String decision) {
    if (decision.equals("C")) {
      return true;
    } else {
      return false;
    }
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

  private boolean isExpensive() {
    return true;
  }

  private boolean hasToPay() {
    return true;
  }

  private boolean doRaise(int raiseByEvaluatingOwnHand, int maxRaiseFromOtherplayer) {
    int risk = raiseByEvaluatingOwnHand - maxRaiseFromOtherplayer;
    if (risk > 0) {
      return true;
    } else {
      return false;
    }
  }

  int evaluateOwnHand(Player player, int numberOfRemainingPlayers) {
    Points points = calculatePoints(player, numberOfRemainingPlayers);
    Strategy strategy = decideBetStrategy(points, player.getNumberOfMarkers());
    logger.debug(player.getName() + " has strategy " + strategy.toString());
    int raiseAmount = decideRaiseAmount(player, strategy);
    return raiseAmount;
  }

  private int decideRaiseAmount(Player player, Strategy strategy) {
    int raiseAmount = 0;

    // Depending on strategy, pot and blind
    // low blind, offensive raises more, the rest joins
    // medium blind, offensive raises more, join joins, join if cheap drops
    // high blind offensive sets rise as blind, rest folds
    switch (strategy) {
      case OFFENSIVE:
        // TODO: Ser percentage of number of markers instead
        raiseAmount = 300;
        break;
      case JOIN:
        raiseAmount = 100;
        break;
      case JOIN_IF_CHEAP:
        raiseAmount = 50;
        break;
    }
    return raiseAmount;
  }

  private Points calculatePoints(Player player, int numberOfRemainingPlayers) {
    Points points = new Points();
    int commonPoints = 0;
    int privatePoints = calculatePrivatePoints(dealer.getPlayerHand(player.getName()), player);
    logger.debug(player.getName() + " private points: " + privatePoints);
    privatePoints = compensatePrivateHandWithNumberOfPlayers(privatePoints, numberOfRemainingPlayers);
    if (turn != Turn.BEFORE_FLOP) {
      commonPoints = calculateCommonPoints(numberOfRemainingPlayers);
    }
    logger.debug(player.getName() + " private points compensated: " + privatePoints + " common points compensated: " + commonPoints);
    points.privatePoints = privatePoints;
    points.commonPoints = commonPoints;
    return points;
  }

  /**
   * If lower than 4 players, an average hand may be a real good one
   */
  private int compensatePrivateHandWithNumberOfPlayers(int privatePoints, int numberOfRemainingPlayers) {
    if (numberOfRemainingPlayers == 3) {
      privatePoints = privatePoints * 2;
    } else if (numberOfRemainingPlayers == 2) {
      privatePoints = privatePoints * 3;
    }
    return privatePoints;
  }

  private int calculateCommonPoints(int numberOfRemainingPlayers) {
    final List<Card> commonHand = dealer.getCommonHand();
    final Map<Card, PokerResult> commonPointsMap = EvaluationHandler.evaluateHand("common", commonHand);
    int commonHandPoints = EvaluationHandler.getResultFromCardPokerResultMap(commonPointsMap).getPoints();
    // less probability that a common hand might fit another players hand
    if (numberOfRemainingPlayers < 4) {
      commonHandPoints = commonHandPoints / 2;
    }
    return commonHandPoints;
  }

  /**
   * Since privatePoints is compensated by the number of players there is no need to consider number of players here
   */
  private Strategy decideBetStrategy(Points points, int numberOfMarkers) {
    switch (turn) {
      case BEFORE_FLOP:
        // No common hand to care
        // Pair is good, raise or go all in if pot is big
        if (points.privatePoints > 113) {
          // Pair of aces and higher
          return OFFENSIVE;
        }
        // Pair
        if (points.privatePoints > 100) {
          return Strategy.JOIN;
        }
        break;
      case BEFORE_TURN:
        // Pair of aces is good or anything higher, raise or go all in if pott is big
        // Low pair, join unless too expensive
        // Bad cards, try to join if cheap otherwise fold
        break;
      case BEFORE_RIVER:
        // Pair of aces is good or anything higher, raise or go all in if pott is big
        // Low pair, join unless too expensive
        // Bad cards, try to join if cheap otherwise fold
        break;
      case END_GAME:
        break;
    }
    return Strategy.JOIN_IF_CHEAP;

//    if (points.privatePoints > 113) { // Pair of aces and higher
//      if (points.commonPoints < 50) {
//        // TODO: calculateRaiseAmount(privatePoints, commonPoints, sizeOfBlind, moneyLeft)
//        // raise
//        raiseAmount = 100;
//      } else {
//        // raise only if no other raises
//        raiseAmount = 50;
//      }
//    } else if (points.privatePoints > 10) {
//      if (points.commonPoints < 5) {
//        // raise if no one else has raised
//        raiseAmount = 10;
//      } else {
//        // don't raise, join if blind is cheap otherwise fold
//        raiseAmount = 5;
//      }
//    } else if (points.privatePoints > 5) {
//      if (points.commonPoints < 5) {
//        // raise if no one else has raised
//        raiseAmount = 5;
//      } else {
//        // don't raise, join if blind is cheap otherwise fold
//        raiseAmount = 0;
//      }
//    } else {
//      // don't raise, join if blind is cheap otherwise fold
//      raiseAmount = 0;
//    }

  }

  private int calculatePrivatePoints(List<Card> hand, Player player) {
    final Map<Card, PokerResult> cardPokerResultMap = EvaluationHandler.evaluateHand(player.getName(), hand);
    return EvaluationHandler.calculatePointsFromHand(cardPokerResultMap);
  }

  private void createRobotPlayers() {
    System.out.println("How many players do you want to startGame with?");
    String numberOfPlayers = getCharFromKeyboard(Lists.newArrayList("1", "2", "3", "4", "5", "6"));
    switch (numberOfPlayers) {
      case "1":
        Player player = new Player("Thomas", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "2":
        player = new Player("Thomas", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "3":
        player = new Player("Thomas", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "4":
        player = new Player("Thomas", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Bosse", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "5":
        player = new Player("Thomas", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Bosse", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Ingemar", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "6":
        player = new Player("Thomas", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Bosse", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Ingemar", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Staffan", TOTAL_MARKERS_PER_PLAYER);
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      default:
        throw new RuntimeException("Number of players should be between 1 and 6: " + numberOfPlayers);

    }
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

  private boolean isFolding(String decision) {
    if (decision.equals("F")) {
      System.out.println("You fold! Bye");
      return true;
    }
    return false;
  }

  boolean allowedCharacterIsPressed(String input, List<String> allowedCharacters) {
    final char[] inputCharArray = input.toCharArray();
    for (char inputChar : inputCharArray) {
      for (String allowedCharacter : allowedCharacters) {
        if (allowedCharacter.contains(String.valueOf(inputChar))) {
          return true;
        }
      }
    }
    return false;
  }

  private String askForInput(String message) {
    System.out.println(message);
    Scanner keyboard = new Scanner(System.in);
    String input = keyboard.next();
    return input;
  }

  private String getCharFromKeyboard(List<String> allowedCharacters) {
    String input = "";
    do {
      input = askForInput("Select :" + allowedCharacters.toString());
    } while (!allowedCharacterIsPressed(input, allowedCharacters));
    return input;
  }

  public void registerRobotPlayer(Player player) {
    player.setToRobot();
    dealer.registerPlayer(player);
  }

  public void setPrivateHand(Player player, List<Card> privateHand) {
    dealer.setPrivateHand(player, privateHand);
  }

  public int betPrivateHand(Player player, int numberOfPlayers) {
    return evaluateOwnHand(player, numberOfPlayers);
  }

  public void clearGame() {
    dealer.clearGame();
  }

  void setTurnForUnitTest(Turn beforeFlop) {
    turn = beforeFlop;
  }

  private class Points {
    int privatePoints;
    int commonPoints;
  }
}
