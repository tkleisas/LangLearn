# LangLearn

An Android app for learning foreign languages through spaced repetition, quizzes, grammar exercises, and phrasebooks.

## Supported Languages

- Chinese (中文)
- Spanish (Español)

## Features

- **Flashcards** — SM-2 spaced repetition algorithm with animated card flip and 4-button rating (Again, Hard, Good, Easy)
- **Progressive Lessons** — Gradual vocabulary and grammar lessons across 5 levels per language
- **Quizzes** — Multiple choice and typing quiz modes with configurable question count
- **Grammar Exercises** — Multiple choice and fill-in-the-blank exercises tied to grammar lessons
- **Phrasebook** — 8 topic categories (Greetings, Travel, Food, Shopping, Emergency, Numbers & Time, Daily Life, Small Talk) with search
- **Pronunciation Practice** — Text-to-speech with language detection and common phrase presets
- **Reference Guide** — Alphabet with pronunciation hints, numbers grid, days/months vocabulary
- **Progress Tracking** — Daily stats, streaks, practice time, and lesson completion tracking
- **Offline-first** — All content bundled; no internet required

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room with Flow-based reactive queries
- **Navigation**: Jetpack Navigation Compose
- **TTS**: Android TextToSpeech with utterance callbacks
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34

## Project Structure

```
app/src/main/java/com/langlearn/app/
├── data/
│   ├── database/
│   │   ├── dao/          # Room DAO interfaces
│   │   ├── entity/       # Database entities
│   │   └── AppDatabase.kt
│   ├── repository/       # Repository layer
│   └── seed/             # Seed data loader
├── domain/
│   ├── model/
│   └── usecase/
├── ui/
│   ├── components/
│   ├── navigation/       # NavHost and route definitions
│   ├── screens/
│   │   ├── flashcards/
│   │   ├── grammar/
│   │   ├── home/
│   │   ├── lessons/
│   │   ├── phrasebook/
│   │   ├── pronunciation/
│   │   ├── quiz/
│   │   ├── reference/
│   │   └── review/
│   ├── theme/
│   └── viewmodel/        # ViewModels with factory pattern
└── util/
    ├── spacedrepetition/ # SM-2 algorithm
    └── tts/              # TTS manager
```

## Seed Data

Each language includes comprehensive content:

| Content | Chinese | Spanish |
|---------|---------|---------|
| Vocabulary words | 52 | 54 |
| Phrases | 50 | 59 |
| Phrase categories | 8 | 8 |
| Grammar rules | 5 | 5 |
| Grammar exercises | 13 | 15 |
| Alphabet entries | 19 | 29 |

## Building

1. Open the project in Android Studio (Hedgehog or later)
2. Sync Gradle
3. Run on device or emulator (API 26+)

## License

MIT
