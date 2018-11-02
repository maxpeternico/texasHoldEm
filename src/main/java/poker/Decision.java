package poker;

public class Decision {
  private DecisionEnum decision;
  private int eventualRaiseAmount;

  public Decision(DecisionEnum decision) {
    this.decision = decision;
  }

  public void setRaiseValue(int value) {
    this.eventualRaiseAmount = value;
  }

  public int getRaiseAmount() {
    return eventualRaiseAmount;
  }

  public boolean isRaise() {
    if (decision.equals(DecisionEnum.RAISE)) {
      return true;
    }
    return false;
  }

  public boolean isCheck() {
    if (decision.equals(DecisionEnum.CHECK)) {
      return true;
    }
    return false;
  }

  public boolean isFold() {
    if (decision.equals(DecisionEnum.FOLD)) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Decision :[" +
            decision +
            ']';
  }
}
