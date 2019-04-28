package poker;

enum Ordinal {
  two(2),
  three(3),
  four(4),
  five(5),
  six(6),
  seven(7),
  eight(8),
  nine(9),
  ten(10),
  knight(11),
  queen(12),
  king(13),
  ace(14);

  private int value;

  Ordinal(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    if (getValue() == 11) {
      return "J";
    }
    if (getValue() == 12) {
      return "Q";
    }
    if (getValue() == 13) {
      return "K";
    }
    if (getValue() == 14) {
      return "A";
    }
    return Integer.toString(value);
  }
}
