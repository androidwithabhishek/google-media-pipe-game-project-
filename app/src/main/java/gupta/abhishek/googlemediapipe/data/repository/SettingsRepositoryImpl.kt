package gupta.abhishek.googlemediapipe.data.repository

import gupta.abhishek.googlemediapipe.data.local.AppSettingsDao
import gupta.abhishek.googlemediapipe.data.local.AppSettingsEntity
import gupta.abhishek.googlemediapipe.domain.model.PlayerSettings
import gupta.abhishek.googlemediapipe.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl @Inject constructor(
    private val dao: AppSettingsDao
) : SettingsRepository {

    override fun observeSettings(): Flow<PlayerSettings> =
        dao.observeSettings().map { row -> row?.toModel() ?: PlayerSettings() }

    override suspend fun setHandControlEnabled(enabled: Boolean) {
        val current = currentOrDefault()
        dao.save(current.copy(handControlEnabled = enabled))
    }

    override suspend fun markHandInstructionSeen() {
        val current = currentOrDefault()
        dao.save(current.copy(handInstructionSeen = true))
    }

    private suspend fun currentOrDefault(): AppSettingsEntity =
        dao.getSettings() ?: AppSettingsEntity(
            handControlEnabled = PlayerSettings().handControlEnabled,
            handInstructionSeen = PlayerSettings().handInstructionSeen
        )

    private fun AppSettingsEntity.toModel() = PlayerSettings(
        handControlEnabled = handControlEnabled,
        handInstructionSeen = handInstructionSeen
    )
}
