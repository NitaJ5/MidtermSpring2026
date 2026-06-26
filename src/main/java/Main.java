import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;
import persistence.DatabaseManager;
import persistence.GameRepository;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    static GameRepository gameRepository = new GameRepository();
    static ArrayList<String> playerNames = new ArrayList<String>();
    static ArrayList<Boolean> humanPlayers = new ArrayList<Boolean>();
    static ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();
    static ArrayList<String> deck = new ArrayList<String>();
    static ArrayList<String> discard = new ArrayList<String>();
    static int[] scores = new int[10];
    static int currentPlayer = 0;
    static int direction = 1;
    static String upCard = "";
    static String calledColor = "";
    static boolean[] unoCalled = new boolean[10];
    static boolean quiet = false;
    static Random random = new Random();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int bots = 3;
        int games = Integer.MAX_VALUE;        int targetScore = 500;
        boolean human = false;
        long seed = System.currentTimeMillis();

        DatabaseManager.initializeDatabase();

        for (String arg : args) {
            if (arg.equals("--recent-games")) {
                gameRepository.printRecentGames();
                return;
            }
            if (arg.equals("--win-counts")) {
                gameRepository.printPlayerWinCounts();
                return;
            }
            if (arg.equals("--highest-scores")) {
                gameRepository.printHighestScores();
                return;
            }
        }

               for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--target") && i + 1 < args.length) {
                targetScore = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--target N] [--human] [--quiet] [--seed N] [--recent-games] [--win-counts] [--highest-scores]");
                return;
            }
        }

        if (quiet) {
            LOGGER.setLevel(Level.WARNING);
            Logger.getLogger("").setLevel(Level.WARNING);
        }

        random = new Random(seed);
        setupPlayers(bots, human);

        if (playerNames.size() < 2 || playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        int round = 1;
        while (round <= games && highestScore() < targetScore) {
            LOGGER.info("Round " + round + " started");
            if (!quiet) {
                System.out.println("\n=== Round " + round + " ===");
            }
            playGame();
            round++;
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < playerNames.size(); i++) {
            System.out.println(playerNames.get(i) + ": " + scores[i]);
        }
    }

    static void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<String>());
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<String>());
        }
    }

    static void playGame() {
        int roundNumber = 1;
        deck.clear();
        deck.addAll(CardRules.createDeck());
        Collections.shuffle(deck, random);
        discard.clear();
        for (int i = 0; i < hands.size(); i++) {
            hands.get(i).clear();
        }
        for (int i = 0; i < playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(draw());
            }
        }
        upCard = draw();
        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = draw();
        }
        calledColor = "";
        java.util.Arrays.fill(unoCalled, false);
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            String name = playerNames.get(currentPlayer);
            ArrayList<String> hand = hands.get(currentPlayer);
            LOGGER.info("Player turn: " + name);

            if (!quiet) {
                System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
                System.out.println(name + " hand: " + join(hand));
            }

            int chosen = -1;
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                chosen = askHuman(hand);
            } else {
                chosen = chooseBotCard(hand);
            }

            if (chosen == -1) {
                String drawn = draw();
                hand.add(drawn);
                if (!quiet) {
                    System.out.println(name + " draws " + drawn);
                }
                LOGGER.info(name + " drew " + drawn);
                if (CardRules.isLegal(drawn, upCard, calledColor)) {
                    if (!humanPlayers.get(currentPlayer).booleanValue()) {
                        chosen = hand.size() - 1;
                    } else {
                        System.out.print("Play drawn card " + drawn + "? y/n: ");
                        String answer = scanner.nextLine();
                        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                            chosen = hand.size() - 1;
                        }
                    }
                }
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    if (!quiet) {
                        System.out.println(name + " selected an invalid index and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = CardRules.isLegal(card, upCard, calledColor);
                if (!ok) {
                    if (!quiet) {
                        System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                hand.remove(chosen);
                discard.add(upCard);
                upCard = card;
                calledColor = "";
                if (!quiet) {
                    System.out.println(name + " plays " + card);
                }
                LOGGER.info(name + " played " + card);

                if (card.equals("W") || card.equals("W4")) {
                    if (humanPlayers.get(currentPlayer).booleanValue()) {
                        calledColor = askColor();
                    } else {
                        calledColor = chooseBotColor(hand);
                    }
                    if (!quiet) {
                        System.out.println(name + " calls " + calledColor);
                    }
                }

                if (CardRules.needsUnoCall(hand.size())) {
                    boolean calledUno = true;

                    if (humanPlayers.get(currentPlayer).booleanValue()) {
                        System.out.print("Call UNO? y/n: ");
                        String unoAnswer = scanner.nextLine().trim();
                        calledUno = unoAnswer.equalsIgnoreCase("y")
                                || unoAnswer.equalsIgnoreCase("yes")
                                || unoAnswer.equalsIgnoreCase("uno");
                    }

                    unoCalled[currentPlayer] = calledUno;

                    if (calledUno) {
                        if (!quiet) {
                            System.out.println(name + " says UNO!");
                        }
                    } else {
                        int penalty = CardRules.missedUnoPenaltyCards();
                        for (int i = 0; i < penalty; i++) {
                            hand.add(draw());
                        }

                        if (!quiet) {
                            System.out.println(name + " missed UNO and draws " + penalty + " cards.");
                        }
                    }
                }

                if (hand.size() == 0) {
                    int points = 0;
                    for (int i = 0; i < hands.size(); i++) {
                        if (i != currentPlayer) {
                            for (int j = 0; j < hands.get(i).size(); j++) {
                                points += points(hands.get(i).get(j));
                            }
                        }
                    }
                    scores[currentPlayer] += points;
                    if (!quiet) {
                        System.out.println(name + " wins and scores " + points);
                    }
                                                         LOGGER.info("Game ended. Winner: " + name + ", score: " + points);

                    int winnerPlayerId = gameRepository.findOrCreatePlayer(name);
                    int gameId = gameRepository.saveGame(winnerPlayerId);
                    gameRepository.saveRound(gameId, roundNumber, winnerPlayerId);

                    for (int i = 0; i < playerNames.size(); i++) {
                        int playerId = gameRepository.findOrCreatePlayer(playerNames.get(i));
                        gameRepository.saveScore(gameId, playerId, scores[i]);
                    }

                    return;

                }

                if (rank(card).equals("SKIP")) {
                    next();
                    next();
                } else if (rank(card).equals("REVERSE")) {
                    direction = direction * -1;
                    if (playerNames.size() == 2) {
                        next();
                        next();
                    } else {
                        next();
                    }
                } else if (rank(card).equals("DRAW_TWO")) {
                    next();
                    hands.get(currentPlayer).add(draw());
                    hands.get(currentPlayer).add(draw());
                    if (!quiet) {
                        System.out.println(playerNames.get(currentPlayer) + " draws two.");
                    }
                    next();
                } else if (rank(card).equals("WILD_DRAW_FOUR")) {
                    next();
                    for (int i = 0; i < 4; i++) {
                        hands.get(currentPlayer).add(draw());
                    }
                    if (!quiet) {
                        System.out.println(playerNames.get(currentPlayer) + " draws four.");
                    }
                    next();
                } else {
                    next();
                }
            } else {
                next();
            }
        }
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    static String draw() {
        if (deck.size() == 0) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }
        if (deck.size() == 0) {
            return "W";
        }
        return deck.remove(0);
    }

    static int chooseBotCard(ArrayList<String> hand) {
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            boolean ok = false;
            if (card.startsWith("W")) ok = true;
            else if (color(card).equals(color(upCard))) ok = true;
            else if (!calledColor.equals("") && color(card).equals(calledColor)) ok = true;
            else if (rank(card).equals(rank(upCard)) && !rank(card).equals("NUMBER")) ok = true;
            else if (rank(card).equals("NUMBER") && rank(upCard).equals("NUMBER") && number(card) == number(upCard)) ok = true;
            if (rank(card).equals("DRAW_TWO") && ok) {
                return i;
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            boolean ok = false;
            if (card.startsWith("W")) ok = true;
            else if (color(card).equals(color(upCard))) ok = true;
            else if (!calledColor.equals("") && color(card).equals(calledColor)) ok = true;
            else if (rank(card).equals(rank(upCard)) && !rank(card).equals("NUMBER")) ok = true;
            else if (rank(card).equals("NUMBER") && rank(upCard).equals("NUMBER") && number(card) == number(upCard)) ok = true;
            if (rank(card).equals("SKIP") && ok) {
                return i;
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            boolean ok = false;
            if (card.startsWith("W")) ok = true;
            else if (color(card).equals(color(upCard))) ok = true;
            else if (!calledColor.equals("") && color(card).equals(calledColor)) ok = true;
            else if (rank(card).equals(rank(upCard)) && !rank(card).equals("NUMBER")) ok = true;
            else if (rank(card).equals("NUMBER") && rank(upCard).equals("NUMBER") && number(card) == number(upCard)) ok = true;
            if (rank(card).equals("NUMBER") && ok) {
                return i;
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) {
                return i;
            }
        }
        return -1;
    }

    static int askHuman(ArrayList<String> hand) {
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("DRAW")) {
                return -1;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    return index;
                }
            } catch (Exception ignored) {
            }
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (CardRules.isLegal(hand.get(i), upCard, calledColor)) {
                        return i;
                    }
                    System.out.println("That card is not legal.");
                }
            }
            System.out.println("Card not found.");
            LOGGER.warning("Invalid input from player");
        }
    }

    static String askColor() {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("R")) {
                return "R";
            }
            if (input.equals("Y")) {
                return "Y";
            }
            if (input.equals("G")) {
                return "G";
            }
            if (input.equals("B")) {
                return "B";
            }
            System.out.println("Bad color.");
            LOGGER.warning("Invalid color entered");
        }
    }

    static String chooseBotColor(ArrayList<String> hand) {
        int r = 0;
        int y = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < hand.size(); i++) {
            String c = color(hand.get(i));
            if (c.equals("R")) {
                r++;
            } else if (c.equals("Y")) {
                y++;
            } else if (c.equals("G")) {
                g++;
            } else if (c.equals("B")) {
                b++;
            }
        }
        if (r >= y && r >= g && r >= b) {
            return "R";
        } else if (y >= r && y >= g && y >= b) {
            return "Y";
        } else if (g >= r && g >= y && g >= b) {
            return "G";
        } else {
            return "B";
        }
    }


    static String color(String card) {
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

    static String rank(String card) {
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

    static int number(String card) {
        if (rank(card).equals("NUMBER")) {
            return Integer.parseInt(card.substring(1));
        }
        return -1;
    }

    static int points(String card) {
        String r = rank(card);
        if (r.equals("NUMBER")) {
            return number(card);
        }
        if (r.equals("SKIP") || r.equals("REVERSE") || r.equals("DRAW_TWO")) {
            return 20;
        }
        if (r.equals("WILD") || r.equals("WILD_DRAW_FOUR")) {
            return 50;
        }
        return 0;
    }

    static void next() {
        currentPlayer += direction;
        if (currentPlayer >= playerNames.size()) {
            currentPlayer = 0;
        }
        if (currentPlayer < 0) {
            currentPlayer = playerNames.size() - 1;
        }
    }

    static String join(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) {
                out += " ";
            }
        }
        return out;
    }
    static int highestScore() {
        int highest = 0;
        for (int i = 0; i < playerNames.size(); i++) {
            if (scores[i] > highest) {
                highest = scores[i];
            }
        }
        return highest;
    }

    static void selfTest() {
        int passed = 0;

        // Card parsing
        if (color("R5").equals("R")) passed++; else fail("color R5");
        if (color("W").equals("")) passed++; else fail("wild has no color");
        if (rank("G+2").equals("DRAW_TWO")) passed++; else fail("rank +2");
        if (rank("YS").equals("SKIP")) passed++; else fail("rank skip");
        if (rank("BR").equals("REVERSE")) passed++; else fail("rank reverse");
        if (rank("W").equals("WILD")) passed++; else fail("rank wild");
        if (rank("W4").equals("WILD_DRAW_FOUR")) passed++; else fail("rank wild draw four");
        if (number("R7") == 7) passed++; else fail("number R7");
        if (number("W") == -1) passed++; else fail("wild number");

        // Legal play rules
        if (CardRules.isLegal("R2", "R9", "")) passed++; else fail("same color");
        if (CardRules.isLegal("G9", "R9", "")) passed++; else fail("same number");
        if (CardRules.isLegal("BS", "YS", "")) passed++; else fail("same action skip");
        if (CardRules.isLegal("GR", "BR", "")) passed++; else fail("same action reverse");
        if (CardRules.isLegal("Y+2", "G+2", "")) passed++; else fail("same action draw two");
        if (CardRules.isLegal("W", "R9", "")) passed++; else fail("wild always legal");
        if (CardRules.isLegal("W4", "R9", "")) passed++; else fail("wild draw four always legal");
        if (CardRules.isLegal("B3", "W", "B")) passed++; else fail("called color");
        if (!CardRules.isLegal("B3", "R9", "")) passed++; else fail("illegal mismatch");
        if (!CardRules.isLegal("B3", "W", "R")) passed++; else fail("wrong called color");

        // Scoring
        if (points("R5") == 5) passed++; else fail("number points");
        if (points("YS") == 20) passed++; else fail("skip points");
        if (points("BR") == 20) passed++; else fail("reverse points");
        if (points("G+2") == 20) passed++; else fail("draw two points");
        if (points("W") == 50) passed++; else fail("wild points");
        if (points("W4") == 50) passed++; else fail("wild draw four points");

        // Bot chooses draw two first if legal
        ArrayList<String> h = new ArrayList<String>();
        h.add("R4");
        h.add("R+2");
        h.add("W");
        upCard = "R9";
        calledColor = "";
        if (chooseBotCard(h) == 1) passed++; else fail("bot prefers draw two");

        // Bot chooses skip before number
        ArrayList<String> hSkip = new ArrayList<String>();
        hSkip.add("R4");
        hSkip.add("RS");
        hSkip.add("W");
        upCard = "R9";
        calledColor = "";
        if (chooseBotCard(hSkip) == 1) passed++; else fail("bot prefers skip before number");

        // Bot chooses number before wild
        ArrayList<String> hNumber = new ArrayList<String>();
        hNumber.add("B3");
        hNumber.add("R4");
        hNumber.add("W");
        upCard = "R9";
        calledColor = "";
        if (chooseBotCard(hNumber) == 1) passed++; else fail("bot normal before wild");

        // Bot chooses wild if no normal card is legal
        ArrayList<String> hWild = new ArrayList<String>();
        hWild.add("B3");
        hWild.add("G4");
        hWild.add("W");
        upCard = "R9";
        calledColor = "";
        if (chooseBotCard(hWild) == 2) passed++; else fail("bot chooses wild");

        // Bot draws if nothing is legal
        ArrayList<String> hNone = new ArrayList<String>();
        hNone.add("B3");
        hNone.add("G4");
        upCard = "R9";
        calledColor = "";
        if (chooseBotCard(hNone) == -1) passed++; else fail("bot draws when no legal card");

        // Bot color choice
        ArrayList<String> h2 = new ArrayList<String>();
        h2.add("B1");
        h2.add("B2");
        h2.add("R3");
        if (chooseBotColor(h2).equals("B")) passed++; else fail("bot color");

        // Draw pile behavior
        deck.clear();
        discard.clear();
        deck.add("R5");
        if (draw().equals("R5")) passed++; else fail("draw removes top card");

        deck.clear();
        discard.clear();
        if (draw().equals("W")) passed++; else fail("empty deck fallback wild");

        // Turn movement
        playerNames.clear();
        playerNames.add("A");
        playerNames.add("B");
        playerNames.add("C");
        currentPlayer = 0;
        direction = 1;
        next();
        if (currentPlayer == 1) passed++; else fail("next forward");

        currentPlayer = 0;
        direction = -1;
        next();
        if (currentPlayer == 2) passed++; else fail("next backward wrap");

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
