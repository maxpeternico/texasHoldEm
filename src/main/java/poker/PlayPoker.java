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
    String playerName = askForInput("Enter your name: ");
    System.out.println("Welcome [" + playerName + "]");
    final Player player = new Player(playerName, TOTAL_MARKERS_PER_PLAYER);
    player.setToHuman();
    getPlayers(dealer);
    List<Player> remainingPlayersInPlayingOrder = dealer.getPlayers();
    dealer.playPrivateHands();

    System.out.println("Blind is: [" + blind / 2 + "] resp: [" + blind + "]");

    final List<Card> privateHand = dealer.getPlayerHand(playerName);
    final String privateHandString = EvaluationHandler.getHandAsString(privateHand);
    System.out.println("Private hand: " + privateHandString);
    printCurrentResult(playerName, privateHand);
    decideBet(remainingPlayersInPlayingOrder);

    /********************************* FLOP *************************************************/

    dealer.drawFlop();
    System.out.println("Total hand after flop: ");
    checkTotalHand(dealer, playerName, privateHand);
    String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;

    /********************************* TURN *************************************************/

    dealer.drawTurn();
    System.out.println("Total hand after turn: ");
    checkTotalHand(dealer, playerName, privateHand);
    decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;

    /********************************* RIVER *************************************************/

    dealer.drawRiver();
    System.out.println("Total hand after river: ");
    checkTotalHand(dealer, playerName, privateHand);
    decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
    if (isFolding(decision)) return;

    /********************************* FIND THE WINNER *************************************************/

    final Player theWinner = dealer.findTheWinner();
    checkTotalHand(dealer, theWinner.getName(), theWinner.getPrivateHand());
    dealer.putCardsBackIntoDeck();
  }

  private int increaseBlind() {
    return blind * 2;
  }

  String decideBet(List<Player> remainingPlayers) {
    StringBuffer result = new StringBuffer();
    int maxRaiseFromOtherplayer = 0;
    List<Player> removePlayers = new ArrayList<>();
    for (Player player : remainingPlayers) {
      String playerDecision = "";
      if (player.isHuman()) {
        String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
        if (isFolding(decision)) {
          removePlayers.add(player);
        }
      } else {
        int raiseAmount = evaluateOwnHand(player);
        boolean fold = doFold(raiseAmount, maxRaiseFromOtherplayer);
        if (fold) {
          playerDecision = "Player " + player.getName() + " fold. ";
          System.out.println(playerDecision);
          removePlayers.add(player);
        } else {
          boolean raise = doRaise(raiseAmount, maxRaiseFromOtherplayer);
          if (raise) {
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
      result.append(playerDecision);
    }
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

  int evaluateOwnHand(Player player) {
    int raiseAmount = 0;
    int privatePoints = calculatePoints(dealer.getPlayerHand(player.getName()), player);
    int commonPoints = calculatePoints(dealer.getCommonHand(), player);
    logger.debug(player.getName() + " private points: " + privatePoints + " common points: " + commonPoints);
    if (privatePoints > 100) {
      if (commonPoints < 50) {
        // TODO: calculateRaiseAmount(privatePoints, commonPoints, sizeOfBlind, moneyLeft)
        // raise no matter what
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

  private int calculatePoints(List<Card> hand, Player player) {
    final Map<Card, PokerResult> cardPokerResultMap = EvaluationHandler.evaluateHand(player.getName(), hand);
    return EvaluationHandler.calculatePointsFromHand(cardPokerResultMap);
  }

  private void getPlayers(Dealer dealer) {
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
    for (String allowedCharacter : allowedCharacters) {
      if (allowedCharacter.equals(input)) {
        return true;
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

  public int betPrivateHand(Player player) {
    return evaluateOwnHand(player);
  }
}
