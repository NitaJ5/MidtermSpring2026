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

}