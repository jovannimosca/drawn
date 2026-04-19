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
3. Update schema version in `@Database` annotation
4. Create migration in `data/database/migration/`
5. Add migration to AppDatabase `.addMigrations()`

## Database Migrations

This project uses Room migrations for schema changes. All database updates MUST include migrations.

### Why Migrations Matter

- **Fresh install**: Data seeded from `assets/database/drawn_prepopulated.db`
- **Existing users**: Migrations run to update schema + add new data
- **No fallback**: Do NOT use `fallbackToDestructiveMigration()` — it loses user data

### Adding a New Spread/Card (Data Update)

When adding new default data (e.g., a new spread), you MUST include it in the migration:

```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Schema changes (if any)
        // db.execSQL("ALTER TABLE spreads ADD COLUMN ...")

        // 2. New default data - CRITICAL for existing users
        db.execSQL("""
            INSERT INTO spreads (id, name, description, positionsJson)
            VALUES (4, 'New Spread', 'Description', '[...]')
        """)
    }
}
```

### Required Steps

1. Export new schema: `./gradlew room.schema`
2. Create migration file in `data/database/migration/`
3. Add migration to AppDatabase:
   ```kotlin
   .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
   ```
4. Update asset DB if needed
5. Test migration path on device with existing data

### Asset Database

The pre-populated database is in `app/src/main/assets/database/drawn_prepopulated.db`.
- Contains default deck, cards, and spreads
- Must match current schema version
- Update when schema changes for fresh installs

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
