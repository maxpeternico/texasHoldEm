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

  public void joinPot(Player player, int raiseCheckValue) {
    int raiseCheckValueLeft = raiseCheckValue;
    int moneyToPot = 0;

    Pot potToSplit = new Pot();
    for (Pot pot : pots) {
      // Pay highest amount in pot until raiseCheckValueLeft is 0
      if (pots.indexOf(pot) == pots.size() - 1) {
        if (raiseCheckValueLeft >= pot.getHighestAmount()) {
          putMarkersToPot(pot, player, raiseCheckValueLeft);
        } else {
          potToSplit = pot;
        }
        break;
      }
      moneyToPot = pot.getHighestAmount();
      if (moneyToPot > raiseCheckValueLeft) {
        potToSplit = pot;
      }
      if (moneyToPot == 0) {
        // Pot is empty, add rest of markers to pot
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
      pots.add(potToSplit.splitPot(raiseCheckValueLeft));
    }
  }

  private void putMarkersToPot(Pot pot,
                               Player player,
                               int raiseCheckValueLeft) {
    logger.debug("Adding [{}] to pot [{}]. ", raiseCheckValueLeft, pots.indexOf(pot));
    pot.addMarkersForMember(player, raiseCheckValueLeft);
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
      if (pot.isMember(player)) {
        markersInPot += pot.getMarkersForMember(player);
      }
    }
    return markersInPot;
  }
}
