package com.example.drawn.data.database

import android.content.Context
import androidx.room.Room
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.robolectric.RuntimeEnvironment

/**
 * Shared test database helper for DAO tests.
 * Creates an in-memory Room database that is destroyed after each test.
 *
 * Subclasses must be annotated with @Config(sdk = [33]) and
 * @ExtendWith(RobolectricExtension::class) for Robolectric to provide
 * the application context.
 */
abstract class DatabaseTest {

    protected lateinit var db: AppDatabase

    @BeforeEach
    fun createDb() {
        val context = RuntimeEnvironment.getApplication() as Context
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @AfterEach
    fun closeDb() {
        db.close()
    }
}
