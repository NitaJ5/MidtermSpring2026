package persistence;

import org.apache.ibatis.session.SqlSession;
import persistence.mapper.GameInsert;
import persistence.mapper.GameMapper;
import persistence.mapper.HighestScoreRow;
import persistence.mapper.RecentGameRow;
import persistence.mapper.WinCountRow;

public class GameRepository {

    public int findOrCreatePlayer(String name) {
        try (SqlSession session = MyBatisUtil.getSessionFactory().openSession(true)) {
            GameMapper mapper = session.getMapper(GameMapper.class);
            mapper.insertPlayerIfMissing(name);
            return mapper.findPlayerIdByName(name);
        }
    }

    public int saveGame(int winnerPlayerId) {
        try (SqlSession session = MyBatisUtil.getSessionFactory().openSession(true)) {
            GameMapper mapper = session.getMapper(GameMapper.class);
            GameInsert game = new GameInsert(winnerPlayerId);
            mapper.insertGame(game);
            return game.getId();
        }
    }

    public void saveRound(int gameId, int roundNumber, int winnerPlayerId) {
        try (SqlSession session = MyBatisUtil.getSessionFactory().openSession(true)) {
            GameMapper mapper = session.getMapper(GameMapper.class);
            mapper.insertRound(gameId, roundNumber, winnerPlayerId);
        }
    }

    public void saveScore(int gameId, int playerId, int score) {
        try (SqlSession session = MyBatisUtil.getSessionFactory().openSession(true)) {
            GameMapper mapper = session.getMapper(GameMapper.class);
            mapper.insertScore(gameId, playerId, score);
        }
    }

    public void printRecentGames() {
        try (SqlSession session = MyBatisUtil.getSessionFactory().openSession(true)) {
            GameMapper mapper = session.getMapper(GameMapper.class);
            for (RecentGameRow row : mapper.recentGames()) {
                System.out.println("Game " + row.id()
                        + " | winner: " + row.winner()
                        + " | rounds: " + row.rounds()
                        + " | played at: " + row.playedAt());
            }
        }
    }

    public void printPlayerWinCounts() {
        try (SqlSession session = MyBatisUtil.getSessionFactory().openSession(true)) {
            GameMapper mapper = session.getMapper(GameMapper.class);
            for (WinCountRow row : mapper.winCounts()) {
                System.out.println(row.player() + ": " + row.wins() + " wins");
            }
        }
    }

    public void printHighestScores() {
        try (SqlSession session = MyBatisUtil.getSessionFactory().openSession(true)) {
            GameMapper mapper = session.getMapper(GameMapper.class);
            for (HighestScoreRow row : mapper.highestScores()) {
                System.out.println(row.player() + ": " + row.score());
            }
        }
    }
}