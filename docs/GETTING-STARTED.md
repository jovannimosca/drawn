# Getting Started

## Prerequisites

- Android Studio Ladybug+ or command-line tools
- Android SDK API 36
- Kotlin 2.0+ toolchain

## Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd drawn
   ```

2. **Sync Gradle**
   ```bash
   ./gradlew --stop && ./gradlew sync
   ```

3. **Build the debug APK**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on device/emulator**
   ```bash
   ./gradlew installDebug
   ```

## Running Tests

```bash
./gradlew test        # Unit tests
./gradlew testDebug   # Unit tests for debug variant
./gradlew connectedAndroidTest  # Instrumented tests (requires device)
```

## Code Quality

```bash
./gradlew lint        # Android lint
./gradlew ktlintCheck # Kotlin style
./gradlew detekt      # Static analysis
```

Run all checks:
```bash
./gradlew check
```

## Project Overview

- **Screens**: Reading list, Add reading wizard, Reading detail
- **Navigation**: Bottom-to-detail flow with wizard for new readings
- **Data**: Pre-populated 78-card RWS deck with common spreads

## Next Steps

- Read [ARCHITECTURE.md](ARCHITECTURE.md) for design details
- Read [DEVELOPMENT.md](DEVELOPMENT.md) for contribution guidelines
- Read [TESTING.md](TESTING.md) for testing approach
