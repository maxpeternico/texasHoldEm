package poker;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BetManager {
  private final int blind;
  List<Player> playerList;
  private Map<Player, Boolean> bettingMap = Maps.newLinkedHashMap();
  private Draw draw;
  private List<Card> commonHand;
  private PotHandler potHandler;
  private int maxRaiseFromAPlayer;
  private static final Logger logger = LogManager.getLogger(BetManager.class);
  private StringBuffer result = new StringBuffer();


  public BetManager(List<Player> playerList, Draw draw, List<Card> commonHand, int blind, PotHandler potHandler) {
    this.playerList = playerList;
    this.draw = draw;
    this.commonHand = commonHand;
    this.blind = blind;
    this.potHandler = potHandler;
    this.maxRaiseFromAPlayer = potHandler.getHighestRaise();
    initCreateBettingDecisionList(playerList);
    logger.debug("Creating new betManager with highest raise: {{}}", maxRaiseFromAPlayer);
  }

  public String bet() {
    Player playerWithHighestRaise = betUntilAllAreSatisfied(false);
    while(doesAnyPlayersWantToBetMore(playerWithHighestRaise)) {
      createBettingDecisionList(playerWithHighestRaise);
      playerWithHighestRaise = betUntilAllAreSatisfied(true);
    }
    logger.info("Players have finished betting. ");
    return result.toString();
  }

  void initCreateBettingDecisionList(List<Player> playerList) {
    for (Player player:playerList) {
      bettingMap.put(player, false);
    }
  }

  private boolean doesAnyPlayersWantToBetMore(Player playerWithHighestRaise) {
    if (getPlayersFromBettingMap().indexOf(playerWithHighestRaise) > 0) {
      return true;
    }
    return false;
  }

  List<Player> getPlayersFromBettingMap() {
    return bettingMap.keySet().stream().collect(Collectors.toList());
  }

  void createBettingDecisionList(Player raisingPlayer) {
    Map<Player, Boolean> oldBettingMap = Maps.newLinkedHashMap();
    oldBettingMap.putAll(bettingMap);
    bettingMap.clear();
    bettingMap.put(raisingPlayer, oldBettingMap.get(raisingPlayer));
    logger.trace("Creating list with player [{}] as first. ", raisingPlayer.getName());
    for (int i = getPlayerIndexFromOldBettingMap(raisingPlayer, oldBettingMap); i < oldBettingMap.keySet().size(); i++) {
      if (i == getPlayerIndexFromOldBettingMap(raisingPlayer, oldBettingMap)) continue; // first player shall always be first
      addPlayerThatShallBet(getPlayerFromOldBettingMapIndex(oldBettingMap, i), oldBettingMap);
    }
    for (int i = 0; i < getPlayerIndexFromOldBettingMap(raisingPlayer, oldBettingMap); i++) {
      addPlayerThatShallBet(getPlayerFromOldBettingMapIndex(oldBettingMap, i), oldBettingMap);
    }
    for (Player player: getPlayersFromBettingMap()) {
      logger.trace("Member in new bettingMap [{}] ", player.getName());
    }
  }

  private Player getPlayerFromOldBettingMapIndex(Map<Player, Boolean> oldBettingMap, int i) {
    return oldBettingMap.keySet().stream().collect(Collectors.toList()).get(i);
  }

  private int getPlayerIndexFromOldBettingMap(Player raisingPlayer, Map<Player, Boolean> oldBettingList) {
    return oldBettingList.keySet().stream().collect(Collectors.toList()).indexOf(raisingPlayer);
  }

  private void addPlayerThatShallBet(Player player, Map<Player, Boolean> oldBettingMap) {

    // If player has gone all in or fold they shall bet only one time
    if (player.isAllIn() || player.hasFolded()) {
      if (oldBettingMap.get(player).equals(false)) {
        bettingMap.put(player, oldBettingMap.get(player));
        logger.trace("Adding player {{}} to new betting list", player.getName());
      } else {
        logger.trace("Player {{}} has already bet and will not be added to new betting list", player.getName());
      }
    } else {
      bettingMap.put(player, oldBettingMap.get(player));
      logger.trace("Adding player {{}} to new betting list", player.getName());
    }
  }

  Player betUntilAllAreSatisfied(boolean firstPlayerAlreadyBet) {
    for (Player player: getPlayersFromBettingMap()) {

      // If a new betting map is created due to raise of another player, list must be re-ordered with the raising player first. Player has already bet
      if (firstPlayerAlreadyBet) {
        if (getPlayersFromBettingMap().indexOf(player) == 0) continue;
      }
      logger.debug(
          "player :[{}] action [{}] maxRaiseFromOtherPlayer:[{}] numbersOfMarkers :[{}]",
          player.getName(),
          player.getAction(),
          maxRaiseFromAPlayer,
          player.getNumberOfMarkers());

      Action action = player.decideAction(draw, calculatePlayersAfter(), commonHand, blind, maxRaiseFromAPlayer);
      if (action.getAmount() > maxRaiseFromAPlayer) {
        maxRaiseFromAPlayer = action.getAmount();
      }
      bettingMap.put(player, true);
      result.append("Player " + player.getName() + " " + action.toString() + ". ");
      logger.trace("Player {{}} has made a bet", player.getName());

      if (!action.isFold()) {
        if (shallPayToPot(potHandler.getPlayerPartInPots(player), maxRaiseFromAPlayer)) {
          final int raiseOrCheckValue = player.getActionAmount(isBeforeFlop(draw));
          potHandler.joinPot(player, raiseOrCheckValue);
          player.decreaseMarkers(raiseOrCheckValue);
          logger.debug("Pot size :[{}]. ", potHandler.getNumberOfMarkersInAllPots());
        }
        if (action.isRaise() || action.isAllIn()) {
          logger.trace("Player [{}] is [{}]. ", player.getName(), action);
          if (getPlayersFromBettingMap().indexOf(player) > 0) {
            logger.trace("Player is not the first player in the list, create new list.");
            return player;
          }
        }
      }
    }
    return getPlayersFromBettingMap().get(0);
  }

  private boolean isBeforeFlop(Draw draw) {
    if (draw == Draw.BEFORE_FLOP) {
      return true;
    }
    return false;
  }

  private boolean shallPayToPot(int numberOfMarkersForPlayerInPot, int maxRaiseFromAPlayer) {
    logger.trace("Number of markers for player in pot {{}} maxRaiseFromAPlayer {{}} ", numberOfMarkersForPlayerInPot, maxRaiseFromAPlayer);
    if (numberOfMarkersForPlayerInPot == maxRaiseFromAPlayer) {
      logger.trace("Player shall not pay to pot.");
      return false;
    }
    logger.trace("Player shall pay to pot.");
    return true;
  }

  private int calculatePlayersAfter() {
    return 0;
  }

  public Map<Player, Boolean> getBettingMap() {
    return bettingMap;
  }
}
