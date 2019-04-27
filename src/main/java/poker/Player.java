package poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static poker.Strategy.ALL_IN;

public abstract class Player {

  private String name;
  private int numberOfMarkers = 0;

  private static final Logger logger = LogManager.getLogger(Player.class);

  private List<Card> cardsOnHand = new ArrayList<>();
  private boolean bigBlind = false;
  private boolean littleBlind = false;
  Strategy strategy = Strategy.NOT_DECIDED;
  protected Action action = new Action(ActionEnum.NOT_DECIDED);
  protected int partInPot = 0;
  Action previousAction = new Action(ActionEnum.NOT_DECIDED);
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
    return 0;
  }

  @Override
  public String toString() {
    return "Player [name=" + name + "]";
  }

  void addPrivateCards(List<Card> newCards) {
    cardsOnHand.addAll(newCards);
  }

  Map<Card, PokerResult> evaluateHand(List<Card> commonHand) {
    List<Card> totalHand = new ArrayList<>();
    totalHand.addAll(cardsOnHand);
    totalHand.addAll(commonHand);
    logger.trace("[" + name + "]:s total hand is [" + EvaluationHandler.getHandAsString(totalHand) + "]");
    return EvaluationHandler.evaluateHand(name, totalHand);
  }

  List<Card> getPrivateHand() {
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
    if (obj == null) return false;
    if (getClass() != obj.getClass()) {
      return false;
    }
    Player other = (Player) obj;
    if (name == null) {
      return other.name == null;
    } else return name.equals(other.name);
  }

  public int getNumberOfMarkers() {
    return numberOfMarkers;
  }

  public boolean hasBlind() {
    if (hasBigBlind()) {
      return true;
    }
    return hasLittleBlind();
  }

  public boolean hasLittleBlind() {
    return littleBlind;
  }

  public boolean hasBigBlind() {
    return bigBlind;
  }

  void setLittleBlind(int blindAmount) {
    this.littleBlind = true;
    this.blindAmount = blindAmount;
  }

  void clearLittleBlind() {
    littleBlind = false;
  }

  void setBigBlind(int blindAmount) {
    bigBlind = true;
    this.blindAmount = blindAmount;
  }

  void clearBigBlind() {
    bigBlind = false;
  }

  boolean canPay(int pot) {
    return numberOfMarkers - pot >= 0;
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
    logger.trace("Player {{}} has {{}} number of markers. ", getName(), numberOfMarkers);
    return numberOfMarkers > 0;
  }

  boolean isDesiredRaiseAmountHigherThanBlind(long amount, int blind) {
    if (amount < blind) {
      System.out.println("You must raise more than blind");
      return false;
    }
    if (hasMarkersForAmount(amount)) {
      System.out.println("You don't have makers for :[" + amount + "]. Number of markers :[" + numberOfMarkers + "]");
      return false;
    }
    return true;
  }

  boolean hasMarkersForAmount(long amount) {
    return amount > numberOfMarkers;
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

  int getActionAmount(boolean isBeforeFlop) {
    if (getAction().isAllIn()) {
      return getAction().getAmount();
    }
    if (getAction().isFold()) {
      return 0;
    }
    if (!isBeforeFlop) {
      return getAction().getAmount();
    }
    // TODO: This is already compensated at BetManager.payToPot(). Should not be done in two places
    if (hasBlind()) {
      logger.trace("{{}} has blind. ", getName());
      if (getAction().getAmount() == 0) {
        return getBlindAmount();
      }
      if (getAction().getAmount() >= getBlindAmount()) {
        logger.trace("Amount: {{}} blindAmount {{}}", getAction().getAmount(), getBlindAmount());
        return getAction().getAmount() - getBlindAmount();
      }
      if (!getAction().isAllIn()) {
        throw new RuntimeException("Action amount [" + getAction().getAmount() + " must be higher than big blind amount [" + getBlindAmount() + "]");
      }
    }
    return getAction().getAmount() - partInPot;
  }

  private int getBlindAmount() {
    return blindAmount;
  }

  protected void setAction(int desiredRaiseAmount,
                                     int maxRaiseFromAPlayerThisRound,
                                     int maxRaiseThisDraw,
                                     int playersPartInPots) {
    logger.debug("Player :[" + getName() + "] desiredRaiseAmount: [" + desiredRaiseAmount + "] maxRaiseFromAPlayerThisRound :[" + maxRaiseFromAPlayerThisRound + "] maxRaiseThisDraw :[" + maxRaiseThisDraw + "]");
    int finalRaiseAmount;
    desiredRaiseAmount = hasPlayerBlindAndIsDesiredRaiseHigher(desiredRaiseAmount);
    if (hasToGoAllIn(desiredRaiseAmount)) {
      finalRaiseAmount = goAllIn();
    } else if (desiredRaiseAmount > maxRaiseFromAPlayerThisRound) {
      if (BetManager.shallPayToPot(playersPartInPots, desiredRaiseAmount)) {
        action = new Action(ActionEnum.RAISE);
      } else {
        action = new Action(ActionEnum.CHECK);
      }
      finalRaiseAmount = desiredRaiseAmount;
    } else if (isCheckSelected(desiredRaiseAmount, maxRaiseFromAPlayerThisRound)) {
      action = new Action(ActionEnum.CHECK);
      finalRaiseAmount = maxRaiseFromAPlayerThisRound;
    } else {
      // If no one is raises there is no need to fold
      if (noRaiseThisDraw(maxRaiseThisDraw)) {
        logger.trace("No raise this draw. ");
        action = new Action(ActionEnum.CHECK);
      } else {
        action = new Action(ActionEnum.FOLD);
      }
      finalRaiseAmount = 0;
    }

    logger.trace("Set raise amount for player {{}} to {{}}", getName(), finalRaiseAmount);
    action.setAmount(finalRaiseAmount);
    partInPot += action.getAmount();
  }

  protected abstract int hasPlayerBlindAndIsDesiredRaiseHigher(int desiredRaiseAmount);

  protected abstract boolean isCheckSelected(int desiredRaiseAmount, int maxRaiseFromAPlayerThisRound);

  protected abstract int setAction2(int raiseAmount,
                                    int maxRaiseFromAPlayer,
                                    int maxRaiseThisDraw,
                                    int playersPartInPots);

  protected boolean noRaiseThisDraw(int maxRaiseThisDraw) {
    logger.debug("Raise this draw: {{}}", maxRaiseThisDraw);
    return maxRaiseThisDraw == 0;
  }

  protected abstract int calculateRaiseAmount(int blind);

  void addMarkers(int markers) {
    numberOfMarkers = numberOfMarkers + markers;
    logger.debug("Player :[" + getName() + "] gets :[" + markers + "]. Total number of markers :[" + numberOfMarkers + "]. ");
  }

  boolean hasFolded() {
    if (action.isFold()) {
      logger.debug("Player {{}} has folded. ", getName());
      return true;
    }
    return false;
  }

  public boolean isAllIn() {
    if (action.isAllIn()) {
      logger.debug("Player {{}} is all in. ", getName());
      return true;
    }
    return false;
  }

  // If player has no more markers player need to go all in
  boolean hasToGoAllIn(int calculatedRaiseAmount) {
    return strategy.equals(ALL_IN) || doPlayerNeedToGoAllIn(calculatedRaiseAmount);
  }

  private boolean doPlayerNeedToGoAllIn(int amount) {
    return amount >= numberOfMarkers;
  }

  int goAllIn() {
    int finalRaiseAmount;
    action = new Action(ActionEnum.ALL_IN);
    finalRaiseAmount = getNumberOfMarkers();
    return finalRaiseAmount;
  }

  boolean hasNotDecided() {
    return getAction().isNotDecided();
  }

  void setActionToNotDecided() {
    action = new Action(ActionEnum.NOT_DECIDED);
  }

  void setActionToCheck() {
    action = new Action(ActionEnum.CHECK);
  }

  int calculateRaiseAmountIncludingBlind(int raiseAmount) {
    if (hasBigBlind()) {
      return blindAmount + raiseAmount;
    }
    if (hasLittleBlind()) {
      return blindAmount / 2 + raiseAmount;
    }
    return raiseAmount;
  }

  void removeCardsFromHand() {
    cardsOnHand.clear();
  }
}

