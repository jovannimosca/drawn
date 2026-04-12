# Development

## Project Structure

```
app/src/main/kotlin/com/example/drawn/
├── data/
│   ├── database/        # Room setup, entities, DAOs
│   └── repository/      # Data repositories
├── domain/
│   └── model/          # Domain models
├── di/                 # Hilt modules
└── ui/
    ├── addreading/     # Add reading wizard
    ├── components/     # Shared UI components
    ├── navigation/      # Nav graph
    ├── readingdetail/   # Reading detail screen
    ├── readinglist/     # Reading list screen
    └── theme/          # Material 3 theme
```

## Adding a New Screen

1. Create screen composable in appropriate `ui/` subdirectory
2. Create ViewModel extending `@HiltViewModel`
3. Add navigation route in `ui/navigation/`
4. Use `hiltViewModel()` to inject ViewModel
5. Add entry to navigation graph

## Adding Database Entities

1. Create entity in `data/database/entity/`
2. Add DAO methods in `data/database/dao/`
3. Update schema version in `DatabaseModule`
4. Create migration if schema changes

## UI Components

Shared components in `ui/components/`:
- `EmptyState` - Empty list placeholder
- `ErrorBanner` - Error display
- `GoldDivider` - Themed divider
- `NebulaBackground` - Background decoration

## Code Style

Follow Kotlin idioms and Material 3 guidelines. Run linting before committing:

```bash
./gradlew ktlintCheck detekt
```

## Git Workflow

1. Create feature branch from `main`
2. Make changes with tests
3. Ensure all checks pass: `./gradlew check`
4. Create pull request
5. Squash merge to main
