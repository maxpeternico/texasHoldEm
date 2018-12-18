package poker;

public class Action {
  private ActionEnum decision;
  private int raiseAmount;

  private int checkValue;

  public Action(ActionEnum decision) {
    this.decision = decision;
  }

  public void setRaiseValue(int value) {
    this.raiseAmount = value;
  }

  public int getRaiseAmount() {
    return raiseAmount;
  }

  public boolean isAllIn() {
    if (decision.equals(ActionEnum.ALL_IN)) {
      return true;
    }
    return false;
  }

  public boolean isRaise() {
    if (decision.equals(ActionEnum.RAISE)) {
      return true;
    }
    return false;
  }

  public boolean isCheck() {
    if (decision.equals(ActionEnum.CHECK)) {
      return true;
    }
    return false;
  }

  public boolean isFold() {
    if (decision.equals(ActionEnum.FOLD)) {
      return true;
    }
    return false;
  }

  public boolean isNotDecided() {
    if (decision.equals(ActionEnum.NOT_DECIDED)) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Action :[" +
            decision +
            ']';
  }

  public void setCheckValue(int checkValue) {
    this.checkValue = checkValue;
  }

  public int getCheckAmount() {
    return checkValue;
  }
}
