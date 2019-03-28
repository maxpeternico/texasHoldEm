package poker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BetManager {

  private final int blind;
  private List<Player> playerList;
  private Map<Player, Boolean> bettingMap = Maps.newLinkedHashMap();
  private Draw draw;
  private List<Card> commonHand = Lists.newArrayList();
  private PotHandler potHandler;
  private int maxRaiseFromAPlayer;
  private static final Logger logger = LogManager.getLogger(BetManager.class);
  private StringBuffer result = null;
  private int maxRaiseThisDraw = 0;

  public BetManager(List<Player> playerList,
                    int blind,
                    PotHandler potHandler) {
    this.playerList = playerList;
    this.blind = blind;
    this.potHandler = potHandler;
    this.maxRaiseFromAPlayer = blind;
    initCreateBettingDecisionList(playerList);
    draw = Draw.BEFORE_FLOP;
    logger.debug("Creating new betManager with highest raise: {{}}", maxRaiseFromAPlayer);
  }

  public void addFlopCardsToCommonhand(List<Card> flopCards, Draw draw) {
    commonHand.addAll(flopCards);
    this.draw = draw;
  }

  public void addRiverCardToCommonHand(List<Card> riverCard, Draw draw) {
    commonHand.addAll(riverCard);
    this.draw = draw;
   }

  public void addTurnCardToCommonHand(List<Card> riverCard, Draw draw) {
    commonHand.addAll(riverCard);
    this.draw = draw;
  }

  public String bet() {
    result = new StringBuffer();
    updateTurn();
    Player playerWithHighestRaise = betUntilAllAreSatisfied(false);
    while (doesAnyPlayersWantToBetMore(playerWithHighestRaise)) {
      createBettingDecisionList(playerWithHighestRaise);
      playerWithHighestRaise = betUntilAllAreSatisfied(true);
    }
    logger.info("Players have finished betting. ");
    return result.toString();
  }

  void initCreateBettingDecisionList(List<Player> playerList) {
    for (Player player : playerList) {
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
      if (i == getPlayerIndexFromOldBettingMap(raisingPlayer, oldBettingMap)) {
        continue; // first player shall always be first
      }
      addPlayerThatShallBet(getPlayerFromOldBettingMapIndex(oldBettingMap, i), oldBettingMap);
    }
    for (int i = 0; i < getPlayerIndexFromOldBettingMap(raisingPlayer, oldBettingMap); i++) {
      addPlayerThatShallBet(getPlayerFromOldBettingMapIndex(oldBettingMap, i), oldBettingMap);
    }
    for (Player player : getPlayersFromBettingMap()) {
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
    for (Player player : getPlayersFromBettingMap()) {

      // If a new betting map is created due to raise of another player, list must be re-ordered with the raising player first. Player has already bet
      if (firstPlayerAlreadyBet) {
        if (getPlayersFromBettingMap().indexOf(player) == 0) continue;
      }
      logger.debug(
        "player :[{}] maxRaiseFromOtherPlayer:[{}] numbersOfMarkers :[{}]",
        player.getName(),
        maxRaiseFromAPlayer,
        player.getNumberOfMarkers());
      if (player.hasBlind() && !player.hasAnyMarkers()) {
        logger.debug("Player {{}} is all in and has already paid to pot", player.getName());
        continue;
      }
      final int playerPartInPots = potHandler.getPlayerPartInPots(player);
      Action action = player.decideAction(draw, calculatePlayersAfter(), commonHand, blind, maxRaiseFromAPlayer, maxRaiseThisDraw, playerPartInPots);
      if (action.getAmount() > maxRaiseFromAPlayer) {
        maxRaiseFromAPlayer = action.getAmount();
      }
      bettingMap.put(player, true);
      result.append("Player " + player.getName() + " " + action.toString() + ". ");
      logger.trace("Player {{}} has made a bet", player.getName());
      System.out.println("Player " + player.getName() + " decides to " + action.toString());
      if (!action.isFold()) {
        if (shallPayToPot(playerPartInPots, maxRaiseFromAPlayer)) {
          final int raiseOrCheckValue = player.getActionAmount(isBeforeFlop(draw));
          System.out.println("Player " + player.getName() + " puts " + raiseOrCheckValue + " markers to the pot. ");
          potHandler.joinPot(player, raiseOrCheckValue);
          player.decreaseMarkers(raiseOrCheckValue);
          logger.debug("Pot size :[{}]. ", potHandler.getNumberOfMarkersInAllPots());
          if (action.getAmount() > maxRaiseThisDraw) {
            maxRaiseThisDraw = action.getAmount();
          }
        }
        if (action.isRaise() || action.isAllIn()) {
          logger.trace("Player [{}] is [{}]. ", player.getName(), action);
          if (getPlayersFromBettingMap().indexOf(player) > 0 && !allPlayersAreAllIn()) {
            logger.trace("Player is not the first player in the list, create new list.");
            return player;
          }
        }
      }
    }
    return getPlayersFromBettingMap().get(0);
  }

  private boolean allPlayersAreAllIn() {
    for (Player player:getPlayersFromBettingMap()) {
      if (!player.getAction().isAllIn()) {
        return false;
      }
    }
    logger.debug("All players are ALL IN. ");
    return true;
  }

  private boolean isBeforeFlop(Draw draw) {
    if (draw == Draw.BEFORE_FLOP) {
      return true;
    }
    return false;
  }

  public static boolean shallPayToPot(int numberOfMarkersForPlayerInPot, int maxRaiseFromAPlayer) {
    logger.trace("Number of markers for player in pot {{}} maxRaiseFromAPlayer {{}} ", numberOfMarkersForPlayerInPot, maxRaiseFromAPlayer);
    if (numberOfMarkersForPlayerInPot < maxRaiseFromAPlayer) {
      logger.trace("Player shall pay to pot.");
      return true;
    }
    logger.trace("Player shall not pay to pot.");
    return false;
  }

  private int calculatePlayersAfter() {
    return 0;
  }

  public Map<Player, Boolean> getBettingMap() {
    return bettingMap;
  }

  public void updateTurn() {
    maxRaiseThisDraw = 0;
  }

  void initResult() {
    result = new StringBuffer();
  }
}
