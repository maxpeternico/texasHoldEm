import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Player {

    private static final Logger logger = LogManager.getLogger(Player.class);

    private List<Card> cardsOnHand = new ArrayList<Card>();
    private int i =0;

    public void setInt() {
        this.i++;
    }

    public int getInt() {
        return this.i;
    }

    @Override
    public String toString() {
        return "Player [name=" + name + "]";
    }

    private String name;

    Player(String name) {
        this.name = name;
    }

    public void addPrivateCards(List<Card> newCards) {
        cardsOnHand.addAll(newCards);

    }

    public Map<Card, PokerResult> evaluateHand(List<Card> commonHand) throws Exception {
        List<Card> totalHand = new ArrayList<Card>();
        totalHand.addAll(cardsOnHand);
        totalHand.addAll(commonHand);
        logger.trace("[" + name + "]:s total hand is [" + EvaluationHandler.printHand(totalHand) + "]");
        Map<Card, PokerResult> result = EvaluationHandler.evaluateHand(name, totalHand);
        return result;
    }

    public List<Card> getPrivateHand() {
        return this.cardsOnHand;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
