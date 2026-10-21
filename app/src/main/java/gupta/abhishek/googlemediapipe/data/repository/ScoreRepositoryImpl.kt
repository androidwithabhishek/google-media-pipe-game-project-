package gupta.abhishek.googlemediapipe.data.repository

import gupta.abhishek.googlemediapipe.data.local.GameScoreDao
import gupta.abhishek.googlemediapipe.data.local.GameScoreEntity
import gupta.abhishek.googlemediapipe.domain.repository.ScoreRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScoreRepositoryImpl @Inject constructor(
    private val dao: GameScoreDao
) : ScoreRepository {

    override fun observeBestScore(): Flow<Int> =
        dao.observeBestScore().map { it ?: 0 }

    override suspend fun saveScore(score: Int) {
        dao.insert(
            GameScoreEntity(
                score = score,
                playedAtMillis = System.currentTimeMillis()
            )
        )
    }
}
