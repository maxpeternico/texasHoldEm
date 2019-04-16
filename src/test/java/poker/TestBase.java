package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;

public abstract class TestBase {
  protected abstract PokerGame getPokerGame();

  void assertMarkersForPlayers(List<Player> players) {
    for (Player player : players) {
      final int playersPartInPot = getPokerGame().getPotHandler().getPlayerPartInPots(player);
      assertEquals("Number of markers not correct for player :[" + player.getName() + "] playersPartInPot :[" + playersPartInPot + "]",
                   PokerGame.TOTAL_MARKERS_PER_PLAYER - playersPartInPot,
                   player.getNumberOfMarkers());
    }
  }

  String createIncorrectNumberOfMarkersForWinnerMessage(int potRaisePerPlayerTotalRound, int calculatedPot, Player player) {
    return "Incorrect number of markers for player :[" + player.getName() + "], potRaisePerPlayerTotalRound :[" + potRaisePerPlayerTotalRound + "] calculated pot :[" + calculatedPot + "]";
  }

  String createMarkersDisappearErrorMessage(List<Player> players) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Pot size not equal. Number of markers for players :");
    for (Player player:players) {
      stringBuilder.append("[").append(player.getNumberOfMarkers()).append("]");
    }
    return stringBuilder.toString();
  }


  int calculatePot(int totalPotRaisePerPlayer, List<Player> players) {
    int totalPot = 0;
    for (Player player : players) {
      if (!player.getAction().isFold()) {
        totalPot += totalPotRaisePerPlayer;
      }
    }
    return totalPot;
  }

  void prepareBeforeFlop(List<Player> players,
                                   int blindAmount,
                                   List<List<Card>> privateHands) {
    getPokerGame().initBlinds(players);
    getPokerGame().payBlinds(players, getPokerGame().playersThatCanBet(players), blindAmount);
    for (int i=0;i < players.size();i++) {
      getPokerGame().setPrivateHand(players.get(i), privateHands.get(i));
    }
  }

  void prepareFlop(List<Card> flopCards) {
    getPokerGame().increaseDrawForTest();
    getPokerGame().addToCommonHand(flopCards);
  }

  void prepareRiver(List<Card> riverCard) {
    getPokerGame().increaseDrawForTest();
    prepareDraw(riverCard.get(0).getColor(), riverCard.get(0).getOrdinal());
  }

  void prepareTurn(List<Card> turnCard) {
    getPokerGame().increaseDrawForTest();
    prepareDraw(turnCard.get(0).getColor(), turnCard.get(0).getOrdinal());
  }

  private void prepareDraw(Color hearts, Ordinal queen) {
    getPokerGame().increaseDrawForTest();
    getPokerGame().addToCommonHand(Collections.singletonList(drawCard(hearts, queen)));
  }

  List<Card> drawPairOfKnightsNegative() {
    final List<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawPairOfKnightsAtFlop() {
    final List<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.spades, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawPairOfKnights() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.knight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.knight);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawPairOfEights1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.eight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.eight);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawPairOfEights2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.eight);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.eight);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawPairOfNinesAtFlop() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.nine);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawBadPrivateHand2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.knight);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawBadPrivateHand1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.clubs, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.diamonds, Ordinal.ten);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawKingAndQueenOfDifferentColor() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.king);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.queen);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawPairOfAces1() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.hearts, Ordinal.ace);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.spades, Ordinal.ace);
    hand.add(aceOfSpades);
    return hand;
  }

  List<Card> drawPairOfAces2() {
    final ArrayList<Card> hand = Lists.newArrayList();
    final Card aceOfHearts = drawCard(Color.diamonds, Ordinal.ace);
    hand.add(aceOfHearts);
    final Card aceOfSpades = drawCard(Color.clubs, Ordinal.ace);
    hand.add(aceOfSpades);
    return hand;
  }

  Card drawCard(Color color, Ordinal ordinal) {
    return new Card(color, ordinal);
  }

  List<Card> getBadFlop() {
    final ArrayList<Card> cards = Lists.newArrayList();
    cards.add(drawCard(Color.diamonds, Ordinal.three));
    cards.add(drawCard(Color.clubs, Ordinal.nine));
    cards.add(drawCard(Color.hearts, Ordinal.two));
    return cards;
  }

  List<Card> getFlopWithNineAndKnight() {
    final ArrayList<Card> cards = Lists.newArrayList();
    cards.add(drawCard(Color.diamonds, Ordinal.knight));
    cards.add(drawCard(Color.clubs, Ordinal.nine));
    cards.add(drawCard(Color.hearts, Ordinal.two));
    return cards;
  }
}
