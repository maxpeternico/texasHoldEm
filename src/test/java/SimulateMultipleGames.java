import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SimulateMultipleGames {

    private static final Logger logger = LogManager.getLogger(SimulateMultipleGames.class.getName());

    @Test
    public void simulateNormalGame() throws Exception {
        Dealer dealer = Dealer.getInstance();
        dealer.registerPlayer("Thomas");
        dealer.registerPlayer("Jörn");
        dealer.registerPlayer("Anders");
        dealer.registerPlayer("Peter");
        dealer.registerPlayer("Bosse");
        dealer.registerPlayer("Ingemar");
        for (int i = 0; i < 10000; i++) {
            dealer.play();
            dealer.findTheWinner();
            dealer.putCardsBackIntoDeck();
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

    //@Test
    public void testWinWithTopCard() throws Exception {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");

        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("Jörn");
        Player anders = dealer.registerPlayer("Anders");
        Player bosse = dealer.registerPlayer("Bosse");
        Player ingemar = dealer.registerPlayer("Ingemar");
        EvaluationHandler.initDrawnCardStatistics();

        for (Ordinal ordinal : Ordinal.values()) {
            if (ordinal.getValue() > ordinal.six.getValue()) {
                logger.info("Checking statistics when peter gets the card of:[" + ordinal.toString() + "]");
                for (int i = 0; i < 1000; i++) {
                    List<Card> privateHand = new ArrayList<Card>();
                    privateHand.add(new Card(Color.hearts, ordinal));
                    privateHand.add(new Card(Color.spades, Shuffle.getRandomOrdinal()));
                    dealer.setPrivateHand(peter, privateHand);

                    dealer.playPrivateHand(thomas);
                    dealer.playPrivateHand(jorn);
                    dealer.playPrivateHand(anders);
                    dealer.playPrivateHand(bosse);
                    dealer.playPrivateHand(ingemar);

                    dealer.drawFlop();
                    for (Player player : dealer.getPlayers()) {
                        dealer.playFlop(player);
                    }
                    dealer.drawTurn();
                    for (Player player : dealer.getPlayers()) {
                        dealer.playTurn(player);
                    }
                    dealer.drawRiver();
                    dealer.findTheWinner();
                    dealer.putCardsBackIntoDeck();
                }
                dealer.printWinStatistics();
            }
        }
    }

   // @Test
    public void testWinWithPairOnHand() {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("Jörn");
        Player anders = dealer.registerPlayer("Anders");
        Player bosse = dealer.registerPlayer("Bosse");
        Player ingemar = dealer.registerPlayer("Ingemar");
        for (Ordinal ordinal:Ordinal.values()) {
            logger.info("Checking statistics when peter gets pair of:[" + ordinal.toString() + "]");
            for (int i = 0; i < 10000; i++) {
                List<Card> privateHand = new ArrayList<Card>();
                privateHand.add(new Card(Color.hearts, ordinal));
                privateHand.add(new Card(Color.clubs, ordinal));
                dealer.setPrivateHand(peter, privateHand);
                dealer.playPrivateHand(thomas);
                dealer.playPrivateHand(jorn);
                dealer.playPrivateHand(anders);
                dealer.playPrivateHand(bosse);
                dealer.playPrivateHand(ingemar);
                dealer.drawFlop();
                for (Player player : dealer.getPlayers()) {
                    dealer.playFlop(player);
                }
                dealer.drawTurn();
                for (Player player : dealer.getPlayers()) {
                    dealer.playTurn(player);
                }
                dealer.drawRiver();
                dealer.findTheWinner();
                dealer.putCardsBackIntoDeck();
            }
            dealer.printWinStatistics();
        }
    }

    //@Test
    public void testWinWithPairOnFlop() {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("Jörn");
        Player anders = dealer.registerPlayer("Anders");
        Player bosse = dealer.registerPlayer("Bosse");
        Player ingemar = dealer.registerPlayer("Ingemar");
        for (Ordinal ordinal:Ordinal.values()) {
            logger.info("Checking statistics when peter gets pair of:[" + ordinal.toString() + "]");
            for (int i = 0; i < 10000; i++) {
                List<Card> privateHand = new ArrayList<Card>();
                privateHand.add(new Card(Color.hearts, ordinal));
                privateHand.add(new Card(Color.clubs, dealer.getAnotherOrdinal(ordinal)));
                dealer.setPrivateHand(peter, privateHand);
                dealer.reserveCardToFlop(Color.diamonds, ordinal);
                dealer.playPrivateHand(thomas);
                dealer.playPrivateHand(jorn);
                dealer.playPrivateHand(anders);
                dealer.playPrivateHand(bosse);
                dealer.playPrivateHand(ingemar);
                dealer.drawFlop();
                for (Player player : dealer.getPlayers()) {
                    dealer.playFlop(player);
                }
                dealer.drawTurn();
                for (Player player : dealer.getPlayers()) {
                    dealer.playTurn(player);
                }
                dealer.drawRiver();
                dealer.findTheWinner();
                dealer.putCardsBackIntoDeck();
            }
            dealer.printWinStatistics();
        }
    }

    //@Test
    public void testWinWithTrippleAfterFlop() {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("Jörn");
        Player anders = dealer.registerPlayer("Anders");
        for (Ordinal ordinal:Ordinal.values()) {
            logger.info("Checking statistics when peter gets a tripple of:[" + ordinal.toString() + "] after flop");
            for (int i = 0; i < 1000; i++) {
                List<Card> privateHand = new ArrayList<Card>();
                privateHand.add(new Card(Color.hearts, ordinal));
                privateHand.add(new Card(Color.clubs, ordinal));
                dealer.setPrivateHand(peter, privateHand);
                dealer.reserveCardToFlop(Color.spades, ordinal);
                dealer.playPrivateHand(thomas);
                dealer.playPrivateHand(jorn);
                dealer.playPrivateHand(anders);
                dealer.drawFlop();
                for (Player player : dealer.getPlayers()) {
                    dealer.playFlop(player);
                }
                dealer.drawTurn();
                for (Player player : dealer.getPlayers()) {
                    dealer.playTurn(player);
                }
                dealer.drawRiver();
                dealer.findTheWinner();
                dealer.putCardsBackIntoDeck();
            }
            dealer.printWinStatistics();
        }
    }

    //@Test
    public void testWinWithTrippleAfterFlopPairOnTable() {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("Jörn");
        Player anders = dealer.registerPlayer("Anders");
        for (Ordinal ordinal:Ordinal.values()) {
            logger.info("Checking statistics when peter gets a tripple of:[" + ordinal.toString() + "] after flop with pair on the flop.");
            for (int i = 0; i < 1000; i++) {
                List<Card> privateHand = new ArrayList<Card>();
                privateHand.add(new Card(Color.hearts, ordinal));
                privateHand.add(new Card(Color.diamonds, dealer.getAnotherOrdinal(ordinal)));
                dealer.setPrivateHand(peter, privateHand);
                dealer.reserveCardToFlop(Color.spades, ordinal);
                dealer.reserveCardToFlop(Color.clubs, ordinal);
                dealer.playPrivateHand(thomas);
                dealer.playPrivateHand(jorn);
                dealer.playPrivateHand(anders);
                dealer.drawFlop();
                for (Player player : dealer.getPlayers()) {
                    dealer.playFlop(player);
                }
                dealer.drawTurn();
                for (Player player : dealer.getPlayers()) {
                    dealer.playTurn(player);
                }
                dealer.drawRiver();
                dealer.findTheWinner();
                dealer.putCardsBackIntoDeck();
            }
            dealer.printWinStatistics();
        }
    }

    //@Test
    public void testWinWithTwoPair() {
        Dealer dealer = Dealer.getInstance();
        Player peter = dealer.registerPlayer("Peter");
        Player thomas = dealer.registerPlayer("Thomas");
        Player jorn = dealer.registerPlayer("Jörn");
        Player anders = dealer.registerPlayer("Anders");
        for (Ordinal ordinal:Ordinal.values()) {
            if (ordinal.getValue() > Ordinal.two.getValue()) { // Don't want to test foursome
                logger.info("Checking statistics when peter gets two pair of twos and :[" + ordinal.toString() + "].");
                for (int i = 0; i < 1000; i++) {
                    List<Card> privateHand = new ArrayList<Card>();
                    privateHand.add(new Card(Color.hearts, ordinal));
                    privateHand.add(new Card(Color.diamonds, Ordinal.two));
                    dealer.setPrivateHand(peter, privateHand);
                    dealer.reserveCardToFlop(Color.spades, ordinal);
                    dealer.reserveCardToFlop(Color.clubs, Ordinal.two);
                    dealer.playPrivateHand(thomas);
                    dealer.playPrivateHand(jorn);
                    dealer.playPrivateHand(anders);
                    dealer.drawFlop();
                    for (Player player : dealer.getPlayers()) {
                        dealer.playFlop(player);
                    }
                    dealer.drawTurn();
                    for (Player player : dealer.getPlayers()) {
                        dealer.playTurn(player);
                    }
                    dealer.drawRiver();
                    dealer.findTheWinner();
                    dealer.putCardsBackIntoDeck();
                }
                dealer.printWinStatistics();
            }
        }
    }

    private void throwExceptionIfCardIsNotTwoOfClubs(Card topCard) {
        if (!topCard.equals(EvaluationHandler.getLeastValueableCard())) {
            throw new RuntimeException("Got doubles of :[" + topCard + "]");
        }
    }
}
