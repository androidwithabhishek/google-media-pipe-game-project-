package gupta.abhishek.googlemediapipe.domain.repository

import kotlinx.coroutines.flow.Flow

// The app talks to this interface only. The database details live in the data layer.
interface ScoreRepository {

    // Emits the best score ever saved (0 if none). Updates itself when a new score is saved.
    fun observeBestScore(): Flow<Int>

    // Saves one finished game's score.
    suspend fun saveScore(score: Int)
}
