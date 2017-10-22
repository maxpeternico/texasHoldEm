import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestTexasHoldEmGames {

    private static final Logger logger = LogManager.getLogger(TestTexasHoldEmGames.class.getName());

    public void playNormal() throws Exception {
        Dealer dealer = Dealer.getInstance();
        dealer.registerPlayer("Thomas");
        dealer.registerPlayer("Jörn");
        dealer.registerPlayer("Anders");
        dealer.registerPlayer("Peter");
        for (int i = 0; i < 1000; i++) {
            dealer.play();
            findTheWinner(dealer);
            putCardsBackIntoDeck(dealer);
        }
        dealer.printWinStatistics();
        verifyDrawCardStatistics();
    }

    private void verifyDrawCardStatistics() {
        int totalNumberOfDraws = EvaluationHandler.getNumberOfDrawnCardsWithOrdinal();
        logger.debug("totalNumberOfDraws: " + totalNumberOfDraws);
        for (Color color:Color.values()) {
            int numberOfColor = EvaluationHandler.getNumberOfDrawnCardsWithColor(color);
            logger.debug("number of "  + color.toString() + " " + numberOfColor);
            if ((numberOfColor < 0.24*totalNumberOfDraws) || (numberOfColor > 0.26*totalNumberOfDraws)) {
                assertTrue("Color :[" + color + "] occured [" + (float)numberOfColor / totalNumberOfDraws + "] of the times when it should be around 25%.", false);
            }

        }
        for (Ordinal ordinal:Ordinal.values()) {
            int numberOfOrdinals = EvaluationHandler.getNumberOfOrdinal(ordinal);
            logger.debug("number of "  + ordinal.toString() + " " + numberOfOrdinals);
            if ((numberOfOrdinals < 0.06*totalNumberOfDraws) || (numberOfOrdinals > 0.08*totalNumberOfDraws)) {
                assertTrue("Ordinal :[" + ordinal + "] occured [" + (float)numberOfOrdinals / totalNumberOfDraws + "] of the times when it should be around 7%.", false);
            }

        }
    }

    private void putCardsBackIntoDeck(Dealer dealer) {
        for (Player player2 : dealer.getPlayers()) {
            dealer.putCardsInHandToDeck(player2.getPrivateHand());
        }
        dealer.putCardsInHandToDeck(dealer.getCommonHand());
        dealer.putCardsInHandToDeck(dealer.getSkippedCards());
        if (!dealer.isDeckFull()) {
            throw new RuntimeException("Cards were lost!");
        }
    }

    public void testWinWithAce() throws Exception {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
//        Player thomas = dealer.registerPlayer("Thomas");
//        Player jorn = dealer.registerPlayer("Jörn");
//        Player anders = dealer.registerPlayer("Anders");
        for (int i = 0; i < 10000; i++) {
            EvaluationHandler.initDrawnCardStatistics();
            List<Card> privateHand = new ArrayList<Card>();
            privateHand.add(new Card(Color.hearts, Ordinal.ace));
            privateHand.add(new Card(Color.hearts, Ordinal.queen));
            dealer.setPrivateHand(peter, privateHand);
/*
            dealer.playPrivateHand(thomas);
            dealer.playPrivateHand(jorn);
            dealer.playPrivateHand(anders);
*/
            dealer.drawLittleBlind();
            for (Player player : dealer.getPlayers()) {
                dealer.playLittleBlind(player);
            }
            dealer.drawBigBlind();
            for (Player player : dealer.getPlayers()) {
                dealer.playBigBlind(player);
            }
            dealer.drawLastDeal();
            findTheWinner(dealer);
            putCardsBackIntoDeck(dealer);
        }
        dealer.printWinStatistics();
    }

    @Test
    public void testWinWithPair() {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("Jörn");
        Player anders = dealer.registerPlayer("Anders");
        for (Ordinal ordinal:Ordinal.values()) {
            logger.info("Checking statistics when peter gets pair of:[" + ordinal.toString() + "]");
            for (int i = 0; i < 100; i++) {
                List<Card> privateHand = new ArrayList<Card>();
                privateHand.add(new Card(Color.hearts, ordinal));
                privateHand.add(new Card(Color.clubs, ordinal));
                dealer.setPrivateHand(peter, privateHand);
                dealer.playPrivateHand(thomas);
                dealer.playPrivateHand(jorn);
                dealer.playPrivateHand(anders);
                dealer.drawLittleBlind();
                for (Player player : dealer.getPlayers()) {
                    dealer.playLittleBlind(player);
                }
                dealer.drawBigBlind();
                for (Player player : dealer.getPlayers()) {
                    dealer.playBigBlind(player);
                }
                dealer.drawLastDeal();
                findTheWinner(dealer);
                putCardsBackIntoDeck(dealer);
            }
            dealer.printWinStatistics();
        }
    }

    private void findTheWinner(Dealer dealer) {
        Player winner = null;
        Map<Card, PokerResult> highScore = new HashMap<Card, PokerResult>();
        highScore.put(EvaluationHandler.getLeastValueableCard(), PokerResult.NO_RESULT);
        for (Player player : dealer.getPlayers()) {
            Map<Card, PokerResult> result = dealer.playLastDeal(player);
            logResult(player, result, highScore);
            if (EvaluationHandler.isResultFromLatestPlayerHigherThanHighScore(result, highScore)) {
                highScore.clear();
                highScore.putAll(result);
                winner = player;
            }
        }
        dealer.updateWinStatistics(winner, highScore);
        logger.debug(
                "And the winner is:[" + winner.getName() + "] with highscore :[" + printPokerResult(highScore) + "]");
    }

    private void logResult(Player player, Map<Card, PokerResult> result, Map<Card, PokerResult> highScore) {
        logger.debug("[" + player.toString() + "] got [" + EvaluationHandler.getResultFromCardPokerResultMap(result)
                + "] with top card [" + EvaluationHandler.getTopCardFromResult(result) + "]");
        logger.trace(" from hand:[" + EvaluationHandler.printHand(player.getPrivateHand()) + "]");
        logger.trace("Highscore is:[" + highScore.toString() + "]");
    }

    private String printPokerResult(Map<Card, PokerResult> highScore) {
        StringBuilder result = new StringBuilder();
        Card topCardFromResult = EvaluationHandler.getTopCardFromResult(highScore);
        result.append(highScore.get(topCardFromResult)).append(" top card: ").append(topCardFromResult.toString());

        return result.toString();

    }

    private void throwExceptionIfCardIsNotTwoOfClubs(Card topCard) {
        if (!topCard.equals(EvaluationHandler.getLeastValueableCard())) {
            throw new RuntimeException("Got doubles of :[" + topCard + "]");
        }

    }
}
