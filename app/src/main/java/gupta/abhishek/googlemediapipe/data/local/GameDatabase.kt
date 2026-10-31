package gupta.abhishek.googlemediapipe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Version 2 added the app_settings table (see Migrations.kt).
@Database(
    entities = [GameScoreEntity::class, AppSettingsEntity::class],
    version = 3,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameScoreDao(): GameScoreDao
    abstract fun appSettingsDao(): AppSettingsDao
}
