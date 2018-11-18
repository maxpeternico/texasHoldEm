package poker;

enum Draw {
  BEFORE_FLOP(0),
  FLOP(1),
  TURN(2),
  RIVER(3);

  private final int drawValue;

  Draw(int value) {
    this.drawValue = value;
  }
}
