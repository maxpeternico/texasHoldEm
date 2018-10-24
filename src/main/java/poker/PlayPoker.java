package poker;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class PlayPoker {
  private static final int FOLD_LEVEL = -5;
  private Dealer dealer;
  private static final Logger logger = LogManager.getLogger(PlayPoker.class);
  private int blind = 50;
  static final int TOTAL_MARKERS_PER_PLAYER = 2500;

  public static void main(String[] args) {
    final PlayPoker playPoker = getInstance();
    playPoker.play();
  }

  public PlayPoker() {
    this.dealer = Dealer.getInstance();
  }

  public static PlayPoker getInstance() {
    return new PlayPoker();
  }

  public void putCardsBackInDeck(List<Card> cardsInHand) {
    dealer.putCardsInHandToDeck(cardsInHand);
  }

  private void play() {
    blind = increaseBlind();
    Turn turn = Turn.BEFORE_FLOP;
    turn = increaseTurn(turn);
    createPlayers();
    List<Player> remainingPlayersInPlayingOrder = dealer.getPlayers();

    System.out.println("Blind is: [" + blind / 2 + "] resp: [" + blind + "]");

    dealer.playPrivateHands();
    printHumanHand();
    decideBet(remainingPlayersInPlayingOrder, turn);

    /********************************* FLOP *************************************************/

    dealer.drawFlop();
    System.out.println("Total hand after flop: ");
    printHumanHand();
    String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;

    /********************************* TURN *************************************************/

    dealer.drawTurn();
    System.out.println("Total hand after turn: ");
    printHumanHand();
    decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;

    /********************************* RIVER *************************************************/

    dealer.drawRiver();
    System.out.println("Total hand after river: ");
    printHumanHand();
    decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;

    /********************************* FIND THE WINNER *************************************************/

    final Player theWinner = dealer.findTheWinner();
    checkTotalHand(dealer, theWinner.getName(), theWinner.getPrivateHand());
    dealer.putCardsBackIntoDeck();
  }

  private void printHumanHand() {
    final List<Player> players = dealer.getPlayers();
    String humanPlayer = "";
    for (Player player:players) {
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
      case END_GAME:
        newTurn = Turn.END_GAME;
        break;
    }
    return newTurn;
  }

  private int increaseBlind() {
    return blind * 2;
  }

  String decideBet(List<Player> remainingPlayers, Turn turn) {
    StringBuffer result = new StringBuffer();
    int maxRaiseFromOtherplayer = 0;
    List<Player> removePlayers = new ArrayList<>();
    boolean doRaise = false;
    String playerDecision = "";
    do {
      int raiseAmount = 0;
      for (Player player : remainingPlayers) {
        if (isPlayerStillInTheGame(player, removePlayers)) {
          logger.debug("player :[" + player + "] doRaise: [" + doRaise + "]. maxRaiseFromOtherPlayer:[" + maxRaiseFromOtherplayer + "]");
          if (player.isHuman()) {
            String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
            if (isFolding(decision)) {
              doRaise = false;
              removePlayers.add(player);
            } else if (isChecking(decision)) {
              // Put player money in pot
              doRaise = false;
            } else if (isRaising(decision)) {
              doRaise = true;
              raiseAmount = getRaiseAmount();
              int totalRaiseAmount = raiseAmount - maxRaiseFromOtherplayer;
              maxRaiseFromOtherplayer = totalRaiseAmount;
            }
          } else {
            raiseAmount = evaluateOwnHand(player, turn, remainingPlayers.size());
            boolean fold = doFold(raiseAmount, maxRaiseFromOtherplayer);
            if (fold) {
              playerDecision = "Player " + player.getName() + " fold. ";
              System.out.println(playerDecision);
              removePlayers.add(player);
            } else {
              doRaise = doRaise(raiseAmount, maxRaiseFromOtherplayer);
              if (doRaise) {
                int totalRaiseAmount = raiseAmount - maxRaiseFromOtherplayer;
                maxRaiseFromOtherplayer = totalRaiseAmount;
                playerDecision = "Player " + player.getName() + " raises " + totalRaiseAmount + ". ";
                System.out.println(playerDecision);
              } else {
                playerDecision = "Player " + player.getName() + " checks. ";
                System.out.println(playerDecision);
              }
            }
          }
        }
      }
    } while (doRaise);
    result.append(playerDecision);
    removePlayers.stream().forEach(e -> {
      if (remainingPlayers.contains(e)) {
        remainingPlayers.remove(e);
        dealer.putCardsInHandToDeck(e.getPrivateHand()); // TODO: Should be done at end of game
      } else {
        throw new RuntimeException("Player [" + e.getName() + "] should not be in this game");
      }
    });
    return result.toString();

  }

  private boolean isPlayerStillInTheGame(Player player, List<Player> removePlayers) {
    return !removePlayers.contains(player);
  }

  private int getRaiseAmount() {
    return Integer.parseInt(getCharFromKeyboard(Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")));
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

  int evaluateOwnHand(Player player, Turn turn, int numberOfRemainingPlayers) {
    int raiseAmount = 0;
    int commonPoints = 0;
    int privatePoints = calculatePrivatePoints(dealer.getPlayerHand(player.getName()), player);
    logger.debug(player.getName() + " private points: " + privatePoints);
    privatePoints = compensatePrivateHandWithNumberOfPlayers(privatePoints, numberOfRemainingPlayers);
    if (turn != Turn.BEFORE_FLOP) {
      commonPoints = calculateCommonPoints(numberOfRemainingPlayers);
    }
    logger.debug(player.getName() + " private points compensated: " + privatePoints + " common points compensated: " + commonPoints);
    calculateLimit(privatePoints, commonPoints, turn, player.getNumberOfMarkers());
    if (privatePoints > 113) { // Pair of aces and higher
      if (commonPoints < 50) {
        // TODO: calculateRaiseAmount(privatePoints, commonPoints, sizeOfBlind, moneyLeft)
        // raise
        raiseAmount = 100;
      } else {
        // raise only if no other raises
        raiseAmount = 50;
      }
    } else if (privatePoints > 10) {
      if (commonPoints < 5) {
        // raise if no one else has raised
        raiseAmount = 10;
      } else {
        // don't raise, join if blind is cheap otherwise fold
        raiseAmount = 5;
      }
    } else if (privatePoints > 5) {
      if (commonPoints < 5) {
        // raise if no one else has raised
        raiseAmount = 5;
      } else {
        // don't raise, join if blind is cheap otherwise fold
        raiseAmount = 0;
      }
    } else {
      // don't raise, join if blind is cheap otherwise fold
      raiseAmount = 0;
    }
    logger.debug(player.getName() + " raises " + raiseAmount);
    return raiseAmount;
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
  private void calculateLimit(int privatePoints, int commonPoints, Turn turn, int numberOfMarkers) {
    switch (turn) {
      case BEFORE_FLOP:
        // Pair is good, raise or go all in if pott is big
        // Average cards, join unless too expensive
        // Bad cards, try to join if cheap otherwise fold
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
  }

  private int calculatePrivatePoints(List<Card> hand, Player player) {
    final Map<Card, PokerResult> cardPokerResultMap = EvaluationHandler.evaluateHand(player.getName(), hand);
    return EvaluationHandler.calculatePointsFromHand(cardPokerResultMap);
  }

  private void createRobotPlayers() {
    System.out.println("How many players do you want to play with?");
    String numberOfPlayers = getCharFromKeyboard(Lists.newArrayList("1", "2", "3", "4", "5", "6"));
    switch (numberOfPlayers) {
      case "1":
        Player player = new Player("Thomas");
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "2":
        player = new Player("Thomas");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn");
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "3":
        player = new Player("Thomas");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders");
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "4":
        player = new Player("Thomas");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Bosse");
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "5":
        player = new Player("Thomas");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Bosse");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Ingemar");
        player.setToRobot();
        dealer.registerPlayer(player);
        break;
      case "6":
        player = new Player("Thomas");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Jörn");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Anders");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Bosse");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Ingemar");
        player.setToRobot();
        dealer.registerPlayer(player);
        player = new Player("Staffan");
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

  private boolean allowedCharacterIsPressed(String input, List<String> allowedCharacters) {
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
    return evaluateOwnHand(player, Turn.BEFORE_FLOP, numberOfPlayers);
  }

  public void clearGame() {
    dealer.clearGame();
  }
}
