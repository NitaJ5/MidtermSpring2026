import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TurnRulesTest {

    @Test
    void skipCardRankIsRecognized() {
        assertEquals("SKIP", CardRules.rank("RS"));
    }

    @Test
    void reverseCardRankIsRecognized() {
        assertEquals("REVERSE", CardRules.rank("BR"));
    }

    @Test
    void drawTwoRankIsRecognized() {
        assertEquals("DRAW_TWO", CardRules.rank("G+2"));
    }

    @Test
    void wildRankIsRecognized() {
        assertEquals("WILD", CardRules.rank("W"));
    }

    @Test
    void wildDrawFourRankIsRecognized() {
        assertEquals("WILD_DRAW_FOUR", CardRules.rank("W4"));
    }

    @Test
    void scoringUsesUnoCardValues() {
        assertEquals(5, Main.points("R5"));
        assertEquals(20, Main.points("YS"));
        assertEquals(20, Main.points("BR"));
        assertEquals(20, Main.points("G+2"));
        assertEquals(50, Main.points("W"));
        assertEquals(50, Main.points("W4"));
    }

    @Test
    void highestScoreReturnsLargestPlayerScore() {
        Main.playerNames.clear();
        Main.playerNames.add("A");
        Main.playerNames.add("B");
        Main.playerNames.add("C");

        Main.scores[0] = 10;
        Main.scores[1] = 120;
        Main.scores[2] = 50;

        assertEquals(120, Main.highestScore());
    }

    @Test
    void botDrawsWhenNoLegalCardExists() {
        Main.upCard = "R9";
        Main.calledColor = "";

        java.util.ArrayList<String> hand = new java.util.ArrayList<>();
        hand.add("B3");
        hand.add("G4");

        assertEquals(-1, Main.chooseBotCard(hand));
    }

    @Test
    void botCanPlayDrawnCardWhenLegal() {
        assertTrue(CardRules.isLegal("R5", "R9", ""));
    }




    @Test
    void drawnCardMayBePlayedWhenLegal() {
        assertTrue(CardRules.drawnCardMayBePlayed("R5", "R9", ""));
    }

    @Test
    void playerMustPassAfterDrawingIllegalCard() {
        assertTrue(CardRules.mustPassAfterDrawing("B3", "R9", ""));
    }

    @Test
    void targetScoreIsReachedWhenScoreMeetsOrExceedsTarget() {
        assertTrue(CardRules.reachedTargetScore(500, 500));
        assertTrue(CardRules.reachedTargetScore(520, 500));
        assertFalse(CardRules.reachedTargetScore(499, 500));
    }






}