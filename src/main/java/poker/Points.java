package poker;

public class Points {
  int privatePoints;
  int commonPoints;

  public int toInt() {
    return privatePoints + commonPoints;
  }
}
