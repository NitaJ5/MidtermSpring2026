import org.junit.jupiter.api.Test;
import persistence.DatabaseManager;
import persistence.GameRepository;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class GameRepositoryTest {

    @Test
    void savesGameAndScores() {
        File db = new File("uno.db");
        if (db.exists()) {
            db.delete();
        }

        DatabaseManager.initializeDatabase();

        GameRepository repo = new GameRepository();

        int gameId = repo.saveGame("Bot1", 12);

        assertTrue(gameId > 0);

        repo.saveScore(gameId, "Bot1", 100);
        repo.saveScore(gameId, "Bot2", 0);
        repo.saveScore(gameId, "Bot3", 0);
    }
}