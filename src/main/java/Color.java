enum Color {
    hearts(0),
    spades(1),
    diamonds(2),
    clubs(3);

    private int colorCode;

    Color(int value) {
        this.colorCode = value;
    }

    public int getValue() {
        return this.colorCode;
    }

}
