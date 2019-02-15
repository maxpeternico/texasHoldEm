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
      int markersPaidToPot = 0;
      amountToJoinPot = getAmountToJoinPot(joinAmount, pot);
      final int potIndex = pots.indexOf(pot);
      final int numberOfPots = pots.size() - 1;
      if (isLatestPot(potIndex, numberOfPots)) {
        logger.trace("Is latest pot");
        final int numberOfMarkersForPlayer = player.getNumberOfMarkers();
        if (canJoinPot(numberOfMarkersForPlayer, amountToJoinPot)) {
          putMarkersToPot(pot, player, amountToJoinPot);
          markersPaidToPot = amountToJoinPot;
        } else {
          putMarkersToPot(pot, player, numberOfMarkersForPlayer);
          potToSplit = setPotToSplit(numberOfMarkersForPlayer, amountToJoinPot, pot, player);
          break;
        }
      } else {
        if (amountToJoinPot > joinAmountLeft) {
          putMarkersToPot(pot, player, joinAmountLeft);
          potToSplit = setPotToSplit(joinAmountLeft, amountToJoinPot, pot, player);
          break;
        }
        if (amountToJoinPot != 0) {
          putMarkersToPot(pot, player, amountToJoinPot);
          markersPaidToPot = amountToJoinPot;
        }
      }
      joinAmountLeft -= markersPaidToPot;
      if (joinAmountLeft == 0) {
        break;
      }
      logger.debug("Join amount left [{}]", joinAmountLeft);
    }
    if (joinAmountLeft > 0) {
      final Pot pot = new Pot();
      pot.addMember(player, joinAmountLeft);
      pots.add(pot);
    }
    if (isPotSplit(potToSplit)) {
      int splitAmount = joinAmountLeft;
      do {
        createNewPot(splitAmount, potToSplit);
        // Old pot may have been even
        splitAmount = getEventualNewSplitValueFromOldPot(potToSplit);
      } while (splitAmount != 0);
    }
  }

  private int getAmountToJoinPot(int joinAmount, Pot pot) {
    int amountToJoinPot;
    amountToJoinPot = pot.getHighestAmount();
    if (amountToJoinPot == 0) {
      amountToJoinPot = joinAmount;
    }
    return amountToJoinPot;
  }

  private boolean isLatestPot(int potIndex, int numberOfPots) {
    return potIndex == numberOfPots;
  }

  private int getEventualNewSplitValueFromOldPot(Pot pot) {
    final int highestAmount = pot.getHighestAmount();
    for (Player player:pot.getMembers()) {
      final int markersForMember = pot.getMarkersForMember(player);
      if (markersForMember != highestAmount) {
        return markersForMember;
      }
    }
    return 0;
  }

  private void createNewPot(int joinAmountLeft, Pot potToSplit) {
    Pot newPot = potToSplit.splitPot(joinAmountLeft);
    // Sort new pot after old pot in list
    pots.add(new Pot());
    int i;
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

  private int previouslyPaied(Player player, Pot pot) {
    if (!pot.hasMember(player)) {
      return 0;
    }
    return pot.getMarkersForMember(player);
  }

  private Pot setPotToSplit(int raiseCheckValueLeft, int amountToJoinPot, Pot pot, Player player) {
    Pot potToSplit;
    logger.debug("Player [{}] can't afford pot, has [{}] markers need [{}]. Split pot. ", player.getName(), raiseCheckValueLeft, amountToJoinPot);
    potToSplit = pot;
    return potToSplit;
  }

  private boolean canJoinPot(int numberOfMarkers, int amountToJoinPot) {
    if (numberOfMarkers >= amountToJoinPot) {
      return true;
    }
    logger.trace("Can't join pot, number of markers {{}} amountToJoinPot {{}}", numberOfMarkers, amountToJoinPot);
    return false;
  }

  private boolean isPotSplit(Pot potToSplit) {
    return potToSplit != null;
  }

  private void putMarkersToPot(Pot pot, Player player, int markers) {
    logger.debug("Adding [{}] with [{}] markers to pot [{}]. ", player.getName(), markers, pots.indexOf(pot));
    if (pot.hasMember(player)) {
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

  public int getAmountToJoinPot() {
    int amountToJoinPot = 0;
    for (Pot pot:pots) {
      amountToJoinPot += pot.getHighestAmount();
    }
    return amountToJoinPot;
  }
}
