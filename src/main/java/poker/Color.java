package poker;

enum Color {
    hearts(3),
    spades(2),
    diamonds(1),
    clubs(0);

    private int colorCode;

    Color(int value) {
        this.colorCode = value;
    }

    public int getValue() {
        return this.colorCode;
    }

    @Override
    public String toString() {
        if (getValue() == 3) {
            return "♡";
        }
        if (getValue() == 2) {
            return "♠";
        }
        if (getValue() == 1) {
            return "♢";
        }
        if (getValue() == 0) {
            return "♣";
        }
        return null;
    }
}
