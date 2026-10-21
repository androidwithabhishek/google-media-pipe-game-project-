package gupta.abhishek.googlemediapipe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameScoreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameScoreDao(): GameScoreDao
}
