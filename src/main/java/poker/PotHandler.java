package poker;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

public class PotHandler {

  private List<Pot> pots = Lists.newArrayList();
  private int highestRaise = 0;
  private static final Logger logger = LogManager.getLogger(PotHandler.class);

  public PotHandler() {
    pots.add(new Pot());
  }

  /*
  Use case 1: 450 markers, 4 pots, size 100, 200, 50, 100. Player checks 450. Pays to all pots

  Use case 2: 450 markers, 4 pots, size 100, 200, 50, 100. Player goes all in on 310. Pay 100 to first pot, 200 to second pot and split 3rd pot to 10 and 40.
  Result: 5 pots 100, 200, 10, 40, 50, 100
   */

  public int getHighestRaise() {
    return highestRaise;
  }

  public void joinPot(Player player, int joinAmount) {
    if (joinAmount > highestRaise) {
      logger.trace("New highest raise: {{}}", highestRaise);
      highestRaise = joinAmount;
    }
    int joinAmountLeft = joinAmount;
    int amountToJoinPot = 0;

    Pot potToSplit = null;
    List<Pot> newPots = Lists.newArrayList();
    // Pay highest amount in pot until joinAmountLeft is 0
    for (Pot pot : pots) {
      int markersPaidToPot = 0;
      amountToJoinPot = getAmountToJoinPot(joinAmount, pot);
      final int potIndex = pots.indexOf(pot);
      final int numberOfPots = pots.size() - 1;
      if (pot.hasMember(player)) {
        if (isLatestPot(potIndex, numberOfPots)) {
          createNewPot(player, joinAmount, joinAmountLeft, newPots, markersPaidToPot);
        }
        continue;
      }
      if (isLatestPot(potIndex, numberOfPots)) {
        if (canJoinPot(joinAmountLeft, amountToJoinPot)) {
          markersPaidToPot = joinLatestPot(player, joinAmount, joinAmountLeft, amountToJoinPot, newPots, pot);
        } else {
          potToSplit = splitCurrentPot(player, joinAmountLeft, amountToJoinPot, pot);
          break;
        }

      } else {
        if (amountToJoinPot > joinAmountLeft) {
          potToSplit = splitCurrentPot(player, joinAmountLeft, amountToJoinPot, pot);
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
    if (!newPots.isEmpty()) {
      pots.addAll(newPots);
    }
    splitPotIfNecessary(joinAmountLeft, potToSplit);
  }

  private Pot splitCurrentPot(Player player, int joinAmountLeft, int amountToJoinPot, Pot pot) {
    Pot potToSplit;
    putMarkersToPot(pot, player, joinAmountLeft);
    potToSplit = setPotToSplit(joinAmountLeft, amountToJoinPot, pot, player);
    return potToSplit;
  }

  private void splitPotIfNecessary(int joinAmountLeft, Pot potToSplit) {
    if (isPotSplit(potToSplit)) {
      int splitAmount = joinAmountLeft;
      do {
        createNewPot(splitAmount, potToSplit);
        // Old pot may have been even
        splitAmount = getEventualNewSplitValueFromOldPot(potToSplit);
      } while (splitAmount != 0);
    }
  }

  private int joinLatestPot(Player player,
                            int joinAmount,
                            int joinAmountLeft,
                            int amountToJoinPot,
                            List<Pot> newPots,
                            Pot pot) {
    int markersPaidToPot;
    putMarkersToPot(pot, player, amountToJoinPot);
    markersPaidToPot = amountToJoinPot;
    if (joinAmountLeft > markersPaidToPot) {
      createNewPot(player, joinAmount, joinAmountLeft, newPots, markersPaidToPot);
    }
    return markersPaidToPot;
  }

  private void createNewPot(Player player, int joinAmount, int joinAmountLeft, List<Pot> newPots, int markersPaidToPot) {
    logger.trace("Player {{}} raises with {{}} markers, creating new pot. ", player.getName(), joinAmount - markersPaidToPot);
    if (joinAmount - markersPaidToPot <= 0) {
      System.out.println("Remove this later");
    }
    final Pot newPotForRestOfJoinAmount = new Pot();
    newPotForRestOfJoinAmount.addMember(player, joinAmountLeft-markersPaidToPot);
    newPots.add(newPotForRestOfJoinAmount);
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
    logger.trace("PotIndex {{}} is latest pot. Number of pots : {{}}", potIndex, numberOfPots);
    if (numberOfPots == 20) {
      System.out.println("Remove this later");
    }
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
    highestRaise = 0;
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

  public int getMaxMarkersForAnyPlayer() {
    if (pots.get(0) == null) return 0;

    final List<Player> members = pots.get(0).getMembers();
    int maxMarkers = 0;
    for (Player player:members) {
      final int playerPartInPots = getPlayerPartInPots(player);
      if (playerPartInPots > maxMarkers) {
        maxMarkers = playerPartInPots;
      }
    }
    return maxMarkers;
  }
}
