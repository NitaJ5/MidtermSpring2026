package persistence.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GameMapper {

    @Insert("INSERT OR IGNORE INTO players (name) VALUES (#{name})")
    void insertPlayerIfMissing(@Param("name") String name);

    @Select("SELECT id FROM players WHERE name = #{name}")
    int findPlayerIdByName(@Param("name") String name);

    @Insert("INSERT INTO games (winner_player_id) VALUES (#{winnerPlayerId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertGame(GameInsert game);

    @Insert("INSERT INTO rounds (game_id, round_number, winner_player_id) VALUES (#{gameId}, #{roundNumber}, #{winnerPlayerId})")
    void insertRound(@Param("gameId") int gameId,
                     @Param("roundNumber") int roundNumber,
                     @Param("winnerPlayerId") int winnerPlayerId);

    @Insert("INSERT INTO scores (game_id, player_id, score) VALUES (#{gameId}, #{playerId}, #{score})")
    void insertScore(@Param("gameId") int gameId,
                     @Param("playerId") int playerId,
                     @Param("score") int score);

    @Select("""
            SELECT g.id AS id, p.name AS winner, COUNT(r.id) AS rounds, g.played_at AS playedAt
            FROM games g
            LEFT JOIN players p ON g.winner_player_id = p.id
            LEFT JOIN rounds r ON r.game_id = g.id
            GROUP BY g.id, p.name, g.played_at
            ORDER BY g.played_at DESC
            LIMIT 10
            """)
    List<RecentGameRow> recentGames();

    @Select("""
            SELECT p.name AS player, COUNT(g.id) AS wins
            FROM players p
            JOIN games g ON g.winner_player_id = p.id
            GROUP BY p.id, p.name
            ORDER BY wins DESC
            """)
    List<WinCountRow> winCounts();

    @Select("""
            SELECT p.name AS player, s.score AS score
            FROM scores s
            JOIN players p ON s.player_id = p.id
            ORDER BY s.score DESC
            LIMIT 10
            """)
    List<HighestScoreRow> highestScores();
}