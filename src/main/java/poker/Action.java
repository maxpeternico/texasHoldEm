package poker;

public class Action {
  private ActionEnum decision;
  private int amount;

  public Action(ActionEnum decision) {
    this.decision = decision;
  }

  void setAmount(int value) {
    this.amount = value;
  }

  int getAmount() {
    return amount;
  }

  boolean isAllIn() {
    return decision.equals(ActionEnum.ALL_IN);
  }

  public boolean isRaise() {
    return decision.equals(ActionEnum.RAISE);
  }

  public boolean isCheck() {
    return decision.equals(ActionEnum.CHECK);
  }

  public boolean isFold() {
    return decision.equals(ActionEnum.FOLD);
  }

  public boolean isNotDecided() {
    return decision.equals(ActionEnum.NOT_DECIDED);
  }

  @Override
  public String toString() {
    return "Action :[" +
        decision +
        ']';
  }
}
