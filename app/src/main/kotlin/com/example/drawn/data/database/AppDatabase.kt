package com.example.drawn.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.drawn.BuildConfig
import com.example.drawn.data.database.converter.ArcanaTypeConverter
import com.example.drawn.data.database.converter.DateTypeConverter
import com.example.drawn.data.database.dao.CardDao
import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.dao.ReadingCardDao
import com.example.drawn.data.database.dao.ReadingDao
import com.example.drawn.data.database.dao.ReadingPhotoDao
import com.example.drawn.data.database.dao.SpreadDao
import com.example.drawn.data.database.entity.CardEntity
import com.example.drawn.data.database.entity.DeckEntity
import com.example.drawn.data.database.entity.ReadingCardEntity
import com.example.drawn.data.database.entity.ReadingEntity
import com.example.drawn.data.database.entity.ReadingPhotoEntity
import com.example.drawn.data.database.entity.SpreadEntity
import com.example.drawn.data.database.migration.MIGRATION_1_2
import java.util.concurrent.Executors

@Database(
    entities = [
        DeckEntity::class,
        CardEntity::class,
        SpreadEntity::class,
        ReadingEntity::class,
        ReadingCardEntity::class,
        ReadingPhotoEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(DateTypeConverter::class, ArcanaTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun spreadDao(): SpreadDao
    abstract fun readingDao(): ReadingDao
    abstract fun readingCardDao(): ReadingCardDao
    abstract fun readingPhotoDao(): ReadingPhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drawn_database"
                )
                    .createFromAsset("database/drawn_prepopulated.db")
                    .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                    .addMigrations(MIGRATION_1_2)

                if (BuildConfig.DEBUG) {
                    builder.setQueryCallback(
                        { sql, bindArgs ->
                            Log.d("Room", "SQL: $sql, Args: $bindArgs")
                        },
                        Executors.newSingleThreadExecutor()
                    )
                }

                builder.build().also { INSTANCE = it }
            }
        }
    }
}
