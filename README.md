# Drawn

A local-only Android app for tracking and organizing tarot card readings.

## Overview

Drawn lets you record tarot readings by selecting a spread, assigning cards to positions, adding notes, and attaching photos of your physical layout. Built with Kotlin and Jetpack Compose, designed for personal use with the option to share with the tarot community.

## Features

- **Reading Management** - Create, view, edit, and delete tarot card readings
- **Spread Selection** - Choose from common spreads (Celtic Cross, Three Card, Past/Present/Future)
- **Card Assignment** - Browse and assign cards from the 78-card Rider-Waite-Smith deck
- **Reversed Cards** - Toggle card orientation with smooth flip animations
- **Notes & Photos** - Add freeform text notes and attach photos from camera or gallery
- **Reading History** - Browse chronological history with search capability
- **Dark Theme** - Mystical aesthetic with nebula backgrounds and gold accents
- **Local Storage** - All data stored on-device with Room database

## Tech Stack

- **Language:** Kotlin 2.2.21
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM with Clean Architecture layers
- **DI:** Hilt
- **Database:** Room with Flow
- **Navigation:** Navigation Compose 3
- **Image Loading:** Coil 3
- **Testing:** JUnit 5, MockK, Turbine, Robolectric

## Requirements

- Android 8.0+ (API 26)
- Gradle 9.x with Kotlin 2.0+

## Building

```bash
./gradlew assembleDebug    # Debug APK
./gradlew assembleRelease  # Release APK (requires signing config)
```

## Project Structure

```
app/src/main/kotlin/com/example/drawn/
├── data/           # Data layer (Room, repositories)
├── domain/         # Domain models
├── di/             # Hilt dependency injection
└── ui/             # Compose UI (screens, components, theme)
```

## License

Open source - see LICENSE file for details.
