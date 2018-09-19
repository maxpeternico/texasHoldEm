import com.google.common.collect.Lists;

import java.util.*;

public class PlayPoker {
    public static void main(String[] args) {
        final PlayPoker playPoker = new PlayPoker();
        playPoker.play();
    }

    private void play() {
        Dealer dealer = Dealer.getInstance();
        String playerName = askForInput("Enter your name: ");
        System.out.println("Welcome [" + playerName + "]");
        dealer.registerPlayer(playerName);
        getPlayers(dealer);
        dealer.playPrivateHands();

        final List<Card> privateHand = dealer.getPlayerHand(playerName);
        final String privateHandString = EvaluationHandler.getHandAsString(privateHand);
        System.out.println("Private hand: " + privateHandString);
        printCurrentResult(playerName, privateHand);
        String decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
        if (evaluate(decision)) return;

        dealer.drawFlop();
        System.out.println("Total hand after flop: ");
        checkTotalHand(dealer, playerName, privateHand);
        decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
        if (evaluate(decision)) return;

        dealer.drawTurn();
        System.out.println("Total hand after turn: ");
        checkTotalHand(dealer, playerName, privateHand);
        decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
        if (evaluate(decision)) return;

        dealer.drawRiver();
        System.out.println("Total hand after river: ");
        checkTotalHand(dealer, playerName, privateHand);
        decision = getCharFromKeyboard(Lists.newArrayList("R", "C", "F"));
        if (evaluate(decision)) return;

        dealer.findTheWinner();
        dealer.putCardsBackIntoDeck();
    }

    private void getPlayers(Dealer dealer) {
        System.out.println("How many players do you want to play with?");
        String numberOfPlayers = getCharFromKeyboard(Lists.newArrayList("1", "2", "3", "4", "5", "6"));
        switch (numberOfPlayers) {
            case "1":
                dealer.registerPlayer("Thomas");
                break;
            case "2":
                dealer.registerPlayer("Thomas");
                dealer.registerPlayer("Jörn");
                break;
            case "3":
                dealer.registerPlayer("Thomas");
                dealer.registerPlayer("Jörn");
                dealer.registerPlayer("Anders");
                break;
            case "4":
                dealer.registerPlayer("Thomas");
                dealer.registerPlayer("Jörn");
                dealer.registerPlayer("Anders");
                dealer.registerPlayer("Bosse");
                break;
            case "5":
                dealer.registerPlayer("Thomas");
                dealer.registerPlayer("Jörn");
                dealer.registerPlayer("Bosse");
                dealer.registerPlayer("Anders");
                dealer.registerPlayer("Ingemar");
                break;
            case "6":
                dealer.registerPlayer("Thomas");
                dealer.registerPlayer("Jörn");
                dealer.registerPlayer("Bosse");
                dealer.registerPlayer("Anders");
                dealer.registerPlayer("Ingemar");
                dealer.registerPlayer("Staffan");
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

    private boolean evaluate(String decision) {
        if (decision.equals("F")) {
            System.out.println("You fold! Bye");
            return true;
        }
        return false;
    }

    private boolean allowedCharacterIsPressed(String input, List<String> allowedCharacters) {
        for(String allowedCharacter:allowedCharacters) {
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
