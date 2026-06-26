# Final Project Report

## Overview

This project extends the original UNO CLI implementation into a more complete command-line game.

The project includes:

* complete UNO deck generation
* legal move validation
* Skip, Reverse, Draw Two, Wild and Wild Draw Four cards
* round scoring
* multi-round play
* configurable target score
* SQLite persistence using MyBatis
* automated unit tests
* command-line interface
* documentation

## Main Changes

The following improvements were implemented:

* extracted reusable card rule logic into `CardRules`
* added complete UNO deck generation
* expanded automated unit tests
* implemented UNO call support
* added missed UNO penalty
* added configurable target score
* improved game loop
* improved documentation
* added persistence for games, rounds and scores

## Testing

The project uses JUnit tests.

The test suite verifies:

* deck composition
* legal card play
* card parsing
* turn rules
* persistence layer

All tests pass successfully.

## Limitations

The implementation intentionally keeps several simplifications:

* no Draw Two stacking
* no Wild Draw Four challenge rule
* simple bot strategy
* text-based interface

## Result

The final project provides a playable command-line UNO implementation together with persistence, automated tests and documentation.
