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

  public static Draw increaseDraw(Draw draw) {
    switch (draw) {
      case BEFORE_FLOP:
        return Draw.FLOP;
      case FLOP:
        return Draw.TURN;
      case TURN:
         return Draw.RIVER;
      case RIVER:
        return Draw.BEFORE_FLOP;
    }
    return null;
  }
}
