package poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BetManager {

  private final int blind;
  private Map<Player, Boolean> bettingMap = Maps.newLinkedHashMap();
  private Draw draw;
  private List<Card> commonHand = Lists.newArrayList();
  private PotHandler potHandler;
  private int maxRaiseFromAPlayer;
  private static final Logger logger = LogManager.getLogger(BetManager.class);
  private StringBuilder result = null;
  private int maxRaiseThisDraw;
  private List<Player> playersThatHaveBet = Lists.newArrayList();

  BetManager(List<Player> playerList,
             int blind,
             PotHandler potHandler) {
    this.blind = blind;
    this.potHandler = potHandler;
    this.maxRaiseFromAPlayer = blind;
    maxRaiseThisDraw = blind;
    initCreateBettingDecisionList(playerList);
    draw = Draw.BEFORE_FLOP;
    logger.debug("Creating new betManager with highest raise: {{}}", maxRaiseFromAPlayer);
  }

  void addFlopCardsToCommonhand(List<Card> flopCards, Draw draw) {
    addCardsToCommonHand(flopCards, draw);
  }

  void addRiverCardToCommonHand(List<Card> riverCard, Draw draw) {
    addCardsToCommonHand(riverCard, draw);
  }

  void addTurnCardToCommonHand(List<Card> riverCard, Draw draw) {
    addCardsToCommonHand(riverCard, draw);
  }

  private void addCardsToCommonHand(List<Card> riverCard, Draw draw) {
    commonHand.addAll(riverCard);
    this.draw = draw;
  }

  public String bet() {
    result = new StringBuilder();
    Player playerWithHighestRaise = betUntilAllAreSatisfied(false);
    while (doesAnyPlayersWantToBetMore(playerWithHighestRaise)) {
      createBettingDecisionList(playerWithHighestRaise);
      playerWithHighestRaise = betUntilAllAreSatisfied(true);
    }
    resetMaxRaiseThisDraw();
    logger.info("Players have finished betting. ");
    return result.toString();
  }

  private void initCreateBettingDecisionList(List<Player> playerList) {
    for (Player player : playerList) {
      bettingMap.put(player, false);
    }
  }

  private boolean doesAnyPlayersWantToBetMore(Player playerWithHighestRaise) {
    return getPlayersFromBettingMap().indexOf(playerWithHighestRaise) > 0;
  }

  private List<Player> getPlayersFromBettingMap() {
    return new ArrayList<>(bettingMap.keySet());
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
      logger.trace("Member in new bettingMap [{}]", player.getName());
    }
  }

  private Player getPlayerFromOldBettingMapIndex(Map<Player, Boolean> oldBettingMap, int i) {
    return new ArrayList<>(oldBettingMap.keySet()).get(i);
  }

  private int getPlayerIndexFromOldBettingMap(Player raisingPlayer, Map<Player, Boolean> oldBettingList) {
    return new ArrayList<>(oldBettingList.keySet()).indexOf(raisingPlayer);
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
      if (hasPlayerBet(player)) continue;

      // If a new betting map is created due to raise of another player, list must be re-ordered with the raising player first. Player has already bet
      if (isNotFirstBettingRound(firstPlayerAlreadyBet, player)) continue;

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
      getEventualNewMaxRaiseFromAPlayer(action);
      bettingMap.put(player, true);
      result.append("Player ").append(player.getName()).append(" ").append(action.toString()).append(". ");
      logger.trace("Player {{}} has made a bet", player.getName());
      System.out.println("Player " + player.getName() + " decides to " + action.toString());
      if (action.isFold()) continue;

      if (shallPayToPot(playerPartInPots, maxRaiseFromAPlayer)) {
        payToPot(player, action);
      }
      if (isAPlayerAfterTheFirstPlayerRaisingOrAllIn(player, action)) return player;
    }
    return getPlayersFromBettingMap().get(0);
  }

  private void payToPot(Player player, Action action) {
    final int raiseOrCheckValue = player.getActionAmount(hasBlindAndIsFirstBetInGame(draw, player));
    System.out.println("Player " + player.getName() + " puts " + raiseOrCheckValue + " markers to the pot. ");
    potHandler.joinPot(player, raiseOrCheckValue);
    player.decreaseMarkers(raiseOrCheckValue);
    logger.debug("Pot size :[{}]. ", potHandler.getNumberOfMarkersInAllPots());
    if (action.getAmount() > maxRaiseThisDraw) {
      maxRaiseThisDraw = action.getAmount();
    }
  }

  private boolean hasBlindAndIsFirstBetInGame(Draw draw, Player player) {
    if (!player.hasBlind()) return false;

    if (isBeforeFlop(draw) && isFirstBet(player)) {
      logger.debug("First bet in game for {{}} compensate for blind", player.getName());
      return true;
    }
    return false;
  }

  /*
    Players with blind are already added to playersThatHaveBet in constructor
   */
  private boolean isFirstBet(Player player) {
    if (playersThatHaveBet.contains(player)) return false;

    playersThatHaveBet.add(player);
    return true;
  }

  private boolean isNotFirstBettingRound(boolean firstPlayerAlreadyBet, Player player) {
    return firstPlayerAlreadyBet && getPlayersFromBettingMap().indexOf(player) == 0;
  }

  private boolean hasPlayerBet(Player player) {
    return (player.isAllIn() || player.hasFolded()) && playerHasBet(player, bettingMap);
  }

  private void getEventualNewMaxRaiseFromAPlayer(Action action) {
    if (action.getAmount() > maxRaiseFromAPlayer) {
      maxRaiseFromAPlayer = action.getAmount();
    }
  }

  private boolean isAPlayerAfterTheFirstPlayerRaisingOrAllIn(Player player, Action action) {
    if (action.isRaise() || action.isAllIn()) {
      logger.trace("Player [{}] is [{}]. ", player.getName(), action);
      if (getPlayersFromBettingMap().indexOf(player) > 0 && !allPlayersAreAllIn()) {
        logger.trace("Player is not the first player in the list, create new list.");
        return true;
      }
    }
    return false;
  }

  private boolean playerHasBet(Player player, Map<Player, Boolean> bettingMap) {
    final Boolean hasBet = bettingMap.get(player);
    logger.trace("HasBet: {{}}", hasBet);
    return hasBet;
  }

  private boolean allPlayersAreAllIn() {
    for (Player player : getPlayersFromBettingMap()) {
      if (!player.getAction().isAllIn()) {
        return false;
      }
    }
    logger.debug("All players are ALL IN. ");
    return true;
  }

  private boolean isBeforeFlop(Draw draw) {
    return draw == Draw.BEFORE_FLOP;
  }

  static boolean shallPayToPot(int numberOfMarkersForPlayerInPot, int maxRaiseFromAPlayer) {
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

  void resetMaxRaiseThisDraw() {
    maxRaiseThisDraw = 0;
  }

  void initResult() {
    result = new StringBuilder();
  }
}
