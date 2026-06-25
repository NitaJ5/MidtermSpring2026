import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DatabaseManager;
import persistence.GameRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class GameRepositoryTest {

    private GameRepository repo;

    @BeforeEach
    void resetDatabase() {
        File db = new File("uno.db");
        if (db.exists()) {
            db.delete();
        }

        DatabaseManager.initializeDatabase();
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM scores");
            stmt.executeUpdate("DELETE FROM rounds");
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("DELETE FROM players");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        repo = new GameRepository();
    }

    @Test
    void savesGameRoundAndScoresWithPlayerLinks() {
        int bot1 = repo.findOrCreatePlayer("Bot1");
        int bot2 = repo.findOrCreatePlayer("Bot2");

        int gameId = repo.saveGame(bot1);
        repo.saveRound(gameId, 1, bot1);
        repo.saveScore(gameId, bot1, 100);
        repo.saveScore(gameId, bot2, 20);

        assertTrue(gameId > 0);
        assertTrue(bot1 > 0);
        assertTrue(bot2 > 0);
    }

    @Test
    void recentGamesReportPrintsSavedGame() {
        int bot1 = repo.findOrCreatePlayer("Bot1");
        int gameId = repo.saveGame(bot1);
        repo.saveRound(gameId, 1, bot1);

        String output = captureOutput(() -> repo.printRecentGames());

        assertTrue(output.contains("Game"));
        assertTrue(output.contains("winner: Bot1"));
        assertTrue(output.contains("rounds: 1"));
    }

    @Test
    void winCountsReportPrintsPlayerWins() {
        int bot1 = repo.findOrCreatePlayer("Bot1");
        int gameId = repo.saveGame(bot1);
        repo.saveRound(gameId, 1, bot1);

        String output = captureOutput(() -> repo.printPlayerWinCounts());
        assertTrue(output.contains("Bot1"));
        assertTrue(output.contains("1 wins"));
    }

    @Test
    void highestScoresReportPrintsPlayerScores() {
        int bot1 = repo.findOrCreatePlayer("Bot1");
        int bot2 = repo.findOrCreatePlayer("Bot2");

        int gameId = repo.saveGame(bot1);
        repo.saveScore(gameId, bot1, 150);
        repo.saveScore(gameId, bot2, 30);

        String output = captureOutput(() -> repo.printHighestScores());

        assertTrue(output.contains("Bot1: 150"));
        assertTrue(output.contains("Bot2: 30"));
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));
            action.run();
            return output.toString();
        } finally {
            System.setOut(originalOut);
        }
    }
}