package poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;

public abstract class TestBase {
  protected abstract PokerGame getPokerGame();
  
  protected int calculatePotRaise(List<Player> players, int potRaisePerPlayerTotalRound) {
    int currentPot = 0;
    for (Player player:players) {
      currentPot += getPlayersPartInPot(player, potRaisePerPlayerTotalRound);
    }
    System.out.println("Current pot: " +currentPot);
    return currentPot;
  }

  protected void assertPotAndMarkers(List<Player> players,
                                     int totalPotRaisePerPlayer,
                                     int bigBlind) {
    //assertEquals(calculatePot(totalPotRaisePerPlayer, players, bigBlind), getPokerGame().getPotHandler().getNumberOfMarkersInAllPots()); TODO: How to assert pot
    for (Player player : players) {
      final int blindCost = calculateBlindCost(player, bigBlind);
      final int playersPartInPot = getPokerGame().getPotHandler().getPlayerPartInPots(player);
      assertEquals("Number of markers not correct for player :[" + player.getName() + "] playersPartInPot :[" + playersPartInPot + "]",
                   PokerGame.TOTAL_MARKERS_PER_PLAYER - playersPartInPot,
                   player.getNumberOfMarkers());
    }
  }

  protected String createIncorrectNumberOfMarkersForWinnerMessage(int potRaisePerPlayerTotalRound, int calculatedPot, Player player) {
    return "Incorrect number of markers for player :[" + player.getName() + "], potRaisePerPlayerTotalRound :[" + potRaisePerPlayerTotalRound + "] calculated pot :[" + calculatedPot + "]";
  }

  protected String createMarkersDisappearErrorMessage(List<Player> players) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Pot size not equal. Number of markers for players :");
    for (Player player:players) {
      stringBuilder.append("[").append(player.getNumberOfMarkers()).append("]");
    }
    return stringBuilder.toString();
  }


  protected int calculatePot(int totalPotRaisePerPlayer, List<Player> players, int bigBlindAmount) {
    return getTotalBlindCost(bigBlindAmount) + getTotalPotRaiseForAllPlayers(totalPotRaisePerPlayer, players);
  }

  protected int getTotalBlindCost(int bigBlindAmount) {
    return (int)(1.5 * bigBlindAmount);
  }

  protected int getTotalPotRaiseForAllPlayers(int totalPotRaisePerPlayer, List<Player> players) {
    int totalPot = 0;
    for (Player player : players) {
      if (!player.getAction().isFold()) {
        totalPot += totalPotRaisePerPlayer;
      }
    }
    return totalPot;
  }

  protected int getPlayersPartInPot(Player player, int potRaisePerPlayer) {
    if (player.getAction().isFold()) {
      return 0;
    }
    return potRaisePerPlayer;
  }

  protected boolean anyPlayerFolds(List<Player> players) {
    for (Player player : players) {
      if (player.getAction().isFold()) {
        return true;
      }
    }
    return false;
  }

  protected int calculateBlindCost(Player player, int bigBlind) {
    int littleBlind = (int) (bigBlind / 2);
    if (player.hasLittleBlind()) {
      return littleBlind;
    }
    if (player.hasBigBlind()) {
      return bigBlind;
    }
    return 0;
  }

  protected void prepareBeforeFlop(List<Player> players,
                                   int blindAmount,
                                   List<List<Card>> privateHands) {
    getPokerGame().setTurnForUnitTest(Draw.BEFORE_FLOP);
    getPokerGame().initBlinds(players);
    getPokerGame().payBlinds(players, blindAmount);
    for (int i=0;i < players.size();i++) {
      getPokerGame().setPrivateHand(players.get(i), privateHands.get(i));
    }
  }

  protected void prepareFlop(List<Card> flopCards) {
    getPokerGame().increaseDraw();
    getPokerGame().addToCommonHand(flopCards);
  }

  protected void prepareRiver(Color spades, Ordinal two) {
    getPokerGame().increaseDraw();
    getPokerGame().addToCommonHand(Arrays.asList(drawCard(spades, two)));
  }

  protected void prepareTurn(Color hearts, Ordinal queen) {
    getPokerGame().increaseDraw();
    getPokerGame().addToCommonHand(Arrays.asList(drawCard(hearts, queen)));
  }

  protected List<Card> drawPairOfKnightsNegative() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawPairOfKnightsAtFlop() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.spades, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawPairOfKnights() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.knight);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawPairOfEights1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.eight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.eight);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawPairOfEights2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.eight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.eight);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawPairOfNinesAtFlop() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.nine);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawBadPrivateHand2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.knight);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawBadPrivateHand1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.clubs, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.diamonds, Ordinal.ten);
    hand.add(aceOfSpades);
    return hand;
  }

  protected List<Card> drawKingAndQueenOfDifferentColor() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  protected ArrayList<Card> drawPairOfAces1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.ace);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.ace);
    hand.add(aceOfSpades);
    return hand;
  }

  protected ArrayList<Card> drawPairOfAces2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.ace);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.ace);
    hand.add(aceOfSpades);
    return hand;
  }

  protected Card drawCard(Color color, Ordinal ordinal) {
    final Card card = new Card(color, ordinal);
    return card;
  }

  protected List<Card> getBadFlop() {
    final ArrayList<Card> cards = Lists.newArrayList();
    cards.add(drawCard(Color.diamonds, Ordinal.three));
    cards.add(drawCard(Color.clubs, Ordinal.nine));
    cards.add(drawCard(Color.hearts, Ordinal.two));
    return cards;
  }
}
