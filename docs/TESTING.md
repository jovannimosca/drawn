# Testing

## Overview

80% code coverage required. Tests run in CI on every pull request.

## Test Types

### Unit Tests

Located in `app/src/test/`:
- ViewModel tests
- Repository tests
- DAO tests (in-memory Room)

Run with:
```bash
./gradlew testDebugUnitTest
```

### Instrumented Tests

Located in `app/src/androidTest/`:
- Compose UI tests
- Integration tests

Run with:
```bash
./gradlew connectedDebugAndroidTest
```

## Testing Libraries

- **JUnit 5** - Test framework
- **MockK** - Mocking Kotlin classes
- **Turbine** - Flow testing
- **Robolectric** - JVM-based Android tests
- **createComposeRule()** - Compose UI testing

## Writing Tests

### ViewModel Tests

```kotlin
@ExtendWith(RobolectricExtension::class)
class MyViewModelTest {
    @Test
    fun `test state emission`() = runTest {
        val viewModel = MyViewModel(repository)
        // Assert StateFlow emissions with Turbine
        viewModel.uiState.test {
            assertEquals(expected, awaitItem())
        }
    }
}
```

### Repository Tests

Mock DAOs with MockK:
```kotlin
@Test
fun `test repository calls dao`() = runTest {
    coEvery { dao.getAll() } returns flowOf(listOf(item))
    val result = repository.getAll()
    assertEquals(1, result.first().size)
}
```

### DAO Tests

Use Room in-memory database:
```kotlin
@RunWith(RobolectricExtension::class)
class MyDaoTest {
    @get:Rule val helper = RoomDatabaseMigrationRule()
    
    @Test
    fun `test insert and retrieve`() = runTest {
        dao.insert(item)
        dao.getAll().test {
            assertTrue(awaitItem().isNotEmpty())
        }
    }
}
```

## Coverage

View coverage reports:
```bash
./gradlew koverReport
open app/build/reports/kover/index.html
```

CI fails if coverage drops below 80%.
