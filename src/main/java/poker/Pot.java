package poker;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

/*
  Use case: Jörn, Bosse, Thomas, Peter plays card
  At flop all bets 1000, total pot 4000

  At Turn Jörn raises 1000, Bosse & Peter check, Thomas only has 500.

  Thomas goes all in in pot1, 500 is checked from Jörn, Bosse and PEter. Pot 1 has 4000 + 4x500 = 6000
  Pot 2 has 3x500 = 1500

  At River Jörn raises 1200, Bosse checks, Peter has only 700. Pot 2 has 1500 + 3x700 = 3600.
  Pot 3 has 2 x 500 = 1000
 */
public class Pot implements Comparable {

  private Map<Player, Integer> members = Maps.newHashMap();
  private static final Logger logger = LogManager.getLogger(Pot.class);

  public void addMember(Player player, int numberOfMarkers) {
    members.put(player, numberOfMarkers);
    logger.debug("Adding [{}] with [{}] markers to pot [{}]. ", player.getName(), numberOfMarkers, this);
  }

  public int getNumberOfMarkers() {
    int numberOfMarkers = 0;
    for (Player player : members.keySet()) {
      numberOfMarkers += members.get(player);
    }
    return numberOfMarkers;
  }

  private Map<Player, Integer> getMembersWithPot() {
    return members;
  }

  public List<Player> getMembers() {
    return new ArrayList<>(members.keySet());
  }

  /*
   * Adds all members that has more markers than splitValue to a new pot and returns it
   */
  public Pot splitPot(int splitValue) {
    logger.debug("Split pot [{}] with splitValue [{}]", toString(), splitValue);
    Iterator<Player> iterator = members.keySet().iterator();
    Pot newPot = new Pot();
    while (iterator.hasNext()) {
      final Player player = iterator.next();
      final Integer numberOfmarkersForPlayer = members.get(player);
      if (numberOfmarkersForPlayer > splitValue) {
        logger.debug("Move [{}] markers for player [{}] to new pot. ",  numberOfmarkersForPlayer - splitValue, player.getName());
        newPot.addMember(player, numberOfmarkersForPlayer-splitValue);
        members.replace(player, splitValue);
      } else {
        logger.debug("Player [{}] is not added to new pot.", player.getName());
      }
    }
    return newPot;
  }

  void addMarkersForMember(Player player, int raiseAmount) {
    int oldMarkersInPot = members.get(player);
    members.replace(player, oldMarkersInPot, oldMarkersInPot + raiseAmount);
  }

  public int getHighestAmount() {
    int highestAmount = 0;
    final Set<Player> players = members.keySet();

    for (Player player : players) {
      final Integer markersForMember = members.get(player);
      if (markersForMember > highestAmount) {
        logger.debug("Highest amount in pot for now [{}]", markersForMember);
        highestAmount = markersForMember;
      }
    }
    return highestAmount;
  }

  public int getMarkersForMember(Player player) {
    return members.get(player);
  }

  boolean hasMember(Player player) {
    return members.containsKey(player);
  }

  @Override
  public int compareTo(Object o) {
    Pot potToCompare = ((Pot)o);
    if (potToCompare.getHighestAmount() > getHighestAmount()) {
      return 1;
    }
    return -1;
  }
}
