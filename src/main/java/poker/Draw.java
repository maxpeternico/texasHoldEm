package poker;

enum Draw {
  BEFORE_FLOP(0),
  BEFORE_TURN(1),
  BEFORE_RIVER(2),
  END_GAME(3);

  private final int drawValue;

  Draw(int value) {
    this.drawValue = value;
  }
}
