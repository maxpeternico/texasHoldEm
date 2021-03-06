package poker;

class Shuffle {

  private Shuffle() {}

  private static final int OFFSET_TWO = 2;

  static Ordinal getRandomOrdinal() {
    int randomOrdinalValue = getRandomNumberUpToValue(Ordinal.values().length) + OFFSET_TWO;
    Ordinal[] ordinals = Ordinal.values();
    Ordinal ordinalValueMatch = null;
    for (Ordinal ordinal : ordinals) {
      if (ordinal.getValue() == randomOrdinalValue) {
        ordinalValueMatch = ordinal;
      }
    }
    if (ordinalValueMatch == null) {
      throw new RuntimeException("No ordinal matches random ordinal:[" + randomOrdinalValue + "]");
    }
    return ordinalValueMatch;
  }

  static Color getRandomColor() {
    int randomColorValue = getRandomNumberUpToValue(Color.values().length);
    Color[] colorValues = Color.values();
    Color colorValueMatch = null;
    for (Color colorValue : colorValues) {
      if (colorValue.getValue() == randomColorValue) {
        colorValueMatch = colorValue;
      }
    }
    if (colorValueMatch == null) {
      throw new RuntimeException("No colorvalue matches random number:[" + randomColorValue + "]");
    }
    return colorValueMatch;
  }

  private static int getRandomNumberUpToValue(int limit) {
    double randomNo = Math.random() * limit;
    return (int)randomNo;
  }
}
