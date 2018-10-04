package poker;

import com.google.common.collect.Lists;

import java.util.*;

public class PlayPoker {
  private Dealer dealer;

  public static void main(String[] args) {
    final PlayPoker playPoker = new PlayPoker();
    playPoker.play();
  }

  private void play() {
    dealer = Dealer.getInstance();
    String playerName = askForInput("Enter your name: ");
    System.out.println("Welcome [" + playerName + "]");
    final Player player = new Player(playerName);
    player.setToHuman();
    dealer.registerPlayer(player);
    getPlayers(dealer);
    List<Player> remainingPlayersInPlayingOrder = dealer.getPlayers();
    dealer.playPrivateHands();

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

  private void decideBet(List<Player> remainingPlayers) {
    List<Player> removePlayers = new ArrayList<>();
    for (Player player : remainingPlayers) {
      if (player.isHuman()) {
        String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
        if (isFolding(decision)) {
          remainingPlayers.remove(player);
        }
      } else {
        int raiseAmount = evaluateOwnHand(player.getPrivateHand(), player);
        int risk = evaluateRaiseFromOtherPlayers();
        if (raiseAmount > 10) {
          if (risk < 10) {
            System.out.println(player.getName() + " raises " + raiseAmount);
          } else {
            System.out.println(player.getName() + " checks.");
          }
        } else if (raiseAmount < 5) {
          if (hasToPay()) {
            if (isExpensive()) {
              System.out.println("Player " + player.getName() + " fold.");
              removePlayers.add(player);
            }
          }
        }
      }
    }
    removePlayers.stream().forEach(e -> {
      if (remainingPlayers.contains(e)) {
        remainingPlayers.remove(e);
        dealer.putCardsInHandToDeck(e.getPrivateHand());
      } else {
        throw new RuntimeException("Player [" + e.getName() + "] should not be in this game");
      }
    });
  }

  private boolean isExpensive() {
    return true;
  }

  private boolean hasToPay() {
    return true;
  }

  private int evaluateRaiseFromOtherPlayers() {
    return 0;
  }


  private int evaluateOwnHand(List<Card> privateHand, Player player) {
    int privatePoints = calculatePoints(privateHand, player);
    int commonPoints = calculatePoints(dealer.getCommonHand(), player);
    if (privatePoints > 0) {
      if (commonPoints < 5) {
        // raise no matter what
        return 10;
      } else {
        // raise only if no other raises
        return 5;
      }
    } else if (privatePoints > 5) {
      if (commonPoints < 5) {
        // raise if no one else has raised
        return 5;
      } else {
        // don't raise, join if blind is cheap otherwise fold
        return 0;
      }
    } else {
      // don't raise, join if blind is cheap otherwise fold
      return 0;
    }
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
      System.out.println(cardPokerResultMap.get(iterator.next()));
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
}