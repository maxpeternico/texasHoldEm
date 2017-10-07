import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestTexasHoldEmGames {

    private static final Logger logger = LogManager.getLogger(TestTexasHoldEmGames.class.getName());

    //    @Before
    //    public void configureLogger() {
    //        BasicConfigurator.resetConfiguration();
    //        BasicConfigurator.configure();
    //    }

    @Test
    public void playNormal() {
        Dealer dealer = Dealer.getInstance();
        dealer.registerPlayer("Peter");
//        dealer.registerPlayer("Thomas");
//        dealer.registerPlayer("J�rn");
//        dealer.registerPlayer("Anders");
        for (int i = 0; i < 1000; i++) {
            dealer.play();
            findTheWinner(dealer);
            putCardsBackIntoDeck(dealer);
        }
        for (Player player : dealer.getPlayers()) {
            logger.info("[" + player.getName() + "] won [" + player.getNumberOfWins() + "] times on ["
                    + printAllWinResults(player) + "]");
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

    public void testWinWithAce() {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("J�rn");
        Player anders = dealer.registerPlayer("Anders");
        for (int i = 0; i < 1000; i++) {
            EvaluationHandler.initStatistics();
            List<Card> privateHand = new ArrayList<Card>();
            //            privateHand.add(new Card(Color.hearts, Ordinal.ace));
            //            privateHand.add(new Card(Color.hearts, Ordinal.six));
            privateHand.add(new Card(Color.hearts, Ordinal.ace));
            privateHand.add(new Card(Color.diamonds, Ordinal.two));
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
            Player player = findTheWinner(dealer);
            putCardsBackIntoDeck(dealer);
        }
        for (Player player2 : dealer.getPlayers()) {
            logger.info("[" + player2.getName() + "] won [" + player2.getNumberOfWins() + "] times on ["
                    + printAllWinResults(player2) + "]");
        }

    }

    private String printAllWinResults(Player player) {
        StringBuilder result = new StringBuilder();
        for (PokerResult pokerResult : PokerResult.values()) {
            if (player.getNumberOfWinsPerPokerResult(pokerResult) != 0) {
                result.append(" ").append(player.getNumberOfWinsPerPokerResult(pokerResult)).append(" ")
                        .append(pokerResult.toString());
            }
        }
        return result.toString();
    }

    private Player findTheWinner(Dealer dealer) {
//        Player winner = null;
        String winnerName = null;
        Map<Card, PokerResult> highScore = new HashMap<Card, PokerResult>();
        highScore.put(EvaluationHandler.getLeastValueableCard(), PokerResult.NO_RESULT);
        for (Player player : dealer.getPlayers()) {
            Map<Card, PokerResult> result = dealer.playLastDeal(player);
            logger.info("[" + player.toString() + "] got [" + EvaluationHandler.getResultFromCardPokerResultMap(result)
                    + "] with top card [" + EvaluationHandler.getTopCardFromResult(result) + "]");
            logger.debug(" from hand:[" + EvaluationHandler.printHand(player.getPrivateHand()) + "]");
            logger.debug("Highscore is:[" + highScore.toString() + "]");
            if (EvaluationHandler.isResultFromLatestPlayerHigherThanHighScore(result, highScore)) {
                highScore.clear();
                highScore.putAll(result);
                //winner = player;
                winnerName = player.getName();
            }
        }
        Player winner = dealer.getPlayer(winnerName);
        logger.info(
                "And the winner is:[" + winnerName + "] with highscore :[" + printPokerResult(highScore) + "]");
        int i = winner.getInt();
        winner.setInt();
        winner.addWin(EvaluationHandler.getResultFromCardPokerResultMap(highScore));
        //dealer.addWin(winner, EvaluationHandler.getResultFromCardPokerResultMap(highScore));
        return winner;
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
