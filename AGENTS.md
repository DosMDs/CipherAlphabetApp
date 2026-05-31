# Project instructions

## Language

Use Kotlin for Android app code.

## UI

Use Jetpack Compose and Material 3.

## Architecture

Keep conversion logic separate from UI.

Preferred structure:

- app/src/main/java/com/dosmds/cipheralphabet/core
- app/src/main/java/com/dosmds/cipheralphabet/core/alphabet
- app/src/main/java/com/dosmds/cipheralphabet/core/converter
- app/src/main/java/com/dosmds/cipheralphabet/ui
- app/src/main/java/com/dosmds/cipheralphabet/ui/screens
- app/src/main/java/com/dosmds/cipheralphabet/ui/components

## Rules

- Do not put conversion logic inside Composable functions.
- Add unit tests for every converter.
- Keep functions small and readable.
- Prefer immutable data.
- Do not add network features.
- The app must work offline.
- Russian UI text is preferred.
- Support English and Russian alphabets.
- Russian alphabet must support both variants: with Ё and without Ё.
- Morse and Braille mappings must be explicit tables.