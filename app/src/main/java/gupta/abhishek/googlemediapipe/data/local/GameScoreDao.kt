package gupta.abhishek.googlemediapipe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameScoreDao {

    @Insert
    suspend fun insert(score: GameScoreEntity)

    // Highest score in the table. Null when the table is empty.
    @Query("SELECT MAX(score) FROM game_scores")
    fun observeBestScore(): Flow<Int?>
}
