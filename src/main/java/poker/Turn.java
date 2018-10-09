package poker;

enum Turn {
  BEFORE_FLOP(0),
  BEFORE_TURN(1),
  BEFORE_RIVER(2),
  END_GAME(3);

  private final int turnValue;

  Turn(int value) {
    this.turnValue = value;
  }
}
