package poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Player {

  private String name;
  private int numberOfMarkers = 0;

  private static final Logger logger = LogManager.getLogger(Player.class);

  private List<Card> cardsOnHand = new ArrayList<Card>();
  private int i = 0;
  private boolean bigBlind = false;
  private boolean littleBlind = false;
  Strategy strategy = Strategy.NOT_DECIDED;
  protected Action action = new Action(ActionEnum.NOT_DECIDED);
  protected int partInPot = 0;
  protected Action previousAction = new Action(ActionEnum.NOT_DECIDED);
  int blindAmount = 0;

  public Action getAction() {
    return action;
  }

  public Player(String playerName, int totalMarkersPerPlayer) {
    this.name = playerName;
    this.numberOfMarkers = totalMarkersPerPlayer;
  }

  Player(String name) {
    this.name = name;
  }

  public int getInt() {
    return this.i;
  }

  @Override
  public String toString() {
    return "Player [name=" + name + "]";
  }

  public void addPrivateCards(List<Card> newCards) {
    cardsOnHand.addAll(newCards);
  }

  public Map<Card, PokerResult> evaluateHand(List<Card> commonHand) {
    List<Card> totalHand = new ArrayList<Card>();
    totalHand.addAll(cardsOnHand);
    totalHand.addAll(commonHand);
    logger.trace("[" + name + "]:s total hand is [" + EvaluationHandler.getHandAsString(totalHand) + "]");
    Map<Card, PokerResult> result = EvaluationHandler.evaluateHand(name, totalHand);
    return result;
  }

  public List<Card> getPrivateHand() {
    return this.cardsOnHand;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Player other = (Player) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  public int getNumberOfMarkers() {
    return numberOfMarkers;
  }

  public boolean hasBlind() {
    if (hasBigBlind()) {
      return true;
    }
    if (hasLittleBlind()) {
      return true;
    }
    return false;
  }

  public boolean hasLittleBlind() {
    return littleBlind;
  }

  public boolean hasBigBlind() {
    return bigBlind;
  }

  public void setLittleBlind(int blindAmount) {
    this.littleBlind = true;
    this.blindAmount = blindAmount;
  }

  public void clearLittleBlind() {
    littleBlind = false;
  }

  public void setBigBlind(int blindAmount) {
    bigBlind = true;
    this.blindAmount = blindAmount;
  }

  public void clearBigBlind() {
    bigBlind = false;
  }

  public boolean canPay(int pot) {
    if (numberOfMarkers - pot < 0) {
      return false;
    }
    return true;
  }

  public void decreaseMarkers(int markers) {
    numberOfMarkers = numberOfMarkers - markers;
    if (numberOfMarkers < 0) {
      logger.debug("Player :[" + getName() + "] is broke!");
      numberOfMarkers = 0;
    }
    logger.debug("Decrease [" + markers + "] for :[" + getName() + "]. Total number of markers :[" + numberOfMarkers + "]");
  }

  public boolean hasAnyMarkers() {
    logger.trace("Player {{}} has {{}}} number of markers. ", getName(), numberOfMarkers);
    if (numberOfMarkers > 0) {
      return true;
    }
    return false;
  }

  protected boolean isDesiredRaiseAmountHigherThanBlind(int amount, int blind) {
    if (amount < blind) {
      System.out.println("You must raise more than blind");
      return false;
    }
    if (!hasMarkersForAmount(amount)) {
      System.out.println("You don't have makers for :[" + amount + "]");
      return false;
    }
    return true;
  }

  public boolean hasMarkersForAmount(int amount) {
    if (amount > numberOfMarkers) {
      return false;
    }
    return true;
  }

  public boolean needToGoAllIn(int amount) {
    if (amount >= numberOfMarkers) {
      return true;
    }
    return false;
  }

  public abstract void decideStrategy(Draw draw, int numberOfRemainingPlayers, List<Card> commonHand);

  public Action decideAction(Draw draw,
                             int numberOfRemainingPlayers,
                             List<Card> commonHand,
                             int blind,
                             int maxRaiseFromAPlayer,
                             int maxRaiseThisDraw,
                             int playersPartInPots) {
    decideStrategy(draw, numberOfRemainingPlayers, commonHand);
    int individualRaiseAmount = calculateRaiseAmount(blind);
    setAction(individualRaiseAmount, maxRaiseFromAPlayer, maxRaiseThisDraw, playersPartInPots);
    logger.debug("Player " + getName() + " decides to :[" + getAction() + "]");
    return getAction();
  }

  public int getActionAmount(boolean isBeforeFlop) {
    if (getAction().isAllIn()) {
      return getAction().getAmount();
    }
    if (!isBeforeFlop) {
      return getAction().getAmount();
    }
    if (hasBlind()) {
      logger.trace("{{}} has blind. isBeforeFrop {{}}", getName(), isBeforeFlop == true);
      if (getAction().getAmount() == 0) {
        return getBlindAmount();
      }
      if (getAction().getAmount() >= getBlindAmount()) {
        logger.trace("Amount: {{}} blindAmount", getBlindAmount());
        return getAction().getAmount() - getBlindAmount();
      }
      if (!getAction().isAllIn()) {
        throw new RuntimeException("Action amount [" + getAction().getAmount() + " must be higher than big blind amount [" + getBlindAmount() + "]");
      }
    }
    if (getAction().isFold()) {
      return 0;
    }
    return getAction().getAmount();
  }

  protected int getBlindAmount() {
    return blindAmount;
//    if (hasLittleBlind()) {
//      return blindAmount / 2;
//    }
//    if (hasBigBlind()) {
//      return blindAmount;
//    }
//    return 0;
  }

  protected abstract void setAction(int raiseAmount,
                                    int maxRaiseFromAPlayer,
                                    int maxRaiseThisDraw,
                                    int playersPartInPots);

  protected abstract int calculateRaiseAmount(int blind);

  public void addMarkers(int markers) {
    numberOfMarkers = numberOfMarkers + markers;
    logger.debug("Player :[" + getName() + "] gets :[" + markers + "]. Total number of markers :[" + numberOfMarkers + "]. ");
  }

  boolean hasFolded() {
    return action.isFold();
  }

  public boolean isAllIn() {
    if (action.isAllIn()) {
      return true;
    }
    return false;
  }

  public boolean hasNotDecided() {
    return getAction().isNotDecided();
  }

  void setActionToNotDecided() {
    action = new Action(ActionEnum.NOT_DECIDED);
  }

  void setActionToCheck() {
    action = new Action(ActionEnum.CHECK);
  }

  public int calculateRaiseAmountIncludingBlind(int raiseAmount) {
    if (hasBigBlind()) {
      return blindAmount + raiseAmount;
    }
    if (hasLittleBlind()) {
      return blindAmount / 2 + raiseAmount;
    }
    return raiseAmount;
  }

  public void removeCardsFromHand() {
    cardsOnHand.clear();
  }
}

