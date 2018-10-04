package poker;

public class PokerResult {
  private final PokerHand pokerHand;
  // Points are only used for rating pair/no_results
  private int points = 0;

  public PokerResult(PokerHand pokerHand, int points) {
    this.pokerHand = pokerHand;
    this.points = points;
  }

  public PokerResult(PokerHand pokerHand) {
    this.pokerHand = pokerHand;
    this.points = pokerHand.getValue();
  }

  public PokerHand getPokerHand() {
    return pokerHand;
  }

  public int getPoints() {
    return pokerHand.getValue() + points;
  }
}
