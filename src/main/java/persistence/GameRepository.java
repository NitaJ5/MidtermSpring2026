package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameRepository {

    public int saveGame(String winner, int rounds) {
        String sql = "INSERT INTO games (winner, rounds) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, winner);
            stmt.setInt(2, rounds);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void saveScore(int gameId, String playerName, int score) {
        String sql = "INSERT INTO scores (game_id, player_name, score) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);
            stmt.setString(2, playerName);
            stmt.setInt(3, score);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printRecentGames() {
        String sql = "SELECT id, winner, rounds, played_at FROM games ORDER BY played_at DESC LIMIT 10";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Game " + rs.getInt("id")
                        + " | winner: " + rs.getString("winner")
                        + " | rounds: " + rs.getInt("rounds")
                        + " | played at: " + rs.getString("played_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printPlayerWinCounts() {
        String sql = "SELECT winner, COUNT(*) AS wins FROM games GROUP BY winner ORDER BY wins DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getString("winner") + ": " + rs.getInt("wins") + " wins");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printHighestScores() {
        String sql = "SELECT player_name, score FROM scores ORDER BY score DESC LIMIT 10";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getString("player_name") + ": " + rs.getInt("score"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}