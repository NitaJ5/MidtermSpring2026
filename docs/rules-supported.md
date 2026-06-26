# Supported UNO Rules

This project implements a text-based UNO game using the local final project rule reference.

## Implemented Rules

### Deck Composition

Implemented.

The deck contains:

- four colors: red, yellow, green, blue
- one 0 card per color
- two copies of each number 1-9 per color
- two Skip cards per color
- two Reverse cards per color
- two Draw Two cards per color
- four Wild cards
- four Wild Draw Four cards

Total deck size: 108 cards.

### Legal Play Validation

Implemented.

A card can be played if it matches:

- current color
- number
- action type
- called wild color
- or if it is Wild / Wild Draw Four

### Skip

Implemented.

Skip causes the next player to lose their turn.

### Reverse

Implemented.

Reverse changes the direction of play.

In a two-player game, Reverse is treated like Skip.

### Draw Two

Implemented.

Draw Two makes the next player draw two cards and lose their turn.

Stacking is not implemented.

### Wild

Implemented.

Wild lets the player choose the next active color.

### Wild Draw Four

Implemented.

Wild Draw Four lets the player choose the next active color. The next player draws four cards and loses their turn.

Challenge rules are not implemented.

### Draw And Pass

Implemented.

If a player draws a card and the drawn card is legal, the implementation allows it to be played immediately.

If the drawn card is not played, the turn passes.

### UNO Call And Penalty

Implemented.

When a player has one card left, the game detects the one-card state.

Bots call UNO automatically.

Human players are prompted to call UNO. If the human player does not call UNO, the player draws two penalty cards.

### Round End

Implemented.

A round ends when a player has no cards left.

### Scoring

Implemented.

The round winner receives points for the cards remaining in other players' hands:

- number cards: face value
- Skip: 20
- Reverse: 20
- Draw Two: 20
- Wild: 50
- Wild Draw Four: 50

### Multi-Round Target Score

Implemented.

The game continues through rounds until the target score is reached. The default target score is 500.

The target can be changed with:

```bash
--target N
```

The maximum number of rounds can still be limited with:

```bash
--games N
```

## Simplifications

The project uses the following simplifications:

- no Draw Two stacking
- no Wild Draw Four challenge rule
- simple bot strategy
- text-only CLI
- starting wild cards are skipped until a non-wild starting card is drawn