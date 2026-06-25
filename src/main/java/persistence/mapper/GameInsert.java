package persistence.mapper;

public class GameInsert {
    private int id;
    private int winnerPlayerId;

    public GameInsert(int winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }

    public int getId() {
        return id;
    }

    public int getWinnerPlayerId() {
        return winnerPlayerId;
    }
}