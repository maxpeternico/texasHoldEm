package poker;

@SuppressWarnings("javadoc")
public enum PokerHand {
  ROYAL_STRAIGHT_FLUSH(900),
  STRAIGHT_FLUSH(800),
  FOURS(700),
  FULL_HOUSE(600),
  FLUSH(500),
  STRAIGHT(400),
  THREES(300),
  TWO_PAIR(200),
  PAIR(100),
  NO_RESULT(0);

  private int value;

  PokerHand(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }
}


