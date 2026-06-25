package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:uno.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS games (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    winner_player_id INTEGER,
                    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(winner_player_id) REFERENCES players(id)
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rounds (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER NOT NULL,
                    round_number INTEGER NOT NULL,
                    winner_player_id INTEGER,
                    FOREIGN KEY(game_id) REFERENCES games(id),
                    FOREIGN KEY(winner_player_id) REFERENCES players(id)
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER NOT NULL,
                    player_id INTEGER NOT NULL,
                    score INTEGER NOT NULL,
                    FOREIGN KEY(game_id) REFERENCES games(id),
                    FOREIGN KEY(player_id) REFERENCES players(id)
                )
                """);

        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize database", e);
        }
    }
}