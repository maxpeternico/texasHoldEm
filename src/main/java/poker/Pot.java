package poker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
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
public class Pot {
  private Map<Player, Integer> members = Maps.newHashMap();
  private static final Logger logger = LogManager.getLogger(PokerGame.class);

  public void addMember(Player player, int numberOfMarkers) {
    members.put(player, numberOfMarkers);
  }

  public int getNumberOfMarkers() {
    int numberOfMarkers = 0;
    final Iterator<Player> iterator = members.keySet().iterator();
    while (iterator.hasNext()) {
      numberOfMarkers += members.get(iterator.next());
    }
    return numberOfMarkers;
  }

  private Map<Player, Integer> getMembersWithPot() {
    return members;
  }

  public List<Player> getMembers() {
    return members.keySet().stream().collect(Collectors.toList());
  }

  public Pot splitPot(int allInValue) {
    List<Player> playersWhoBetMoreThanAllIn = Lists.newArrayList();
    Iterator<Player> iterator = getMembersWithPot().keySet().iterator();
    while (iterator.hasNext()) {
      final Player player = iterator.next();
      if (members.get(player) < allInValue) {
        throw new RuntimeException("Player :[" + player.getName() + "] can't afford AllInVale!");
      }
      if (members.get(player) > allInValue) {
        playersWhoBetMoreThanAllIn.add(player);
      }
    }
    Pot newPot = new Pot();
    for (Player player:playersWhoBetMoreThanAllIn) {
      int markersToNewPot = takeMarkersFromMember(player, allInValue);
      newPot.addMember(player, markersToNewPot);
      logger.debug("Adding :[" + player.getName() + "] to new pot.");
    }
    return newPot;
  }

  private int takeMarkersFromMember(Player player, int allInValue) {
    int oldNumberOfMarkers = members.get(player);
    final int markersToNewPot = oldNumberOfMarkers - allInValue;
    logger.debug("Removing [{}] markers for player [{}] from old pot to new pot.", markersToNewPot, player.getName());
    members.replace(player, allInValue);
    return markersToNewPot;
  }

  public void addMarkersForMember(Player player, int raiseAmount) {

  }
}
