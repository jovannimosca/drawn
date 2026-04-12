# Architecture

Drawn follows Clean Architecture with MVVM presentation, organized into distinct layers with unidirectional data flow.

## Architecture Layers

### UI Layer (`ui/`)

Jetpack Compose screens and components with Material 3 theming.

```
ui/
├── addreading/      # Add reading wizard screens
├── components/      # Shared composables
├── navigation/      # Navigation graph and routes
├── readingdetail/   # Reading detail screen
├── readinglist/     # Reading list screen
└── theme/          # Material 3 dark theme
```

### Domain Layer (`domain/model/`)

Pure Kotlin data classes representing business entities.

- `Card` - Tarot card with name, image resource, keywords
- `Reading` - Complete reading with spread, cards, notes
- `Spread` - Spread definition with positions
- `ReadingCard` - Card assigned to a position in a reading

### Data Layer (`data/`)

Room database with repositories providing reactive data streams.

```
data/
├── database/
│   ├── dao/        # Data Access Objects
│   ├── entity/     # Room entities
│   ├── converter/  # Type converters
│   └── migration/  # Schema migrations
└── repository/     # Repository implementations
```

### DI Layer (`di/`)

Hilt modules wiring up dependencies.

- `DatabaseModule` - Room database and DAOs
- `RepositoryModule` - Repository bindings
- `AppModule` - Application-level providers

## Data Flow

```
User Action → ViewModel (StateFlow) → Repository → DAO → Room → SQLite
                ↓
         Compose UI (collectAsStateWithLifecycle)
```

## Navigation

Navigation Compose 3 with type-safe routes defined as `@Serializable` data classes.

## Key Design Decisions

1. **Local-only storage** - Room database with no network layer
2. **Freeform entry** - Users record readings retrospectively, not during draw
3. **Bundled card images** - RWS images in `res/drawable-nodpi/`
4. **80% test coverage** - Required by CI/CD pipeline
