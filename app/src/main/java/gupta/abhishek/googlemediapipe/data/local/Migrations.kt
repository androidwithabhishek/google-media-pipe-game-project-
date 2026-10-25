package gupta.abhishek.googlemediapipe.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Database version 1 -> 2: adds the app_settings table. Saved scores are kept.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `app_settings` (" +
                "`id` INTEGER NOT NULL, " +
                "`handControlEnabled` INTEGER NOT NULL, " +
                "`handInstructionSeen` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`))"
        )
    }
}
