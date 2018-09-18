import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayPoker {
    public static void main(String[] args)  {
        final PlayPoker playPoker = new PlayPoker();
        playPoker.play();
    }

    private void play() {
        Dealer dealer = Dealer.getInstance();
        String playerName = askForInput("Enter your name: ");
        System.out.println("Welcome [" + playerName + "]");
        dealer.registerPlayer(playerName);
        dealer.registerPlayer("Thomas");
        dealer.registerPlayer("Jörn");
        dealer.registerPlayer("Anders");
        dealer.registerPlayer("Bosse");
        dealer.registerPlayer("Ingemar");
        dealer.playPrivateHands();
        final List<Card> privateHand = dealer.getPlayerHand(playerName);
        final String privateHandString = EvaluationHandler.printHand(privateHand);
        System.out.println("Private hand: " + privateHandString);
        EvaluationHandler.evaluateHand(playerName, privateHand);
        String decision = askForDecision();
        if (evaluate(decision)) return;

        dealer.drawFlop();
        List<Card> totalHand = dealer.getCommonHand();
        List<Card> totalHandAfterFlop = new ArrayList<Card>(privateHand);
        totalHandAfterFlop.addAll(totalHand);
        final String totalHandAfterFlopString = EvaluationHandler.printHand(totalHandAfterFlop);
        System.out.println("Total hand after flop: " + totalHandAfterFlopString);
        EvaluationHandler.evaluateHand(playerName, totalHandAfterFlop);
        decision = askForDecision();
        if (evaluate(decision)) return;

        dealer.drawTurn();
        totalHand = dealer.getCommonHand();
        List<Card> totalHandAfterTurn = new ArrayList<Card>(privateHand);
        totalHandAfterTurn.addAll(totalHand);
        final String totalHandAfterTurnString = EvaluationHandler.printHand(totalHandAfterTurn);
        System.out.println("Total hand after turn: " + totalHandAfterTurnString);
        EvaluationHandler.evaluateHand(playerName, totalHandAfterTurn);
        decision = askForDecision();
        if (evaluate(decision)) return;

        dealer.drawRiver();
        totalHand = dealer.getCommonHand();
        List<Card> totalHandAfterRiver = new ArrayList<Card>(privateHand);
        totalHandAfterRiver.addAll(totalHand);
        final String totalHandAfterRiverString = EvaluationHandler.printHand(totalHandAfterRiver);
        System.out.println("Total hand after river: " + totalHandAfterRiverString);
        EvaluationHandler.evaluateHand(playerName, totalHandAfterRiver);
        decision = askForDecision();
        if (evaluate(decision)) return;

        dealer.findTheWinner();
        dealer.putCardsBackIntoDeck();
    }

    private boolean evaluate(String decision) {
        if (decision.equals("F")) {
            System.out.println("You fold! Bye");
            return true;
        }
        return false;
    }

    private boolean isRCFpressed(String input) {
        switch (input) {
            case "R":
                return true;
            case "C":
                return true;
            case "F":
                return true;
            default:
                return false;
        }
    }

    private String askForInput(String message) {
        System.out.println(message);
        Scanner keyboard = new Scanner(System.in);
        String input = keyboard.next();
        return input;
    }

    private String askForDecision() {
        String input = "";
        do {
            input = askForInput("(R)aise/(C)heck/(F)old:");
        } while (!isRCFpressed(input));
        return input;
    }
}
