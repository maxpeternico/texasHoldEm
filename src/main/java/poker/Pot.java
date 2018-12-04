package poker;

import java.util.List;

import com.google.common.collect.Lists;

/*
  Use case: Jörn, Bosse, Thomas, Peter plays card
  At flop all bets 1000, total pot 4000

  At Turn Jörn raises 1000, Bosse & Peter check, Thomas only has 500.

  Thomas goes all in in pot1, 500 is checked from Jörn, Bosse and PEter. Pot 1 has 4000 + 4x500 = 6000
  Pot 2 has 3x500 = 1500

  At River Jörn raises 1200, Bosse checks, Peter has only 700. Pot 2 has 1500 + 3x700 = 3600.
  Pot 3 has 2 x 500 = 1000
 */
public class Pot {
  private int numberOfMarkers = 0;
  private List<Player> members = Lists.newArrayList();

  public void addMember(Player player, int numberOfMarkers) {
    this.numberOfMarkers = numberOfMarkers;
    members.add(player);
  }

  public int getNumberOfMarkers() {
    return numberOfMarkers;
  }

  public int splitPot(int numberOfMarkersToSplit) {
    numberOfMarkers -= (markersToTransferToNewPot(numberOfMarkersToSplit));
    return markersToTransferToNewPot(numberOfMarkersToSplit);
  }

  private int markersToTransferToNewPot(int numberOfMarkersToSplit) {
    return numberOfMarkersToSplit * members.size();
  }
}
