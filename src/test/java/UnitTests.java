import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class UnitTests {

    @Before
    public void init() {
        EvaluationHandler.initStatistics();
    }

    @Test
    public void testFlush() {
        List<Card> cardsOnHand = new ArrayList<Card>();
        cardsOnHand.add(new Card(Color.spades, Ordinal.five));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.ace));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.king));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.two));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.five));
        cardsOnHand.add(new Card(Color.spades, Ordinal.five));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.six));
        Map<Card, PokerResult> evaluateHand = EvaluationHandler.evaluateHand("test", cardsOnHand);
        assertEquals(PokerResult.FLUSH, evaluateHand.get(new Card(Color.hearts, Ordinal.ace)));
    }

    @Test
    public void testFlushNegative() {
        List<Card> cardsOnHand = new ArrayList<Card>();
        cardsOnHand.add(new Card(Color.spades, Ordinal.five));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.ace));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.king));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.two));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.eight));
        cardsOnHand.add(new Card(Color.spades, Ordinal.ten));
        cardsOnHand.add(new Card(Color.clubs, Ordinal.six));
        Map<Card, PokerResult> evaluateHand = EvaluationHandler.evaluateHand("test", cardsOnHand);
        assertEquals(PokerResult.NO_RESULT, evaluateHand.get(new Card(Color.hearts, Ordinal.ace)));
    }

    @Test
    public void testStraight() {
        List<Card> cardsOnHand = new ArrayList<Card>();
        cardsOnHand.add(new Card(Color.spades, Ordinal.five));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.six));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.king));
        cardsOnHand.add(new Card(Color.diamonds, Ordinal.two));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.seven));
        cardsOnHand.add(new Card(Color.spades, Ordinal.eight));
        cardsOnHand.add(new Card(Color.clubs, Ordinal.four));
        Map<Card, PokerResult> evaluateHand = EvaluationHandler.evaluateHand("test", cardsOnHand);
        assertEquals(PokerResult.STRAIGHT, evaluateHand.get(new Card(Color.spades, Ordinal.eight)));
    }

    @Test
    public void testStraightNegative() {
        List<Card> cardsOnHand = new ArrayList<Card>();
        cardsOnHand.add(new Card(Color.spades, Ordinal.five));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.six));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.king));
        cardsOnHand.add(new Card(Color.diamonds, Ordinal.two));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.seven));
        cardsOnHand.add(new Card(Color.spades, Ordinal.queen));
        cardsOnHand.add(new Card(Color.clubs, Ordinal.four));
        Map<Card, PokerResult> evaluateHand = EvaluationHandler.evaluateHand("test", cardsOnHand);
        assertEquals(PokerResult.NO_RESULT, evaluateHand.get(new Card(Color.hearts, Ordinal.king)));
    }

    @Test
    public void testStraightSeven() {
        List<Card> cardsOnHand = new ArrayList<Card>();
        cardsOnHand.add(new Card(Color.spades, Ordinal.five));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.six));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.eight));
        cardsOnHand.add(new Card(Color.diamonds, Ordinal.three));
        cardsOnHand.add(new Card(Color.hearts, Ordinal.seven));
        cardsOnHand.add(new Card(Color.spades, Ordinal.nine));
        cardsOnHand.add(new Card(Color.clubs, Ordinal.four));
        Map<Card, PokerResult> evaluateHand = EvaluationHandler.evaluateHand("test", cardsOnHand);
        assertEquals(PokerResult.STRAIGHT, evaluateHand.get(new Card(Color.spades, Ordinal.nine)));
    }

    @Test
    public void testIsResultFromLatestPlayerHigherThanHighScore() {
        Map<Card, PokerResult> latestResult = new HashMap<Card, PokerResult>();
        latestResult.put(new Card(Color.hearts, Ordinal.ace), PokerResult.PAIR);
        Map<Card, PokerResult> highScore = new HashMap<Card, PokerResult>();
        highScore.put(new Card(Color.spades, Ordinal.queen), PokerResult.PAIR);
        boolean resultFromLatestPlayerHigherThanHighScore = EvaluationHandler.isResultFromLatestPlayerHigherThanHighScore(latestResult, highScore);
        assertTrue(resultFromLatestPlayerHigherThanHighScore);
    }
    

    @Test
    public void testIsResultFromLatestPlayerHigherThanHighScoreNegative() {
        Map<Card, PokerResult> latestResult = new HashMap<Card, PokerResult>();
        latestResult.put(new Card(Color.hearts, Ordinal.queen), PokerResult.PAIR);
        Map<Card, PokerResult> highScore = new HashMap<Card, PokerResult>();
        highScore.put(new Card(Color.spades, Ordinal.ace), PokerResult.PAIR);
        boolean resultFromLatestPlayerHigherThanHighScore = EvaluationHandler.isResultFromLatestPlayerHigherThanHighScore(latestResult, highScore);
        assertFalse(resultFromLatestPlayerHigherThanHighScore);
    }

}

