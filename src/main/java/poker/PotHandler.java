package poker;

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

  /*
  Use case 1: 450 markers, 4 pots, size 100, 200, 50, 100. Player checks 450. Pays to all pots

  Use case 2: 450 markers, 4 pots, size 100, 200, 50, 100. Player goes all in on 310. Pay 100 to first pot, 200 to second pot and split 3rd pot to 10 and 40.
  Result: 5 pots 100, 200, 10, 40, 50, 100
   */

  public void joinPot(Player player, int joinAmount) {
    int joinAmountLeft = joinAmount;
    int amountToJoinPot = 0;

    Pot potToSplit = null;
    // Pay highest amount in pot until joinAmountLeft is 0
    for (Pot pot : pots) {
      amountToJoinPot = pot.getHighestAmount();
      if (pots.indexOf(pot) == pots.size() - 1) {
        if (canJoinPot(joinAmountLeft, amountToJoinPot)) {
          putMarkersToPot(pot, player, joinAmountLeft);
        } else {
          putMarkersToPot(pot, player, joinAmountLeft);
          potToSplit = setPotToSplit(joinAmountLeft, amountToJoinPot, pot, player);
          break;
        }
      } else {
        if (amountToJoinPot > joinAmountLeft) {
          putMarkersToPot(pot, player, joinAmountLeft);
          potToSplit = setPotToSplit(joinAmountLeft, amountToJoinPot, pot, player);
          break;
        }
        if (amountToJoinPot == 0) {
          putMarkersToPot(pot, player, joinAmountLeft);
          amountToJoinPot = joinAmountLeft;
        } else {
          putMarkersToPot(pot, player, amountToJoinPot);
        }
        joinAmountLeft -= amountToJoinPot;
        if (joinAmountLeft == 0) {
          break;
        }
      }
    }
    if (isPotSplit(potToSplit)) {
      Pot newPot = potToSplit.splitPot(joinAmountLeft);
      // Sort new pot after old pot in list
      pots.add(new Pot());
      int i;
      System.out.println(pots.size());
      System.out.println(pots.indexOf(potToSplit));
      /*
      Use case 1, One pot has been split, pot 0 is old pot, new pot shall be put at position 1
      pots.size() = 2, start condition i=1, end condition i==1 index of pot to split = 0

      Use case 1, 3 pots (0-2), pot 1 has been split, pot 1 is old pot, new pot shall be put at position 2. pot 2 shall be put to position 3
      pots.size() = 3, start condition i=3, end condition i==2 index of pot to split = 2
      Mot

       */
      for (i = pots.size()-1; i > pots.indexOf(potToSplit) + 1; i--) {
        pots.set(i, pots.get(i - 1));
        logger.debug("Moving pot [{}] from position [{}] to position [{}]", pots.get(i-1), i-1, i);
      }
      pots.set(i, newPot);
    }
  }

  private Pot setPotToSplit(int raiseCheckValueLeft, int amountToJoinPot, Pot pot, Player player) {
    Pot potToSplit;
    logger.debug("Player [{}] can't afford pot, has [{}] markers need [{}]. Split pot. ", player.getName(), raiseCheckValueLeft, amountToJoinPot);
    potToSplit = pot;
    return potToSplit;
  }

  private boolean canJoinPot(int numberOfMarkers, int amountToJoinPot) {
    if (amountToJoinPot == 0) {
      return true;
    }
    if (numberOfMarkers >= amountToJoinPot) {
      return true;
    }
    return false;
  }

  private boolean isPotSplit(Pot potToSplit) {
    return potToSplit != null;
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
    if (isPotSplit(potToSplit)) {
      pots.add(potToSplit.splitPot(raiseCheckValueLeft));
    }
  }

  private void putMarkersToPot(Pot pot, Player player, int markers) {
    logger.debug("Adding [{}] with [{}] markers to pot [{}]. ", player.getName(), markers, pots.indexOf(pot));
    if (pot.hasMember(player)) {
      markers += pot.getMarkersForMember(player);
      pot.addMarkersForMember(player, markers);
    } else {
      pot.addMember(player, markers);
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
    pots.add(pots.get(getCurrentPot()).splitPot(allInAmount));
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
    pots.add(potToSplit.splitPot(allInAmount));
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
      if (pot.hasMember(player)) {
        markersInPot += pot.getMarkersForMember(player);
      }
    }
    return markersInPot;
  }
}
