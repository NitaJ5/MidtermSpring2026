public class CardRules {
   public static String color(String card) {
        if (card.startsWith("R")) {
            return "R";
        }
        if (card.startsWith("Y")) {
            return "Y";
        }
        if (card.startsWith("G")) {
            return "G";
        }
        if (card.startsWith("B")) {
            return "B";
        }
        return "";
    }

    public static String rank(String card) {
        if (card.equals("W")) {
            return "WILD";
        }
        if (card.equals("W4")) {
            return "WILD_DRAW_FOUR";
        }
        if (card.endsWith("S")) {
            return "SKIP";
        }
        if (card.endsWith("R")) {
            return "REVERSE";
        }
        if (card.endsWith("+2")) {
            return "DRAW_TWO";
        }
        return "NUMBER";
    }
    public static int number(String card) {
        if (rank(card).equals("NUMBER")) {
            return Integer.parseInt(card.substring(1));
        }
        return -1;
    }

    public static boolean isLegal(String card, String up, String call) {
        if (card.startsWith("W")) {
            return true;
        }
        if (color(card).equals(color(up))) {
            return true;
        }
        if (!call.equals("") && color(card).equals(call)) {
            return true;
        }
        if (rank(card).equals(rank(up)) && !rank(card).equals("NUMBER")) {
            return true;
        }
        if (rank(card).equals("NUMBER") && rank(up).equals("NUMBER") && number(card) == number(up)) {
            return true;
        }
        return false;
    }
    public static java.util.List<String> createDeck() {
        java.util.List<String> deck = new java.util.ArrayList<>();
        String[] colors = {"R", "Y", "G", "B"};

        for (String color : colors) {
            deck.add(color + "0");

            for (int copy = 0; copy < 2; copy++) {
                for (int number = 1; number <= 9; number++) {
                    deck.add(color + number);
                }
                deck.add(color + "S");
                deck.add(color + "R");
                deck.add(color + "+2");
            }
        }

        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }

        return deck;
    }
    public static boolean needsUnoCall(int handSize) {
        return handSize == 1;
    }

    public static int missedUnoPenaltyCards() {
        return 2;
    }
}
