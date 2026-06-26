# Midterm UNO CLI

This is a standalone CLI UNO-like game.

The code is written as plausible feature-grown Java: almost everything lives in one procedural `Main` class. It works, but it has mixed responsibilities, duplicated rule logic, primitive-heavy card handling, global state, and condition-heavy gameplay code. The goal is to refactor it safely, not rewrite it.

## Compile

```bash
scripts/compile.sh
```

## Run Bot Games

```bash
scripts/run.sh --bots 3 --games 5 --quiet
```

## Run Interactive Game

```bash
scripts/run.sh --human --bots 2 --games 1
```

Card input examples:

```text
R5   red 5
YS   yellow skip
BR   blue reverse
G+2  green draw two
W    wild
W4   wild draw four
draw draw a card
```

## Characterization Checks

```bash
scripts/test.sh
```

## Submission

Submit your work through GitHub:

1. Fork this repository to your GitHub account.
2. Clone your fork locally.
3. Complete the midterm work in your fork.
4. Commit your changes with clear commit messages.
5. Push your branch to GitHub.
6. Open a pull request from your fork back to the original repository.

Your pull request must include:

* refactored source code
* characterization tests
* `docs/refactoring-report.md`
* `docs/extension-readiness.md`

Do not submit a zip file instead of a pull request unless the instructor explicitly asks for it.

## Rules

See `docs/rules.html` for the implemented game rules.

## Midterm Materials

* `docs/midterm-exam.md`: midterm brief
* `docs/rubric.md`: grading rubric
* `docs/refactoring-guide.md`: suggested refactoring path

## Assignment 4

### Local Build

Compile the project:

```bash
mvn compile
```

### Run Tests

Run all tests:

```bash
mvn test
```

### Package Application

Create the executable JAR:

```bash
mvn package
```

### Run Application

Run the packaged JAR:

```bash
java -jar target/midterm-spring-2026-1.0-SNAPSHOT.jar
```

### Docker Build

The Dockerfile uses a multi-stage build. The build stage uses Maven to compile and package the application from `pom.xml` and `src`. The runtime stage copies the generated JAR from the build stage, allowing the image to be built from a clean checkout without requiring a local `target` directory.


Build the Docker image:

```bash
docker build -t uno-cli .
```

### Docker Run

Run the application inside Docker:

```bash
docker run --rm uno-cli
```

Run quiet bot games:

```bash
docker run --rm uno-cli --bots 3 --games 5 --quiet
```

## Assignment 5: ORM Persistence For UNO

This project uses SQLite as the local development database and MyBatis as the persistence mapper framework.

### Selected Database

SQLite is used for local persistence.

The database file is created automatically when the program runs:

```bash
uno.db
```

### Persistence Framework / Mapping Approach

The project uses MyBatis mapper interfaces with SQLite:
- persistence.DatabaseManager
- persistence.GameRepository

Game logic does not contain raw SQL directly. SQL access is handled inside the repository classes.

### Schema

The database contains tables for:

- games
- players
- scores

The schema supports storing:

- player names
- game results
- winner
- rounds played
- per-player scores
- timestamp


Tables are created automatically by calling:

DatabaseManager.initializeDatabase()

### Running Persistence Tests

Run all tests:

```bash
mvn test
```

The persistence test initializes the database and verifies that game data and scores can be saved.

### Running The Game And Saving History

Run the game normally:

```bash
java -jar target/midterm-spring-2026-1.0-SNAPSHOT.jar
```

When a game ends, player records, game information, rounds, and scores are persisted in the normalized SQLite schema using MyBatis.
### Viewing Game History And Statistics

List recent games:

```bash
java -jar target/midterm-spring-2026-1.0-SNAPSHOT.jar --recent-games
```

Show player win counts:

```bash
java -jar target/midterm-spring-2026-1.0-SNAPSHOT.jar --win-counts
```

Show highest scores:

```bash
java -jar target/midterm-spring-2026-1.0-SNAPSHOT.jar --highest-scores
```

## Final Project: Full UNO Product

This final project extends the UNO CLI into a fuller UNO product.

### Final Project Features

Implemented rules include:

- correct 108-card UNO deck composition
- legal play validation
- Skip
- Reverse
- Draw Two
- Wild
- Wild Draw Four
- draw/pass behavior
- UNO call and missed-UNO penalty
- round scoring
- multi-round game to target score

### Final Project Commands

Run tests:

```bash
mvn test