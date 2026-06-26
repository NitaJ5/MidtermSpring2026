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

    @Test
    void deckHasCorrectUnoComposition() {
        java.util.List<String> deck = CardRules.createDeck();

        assertEquals(108, deck.size());

        assertEquals(1, java.util.Collections.frequency(deck, "R0"));
        assertEquals(1, java.util.Collections.frequency(deck, "Y0"));
        assertEquals(1, java.util.Collections.frequency(deck, "G0"));
        assertEquals(1, java.util.Collections.frequency(deck, "B0"));

        assertEquals(2, java.util.Collections.frequency(deck, "R1"));
        assertEquals(2, java.util.Collections.frequency(deck, "Y9"));
        assertEquals(2, java.util.Collections.frequency(deck, "RS"));
        assertEquals(2, java.util.Collections.frequency(deck, "BR"));
        assertEquals(2, java.util.Collections.frequency(deck, "G+2"));

        assertEquals(4, java.util.Collections.frequency(deck, "W"));
        assertEquals(4, java.util.Collections.frequency(deck, "W4"));
    }


    @Test
    void sameActionCardIsLegal() {
        assertTrue(CardRules.isLegal("BS", "YS", ""));
    }

    @Test
    void wildCardIsAlwaysLegal() {
        assertTrue(CardRules.isLegal("W", "R9", ""));
    }

    @Test
    void wildDrawFourCardIsAlwaysLegal() {
        assertTrue(CardRules.isLegal("W4", "G5", ""));
    }


    @Test
    void calledColorMakesCardLegal() {
        assertTrue(CardRules.isLegal("B3", "W", "B"));
    }

    @Test
    void wrongCalledColorMakesCardIllegal() {
        assertFalse(CardRules.isLegal("B3", "W", "R"));
    }



    @Test
    void numberMethodReturnsCorrectValue() {
        assertEquals(7, CardRules.number("R7"));
        assertEquals(-1, CardRules.number("W"));
    }

    @Test
    void oneCardHandNeedsUnoCall() {
        assertTrue(CardRules.needsUnoCall(1));
        assertFalse(CardRules.needsUnoCall(2));
        assertFalse(CardRules.needsUnoCall(0));
    }

    @Test
    void missedUnoPenaltyIsTwoCards() {
        assertEquals(2, CardRules.missedUnoPenaltyCards());
    }

}