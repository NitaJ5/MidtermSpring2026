package persistence.records;

public record ScoreRecord(int id, int gameId, int playerId, int score) {
}