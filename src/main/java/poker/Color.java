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

}
