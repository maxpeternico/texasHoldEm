package poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public abstract class Player {
  private String name;
  private int numberOfMarkers = 0;

  private static final Logger logger = LogManager.getLogger(Player.class);

  private List<Card> cardsOnHand = new ArrayList<Card>();
  private int i = 0;
  private boolean bigBlind = false;
  private boolean littleBlind = false;
  Strategy strategy = Strategy.NOT_DECIDED;

  public Player(String playerName, int totalMarkersPerPlayer) {
    this.name = playerName;
    this.numberOfMarkers = totalMarkersPerPlayer;
  }

  Player(String name) {
    this.name = name;
  }

  public void setInt() {
    this.i++;
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Player other = (Player) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
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

  public void setLittleBlind(int blind) {
    littleBlind = true;
    numberOfMarkers = numberOfMarkers - blind / 2;
  }

  public void clearLittleBlind() {
    littleBlind = false;
  }

  public void setBigBlind(int blind) {
    bigBlind = true;
    numberOfMarkers = numberOfMarkers - blind;
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
      throw new RuntimeException("Number of markers should never be negative.");
    }
  }

  public boolean hasAnyMarkers() {
    if (numberOfMarkers >= 0) {
      return true;
    }
    return false;
  }

  protected boolean isDesiredRaiseAmountHigherThanBlind(int amount, int blind) {
    if (amount > blind) {
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

  public abstract void decideStrategy(Turn turn, int numberOfRemainingPlayers, List<Card> commonHand, int blind);

  public Decision getDecision(Turn turn, int numberOfRemainingPlayers, List<Card> commonHand, int blind, int maxRaiseFromOtherPlayer) {
    decideStrategy(turn, numberOfRemainingPlayers, commonHand, blind);
    Decision decision = null;
    switch (strategy) {
      case ALL_IN:
      case OFFENSIVE:
        final int raiseAmount = calculateRaiseAmount(blind, maxRaiseFromOtherPlayer);
        if (raiseAmount > blind) {
          decision = new Decision(DecisionEnum.RAISE);
          decision.setRaiseValue(raiseAmount);
        } else {
          decision = new Decision(DecisionEnum.CHECK);
        }
        break;
      case JOIN:
        decision = new Decision(DecisionEnum.CHECK);
        break;
      case JOIN_IF_CHEAP:
        if (hasBigBlind()) {
          decision = new Decision(DecisionEnum.CHECK);
        }
        break;
    default:
        decision = new Decision(DecisionEnum.FOLD);
      break;
    }
    logger.debug("Player " + getName() + " decides to :[" + decision + "]");
    return decision;
  }

  protected abstract int calculateRaiseAmount(int blind, int maxRaiseFromOtherplayer);

  public void addMarkers(int pot) {
    numberOfMarkers = numberOfMarkers + pot;
  }
}
