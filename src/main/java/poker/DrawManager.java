package poker;

public class DrawManager {

  private Draw draw;

  public DrawManager() {
    this.draw = Draw.BEFORE_FLOP;
  }

  public Draw getDraw() {
    return draw;
  }

  public void increaseDraw() {
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
