package poker;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestPlayFullGame {
  private final PokerGame pokerGame = PokerGame.getInstance();

  @Test
  public void playUntilAPlayerWinsTheGame() {
    Player jorn = new RobotPlayer("Jörn", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(jorn);
    List<Card> jornsPrivateHand = new ArrayList<>();
    jornsPrivateHand.add(new Card(Color.hearts, Ordinal.ace));
    jornsPrivateHand.add(new Card(Color.spades, Ordinal.ace));
    pokerGame.setPrivateHand(jorn, jornsPrivateHand);
    Player staffan = new RobotPlayer("Staffan", PokerGame.TOTAL_MARKERS_PER_PLAYER);
    pokerGame.registerPlayer(staffan);
    List<Card> staffansPrivateHand = new ArrayList<>();
    staffansPrivateHand.add(new Card(Color.hearts, Ordinal.king));
    staffansPrivateHand.add(new Card(Color.spades, Ordinal.queen));
    pokerGame.setPrivateHand(staffan, staffansPrivateHand);
    List<Player> players = new ArrayList<>();
    players.add(jorn);
    players.add(staffan);
    int blind = 50;

    pokerGame.initBlinds(players);
    do {
      pokerGame.payBlinds(players, pokerGame.playersThatCanBet(players), blind);
      pokerGame.playRound(players);
      blind = blind * 2;
    } while (moreThanOnePlayerHasMarkers(players));
  }

  @Test
  public void playUntilAPlayerWinsTheGameMaxPlayers() {
    List<Player> players = pokerGame.createNumberOfRobotPlayers(PokerGame.MAX_NUMBER_OF_PLAYERS, PokerGame.TOTAL_MARKERS_PER_PLAYER);
    for (Player player : players) {
      pokerGame.registerPlayer(player);
    }
    int blind = 50;
    pokerGame.initBlinds(players);
    do {
      pokerGame.payBlinds(players, pokerGame.playersThatCanBet(players), blind);
      pokerGame.playRound(players);
      blind = blind * 2;
    } while (moreThanOnePlayerHasMarkers(players));
  }

  private boolean moreThanOnePlayerHasMarkers(List<Player> players) {
    int i = 0;
    for (Player player : players) {
      if (player.hasAnyMarkers()) {
        i++;
      }
    }
    return i > 1;
  }

  @After
  public void clearGame() {
    pokerGame.clearGameForTests();
  }
}
