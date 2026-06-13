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
    CREATE TABLE IF NOT EXISTS games (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        winner TEXT,
        rounds INTEGER,
        played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS players (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL
    )
""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS scores (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        game_id INTEGER,
        player_name TEXT,
        score INTEGER,
        FOREIGN KEY(game_id) REFERENCES games(id)
    )
""");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}