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

  public void joinPot(Player player, int raiseCheckValue) {
    int raiseCheckValueLeft = raiseCheckValue;
    int allInAmount = 0;
    // Do player owe debt to older pot?
    // Pay allIn amounts for all pots but the last one, if markers left put it in last pot
    // if can't afford split pot
    if (pots.size() > 1) {
      for (Pot pot:pots) {
        if (pots.indexOf(pot) == pots.size() - 1) {
          putMarkersToPot(pot, player, raiseCheckValueLeft);
        } else {
          logger.debug("Pot is allIn.");
          allInAmount = pot.getAllInAmount();
          putMarkersToPot(pot, player, allInAmount);
        }
        raiseCheckValueLeft -= allInAmount;
      }
    } else {
      final Pot pot = pots.get(0);
      if (pot.isMember(player)) {
        pot.addMarkersForMember(player, raiseCheckValue);
      } else {
        pot.addMember(player, raiseCheckValue);
      }
    }
  }

  private void putMarkersToPot(Pot pot,
                              Player player,
                              int raiseCheckValue) {
    final List<Player> members = pot.getMembers();
    if (members.contains(player)) {
      int numberOfMarkersAlreadyInPot = pot.getMarkersForMember(player);
      final int raiseCheckValueForThisRound = raiseCheckValue + numberOfMarkersAlreadyInPot;
      if (pot.getAllInAmount() > raiseCheckValueForThisRound) {
        // can't afford more, go all in and split pot
        Pot newPot = pot.splitPot(raiseCheckValue);
        pots.add(newPot);
      } else {
        pot.addMarkersForMember(player, raiseCheckValueForThisRound);
      }
    } else {
      pot.addMember(player, raiseCheckValue);
    }
  }


  public void createPot() {
    pots.add(new Pot());
  }

  public List<Pot> getPots() {
    return pots;
  }

  public void clear() {
    pots.clear();
    pots.add(new Pot());
  }

  public void splitPot(int allInAmount) {
    Pot pot = pots.get(getCurrentPot()).splitPot(allInAmount);
    pots.add(pot);
  }

  private int getCurrentPot() {
    return pots.size()-1;
  }

  public int getNumberOfMarkersInAllPots() {
    int numberOfMarkers = 0;
    for (Pot pot:pots) {
      numberOfMarkers += pot.getNumberOfMarkers();
    }
    return numberOfMarkers;
  }

  public Pot getPot(int i) {
    return pots.get(i);
  }
}
