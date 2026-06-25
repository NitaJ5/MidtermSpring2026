package persistence.records;

public record RoundRecord(int id, int gameId, int roundNumber, Integer winnerPlayerId) {
}