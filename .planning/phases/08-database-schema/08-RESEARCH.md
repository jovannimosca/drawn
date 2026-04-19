# Phase 8: Database Schema - Research

**Researched:** 2026-04-19
**Phase:** 8 - Database Schema
**Objective:** How to implement relational schema for custom decks, tags, and favorites

---

## Domain Analysis

### What We Need to Know

1. **Room many-to-many relationships** - How to implement Reading-Tag junction table
2. **Entity extension patterns** - How to add isFavorite to existing ReadingEntity
3. **Auto-migration** - How to handle schema version bumps
4. **Custom card storage** - How to structure CustomCardEntity for deck cards

---

## Technical Approach

### Entity Design

#### CustomCardEntity (New)

```kotlin
@Entity(
    tableName = "custom_cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId")]
)
data class CustomCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val name: String,
    val imagePath: String?,
    val uprightMeaning: String?,
    val reversedMeaning: String?,
    val sortOrder: Int = 0
)
```

**Why:** Each card belongs to a deck via foreign key. Image path optional for card images. Sort order for manual ordering.

#### TagEntity (New)

```kotlin
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String? // Hex string like "#FF5722"
)
```

**Why:** Simple entity. Color nullable for default theme colors.

#### ReadingTagCrossRef (Junction)

```kotlin
@Entity(
    tableName = "reading_tags",
    primaryKeys = ["readingId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = ReadingEntity::class,
            parentColumns = ["id"],
            childColumns = ["readingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("readingId"), Index("tagId")]
)
data class ReadingTagCrossRef(
    val readingId: Long,
    val tagId: Long
)
```

**Why:** Junction table for many-to-many. Room's `@Junction` annotation creates efficient indexed lookups.

#### ReadingWithTags (Relation)

```kotlin
data class ReadingWithTags(
    @Embedded val reading: ReadingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ReadingTagCrossRef::class)
    )
    val tags: List<TagEntity>
)
```

---

### Modifying Existing Entities

#### Add isFavorite to ReadingEntity

```kotlin
@Entity(...)
data class ReadingEntity(
    ...
    val isFavorite: Boolean = false
)
```

**Migration:** This is an additive change - no data loss. Room auto-migration handles it.

---

### DAO Operations

#### TagDao (New)

```kotlin
@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name")
    fun getAllTags(): Flow<List<TagEntity>>

    @Insert
    suspend fun insert(tag: TagEntity): Long

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteById(tagId: Long)
}
```

#### ReadingTagDao (New)

```kotlin
@Dao
interface ReadingTagDao {
    @Insert
    suspend fun insert(crossRef: ReadingTagCrossRef)

    @Delete
    suspend fun delete(crossRef: ReadingTagCrossRef)

    @Query("SELECT tagId FROM reading_tags WHERE readingId = :readingId")
    fun getTagsForReading(readingId: Long): Flow<List<Long>>

    @Transaction
    @Query("SELECT * FROM readings WHERE id = :readingId")
    fun getReadingWithTags(readingId: Long): Flow<ReadingWithTags?>
}
```

#### CustomCardDao (New)

```kotlin
@Dao
interface CustomCardDao {
    @Query("SELECT * FROM custom_cards WHERE deckId = :deckId ORDER BY sortOrder")
    fun getCardsForDeck(deckId: Long): Flow<List<CustomCardEntity>>

    @Insert
    suspend fun insert(card: CustomCardEntity): Long

    @Update
    suspend fun update(card: CustomCardEntity)

    @Delete
    suspend fun delete(card: CustomCardEntity)
}
```

---

### Database Version Bump

- Current version: 1 (from v1.0)
- New version: 2
- Export schema to JSON for version tracking

```kotlin
@Database(
    version = 2,
    entities = [
        ReadingEntity::class,
        TagEntity::class,
        ReadingTagCrossRef::class,
        CustomCardEntity::class
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() { ... }
```

---

## Validation Architecture

### Unit Tests (TestDatabase)

Using in-memory Room database for fast DAO tests:

```kotlin
@Test
fun testTagInsert() = runTest {
    db.tagDao().insert(TagEntity(name = "Love", color = "#FF5722"))
    val tags = db.tagDao().getAllTags().first()
    assert(tags.size == 1)
    assert(tags[0].name == "Love")
}
```

### Integration Tests

Full database tests with real schema migrations.

---

## Common Pitfalls to Avoid

1. **Missing indices** - Always index foreign keys and junction table columns
2. **Cascade delete** - Ensure foreign keys have ON DELETE CASCADE
3. **Thread safety** - Use @Transaction for read-with-relations
4. **Migration testing** - Always test migrations before production

---

## Dependencies

- Room 2.8.4 (from STACK.md)
- Kotlinx Coroutines for Flow
- JUnit 5 for testing

---

*Research complete for Phase 8*