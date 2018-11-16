package poker;

public class Points {
  int totalPoints;
  int privatePoints;
  int commonPoints;

  public int toInt() {
    return privatePoints + commonPoints;
  }
}
