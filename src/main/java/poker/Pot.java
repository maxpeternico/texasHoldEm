package poker;

import java.util.List;

import com.google.common.collect.Lists;

public class Pot {
  List<Integer> pots = Lists.newArrayList();

  public int getCurrentPot() {
    return pots.get(pots.size() -1);
  }

  public void addMarkersToPot(int markers) {
    final Integer numberOfMarkers = pots.get(pots.size() - 1);
    pots.remove(pots.size() - 1);
    pots.add(numberOfMarkers + markers);
  }

}
