@SuppressWarnings("javadoc")
public enum PokerResult {
    ROYAL_STRAIGHT_FLUSH(9),
    STRAIGHT_FLUSH(8),
    FOURS(7),
    FULL_HOUSE(6),
    FLUSH(5),
    STRAIGHT(4),
    THREES(3),
    TWO_PAIR(2),
    PAIR(1),
    NO_RESULT(0);

    private int value;

    PokerResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
