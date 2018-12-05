package poker;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  public void addMember(Player player, int numberOfMarkers) {
    members.put(player, numberOfMarkers);
  }

  public int getNumberOfMarkers() {
    int numberOfMarkers = 0;
    final Iterator<Player> iterator = members.keySet().iterator();
    while (iterator.hasNext()) {
      numberOfMarkers += iterator.next().getNumberOfMarkers();
    }
    return numberOfMarkers;
  }

  public int splitPot(int allInValue, int raiseValue) {
    List<Player> playersWhoBetMoreThanAllIn = Lists.newArrayList();
    Iterator<Player> iterator = members.keySet().iterator();
    while (iterator.hasNext()) {
      final Player player = iterator.next();
      if (player.getNumberOfMarkers() > allInValue) {
        playersWhoBetMoreThanAllIn.add(player);
      }
    }
    for (Player player:playersWhoBetMoreThanAllIn) {

    }
  }

  public List<Player> getMembers() {
    return members.keySet().stream().collect(Collectors.toList());
  }
}
