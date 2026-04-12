# Configuration

## Gradle Configuration

### Version Catalog (`gradle/libs.versions.toml`)

Centralized dependency management. Update versions here to ensure consistency.

### Build Types

- **debug** - Development builds with debug symbols
- **release** - Production builds requiring signing config

### Signing

Release builds require a keystore. For local development, a placeholder `drawn-release.jks` is included with dummy credentials.

To configure your own signing:
1. Create a keystore or use Android's "Generate Signed APK" wizard
2. Add to `local.properties`:
   ```
   keystore.path=/path/to/your/keystore.jks
   keystore.password=your_password
   keystore.alias=your_alias
   keystore.alias_password=your_alias_password
   ```
3. Reference in `app/build.gradle.kts`

## Theme Configuration

### Colors (`ui/theme/Color.kt`)

The app uses a custom dark theme with purple/gold palette. Edit the color definitions to adjust the visual appearance.

### Typography (`ui/theme/Type.kt`)

Material 3 typography scale. Currently uses 400/500 weights only.

## Database Schema

### Room Database

Database name: `drawn.db`

Entities:
- `CardEntity` - Card definitions
- `DeckEntity` - Card decks
- `ReadingEntity` - Reading records
- `ReadingCardEntity` - Card assignments to readings
- `ReadingPhotoEntity` - Photo attachments
- `SpreadEntity` - Spread definitions
- `PositionEntity` - Spread positions

### Pre-population

Card data is pre-populated from `assets/database/` using Room's `createFromAsset()`.

## Image Assets

Card images stored in `res/drawable-nodpi/` with naming convention:
- Major Arcana: `card_major_00_fool.png` (0-21)
- Minor Arcana: `card_minor_pentacles_ace.png` (suit_rank)

The `nodpi` qualifier prevents Android from scaling bundled card images.

## GitHub Actions

CI/CD configured in `.github/workflows/`. Required secrets:
- None for basic CI (tests, lint, detekt)
- Release workflow uses environment-based version bump
