package gupta.abhishek.googlemediapipe.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    // Keeps emitting the settings whenever they change.
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    // Reads the settings once (used before changing one value).
    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettings(): AppSettingsEntity?

    // Inserts the row, or replaces it if it already exists.
    @Upsert
    suspend fun save(settings: AppSettingsEntity)
}
