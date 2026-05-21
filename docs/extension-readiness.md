# Extension Readiness

## Possible Extension

The current refactored design would support adding new card rules more easily.

Example extensions:

- smarter bot strategy
- replay log
- additional UNO rules
- new action cards

---

## Why The Design Is Better

The new `CardRules` class separates rule logic from the main game loop.

This makes rule behavior easier to test independently on the CLI interface.

The legality logic is now centralized instead of duplicated across multiple locations.

---

## Remaining Difficulties

The project still has some design limitations:

- large Main class
- global mutable state
- CLI logic mixed with gameplay flow
- bot logic still inside Main.java

These areas would need additional refactoring for larger extensions.