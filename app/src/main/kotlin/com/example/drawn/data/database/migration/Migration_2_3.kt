package com.example.drawn.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add isFavorite column to readings (default false for existing)
        db.execSQL("ALTER TABLE readings ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")

        // Create custom_cards table
        db.execSQL("""
            CREATE TABLE custom_cards (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                deckId INTEGER NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                imagePath TEXT,
                keywords TEXT NOT NULL DEFAULT '',
                uprightMeaning TEXT NOT NULL DEFAULT '',
                reversedMeaning TEXT NOT NULL DEFAULT '',
                sortOrder INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS index_custom_cards_deckId ON custom_cards(deckId)")

        // Create tags table
        db.execSQL("""
            CREATE TABLE tags (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL UNIQUE,
                color TEXT
            )
        """.trimIndent())

        // Create reading_tags junction table
        db.execSQL("""
            CREATE TABLE reading_tags (
                readingId INTEGER NOT NULL REFERENCES readings(id) ON DELETE CASCADE,
                tagId INTEGER NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
                PRIMARY KEY (readingId, tagId)
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS index_reading_tags_readingId ON reading_tags(readingId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_reading_tags_tagId ON reading_tags(tagId)")
    }
}