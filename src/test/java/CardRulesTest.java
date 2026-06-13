import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardRulesTest {

    @Test
    void sameColorCardIsLegal() {
        assertTrue(CardRules.isLegal("R2", "R9", ""));
    }

    @Test
    void sameNumberCardIsLegal() {
        assertTrue(CardRules.isLegal("G9", "R9", ""));
    }

    @Test
    void mismatchCardIsIllegal() {
        assertFalse(CardRules.isLegal("B3", "R9", ""));
    }
}