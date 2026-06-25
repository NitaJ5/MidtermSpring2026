package persistence.records;

public record GameRecord(int id, Integer winnerPlayerId, String playedAt) {
}