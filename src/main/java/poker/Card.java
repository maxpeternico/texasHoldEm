package poker;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

class Card {
    private static final Logger logger = LogManager.getLogger(Card.class.getName());

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ordinal.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if ((this.getColor() == ((Card) obj).getColor()) && (this.getOrdinal() == ((Card) obj).getOrdinal())) {
            return true;
        }
        return false;
    }

    private Color color;
    private Ordinal ordinal;

    public Card(Color color, Ordinal ordinal) {
        this.color = color;
        this.ordinal = ordinal;

    }

    public Color getColor() {
        return color;
    }

    public Ordinal getOrdinal() {
        return ordinal;
    }

    public String toString() {
        return this.getColor() + this.getOrdinal().toString() + " ";

    }

    private Card getOuterType() {
        return Card.this;
    }

    public Boolean isHigher(Card otherCard) {
        boolean isHigher;
        logger.trace("Comparing the ordinal value of this card:[" + this.toString() + "] with [" + otherCard.toString() + "]");
        if (this.ordinal.getValue() > otherCard.ordinal.getValue()) {
            logger.trace("[" + this.toString() + "] is higher.");
            isHigher = true;
        } else if (this.ordinal.getValue() == otherCard.ordinal.getValue()) {
            logger.trace("Same value, compare color.");
            isHigher = compareColor(otherCard);
        } else {
            logger.trace("[" + otherCard.toString() + "] is higher.");
            isHigher = false;
        }
        return isHigher;
    }

    private boolean compareColor(Card otherCard) {
        boolean isColorHigher = false;
        logger.trace("Comparing the color of this card:[" + this.toString() + "] with [" + otherCard.toString() + "]");
        if (this.color.getValue() > otherCard.getColor().getValue()) {
            logger.trace("[" + this.toString() + "] is higher.");
            isColorHigher = true;
        } else if (this.color.getValue() == otherCard.getColor().getValue()) {
            logger.trace("Card was compared to itself");
        } else {
            logger.trace("[" + otherCard.toString() + "] is higher.");
            isColorHigher = false;
        }
        return isColorHigher;
    }
}
