package poker;

class DrawManager {

  private Draw draw;

  DrawManager() {
    this.draw = Draw.BEFORE_FLOP;
  }

  Draw getDraw() {
    return draw;
  }

  void increaseDraw() {
    switch (draw) {
      case BEFORE_FLOP:
        draw = Draw.FLOP;
        break;
      case FLOP:
        draw = Draw.TURN;
        break;
      case TURN:
        draw = Draw.RIVER;
        break;
      case RIVER:
        break;
    }
  }
}
