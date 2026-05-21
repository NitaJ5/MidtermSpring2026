# Refactoring Report

## Refactoring Performed

### 1. Extracted Card Rule Logic

Created a new class:

- `CardRules.java`

Moved the following methods from `Main.java` into `CardRules.java`:

- `color()`
- `rank()`
- `number()`
- `isLegal()`

Updated `Main.java` to use:

`CardRules.isLegal(...)`

instead of directly handling legality logic.

---

## Design Smells Addressed

The refactoring improved the following design problems:

- duplicated legality checks
- mixed game logic inside Main class
- primitive-heavy card rule handling

---

## Behavior Preservation

Behavior was preserved using characterization tests.

Executed:

```bash
java -cp out Main --self-test
```

Result:

```text
Passed 35 characterization checks.
```

---

## Refactoring Techniques Used

- Extract Method
- Extract Class
- Move Method

---

## Git Commits

Example commits:

- Add characterization tests for UNO behavior
- Replace duplicated legality logic with isLegal method
- Extract card rule logic into CardRules class