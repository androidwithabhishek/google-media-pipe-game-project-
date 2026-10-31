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
        dao.save(currentOrDefault().copy(handControlEnabled = enabled))
    }

    override suspend fun markHandInstructionSeen() {
        dao.save(currentOrDefault().copy(handInstructionSeen = true))
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        dao.save(currentOrDefault().copy(soundEnabled = enabled))
    }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        dao.save(currentOrDefault().copy(musicEnabled = enabled))
    }

    private suspend fun currentOrDefault(): AppSettingsEntity {
        val defaults = PlayerSettings()
        return dao.getSettings() ?: AppSettingsEntity(
            handControlEnabled = defaults.handControlEnabled,
            handInstructionSeen = defaults.handInstructionSeen,
            soundEnabled = defaults.soundEnabled,
            musicEnabled = defaults.musicEnabled
        )
    }

    private fun AppSettingsEntity.toModel() = PlayerSettings(
        handControlEnabled = handControlEnabled,
        handInstructionSeen = handInstructionSeen,
        soundEnabled = soundEnabled,
        musicEnabled = musicEnabled
    )
}