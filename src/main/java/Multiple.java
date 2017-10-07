enum Multiple {
    SINGLE(1),
    PAIR(2),
    THREES(3),
    FOURS(4);

    private int value;

    Multiple(int value) {
        this.value = value;
    }

    int getValue() {
        return this.value;
    }
}
