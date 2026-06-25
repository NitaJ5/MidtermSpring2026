package persistence;

import org.apache.ibatis.jdbc.SQL;

import java.sql.*;


public class GameRepository {

    public int findOrCreatePlayer(String name) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement insert = conn.prepareStatement(
                    "INSERT OR IGNORE INTO players (name) VALUES (?)")) {
                insert.setString(1, name);
                insert.executeUpdate();
            }

            try (PreparedStatement select = conn.prepareStatement(
                    "SELECT id FROM players WHERE name = ?")) {
                select.setString(1, name);
                ResultSet rs = select.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException("Player not found: " + name);
    }

    public int saveGame(int winnerPlayerId) {
        String sql = new SQL()
                .INSERT_INTO("games")
                .VALUES("winner_player_id", "?")
                .toString();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, winnerPlayerId);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return -1;
    }

    public void saveRound(int gameId, int roundNumber, int winnerPlayerId) {
        String sql = new SQL()
                .INSERT_INTO("rounds")
                .VALUES("game_id", "?")
                .VALUES("round_number", "?")
                .VALUES("winner_player_id", "?")
                .toString();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, roundNumber);
            stmt.setInt(3, winnerPlayerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void saveScore(int gameId, int playerId, int score) {
        String sql = new SQL()
                .INSERT_INTO("scores")
                .VALUES("game_id", "?")
                .VALUES("player_id", "?")
                .VALUES("score", "?")
                .toString();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, playerId);
            stmt.setInt(3, score);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void printRecentGames() {
        String sql = """
                SELECT g.id, p.name AS winner, COUNT(r.id) AS rounds, g.played_at
                FROM games g
                LEFT JOIN players p ON g.winner_player_id = p.id
                LEFT JOIN rounds r ON r.game_id = g.id
                GROUP BY g.id, p.name, g.played_at
                ORDER BY g.played_at DESC
                LIMIT 10
                """;

        printQuery(sql, "recent");
    }

    public void printPlayerWinCounts() {
        String sql = """
                SELECT p.name AS player, COUNT(g.id) AS wins
                FROM players p
                JOIN games g ON g.winner_player_id = p.id
                GROUP BY p.id, p.name
                ORDER BY wins DESC
                """;

        printQuery(sql, "wins");
    }

    public void printHighestScores() {
        String sql = """
                SELECT p.name AS player, s.score
                FROM scores s
                JOIN players p ON s.player_id = p.id
                ORDER BY s.score DESC
                LIMIT 10
                """;

        printQuery(sql, "scores");
    }

    private void printQuery(String sql, String type) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                if (type.equals("recent")) {
                    System.out.println("Game " + rs.getInt("id")
                            + " | winner: " + rs.getString("winner")
                            + " | rounds: " + rs.getInt("rounds")
                            + " | played at: " + rs.getString("played_at"));
                } else if (type.equals("wins")) {
                    System.out.println(rs.getString("player") + ": " + rs.getInt("wins") + " wins");
                } else {
                    System.out.println(rs.getString("player") + ": " + rs.getInt("score"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}