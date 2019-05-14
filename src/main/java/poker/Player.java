package poker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
  private int partInPot = 0;
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

  boolean canPay(int amount) {
    return numberOfMarkers - amount >= 0;
  }

  public void decreaseMarkers(int markers) {
    numberOfMarkers = numberOfMarkers - markers;
    if (numberOfMarkers < 0) {
      throw new RuntimeException("Number of markers cannot be negative. ");
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
    int valuedHandInMarkers = valueHandToMarkers(blind);
    setAction(valuedHandInMarkers, maxRaiseFromAPlayer, maxRaiseThisDraw, playersPartInPots);
    logger.debug("Player " + getName() + " decides to :[" + getAction() + "]");
    return getAction();
  }

  int getActionAmount(boolean hasBlindAndIsFirstBetInGame) {
    int amountToPot;
    if (getAction().isAllIn()) {
      amountToPot = getAction().getAmount();
    } else if (getAction().isFold()) {
      amountToPot = 0;
    } else if (hasBlindAndIsFirstBetInGame) {
      amountToPot = getAction().getAmount() - partInPot;
    } else {
      amountToPot = getAction().getAmount();
    }
    partInPot += amountToPot;
    logger.debug("Amount {{}} partInPot {{}}", getAction().getAmount(), partInPot);
    return amountToPot;
  }

  private int getBlindAmount() {
    return blindAmount;
  }

  private void setAction(int valuedHandInMarkers,
                         int maxRaiseFromAPlayerThisRound,
                         int maxRaiseThisDraw,
                         int playersPartInPots) {
    logger.debug(
        "Player :{{}} strategy {{}} valuedHandInMarkers: {{}} maxRaiseFromAPlayerThisRound {{}} maxRaiseThisDraw :{{}}",
        getName(),
        strategy.name(),
        valuedHandInMarkers,
        valuedHandInMarkers,
        maxRaiseThisDraw);

    int finalRaiseAmount;
    valuedHandInMarkers = hasPlayerBlindAndIsDesiredRaiseHigher(valuedHandInMarkers);
    if (isPlayerGoingAllIn(valuedHandInMarkers)) {
      action = new Action(ActionEnum.ALL_IN);
      finalRaiseAmount = getNumberOfMarkers();
    } else if (isPlayerRaising(valuedHandInMarkers, maxRaiseFromAPlayerThisRound)) {
      if (BetManager.shallPayToPot(playersPartInPots, valuedHandInMarkers)) {
        action = new Action(ActionEnum.RAISE);
      } else {
        action = new Action(ActionEnum.CHECK);
      }
      finalRaiseAmount = valuedHandInMarkers;
    } else if (isPlayerChecking(valuedHandInMarkers, maxRaiseFromAPlayerThisRound)) {
      action = new Action(ActionEnum.CHECK);
      if (maxRaiseFromAPlayerThisRound > getNumberOfMarkers()) {
        System.out.println("Player {" + getName() + "} cannot afford check, has to go ALL IN. ");
        action = new Action(ActionEnum.ALL_IN);
        finalRaiseAmount = getNumberOfMarkers();
      } else {
        finalRaiseAmount = maxRaiseFromAPlayerThisRound;
      }
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
  }

  private boolean isPlayerRaising(int valuedHandInMarkers, int maxRaiseFromAPlayerThisRound) {
    return valuedHandInMarkers > maxRaiseFromAPlayerThisRound;
  }

  protected abstract int hasPlayerBlindAndIsDesiredRaiseHigher(int desiredRaiseAmount);

  protected abstract boolean isPlayerChecking(int desiredRaiseAmount, int maxRaiseFromAPlayerThisRound);

  private boolean noRaiseThisDraw(int maxRaiseThisDraw) {
    logger.debug("Raise this draw: {{}}", maxRaiseThisDraw);
    if (maxRaiseThisDraw == 0) return true;

    if (partInPot == maxRaiseThisDraw) return true;

    return false;
  }

  private int valueHandToMarkers(int blind) {
    int individualRaiseAmount = 0;

    // Depending on strategy, pot and blind
    // low blind, offensive raises more, the rest joins
    // medium blind, offensive raises more, join joins, join if cheap drops
    // high blind offensive sets rise as blind, rest folds
    switch (strategy) {
      case ALL_IN:
        individualRaiseAmount = getNumberOfMarkers();
        break;
      case OFFENSIVE:
        individualRaiseAmount = setOffensiveRaiseAmount(blind);
        break;
      case JOIN:
        individualRaiseAmount = setJoinRaiseAmount(blind);
        break;
      case JOIN_IF_CHEAP:
        individualRaiseAmount = setJoinIfCheapRaiseAmount(blind);
        break;
      default:
        break;
    }
    if (individualRaiseAmount > getNumberOfMarkers()) {
      individualRaiseAmount = getNumberOfMarkers();
    }
    logger.debug(getName() + " getAmount amount: " + individualRaiseAmount);
    return individualRaiseAmount;
  }

  protected abstract int setJoinIfCheapRaiseAmount(int blind);

  protected abstract int setJoinRaiseAmount(int blind);

  protected abstract int setOffensiveRaiseAmount(int blind);

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
      logger.debug("Player {{}} is ALL IN. ", getName());
      return true;
    }
    return false;
  }

  // If player has no more markers player need to go all in
  private boolean isPlayerGoingAllIn(int calculatedRaiseAmount) {
    if (strategy.equals(ALL_IN) || doPlayerNeedToGoAllIn(calculatedRaiseAmount)) {
      logger.debug("Player {{}} goes ALL_IN", getName());
      return true;
    }
    return false;
  }

  private boolean doPlayerNeedToGoAllIn(int amount) {
    logger.trace("Player {{}} has {{}} markers and considers amount {{}}", getName(), numberOfMarkers, amount);

    return amount >= numberOfMarkers;
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

  void setPartInPot(int amount) {
    partInPot = amount;
  }
}

