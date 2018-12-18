package poker;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

public class PotHandler {

  private List<Pot> pots = Lists.newArrayList();
  private static final Logger logger = LogManager.getLogger(PotHandler.class);

  public PotHandler() {
    pots.add(new Pot());
  }

  public void joinPot(Player player, int raiseCheckValue) {
    int raiseCheckValueLeft = raiseCheckValue;
    int moneyToPot = 0;

    // Do player owe debt to older pot?
    // Pay allIn amounts for all pots but the last one, if markers left put it in last pot
    // if can't afford split pot
    splitPotIfNecessary(pots, raiseCheckValue);
    for (Pot pot : pots) {
      moneyToPot = pot.getHighestAmount();
      if (moneyToPot > raiseCheckValueLeft) {
        splitPot(raiseCheckValueLeft);
        moneyToPot = raiseCheckValueLeft;
      } else if (moneyToPot == 0) {
        putMarkersToPot(pot, player, raiseCheckValueLeft);
        moneyToPot = raiseCheckValueLeft;
      } else {
        putMarkersToPot(pot, player, moneyToPot);
      }
      raiseCheckValueLeft -= moneyToPot;
      if (raiseCheckValueLeft == 0) {
        break;
      }
    }
    if (raiseCheckValueLeft > 0) {
      Pot pot = new Pot();
      pot.addMember(player, raiseCheckValueLeft);
      pots.add(pot);
      Collections.sort(pots);
    }
  }

  private void splitPotIfNecessary(List<Pot> pots, int raiseCheckValue) {
    Pot potToSplit = null;
    int raiseCheckValueLeft = raiseCheckValue;
    for (Pot pot : pots) {
      int checkAmount = pot.getHighestAmount();
      if (raiseCheckValue < checkAmount) {
        logger.debug("Too few markers [{}] for pot [{}], split pot. ", raiseCheckValue, checkAmount);
        potToSplit = pot;
        break;
      }
      raiseCheckValueLeft -= checkAmount;
    }
    if (potToSplit != null) {
      Pot newPot = potToSplit.splitPot(raiseCheckValueLeft);
      pots.add(newPot);
      Collections.sort(pots);
    }
  }

  private void putMarkersToPot(Pot pot,
                               Player player,
                               int raiseCheckValue) {
    final List<Player> members = pot.getMembers();
    if (members.contains(player)) {
      int numberOfMarkersAlreadyInPot = pot.getMarkersForMember(player);
      final int raiseCheckValueForThisRound = raiseCheckValue + numberOfMarkersAlreadyInPot;
      if (pot.getHighestAmount() > raiseCheckValueForThisRound) {
        // can't afford more, go all in and split pot
        Pot newPot = pot.splitPot(raiseCheckValue);
        pots.add(newPot);
      } else {
        logger.debug("Adding [{}] to pot. ", raiseCheckValueForThisRound);
        pot.addMarkersForMember(player, raiseCheckValueForThisRound);
      }
    } else {
      pot.addMember(player, raiseCheckValue);
      logger.debug("Adding new member [{}] to pot. ", player.getName());
    }
  }

  public List<Pot> getPots() {
    return pots;
  }

  public void clear() {
    pots.clear();
    pots.add(new Pot());
  }

  public void splitPot2(int allInAmount) {
    Pot pot = pots.get(getCurrentPot()).splitPot(allInAmount);
    pots.add(pot);
  }

  /*
  use case 1:

  One pot exists with markers 200, player split with 100

  use case 2:

  Two pots exists with markers 200 each, player split with 100

  use case 3:

  Two pots exists with markers 50 and 100, player split with 75
   */
  public void splitPot(int allInAmount) {
    Pot potToSplit = null;
    for (Pot pot : pots) {
      if (pot.getHighestAmount() > allInAmount) {
        potToSplit = pot;
      }
    }
    Pot pot = potToSplit.splitPot(allInAmount);
    pots.add(pot);
  }

  private int getCurrentPot() {
    return pots.size() - 1;
  }

  public int getNumberOfMarkersInAllPots() {
    int numberOfMarkers = 0;
    for (Pot pot : pots) {
      numberOfMarkers += pot.getNumberOfMarkers();
    }
    return numberOfMarkers;
  }

  public Pot getPot(int i) {
    return pots.get(i);
  }

  public int getPlayerPartInPots(Player player) {
    int markersInPot = 0;
    for (Pot pot : pots) {
      if (pot.isMember(player)) {
        markersInPot += pot.getMarkersForMember(player);
      }
    }
    return markersInPot;
  }
}
